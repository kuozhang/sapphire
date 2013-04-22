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

package org.eclipse.sapphire.ui.internal.binding;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class AbstractEnumBinding extends AbstractBinding
{
    protected Enum<?>[] enumValues;
    
    public AbstractEnumBinding( final PropertyEditorPart editor,
                                final SapphireRenderingContext context,
                                final Control control )
    {
        super( editor, context, control );
    }
    
    @Override
    
    protected void initialize( final PropertyEditorPart editor,
                               final SapphireRenderingContext context,
                               final Control control )
    {
        super.initialize( editor, context, control );
        
        this.enumValues = (Enum<?>[]) editor.property().definition().getTypeClass().getEnumConstants();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    
    public Value<Enum<?>> property()
    {
        return (Value<Enum<?>>) super.property();
    }
    
    @Override
    
    protected final void doUpdateModel()
    {
        final int index = getSelectionIndex();
        
        if( index >= 0 && index < this.enumValues.length )
        {
            property().write( this.enumValues[ index ] );
            removeMalformedItem();
        }
    }

    @Override
    
    protected final void doUpdateTarget()
    {
        final int existingSelection = getSelectionIndex();
        final Value<Enum<?>> value = property();

        int newSelection = this.enumValues.length;
        
        if( ! value.malformed() )
        {
            final Enum<?> newValueEnum = value.content( true );
            
            for( int i = 0, n = this.enumValues.length; i < n; i++ )
            {
                if( this.enumValues[ i ] == newValueEnum )
                {
                    newSelection = i;
                    break;
                }
            }
        }
        
        if( newSelection == this.enumValues.length )
        {
            final String newValueString = value.text( true );
            final String label = ( newValueString == null ? Resources.nullValueLabel : newValueString );

            createMalformedItem( label );
        }
        else
        {
            removeMalformedItem();
        }
        
        if( existingSelection != newSelection )
        {
            setSelectionIndex( newSelection );
        }
    }
    
    protected abstract int getSelectionIndex();
    protected abstract void setSelectionIndex( int index );
    protected abstract void createMalformedItem( String label );
    protected abstract void removeMalformedItem();
    
    protected static final class Resources
        
        extends NLS
        
    {
        public static String nullValueLabel;
    
        static
        {
            initializeMessages( AbstractEnumBinding.class.getName(), 
                                Resources.class );
        }
    }

}
