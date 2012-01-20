/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342098] Separate dependency on org.eclipse.core.runtime (part 1)
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.sapphire.modeling.StatusException;
import org.eclipse.sapphire.modeling.util.internal.FileUtil;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramEditorFactory 
{
    public static final String SAPPHIRE_DIAGRAM_TYPE = "sapphireDiagram";
    
    public static SapphireDiagramEditorInput createEditorInput(IFile file)
            throws StatusException, CoreException
    {
        return createEditorInput(file, null, false);
    }
    
    public static SapphireDiagramEditorInput createEditorInput(IFile file, String diagramPageId, boolean sideBySideLayoutFile) 
        throws StatusException, CoreException
    {
        if (file == null)
            return null;
        
        IProject project = file.getProject();
        IPath inputFilePath = file.getProjectRelativePath().removeLastSegments(1);        
        
        String fileName;
        String inputFileName = file.getName();
        if (inputFileName.endsWith(".xml"))
        {
            fileName = inputFileName.substring(0, inputFileName.indexOf(".xml"));
        }
        else
        {
            fileName = inputFileName;
        }
        if (diagramPageId != null)
        {
            fileName += "_" + diagramPageId;
        }
        
        // compute layout folder path
        
        IFolder diagramSettingRootFolder = project.getFolder(".settings/org.eclipse.sapphire.ui.diagram/");                    
        IPath diagramSettingFolderPath = diagramSettingRootFolder.getProjectRelativePath().append(inputFilePath);
        IFolder diagramSettingFolder = project.getFolder(diagramSettingFolderPath);
        if (!diagramSettingFolder.exists())
        {
            FileUtil.mkdirs(diagramSettingFolder.getLocation().toFile());
            diagramSettingFolder.refreshLocal(IResource.DEPTH_ONE, null);
        }
        
        IFolder layoutFolder;
        if (!sideBySideLayoutFile)
        {
            layoutFolder = diagramSettingFolder;
        }
        else
        {
            layoutFolder = (IFolder)file.getParent();
        }
        
        // create diagram layout file if it doesn't exist
        boolean existingLayout = true;
        IFile layoutFile = layoutFolder.getFile(fileName + ".layout");
        if (!layoutFile.exists())
        {
            layoutFile.create(new ByteArrayInputStream(new byte[0]), true, null);
            existingLayout = false;
        }
        
    	final Diagram diagram = Graphiti.getPeCreateService().createDiagram(SAPPHIRE_DIAGRAM_TYPE, fileName, 10, true);
    	// Create a virtual URI with a custom schema
    	URI uri = URI.createHierarchicalURI("Virtual",
    			null,
    			null,
    			new String[] { "diagram", fileName },
    			null,
    			null);

    	// Create a regular resource in the uri
    	TransactionalEditingDomain editingDomain = DiagramEditorFactory.createResourceSetAndEditingDomain();
    	final Resource diagramResource = editingDomain.getResourceSet().createResource(uri);				

    	// Add the diagram to the resource
    	editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) 
		{
			protected void doExecute() 
			{			    					
				diagramResource.getContents().add(diagram);
			}
		});
    	String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
        final SapphireDiagramEditorInput diagramEditorInput = 
            SapphireDiagramEditorInput.createEditorInput(diagram, editingDomain, providerId, false);
        diagramEditorInput.setLayoutFile(layoutFile);
        diagramEditorInput.setNoExistingLayout(!existingLayout);
        return diagramEditorInput;        	
    }
    
}