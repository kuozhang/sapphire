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
import org.eclipse.sapphire.ui.swt.renderer.SapphireKeyboardActionPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */
final class CompactTextBinding implements ModifyListener {
	
	private final CompactListPropertyEditorRenderer compactListPropertyEditorRenderer;
	private Text text;
	private CompactListProxyResource resource;
	private ToolBar toolbar;
	private CompactListPropertyEditorRenderer.DeleteActionHandler deleteActionHandler;
	private PropertyEditorAssistDecorator decorator;
	private SapphireKeyboardActionPresentation actionPresentationKeyboard;
	private boolean modifying = false;
	
	public CompactTextBinding(CompactListPropertyEditorRenderer compactListPropertyEditorRenderer, Text text, CompactListProxyResource resource) {
		this.compactListPropertyEditorRenderer = compactListPropertyEditorRenderer;
		this.text = text;
		this.text.addModifyListener(this);

		this.resource = resource;
	}
	
	public void removeListener() {
		this.text.removeModifyListener(this);
		this.actionPresentationKeyboard.dispose();
	}
	
	public Text getText() {
		return this.text;
	}
	
	public CompactListProxyResource getResource() {
		return this.resource;
	}
	
	public void refreshModelElement(IModelElement element) {
		setModelElement(element);
    	String value = element != null ? element.read(this.resource.getValueProperty()).getText() : null;
    	value = value == null ? "" : value;
		if (!CompactListPropertyEditorRenderer.equals(value, this.text.getText())) {
			this.text.setText(value);
    	}
	}
	
	public void setModelElement(IModelElement element) {
		this.resource.setModelElement(element);
	}
	
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

	public SapphireKeyboardActionPresentation getActionPresentationKeyboard() {
		return this.actionPresentationKeyboard;
	}

	public void setActionPresentationKeyboard(SapphireKeyboardActionPresentation actionPresentationKeyboard) {
		this.actionPresentationKeyboard = actionPresentationKeyboard;
		this.actionPresentationKeyboard.attach(this.text);
		this.actionPresentationKeyboard.render();
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
	
	public void modifyText(ModifyEvent e) {
        if( ! this.text.isDisposed() && ( this.text.getStyle() & SWT.READ_ONLY ) == 0 ) 
        {
        	IModelElement element = this.resource.getModelElement();
        	final String value = this.text.getText();
        	if (value.length() == 0 && e.getSource().equals(this.text) && element == null) {
        		// do nothing..
        	} else {
            	this.modifying = true;
        		
            	boolean createNew = false;
        		if (element == null) {
        			// new element may not be the last one - insert empty strings 
        			this.compactListPropertyEditorRenderer.insertEmpty(this);
        			
        			final IModelElement newElement = this.compactListPropertyEditorRenderer.getList().addNewElement();
        			setModelElement(newElement);
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