/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.internal;

import java.util.Set;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ValueNormalizationService;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramComboBoxCellEditor extends ComboBoxCellEditor 
{
	private Property property;
	private CCombo combo;
	private TextPart textPart;
	
	public DiagramComboBoxCellEditor(TextPart textPart, Composite parent, Property property)
	{
		this.textPart = textPart;
		this.property = property;
		create(parent);
		this.combo = (CCombo)getControl();
		PossibleValuesService possibleValuesService = this.property.service(PossibleValuesService.class);
		Set<String> possibleValues = possibleValuesService.values();
		final String[] contentForCombo = new String[possibleValues.size()];
		possibleValues.toArray(contentForCombo);
		setItems(contentForCombo);
	}
	
    @Override
    protected Object doGetValue()
    {
        final int index = this.combo.getSelectionIndex();
        
        if( index == -1 )
        {
            final ValueNormalizationService valueNormalizationService = this.property.service( ValueNormalizationService.class );
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
            return this.combo.getItem(index);
        }
    }
    
    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected Control createControl(Composite parent) 
    {
    	CCombo comboBox = (CCombo)super.createControl(parent);
    	comboBox.addTraverseListener(new TraverseListener()
    	{
    		@Override
    		public void keyTraversed(TraverseEvent e) 
    		{
    	        if (e.detail == SWT.TRAVERSE_TAB_NEXT) 
    	        {
    	        	TraverseUtil.gotoNextTextPart(textPart);
    	        }
    		}
    		
    	});

    	return comboBox;
    }
    
	
}
