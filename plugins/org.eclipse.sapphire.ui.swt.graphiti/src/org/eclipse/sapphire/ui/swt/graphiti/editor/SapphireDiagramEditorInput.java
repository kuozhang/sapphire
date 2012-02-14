/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramEditorInput extends DiagramEditorInput 
{
    private Diagram diagram;
    private IFile layoutFile;
    private boolean noExistingLayout;
    
    public SapphireDiagramEditorInput(Diagram diagram, String diagramUriString,
            TransactionalEditingDomain domain, String providerId,
            boolean disposeEditingDomain) 
    {
        super(diagramUriString, domain, providerId, disposeEditingDomain);
        this.diagram = diagram;
    }

    public SapphireDiagramEditorInput(Diagram diagram, URI diagramUri,
            TransactionalEditingDomain domain, String providerId,
            boolean disposeEditingDomain) 
    {
        super(diagramUri, domain, providerId, disposeEditingDomain);
        this.diagram = diagram;
    }
    
    public Diagram getDiagram() 
    {
        return this.diagram;
    }    
    
    public TransactionalEditingDomain getEditingDomain() 
    {
        return this.editingDomain;
    }
    
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) 
    {
        if (EObject.class.isAssignableFrom(adapter)) 
        {
            return getDiagram();
        } 
        else if (Diagram.class.isAssignableFrom(adapter)) 
        {
            return getDiagram();
        }
        else if (TransactionalEditingDomain.class.isAssignableFrom(adapter)) 
        {
            return getEditingDomain();
        } 
        else if (ResourceSet.class.isAssignableFrom(adapter)) 
        {
            return getEditingDomain().getResourceSet();
        }        
        return null;
    }
    
    public IFile getLayoutFile()
    {
        return this.layoutFile;
    }
    
    public void setLayoutFile(IFile file)
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
    
    public static SapphireDiagramEditorInput createEditorInput(Diagram diagram, 
            TransactionalEditingDomain domain, String providerId, boolean disposeEditingDomain) 
    {
        final Resource resource = diagram.eResource();
        if (resource == null) {
            throw new IllegalArgumentException();
        }
        final String fragment = resource.getURIFragment(diagram);
        final URI fragmentUri = resource.getURI().appendFragment(fragment);
        SapphireDiagramEditorInput diagramEditorInput;
        diagramEditorInput = new SapphireDiagramEditorInput(diagram, fragmentUri,
                domain, providerId, disposeEditingDomain);
        return diagramEditorInput;
    }    
    
}
