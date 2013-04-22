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

package org.eclipse.sapphire.ui.renderers.swt;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;

import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.swt.renderer.TextOverlayPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ScalePropertyEditorRenderer extends ValuePropertyEditorRenderer
{
    private int minimum;
    private int maximum;
    private int offset;
    private Scale scale;
    private Text textField;
    
    public ScalePropertyEditorRenderer( final SapphireRenderingContext context,
                                        final PropertyEditorPart part )
    {
        super( context, part );
    }

    @Override
    protected void createContents( final Composite parent )
    {
        final NumericRange rangeAnnotation = property().definition().getAnnotation( NumericRange.class );
        
        try
        {
            this.minimum = Integer.parseInt( rangeAnnotation.min() );
            this.maximum = Integer.parseInt( rangeAnnotation.max() );
        }
        catch( NumberFormatException e )
        {
            // Should not happen here. We already checked this in property editor applicability test.
            
            throw new RuntimeException( e );
        }
        
        if( this.minimum < 0 )
        {
            this.offset = this.minimum * -1;
            this.minimum += this.offset;
            this.maximum += this.offset;
        }
        else
        {
            this.offset = 0;
        }
        
        final Composite composite = createMainComposite( parent );
        composite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

        final Composite textFieldComposite = new Composite( composite, SWT.NONE );
        textFieldComposite.setLayoutData( gdwhint( gd(), 60 ) );
        textFieldComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

        final PropertyEditorAssistDecorator decorator = createDecorator( textFieldComposite ); 
        decorator.control().setLayoutData( gdvalign( gd(), SWT.TOP ) );
        
        this.textField = new Text( textFieldComposite, SWT.BORDER );
        this.textField.setLayoutData( gdhfill() );
        
        this.textField.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent event )
                {
                    setPropertyValue( ScalePropertyEditorRenderer.this.textField.getText() );
                }
            }
        );
        
        final TextOverlayPainter.Controller textOverlayPainterController = new TextOverlayPainter.Controller()
        {
            @Override
            public String getDefaultText()
            {
                return property().getDefaultText();
            }
        };
        
        TextOverlayPainter.install( this.textField, textOverlayPainterController );
        
        this.scale = new Scale( composite, SWT.HORIZONTAL );
        this.scale.setLayoutData( gdhfill() );
        this.scale.setMinimum( this.minimum );
        this.scale.setMaximum( this.maximum );
        this.scale.setIncrement( 1 );
        this.scale.setPageIncrement( 1 );
        
        this.scale.addSelectionListener
        (
            new SelectionAdapter()
            {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    final int value = ScalePropertyEditorRenderer.this.scale.getSelection() - ScalePropertyEditorRenderer.this.offset;
                    setPropertyValue( String.valueOf( value ) );
                }
            }
        );

        decorator.addEditorControl( composite );
        
        addControl( this.textField );
        addControl( this.scale );
    }
    
    @Override
    protected void handlePropertyChangedEvent()
    {
        super.handlePropertyChangedEvent();
        
        final Value<?> value = property();
        
        final String existingValueInTextField = this.textField.getText();
        final String newValueForTextField = value.text( false );
        
        if( ! existingValueInTextField.equals( newValueForTextField ) )
        {
            this.textField.setText( newValueForTextField == null ? MiscUtil.EMPTY_STRING : newValueForTextField );
        }

        final Integer newValueInteger = (Integer) value.content( true );
        int newValueForScale = ( newValueInteger == null ? this.scale.getMinimum() : newValueInteger.intValue() + this.offset );
        
        if( newValueForScale < this.minimum )
        {
            newValueForScale = this.minimum;
        }
        else if( newValueForScale > this.maximum )
        {
            newValueForScale = this.maximum;
        }
        
        if( this.scale.getSelection() != newValueForScale )
        {
            this.scale.setSelection( newValueForScale );
        }
    }
    
    @Override
    protected void handleFocusReceivedEvent()
    {
        this.scale.setFocus();
    }

    public static final class Factory extends PropertyEditorRendererFactory
    {
        @Override
        public boolean isApplicableTo( final PropertyEditorPart propertyEditorPart )
        {
            final PropertyDef property = propertyEditorPart.property().definition();
            
            if( property.isOfType( Integer.class ) )
            {
                final NumericRange rangeAnnotation = property.getAnnotation( NumericRange.class );
                
                if( rangeAnnotation != null )
                {
                    final String minStr = rangeAnnotation.min();
                    final String maxStr = rangeAnnotation.max();
                    
                    if( minStr.length() > 0 && maxStr.length() > 0 )
                    {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        @Override
        public PropertyEditorRenderer create( final SapphireRenderingContext context,
                                              final PropertyEditorPart part )
        {
            return new ScalePropertyEditorRenderer( context, part );
        }
    }

}
