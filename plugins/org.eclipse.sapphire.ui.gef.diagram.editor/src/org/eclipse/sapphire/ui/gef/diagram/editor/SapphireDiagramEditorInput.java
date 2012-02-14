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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorInput implements IEditorInput, IPersistableElement
{
	private IFile diagramFile;
    private IFile layoutFile;
    private boolean noExistingLayout;

    public SapphireDiagramEditorInput(IFile file, String providerId, boolean disposeEditingDomain) {
    	this.diagramFile = file;
    }
    
    public IFile getLayoutFile()
    {
        return this.layoutFile;
    }
    
    public void setLayoutFile(IFile file)
    {
        this.layoutFile = file;
    }
    
    public IFile getDiagramFile() 
    {
    	return this.diagramFile;
    }
    
    
    public boolean noExistingLayout()
    {
    	return this.noExistingLayout;
    }
    
    public void setNoExistingLayout(boolean value)
    {
    	this.noExistingLayout = value;
    }

    public static SapphireDiagramEditorInput createEditorInput(IFile file, String providerId, boolean disposeEditingDomain) 
    {
//        final Resource resource = diagram.eResource();
//        if (resource == null) {
//            throw new IllegalArgumentException();
//        }
//        final String fragment = resource.getURIFragment(diagram);
//        final URI fragmentUri = resource.getURI().appendFragment(fragment);
        SapphireDiagramEditorInput diagramEditorInput;
        diagramEditorInput = new SapphireDiagramEditorInput(file, providerId, disposeEditingDomain);
        return diagramEditorInput;
    }

    @SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveState(IMemento memento) {
		// TODO Auto-generated method stub
		
	}

	public String getFactoryId() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "SapphireDiagramEditorInput-name";
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		// TODO Auto-generated method stub
		return "SapphireDiagramEditorInput-toolTipText";
	}    
    
}
