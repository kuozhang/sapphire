/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */
public final class CompactListBinding implements ModifyListener {
	
	private final CompactListPropertyEditorRenderer compactListPropertyEditorRenderer;
	private Text text;
	private IModelElement element;
	
	public CompactListBinding(CompactListPropertyEditorRenderer compactListPropertyEditorRenderer, Text text) {
		this.compactListPropertyEditorRenderer = compactListPropertyEditorRenderer;
		this.text = text;
	}
	
	public Text getText() {
		return this.text;
	}
	
	public void setModelElement(IModelElement element) {
		this.element = element;
	}
	
	public IModelElement getModelElement() {
		return this.element;
	}

	public void modifyText(ModifyEvent e) {
        if( ! this.text.isDisposed() && ( this.text.getStyle() & SWT.READ_ONLY ) == 0 ) 
        {
        	final String value = this.text.getText();
        	if (this.element != null || value.length() > 0) {
        		boolean createNew = false;
        		if (this.element == null) {
        			this.element = this.compactListPropertyEditorRenderer.getList().addNewElement();
        			createNew = true;
        		}
	            this.element.write( this.compactListPropertyEditorRenderer.memberProperty, value );
	            if (createNew) {
	            	this.text.setSelection(value.length(), value.length());
	            }
        	}
        }
	}
}