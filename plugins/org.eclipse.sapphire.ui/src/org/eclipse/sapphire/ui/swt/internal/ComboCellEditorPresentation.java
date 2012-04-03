/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - Bug 328777 Table cell editor overlaps neighboring field
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.internal;

import java.util.SortedSet;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.sapphire.services.ValueNormalizationService;
import org.eclipse.sapphire.ui.renderers.swt.DefaultListPropertyEditorRenderer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ComboCellEditorPresentation extends ComboBoxCellEditor
{
    private final StructuredViewer viewer;
    private final DefaultListPropertyEditorRenderer.SelectionProvider selectionProvider;
    private final IModelElement element;
    private final ValueProperty property;
    private String[] possibleValues;
    private boolean isDefaultValue;
    private CCombo combo;
    private boolean disableFocusLostHandler;
    private ISelection selectionPriorToActivation;
    
    public ComboCellEditorPresentation( final StructuredViewer parent,
                                       final DefaultListPropertyEditorRenderer.SelectionProvider selectionProvider,
                                       final IModelElement element,
                                       final ValueProperty property,
                                       final int style )
    {
        super();
        
        this.viewer = parent;
        this.selectionProvider = selectionProvider;
        this.element = element;
        this.property = property;
        
        setStyle( style );
        create( (Composite) parent.getControl() );
        
        this.combo = (CCombo) getControl();
        
        this.combo.addSelectionListener
        (
             new SelectionAdapter()
             {
                @Override
                public void widgetSelected( final SelectionEvent event )
                {
                    ComboCellEditorPresentation.this.isDefaultValue = false;
                }
             }
        );
        
        this.combo.addModifyListener
        (
            new ModifyListener()
            {
                public void modifyText( final ModifyEvent event )
                {
                    ComboCellEditorPresentation.this.isDefaultValue = false;
                }
            }
        );
        
        final PossibleValuesService possibleValuesService = element.service( property, PossibleValuesService.class );
        final ValueLabelService valueLabelService = element.service( property, ValueLabelService.class );
        final ValueNormalizationService valueNormalizationService = element.service( property, ValueNormalizationService.class );
        
        final SortedSet<String> possibleValuesFromService = possibleValuesService.values();
        
        this.possibleValues = new String[ possibleValuesFromService.size() ];
        final String[] contentForCombo = new String[ this.possibleValues.length ];
        
        {
            int i = 0;

            for( String possibleValue : possibleValuesFromService )
            {
                final String normalizedPossibleValue = valueNormalizationService.normalize( possibleValue );
                this.possibleValues[ i ] = normalizedPossibleValue;
                contentForCombo[ i ] = valueLabelService.provide( normalizedPossibleValue );
                
                i++;
            }
        }
        
        setItems( contentForCombo );
    }
    
    @Override
    protected Object doGetValue()
    {
        if( this.isDefaultValue )
        {
            return null;
        }
        else
        {
            final int index = this.combo.getSelectionIndex();
            
            if( index == -1 )
            {
                final ValueNormalizationService valueNormalizationService = this.element.service( this.property, ValueNormalizationService.class );
                final String value = valueNormalizationService.normalize( this.combo.getText() );
                
                if( value.length() > 0 )
                {
                    return value;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return this.possibleValues[ index ];
            }
        }
    }

    @Override
    protected void doSetValue( final Object value )
    {
        final Value<?> val = (Value<?>) value;
        
        final ValueNormalizationService valueNormalizationService = this.element.service( this.property, ValueNormalizationService.class );
        
        final String text = valueNormalizationService.normalize( val.getText() );
        int index = -1;
        
        for( int i = 0; index == -1 && i < this.possibleValues.length; i++ )
        {
            if( this.possibleValues[ i ].equals( text ) )
            {
                index = i;
            }
        }
        
        super.doSetValue( index );
        
        if( val.getText( false ) == null && val.getDefaultContent() != null )
        {
            this.isDefaultValue = true;
        }
        else
        {
            this.isDefaultValue = false;
        }
    }
    
    @Override
    public void activate()
    {
        this.selectionPriorToActivation = this.viewer.getSelection();
        
        if( this.selectionProvider != null )
        {
            this.selectionProvider.setFakeSelection( this.selectionPriorToActivation );
        }
        
        this.viewer.setSelection( StructuredSelection.EMPTY );
        
        super.activate();
    }

    @Override
    protected void focusLost()
    {
        if( ! this.disableFocusLostHandler )
        {
            this.disableFocusLostHandler = true;
            super.focusLost();
            this.disableFocusLostHandler = false;
            
            this.viewer.setSelection( this.selectionPriorToActivation );

            if( this.selectionProvider != null )
            {
                this.selectionProvider.setFakeSelection( null );
            }
            
            this.selectionPriorToActivation = null;
        }
    }
    
}

