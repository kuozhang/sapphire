/******************************************************************************
 * Copyright (c) 2011 Oracle
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
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */
public final class CompactTextBinding implements ModifyListener {
	
	private final CompactListPropertyEditorRenderer compactListPropertyEditorRenderer;
	private Text text;
	private CompactListProxyResource resource;
	private ToolBar toolbar;
	private CompactListPropertyEditorRenderer.DeleteActionHandler deleteActionHandler;
	private PropertyEditorAssistDecorator decorator;
	private boolean modifying = false;
	
	public CompactTextBinding(CompactListPropertyEditorRenderer compactListPropertyEditorRenderer, Text text, CompactListProxyResource resource) {
		this.compactListPropertyEditorRenderer = compactListPropertyEditorRenderer;
		this.text = text;
		this.text.addModifyListener(this);

		this.resource = resource;
	}
	
	public void removeListener() {
		this.text.removeModifyListener(this);
	}
	
	public Text getText() {
		return this.text;
	}
	
	public CompactListProxyResource getResource() {
		return this.resource;
	}
	
	public void refreshModelElement(IModelElement element) {
		refreshModelElement(element, true);
	}
	
	private void refreshModelElement(IModelElement element, boolean writeText) {
		this.resource.setModelElement(element);
    	String value = element != null ? element.read(this.resource.getValueProperty()).getText() : null;
    	value = value == null ? "" : value;
		if (writeText && !CompactListPropertyEditorRenderer.equals(value, this.text.getText())) {
			this.text.setText(value);
    	}
	}
	
//	public void setModelElement(IModelElement element) {
//		this.element = element;
//		
//		this.resource.setElement(element);
//	}
//	
	public IModelElement getModelElement() {
		return this.resource.getModelElement();
	}
	
	public boolean isModifying() {
		return this.modifying;
	}
	
	public void setModifying(boolean modifying) {
		this.modifying = modifying;
	}

	public ToolBar getToolbar() {
		return this.toolbar;
	}

	public void setToolbar(ToolBar toolbar) {
		this.toolbar = toolbar;
	}

	public CompactListPropertyEditorRenderer.DeleteActionHandler getDeleteActionHandler() {
		return this.deleteActionHandler;
	}

	public void setDeleteActionHandler(
			CompactListPropertyEditorRenderer.DeleteActionHandler deleteActionHandler) {
		this.deleteActionHandler = deleteActionHandler;
	}

	public PropertyEditorAssistDecorator getDecorator() {
		return this.decorator;
	}

	public void setDecorator(PropertyEditorAssistDecorator decorator) {
		this.decorator = decorator;
	}
	
//	public boolean hasModelElement() {
//		return this.resource.getModelElement() != null;
//	}

	public void modifyText(ModifyEvent e) {
        if( ! this.text.isDisposed() && ( this.text.getStyle() & SWT.READ_ONLY ) == 0 ) 
        {
        	IModelElement element = this.resource.getModelElement();
        	final String value = this.text.getText();
        	if (value.length() == 0 && e.getSource().equals(this.text)) {
        		if (element != null) {
                	this.modifying = true;

                	System.out.println("TODO remove " + value);
                	this.compactListPropertyEditorRenderer.getList().remove(element);
        			refreshModelElement(null, false);
    	            
        			this.modifying = false;
        		}
        	} else if (element != null || value.length() > 0) {
            	this.modifying = true;
        		
            	boolean createNew = false;
        		if (element == null) {
        			// TODO new element may not be the last one
        			final IModelElement newElement = this.compactListPropertyEditorRenderer.getList().addNewElement();
        			refreshModelElement(newElement, false);
        			createNew = true;
        		}
        		this.resource.write(value);
	            if (createNew) {
	            	this.text.setSelection(value.length(), value.length());
	            }

	            this.modifying = false;
        	}
        }
	}
}