/******************************************************************************
 * Copyright (c) 2013 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Gregory Amerson - [363765] Page book control property should handle model paths
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.lang.reflect.Field;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.PageBookExtDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Greg Amerson</a>
 */

public final class SapphireEnumControlledPageBook extends PageBookPart
{
    @Text( "Page book's property reference path \"{0}\" is invalid." )
    private static LocalizableText invalidPathMsg;
    
    @Text( "Page book key \"{0}\" is invalid. Class \"{1}\" could not be resolved." )
    private static LocalizableText invalidPageKeyClassNotResolvedMsg;
    
    @Text( "Page book key \"{0}\" is invalid. Class \"{1}\" does not match property type." )
    private static LocalizableText invalidPageKeyClassNotMatchedMsg;
    
    @Text( "Page book key \"{0}\" is invalid. Enum item \"{2}\" could not be resolved in {1}.")
    private static LocalizableText invalidPageKeyEnumItemMsg;

    static
    {
        LocalizableText.init( SapphireEnumControlledPageBook.class );
    }

    private Property property;
    private Listener listener;
    
    @Override
    protected void init()
    {
        final String pathString = ( (PageBookExtDef) this.definition ).getControlProperty().content();
        final String pathStringSubstituted = substituteParams( pathString, this.params );
        final ModelPath path = new ModelPath( pathStringSubstituted );
        
        Element element = getLocalModelElement();

        for( int i = 0, n = path.length(); i < n; i++ )
        {
            final ModelPath.Segment segment = path.segment( i );

            if( segment instanceof ModelPath.ModelRootSegment )
            {
                element = element.root();
            }
            else if( segment instanceof ModelPath.ParentElementSegment )
            {
                element = element.parent().element();
            }
            else if( segment instanceof ModelPath.PropertySegment )
            {
                this.property = element.property( ( (ModelPath.PropertySegment) segment ).getPropertyName() );
                
                if( this.property == null || i + 1 != n )
                {
                    throw new RuntimeException( invalidPathMsg.format( pathStringSubstituted ) );
                }
            }
            else
            {
                throw new RuntimeException( invalidPathMsg.format( pathStringSubstituted ) );
            }
        }
        
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                updateCurrentPage();
            }
        };
        
        this.property.attach( this.listener );
        
        super.init();
        
        setExposePageValidationState( true );
        updateCurrentPage();
    }

    @Override
    protected Object parsePageKey( final String panelKeyString )
    {
        final Class<?> enumType = this.property.definition().getTypeClass();
        final int lastDot = panelKeyString.lastIndexOf( '.' );
        final String enumItemName;
        
        if( lastDot == -1 )
        {
            enumItemName = panelKeyString;
        }
        else
        {
            final String className = panelKeyString.substring( 0, lastDot );
            enumItemName = panelKeyString.substring( lastDot + 1 );
            
            final ISapphireUiDef rootdef = this.definition.nearest( ISapphireUiDef.class );
            final Class<?> specifiedEnumType = rootdef.resolveClass( className );
            
            if( specifiedEnumType == null )
            {
                throw new RuntimeException( invalidPageKeyClassNotResolvedMsg.format( panelKeyString, className ) );
            }
            
            if( specifiedEnumType != enumType )
            {
                throw new RuntimeException( invalidPageKeyClassNotMatchedMsg.format( panelKeyString, className ) );
            }
        }
        
        Field field = null;
        
        for( Field f : enumType.getFields() )
        {
            if( f.isEnumConstant() && f.getName().equalsIgnoreCase( enumItemName ) )
            {
                field = f;
                break;
            }
        }
        
        if( field == null )
        {
            throw new RuntimeException( invalidPageKeyEnumItemMsg.format( panelKeyString, enumType.getSimpleName(), enumItemName ) );
        }
        
        try
        {
            return field.get( null );
        }
        catch( IllegalAccessException e )
        {
            throw new RuntimeException( e );
        }
    }
    
    private void updateCurrentPage()
    {
        final Value<?> newEnumItemValue = (Value<?>) this.property;
        final Enum<?> newEnumItem = (Enum<?>) newEnumItemValue.content( true );

        changePage( this.property.element(), newEnumItem );
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            this.property.detach( this.listener );
        }
    }
    
}
