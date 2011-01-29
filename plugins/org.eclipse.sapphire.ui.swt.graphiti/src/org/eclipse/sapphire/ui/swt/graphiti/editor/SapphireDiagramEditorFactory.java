/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
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
		throws CoreException
	{
		if (file == null)
			return null;
		
		final IProject project = file.getProject();
		
		final IFolder diagramFolder = project.getFolder(".settings/diagrams/");
		String fileName = file.getName();
		if (fileName.endsWith(".xml"))
		{
			fileName = fileName.substring(0, fileName.indexOf(".xml"));
		}
		
		TransactionalEditingDomain domain = null;
		ResourceSet resourceSet = null;
		Diagram diagram = null;
		URI diagramFileUri = null;
		
		// create diagram file if it doesn't exist
		final IFile diagramFile = diagramFolder.getFile(fileName + ".xmi");
		if (!diagramFile.exists())
		{
			FileUtil.mkdirs( diagramFile.getParent().getLocation().toFile() );
			diagramFile.create( new ByteArrayInputStream(new byte[0]), true, null );
			
			// Create Diagram Obj
			diagram = Graphiti.getPeCreateService().createDiagram(
					SAPPHIRE_DIAGRAM_TYPE, fileName, 10, false);
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
		// create diagram node position file if it doesn't exist
		IFile nodePosFile = diagramFile.getParent().getFile(new Path(fileName + ".np"));
		if (!nodePosFile.exists())
		{
			nodePosFile.create( new ByteArrayInputStream(new byte[0]), true, null );
		}

		// create sapphire diagram editor input
		if (diagram != null)
		{
			String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
			final SapphireDiagramEditorInput diagramEditorInput = 
				SapphireDiagramEditorInput.createEditorInput(diagram, domain, providerId, false);
			diagramEditorInput.setNodePositionFile(nodePosFile);
			return diagramEditorInput;
		}
	
		return null;
	}

}
