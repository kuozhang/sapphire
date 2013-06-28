/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.EditFailedException;
import org.eclipse.sapphire.services.PossibleTypesService;
import org.eclipse.sapphire.ui.PartVisibilityEvent;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentOutline;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OutlineNodeAddActionHandlerFactory extends SapphireActionHandlerFactory
{
    public static final String ID_BASE = "Sapphire.Add.";
    
    private Listener possibleTypesServiceListener;
    private Listener nodeFactoryListener;
    
    @Override
    public void init( final SapphireAction action,
                      final ActionHandlerFactoryDef def )
    {
        super.init( action, def );
        
        this.possibleTypesServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                broadcast( new Event() );
            }
        };
        
        this.nodeFactoryListener = new FilteredListener<PartVisibilityEvent>()
        {
            @Override
            protected void handleTypedEvent( final PartVisibilityEvent event )
            {
                broadcast( new Event() );
            }
        };
        
        final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();

        node.executeAfterInitialization
        (
            new Runnable()
            {
                public void run()
                {
                    for( MasterDetailsContentNode.NodeFactory factory : node.factories() )
                    {
                        factory.attach( OutlineNodeAddActionHandlerFactory.this.nodeFactoryListener );
                        
                        final PossibleTypesService possibleTypesService = factory.property().service( PossibleTypesService.class );
                        possibleTypesService.attach( OutlineNodeAddActionHandlerFactory.this.possibleTypesServiceListener );
                    }
                    
                    broadcast( new Event() );
                }
            }
        );
    }

    @Override
    public List<SapphireActionHandler> create()
    {
        final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();
        final List<SapphireActionHandler> handlers = new ArrayList<SapphireActionHandler>();
        
        for( MasterDetailsContentNode.NodeFactory factory : node.factories() )
        {
            final Property property = factory.property();
            
            if( factory.visible() && ! property.definition().isReadOnly() )
            {
                final PossibleTypesService possibleTypesService = property.service( PossibleTypesService.class );
    
                if( property instanceof ElementList )
                {
                    final ListProperty pdef = (ListProperty) property.definition();
                    
                    for( final ElementType memberType : possibleTypesService.types() )
                    {
                        final ListPropertyActionHandler handler = new ListPropertyActionHandler( pdef, memberType );
                        handlers.add( handler );
                    }
                }
                else if( property instanceof ElementHandle && ! ( property.definition() instanceof ImpliedElementProperty ) )
                {
                    final ElementProperty pdef = (ElementProperty) property.definition();
                    
                    for( final ElementType memberType : possibleTypesService.types() )
                    {
                        final ElementPropertyActionHandler handler = new ElementPropertyActionHandler( pdef, memberType );
                        handlers.add( handler );
                    }
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
        }
        
        return handlers;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();

        for( MasterDetailsContentNode.NodeFactory factory : node.factories() )
        {
            factory.detach( this.nodeFactoryListener );
            
            final PossibleTypesService possibleTypesService = factory.property().service( PossibleTypesService.class );
            possibleTypesService.detach( this.possibleTypesServiceListener );
        }
    }

    private static abstract class AbstractActionHandler extends SapphireActionHandler
    {
        private final PropertyDef property;
        private final ElementType type;
        private MasterDetailsContentOutline contentTree;
        
        public AbstractActionHandler( final PropertyDef property,
                                      final ElementType type )
        {
            this.property = property;
            this.type = type;
        }
    
        @Override
        public void init( final SapphireAction action,
                          final ActionHandlerDef def )
        {
            super.init( action, def );
            
            setId( ID_BASE + this.type.getSimpleName() );
            setLabel( this.type.getLabel( true, CapitalizationType.NO_CAPS, false ) );
            
            final ImageData typeSpecificAddImage = this.type.image();
            
            if( typeSpecificAddImage != null )
            {
                addImage( typeSpecificAddImage );
            }
            
            this.contentTree = ( (MasterDetailsContentNode) getPart() ).getContentTree();
            
            final Listener contentTreeListener = new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof MasterDetailsContentOutline.FilterChangedEvent )
                    {
                        refreshEnablementState();
                    }
                }
            };
            
            this.contentTree.attach( contentTreeListener );
            
            refreshEnablementState();
            
            attach
            (
                new Listener()
                {
                    @Override
                    public void handle( final Event event )
                    {
                        if( event instanceof DisposeEvent )
                        {
                            AbstractActionHandler.this.contentTree.detach( contentTreeListener );
                        }
                    }
                }
            );
        }
    
        protected final void refreshEnablementState()
        {
            setEnabled( computeEnablementState() );
        }
        
        protected boolean computeEnablementState()
        {
            return ( this.contentTree != null && this.contentTree.getFilterText().length() == 0 );
        }
        
        public PropertyDef property()
        {
            return this.property;
        }
        
        @Override
        protected final Object run( final SapphireRenderingContext context )
        {
            final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();
            final Element element = node.getLocalModelElement();
            
            Element newModelElement = null;
            
            try
            {
                newModelElement = create( element, this.property, this.type );
            }
            catch( Exception e )
            {
                // Log this exception unless the cause is EditFailedException. These exception
                // are the result of the user declining a particular action that is necessary
                // before the edit can happen (such as making a file writable).
                
                final EditFailedException editFailedException = EditFailedException.findAsCause( e );
                
                if( editFailedException == null )
                {
                    SapphireUiFrameworkPlugin.log( e );
                }
            }

            if( newModelElement != null )
            {
                for( MasterDetailsContentNode n : node.nodes().visible() )
                {
                    if( n.getModelElement() == newModelElement )
                    {
                        n.select();
                        getPart().nearest( MasterDetailsEditorPagePart.class ).setFocusOnDetails();
                        break;
                    }
                }
            }
            
            return newModelElement;
        }
        
        protected abstract Element create( Element element,
                                                 PropertyDef property,
                                                 ElementType type );
    }
    
    private static final class ListPropertyActionHandler extends AbstractActionHandler
    {
        public ListPropertyActionHandler( final ListProperty property,
                                          final ElementType type )
        {
            super( property, type );
        }
        
        @Override
        protected Element create( final Element element,
                                        final PropertyDef property,
                                        final ElementType type )
        {
            return element.property( (ListProperty) property ).insert( type );
        }
    }

    private static final class ElementPropertyActionHandler extends AbstractActionHandler
    {
        public ElementPropertyActionHandler( final ElementProperty property,
                                             final ElementType type )
        {
            super( property, type );
        }
        
        @Override
        public void init( SapphireAction action,
                          ActionHandlerDef def )
        {
            super.init( action, def );
            
            final Listener listener = new FilteredListener<PropertyEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyEvent event )
                {
                    refreshEnablementState();
                }
            };
            
            final Property property = getPart().getLocalModelElement().property( property() );
            
            property.attach( listener );
            
            attach
            (
                new Listener()
                {
                    @Override
                    public void handle( final Event event )
                    {
                        if( event instanceof DisposeEvent )
                        {
                            property.detach( listener );
                        }
                    }
                }
            );
        }

        @Override
        public ElementProperty property()
        {
            return (ElementProperty) super.property();
        }
        
        @Override
        protected Element create( final Element element,
                                        final PropertyDef property,
                                        final ElementType type )
        {
            return element.property( (ElementProperty) property ).content( true, type );
        }

        @Override
        protected boolean computeEnablementState()
        {
            boolean state = super.computeEnablementState();
            
            if( state == true )
            {
                final Element element = ( (MasterDetailsContentNode) getPart() ).getLocalModelElement();
                final ElementHandle<?> handle = element.property( property() );
                state = ( handle.content() == null );
            }
            
            return state;
        }
    }

}

