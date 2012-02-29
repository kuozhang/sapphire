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

package org.eclipse.sapphire.ui.diagram.layout.standard.internal;

import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.FileResourceStore;
import org.eclipse.sapphire.modeling.StatusException;
import org.eclipse.sapphire.modeling.util.internal.FileUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.layout.standard.StandardDiagramLayout;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.IEditorInput;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class WorkspaceDiagramLayoutPersistenceService extends
		LazyLoadLayoutPersistenceService 
{
	private static final String WORKSPACE_LAYOUT_FOLDER = ".metadata/.plugins/org.eclipse.sapphire.ui.diagram/layouts";
	
	public WorkspaceDiagramLayoutPersistenceService(IEditorInput editorInput, SapphireDiagramEditorPagePart diagramPart)
	{
		super(editorInput, diagramPart);
	}
	
	@Override
	protected StandardDiagramLayout initLayoutModel() 
	{
		StandardDiagramLayout layoutModel = null;
		try
		{
			String fileName = computeLayoutFileName(this.editorInput);
			if (fileName != null)
			{
				File layoutFile = getLayoutPersistenceFile(fileName);
				final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore(layoutFile));
				layoutModel = StandardDiagramLayout.TYPE.instantiate(new RootXmlResource( resourceStore ));			
			}
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
		}
		return layoutModel;
	}
	
	private File getLayoutPersistenceFile(String fileName) throws StatusException, CoreException
	{
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        File layoutFolder = workspaceRoot.getLocation().toFile();
        layoutFolder = new File(layoutFolder, WORKSPACE_LAYOUT_FOLDER);
        if (!layoutFolder.exists())
        {
        	FileUtil.mkdirs(layoutFolder);
        }
        File layoutFile = new File (layoutFolder, fileName);
        return layoutFile;
	}
	
}
