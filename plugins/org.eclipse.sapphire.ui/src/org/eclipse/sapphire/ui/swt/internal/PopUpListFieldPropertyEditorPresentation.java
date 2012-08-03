/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.EMPTY_STRING;
import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;

import java.util.SortedSet;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.PropertyContentEvent;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.sapphire.services.ValueNormalizationService;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.renderers.swt.ValuePropertyEditorRenderer;
import org.eclipse.sapphire.util.MutableReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PopUpListFieldPropertyEditorPresentation extends ValuePropertyEditorRenderer
{
    private final PopUpListFieldStyle style;
    private Combo combo;

    public PopUpListFieldPropertyEditorPresentation( final SapphireRenderingContext context,
                                                     final PropertyEditorPart part,
                                                     final PopUpListFieldStyle style )
    {
        super( context, part );
        
        if( style == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.style = style;
    }

    @Override
    protected void createContents( final Composite parent )
    {
        final PropertyEditorPart part = getPart();
        final IModelElement element = part.getLocalModelElement();
        final ValueProperty property = (ValueProperty) part.getProperty();
        
        final Composite composite = createMainComposite( parent );
        composite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );
        
        final PropertyEditorAssistDecorator decorator = createDecorator( composite );
        decorator.addEditorControl( composite );
        decorator.control().setLayoutData( gdvalign( gd(), SWT.TOP ) );

        final Combo combo = new Combo( composite, SWT.SINGLE | SWT.BORDER | ( this.style == PopUpListFieldStyle.STRICT ? SWT.READ_ONLY : SWT.NONE ) );
        combo.setLayoutData( gdhfill() );
        combo.setVisibleItemCount( 10 );

        decorator.addEditorControl( combo, true );
        addControl( combo );
        this.context.adapt( combo );
        this.combo = combo;
        
        final PossibleValuesService possibleValuesService = element.service( property, PossibleValuesService.class );
        final ValueLabelService valueLabelService = element.service( property, ValueLabelService.class );
        final ValueNormalizationService valueNormalizationService = element.service( property, ValueNormalizationService.class );
        
        final MutableReference<String[]> possibleValuesRef = new MutableReference<String[]>();
        
        final Runnable updateComboSelectionOp = new Runnable()
        {
            public void run()
            {
                final String value = valueNormalizationService.normalize( element.read( property ).getText() );
                
                if( value == null )
                {
                    combo.deselectAll();
                    combo.setText( EMPTY_STRING );
                }
                else
                {
                    if( PopUpListFieldPropertyEditorPresentation.this.style == PopUpListFieldStyle.STRICT )
                    {
                        final String[] possibleValues = possibleValuesRef.get();
                        int possibleValueIndex = -1;
                        
                        for( int i = 0; i < possibleValues.length && possibleValueIndex == -1; i++ )
                        {
                            if( equal( possibleValues[ i ], value ) )
                            {
                                possibleValueIndex = i;
                            }
                        }
                        
                        if( possibleValueIndex == -1 )
                        {
                            if( possibleValues.length == combo.getItemCount() )
                            {
                                combo.add( value );
                            }
                            else
                            {
                                final String existingNonConformingValue = combo.getItem( possibleValues.length );
                                
                                if( ! existingNonConformingValue.equals( value ) )
                                {
                                    combo.setItem( possibleValues.length, value );
                                }
                            }
                            
                            possibleValueIndex = possibleValues.length;
                        }
                        else
                        {
                            if( possibleValues.length < combo.getItemCount() )
                            {
                                combo.remove( possibleValues.length );
                            }
                        }
                        
                        if( combo.getSelectionIndex() != possibleValueIndex )
                        {
                            combo.select( possibleValueIndex );
                        }
                    }
                    else
                    {
                        if( ! equal( valueNormalizationService.normalize( combo.getText() ), value ) )
                        {
                            combo.setText( value );
                        }
                    }
                }
            }
        };

        final Runnable updateComboContentOp = new Runnable()
        {
            public void run()
            {
                final SortedSet<String> possibleValues = possibleValuesService.values();
                
                final String[] possibleValuesArray = new String[ possibleValues.size() ];
                final String[] contentForCombo = new String[ possibleValuesArray.length ];
                
                {
                    int i = 0;

                    for( String possibleValue : possibleValues )
                    {
                        final String normalizedPossibleValue = valueNormalizationService.normalize( possibleValue );
                        possibleValuesArray[ i ] = normalizedPossibleValue;
                        contentForCombo[ i ] = valueLabelService.provide( normalizedPossibleValue );
                        
                        i++;
                    }
                }
                
                combo.setItems( contentForCombo );
                
                possibleValuesRef.set( possibleValuesArray );
                updateComboSelectionOp.run();
            }
        };
        
        updateComboContentOp.run();
        
        final Listener possibleValuesServiceListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                updateComboContentOp.run();
            }
        };
        
        possibleValuesService.attach( possibleValuesServiceListener );
        
        final Listener propertyListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                updateComboSelectionOp.run();
            }
        };
        
        element.attach( propertyListener, property.getName() );
        
        final Runnable updateModelOp = new Runnable()
        {
            public void run()
            {
                String value = null;
                final int index = combo.getSelectionIndex();
                
                if( index != -1 )
                {
                    final String[] possible = possibleValuesRef.get();
                    
                    if( index < possible.length )
                    {
                        value = possible[ index ];
                    }
                    else
                    {
                        value = combo.getText();
                    }
                }
                
                element.write( property, value );
            }
        };
        
        combo.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent e )
                {
                    updateModelOp.run();
                }
            }
        );
        
        addOnDisposeOperation
        (
            new Runnable()
            {
                public void run()
                {
                    possibleValuesService.detach( possibleValuesServiceListener );
                    element.detach( propertyListener, property.getName() );
                }
            }
        );
    }
    
    @Override
    protected void handleFocusReceivedEvent()
    {
        this.combo.setFocus();
    }

}
