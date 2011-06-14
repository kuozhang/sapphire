/******************************************************************************
 * Copyright (c) 2011 Oracle
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
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorFactory;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.sapphire.modeling.StatusException;
import org.eclipse.sapphire.modeling.util.internal.FileUtil;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.graphiti.GraphitiFileService;

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
        IFile layoutFile = layoutFolder.getFile(fileName + ".layout");
        if (!layoutFile.exists())
        {
            layoutFile.create(new ByteArrayInputStream(new byte[0]), true, null);
        }
        
        // We don't need to persist Graphiti diagram. But due to limitations on Graphiti's
        // diagram model, we still need to pass a file when creating Graphiti Diagram obj.
        // See http://www.eclipse.org/forums/index.php/t/202467/
        
        TransactionalEditingDomain domain = null;
        ResourceSet resourceSet = null;
        Diagram diagram = null;
        URI diagramFileUri = null;

        String diagramFileName = fileName + ".xmi";
        IFile diagramFile = diagramSettingFolder.getFile(diagramFileName);

        if (!diagramFile.exists())
        {
            diagramFile.create(new ByteArrayInputStream(new byte[0]), true, null);            
            
            // Create Diagram Obj
            diagram = Graphiti.getPeCreateService().createDiagram(
                    SAPPHIRE_DIAGRAM_TYPE, fileName, 10, true);
            diagramFileUri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
            domain = GraphitiFileService.createEmfFileForDiagram(diagramFileUri, diagram);
        }
        else
        {            
            domain = DiagramEditorFactory.createResourceSetAndEditingDomain();
            resourceSet = domain.getResourceSet();
        
            diagramFileUri = GraphitiUiInternal.getEmfService().getFileURI(diagramFile, resourceSet);
            if (diagramFileUri != null)
            {
                final Resource resource = resourceSet.createResource(diagramFileUri);
                try
                {
                    resource.load(null);
                }
                catch (IOException ie)
                {
                    SapphireUiFrameworkPlugin.log(ie);
                }
                EList<EObject> objs = resource.getContents();
                Iterator<EObject> it = objs.iterator();                    
                while (it.hasNext()) 
                {
                    EObject obj = it.next();
                    if ((obj == null) && !Diagram.class.isInstance(obj))
                        continue;
                    diagram = (Diagram)obj;
                    break;
                }
            }                
        }

        // create sapphire diagram editor input
        if (diagram != null)
        {
            String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
            final SapphireDiagramEditorInput diagramEditorInput = 
                SapphireDiagramEditorInput.createEditorInput(diagram, domain, providerId, false);
            diagramEditorInput.setLayoutFile(layoutFile);
            return diagramEditorInput;
        }
        else
        {
            return null;
        }
    }

}
