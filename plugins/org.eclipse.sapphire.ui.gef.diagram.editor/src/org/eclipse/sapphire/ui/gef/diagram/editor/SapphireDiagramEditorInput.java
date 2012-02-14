/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorInput implements IEditorInput, IPersistableElement
{
    private File layoutFile;
    private String providerId;
    private boolean noExistingLayout;

    public SapphireDiagramEditorInput(String providerId) 
    {
    	this.providerId = providerId;
    }
    
    public File getLayoutFile()
    {
        return this.layoutFile;
    }
    
    public void setLayoutFile(File file)
    {
        this.layoutFile = file;
    }
        
    public boolean noExistingLayout()
    {
    	return this.noExistingLayout;
    }
    
    public void setNoExistingLayout(boolean value)
    {
    	this.noExistingLayout = value;
    }

	public static SapphireDiagramEditorInput createEditorInput(String providerId) 
    {
        SapphireDiagramEditorInput diagramEditorInput;
        diagramEditorInput = new SapphireDiagramEditorInput(providerId);
        return diagramEditorInput;
    }

    @SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void saveState(IMemento memento) {
	}

	public String getFactoryId() {
		return getClass().getName();
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return getProviderId();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getProviderId() {
		return this.providerId;
	}

	public String getToolTipText() {
		return getProviderId();
	}    
    
}
