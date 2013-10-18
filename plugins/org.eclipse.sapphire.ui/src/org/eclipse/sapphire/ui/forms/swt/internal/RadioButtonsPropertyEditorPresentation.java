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

package org.eclipse.sapphire.ui.forms.swt.internal;

import static org.eclipse.sapphire.ui.forms.PropertyEditorPart.DATA_BINDING;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdhindent;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.gdvindent;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.forms.swt.GridLayoutUtil.glspacing;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.SapphirePart.LabelChangedEvent;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentationFactory;
import org.eclipse.sapphire.ui.forms.swt.RadioButtonsGroup;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.ui.forms.swt.ValuePropertyEditorPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RadioButtonsPropertyEditorPresentation extends ValuePropertyEditorPresentation
{
    private RadioButtonsGroup control;

    public RadioButtonsPropertyEditorPresentation( final FormComponentPart part, final SwtPresentation parent, final Composite composite )
    {
        super( part, parent, composite );
    }

    @Override
    protected void createContents( final Composite parent )
    {
        final PropertyEditorPart part = part();
        
        final boolean showLabel = part.getShowLabel();
        final int leftMargin = part.getMarginLeft();
        final boolean preferVerticalRadioButtonBinding = part.getRenderingHint( PropertyEditorDef.HINT_PREFER_VERTICAL_RADIO_BUTTONS, false );
        
        PropertyEditorAssistDecorator decorator = null;
        
        if( preferVerticalRadioButtonBinding )
        {
            final Composite composite = createMainComposite
            (
                parent,
                new CreateMainCompositeDelegate( part )
                {
                    @Override
                    public boolean getShowLabel()
                    {
                        return false;
                    }

                    @Override
                    public boolean getSpanBothColumns()
                    {
                        return true;
                    }
                }
            );
            
            composite.setLayout( glspacing( glayout( 2, 0, 0 ), 2, 5 ) );

            decorator = createDecorator( composite );
            decorator.addEditorControl( composite );

            if( showLabel )
            {
                decorator.control().setLayoutData( gdvalign( gd(), SWT.CENTER ) );
                
                final Label label = new Label( composite, SWT.WRAP );
                label.setLayoutData( gd() );
                
                final Runnable updateLabelOp = new Runnable()
                {
                    public void run()
                    {
                        label.setText( part.getLabel( CapitalizationType.FIRST_WORD_ONLY, true ) );
                    }
                };
                
                final org.eclipse.sapphire.Listener listener = new org.eclipse.sapphire.Listener()
                {
                    @Override
                    public void handle( final org.eclipse.sapphire.Event event )
                    {
                        if( event instanceof LabelChangedEvent )
                        {
                            updateLabelOp.run();
                            layout();
                        }
                    }
                };
                
                part.attach( listener );
                updateLabelOp.run();
                
                label.addDisposeListener
                (
                    new DisposeListener()
                    {
                        public void widgetDisposed( final DisposeEvent event )
                        {
                            part.detach( listener );
                        }
                    }
                );
                
                decorator.addEditorControl( label );
            }
            else
            {
                decorator.control().setLayoutData( gdvindent( gdvalign( gd(), SWT.TOP ), 4 ) );
            }
            
            this.control = new RadioButtonsGroup( composite, true );
            
            if( showLabel )
            {
                this.control.setLayoutData( gdhindent( gdhspan( gdhfill(), 2 ), leftMargin + 20 ) );
            }
            else
            {
                this.control.setLayoutData( gdhfill() );
            }
        }
        else
        {
            final Composite composite = createMainComposite( parent );
            composite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );
            
            decorator = createDecorator( composite );
            decorator.addEditorControl( composite );
            
            decorator.control().setLayoutData( gdvalign( gd(), SWT.CENTER ) );

            this.control = new RadioButtonsGroup( composite, false );
            this.control.setLayoutData( gdhfill() );
        }
    
        this.binding = new RadioButtonsGroupBinding( this, this.control );            
    
        this.control.setData( DATA_BINDING, this.binding );
        decorator.addEditorControl( this.control, true );
    
        addControl( this.control );
    }
    
    @Override
    protected void handleFocusReceivedEvent()
    {
        this.control.setFocus();
    }

    public static final class Factory extends PropertyEditorPresentationFactory
    {
        @Override
        public boolean isApplicableTo( final PropertyEditorPart part )
        {
            final Property property = part.property();
            
            if( property instanceof Value && property.definition().isOfType( Enum.class ) )
            {
                final boolean preferVerticalRadioButtonBinding = part.getRenderingHint( PropertyEditorDef.HINT_PREFER_VERTICAL_RADIO_BUTTONS, false );                
                final boolean preferRadioButtonBinding = part.getRenderingHint( PropertyEditorDef.HINT_PREFER_RADIO_BUTTONS, false );
                final boolean preferComboBinding = part.getRenderingHint( PropertyEditorDef.HINT_PREFER_COMBO, false );
                final Enum<?>[] enumValues = (Enum<?>[]) property.definition().getTypeClass().getEnumConstants();
                
                return ( preferVerticalRadioButtonBinding || preferRadioButtonBinding || ( enumValues.length <= 3 && ! preferComboBinding ) );
            }
            
            return false;
        }
        
        @Override
        public PropertyEditorPresentation create( final FormComponentPart part, final SwtPresentation parent, final Composite composite )
        {
            return new RadioButtonsPropertyEditorPresentation( part, parent, composite );
        }
    }

}
