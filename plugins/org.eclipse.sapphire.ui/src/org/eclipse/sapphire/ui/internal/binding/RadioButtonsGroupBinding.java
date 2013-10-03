/******************************************************************************
 * Copyright (c) 2013 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Gregory Amerson - [418602] Radio buttons property editor should show images
 ******************************************************************************/

package org.eclipse.sapphire.ui.internal.binding;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.MasterConversionService;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.EnumValueType;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.services.ValueImageService;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.PropertyEditorDef;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public final class RadioButtonsGroupBinding extends AbstractEnumBinding
{
    private RadioButtonsGroup buttonsGroup;
    private Button badValueButton;
    
    public RadioButtonsGroupBinding( final PropertyEditorPart editor,
                                     final SapphireRenderingContext context,
                                     final RadioButtonsGroup buttonsGroup )
    {
        super( editor, context, buttonsGroup );
    }
    
    @Override
    
    protected void initialize( final PropertyEditorPart editor,
                               final SapphireRenderingContext context,
                               final Control control )
    {
        super.initialize( editor, context, control );
        
        this.buttonsGroup = (RadioButtonsGroup) control;
        
        final Property property = editor.property();
        final EnumValueType enumValueType = new EnumValueType( this.enumValues[ 0 ].getDeclaringClass() );

        for( Enum<?> enumItem : this.enumValues )
        {
            final String enumItemStr = property.service( MasterConversionService.class ).convert( enumItem, String.class );
            final String auxText = editor.getRenderingHint( PropertyEditorDef.HINT_AUX_TEXT + "." + enumItemStr, null );
            final ValueImageService imageService = property.service( ValueImageService.class );
            final ImageData imageData = imageService.provide( enumItemStr );
            final Image image = context.getImageCache().image( imageData );
            final Button button = this.buttonsGroup.addRadioButton( enumValueType.getLabel( enumItem, false, CapitalizationType.FIRST_WORD_ONLY, true ), auxText, image );
            button.setData( enumItem );
        }
        
        this.buttonsGroup.addSelectionListener
        ( 
            new SelectionAdapter()
            {
                public void widgetSelected( final SelectionEvent event )
                {
                    updateModel();
                    updateTargetAttributes();
                }
            }
        );
    }
    
    @Override
    
    protected int getSelectionIndex()
    {
        return this.buttonsGroup.getSelectionIndex();
    }

    @Override
    
    protected void setSelectionIndex( final int index )
    {
        this.buttonsGroup.setSelectionIndex( index );
    }
    
    @Override
    
    protected void createMalformedItem( String label )
    {
        if( this.badValueButton == null )
        {
            this.badValueButton = this.buttonsGroup.addRadioButton( MiscUtil.EMPTY_STRING );
        }
        
        this.badValueButton.setText( label );
        getContext().layout();
    }

    @Override
    
    protected void removeMalformedItem()
    {
        if( ! this.buttonsGroup.isDisposed() )
        {
            if( this.badValueButton != null )
            {
                this.badValueButton.dispose();
                this.badValueButton = null;
                getContext().layout();
            }
        }
    }

}
