/******************************************************************************
 * Copyright (c) 2013 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Gregory Amerson - [363765] Page book control property should handle model paths
 *    Gregory Amerson - [377329] SapphireListControlledPageBook fails to render with model path for control property
 ******************************************************************************/

package org.eclipse.sapphire.ui.forms;

import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.ListSelectionService;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.util.IdentityHashSet;
import org.eclipse.sapphire.util.MutableReference;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Greg Amerson</a>
 */

public final class DetailSectionPart extends PageBookPart
{
    @Text( "Page book's property reference path \"{0}\" is invalid." )
    private static LocalizableText invalidPath;

    static
    {
        LocalizableText.init( DetailSectionPart.class );
    }

    private Element element;
    private ListProperty property;

    @Override
    protected void init()
    {
        super.init();

        final String pathString = ( (DetailSectionDef) this.definition ).getProperty().content();
        final String pathStringSubstituted = substituteParams( pathString, this.params );
        final ModelPath path = new ModelPath( pathStringSubstituted );
        
        this.element = getLocalModelElement();

        for( int i = 0, n = path.length(); i < n; i++ )
        {
            final ModelPath.Segment segment = path.segment( i );

            if( segment instanceof ModelPath.ModelRootSegment )
            {
                this.element = this.element.root();
            }
            else if( segment instanceof ModelPath.ParentElementSegment )
            {
                this.element = this.element.parent().element();
            }
            else if( segment instanceof ModelPath.PropertySegment )
            {
                this.property = (ListProperty) resolve( this.element, ( (ModelPath.PropertySegment) segment ).getPropertyName() );

                if ( i + 1 != n )
                {
                    throw new RuntimeException( invalidPath.format( pathString ) );
                }
            }
            else
            {
                throw new RuntimeException( invalidPath.format( pathString ) );
            }
        }
        
        ISapphirePart ancestor = parent();
        SapphirePart highestUninitializedAncestor = null;
        
        while( ancestor instanceof SapphirePart && ! ( (SapphirePart) ancestor ).initialized() )
        {
            highestUninitializedAncestor = (SapphirePart) ancestor;
            ancestor = ancestor.parent();
        }
        
        if( highestUninitializedAncestor == null )
        {
            initListSelectionServiceListener();
        }
        else
        {
            highestUninitializedAncestor.attach
            (
                new FilteredListener<PartInitializationEvent>()
                {
                    @Override
                    protected void handleTypedEvent( final PartInitializationEvent event )
                    {
                        event.part().detach( this );
                        initListSelectionServiceListener();
                    }
                }
            );
        }
    }

    private void initListSelectionServiceListener()
    {
        final PropertyEditorPart listPropertyEditorPart = findPropertyEditor( this, this.element, this.property );
        
        if( listPropertyEditorPart == null )
        {
            final String msg = "DetailsSectionPart did not find " + this.property;
            throw new RuntimeException( msg );
        }
        
        final ListSelectionService listSelectionService = listPropertyEditorPart.service( ListSelectionService.class );
        
        final MutableReference<Element> selectedModelElementRef = new MutableReference<Element>();

        final Listener listSelectionServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                final List<Element> selection = listSelectionService.selection();
                final Element newModelElement;
                final ClassBasedKey newPageKey;
                
                if( ! selection.isEmpty() )
                {
                    newModelElement = selection.get( 0 );
                    newPageKey = ClassBasedKey.create( newModelElement );
                }
                else
                {
                    newModelElement = DetailSectionPart.this.element;
                    newPageKey = null;
                }
                
                if( selectedModelElementRef.get() != newModelElement )
                {
                    selectedModelElementRef.set( newModelElement );
                    
                    final Runnable inputChangeOperation = new Runnable()
                    {
                        public void run()
                        {
                            changePage( newModelElement, newPageKey );
                        }
                    };
                    
                    Display.getDefault().syncExec( inputChangeOperation );
                }
            }
        };
        
        listSelectionService.attach( listSelectionServiceListener );
        
        attach
        (
            new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        listSelectionService.detach( listSelectionServiceListener );
                    }
                }
            }
        );
        
        changePage( this.element, (String) null );
    }
    
    @Override
    protected Object parsePageKey( final String pageKeyString )
    {
        final ISapphireUiDef rootdef = this.definition.nearest( ISapphireUiDef.class );
        final Class<?> cl = rootdef.resolveClass( pageKeyString );
        return ClassBasedKey.create( cl );
    }

    private PropertyEditorPart findPropertyEditor( final ISapphirePart part,
                                                   final Element element,
                                                   final PropertyDef property )
    {
        return findPropertyEditor( part, element, property, new IdentityHashSet<ISapphirePart>() );
    }

    private PropertyEditorPart findPropertyEditor( final ISapphirePart part,
                                                   final Element element,
                                                   final PropertyDef property,
                                                   final Set<ISapphirePart> searchedParts )
    {
        if( searchedParts.contains( part ) )
        {
            return null;
        }
        
        if( part instanceof PropertyEditorPart )
        {
            final PropertyEditorPart propertyEditorPart = (PropertyEditorPart) part;
            
            if( propertyEditorPart.getLocalModelElement() == element && propertyEditorPart.property().definition() == property )
            {
                return propertyEditorPart;
            }
        }
        
        searchedParts.add( part );
        
        if( part instanceof ContainerPart )
        {
            final ContainerPart<?> container = (ContainerPart<?>) part;
            
            for( SapphirePart child : container.children().all() )
            {
                final PropertyEditorPart propertyEditorPart = findPropertyEditor( child, element, property, searchedParts );
                
                if( propertyEditorPart != null )
                {
                    return propertyEditorPart;
                }
            }
        }
        
        final ISapphirePart parent = part.parent();
        
        if( parent != null )
        {
            return findPropertyEditor( parent, element, property, searchedParts );
        }
        
        return null;
    }

}
