/******************************************************************************
 * Copyright (c) 2012 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - [365019] SapphireDiagramEditor does not work on non-workspace files 
 *    Gregory Amerson - [371576] Support non-local files in SapphireDiagramEditor
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.LayoutStorage;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.standard.internal.ProjectDiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.standard.internal.SideBySideLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.standard.internal.WorkspaceDiagramLayoutPersistenceService;
import org.eclipse.ui.IEditorInput;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorFactory 
{
    public static final String SAPPHIRE_DIAGRAM_TYPE = "sapphireDiagram";    
    
    public static DiagramLayoutPersistenceService getLayoutPersistenceService(SapphireDiagramEditorPagePart diagramPart)
    {
    	IModelElement rootElement = diagramPart.getLocalModelElement();
    	IEditorInput input = rootElement.adapt(IEditorInput.class);
    	IDiagramEditorPageDef pageDef = diagramPart.getPageDef();
    	LayoutStorage layoutStorage = pageDef.getLayoutStorage().getContent();
    	DiagramLayoutPersistenceService layoutPersistentService = null;
    	
    	if (layoutStorage == LayoutStorage.WORKSPACE)
    	{
    		layoutPersistentService = new WorkspaceDiagramLayoutPersistenceService(input, diagramPart);
    	}
    	else if (layoutStorage == LayoutStorage.PROJECT)
    	{
    		layoutPersistentService = new ProjectDiagramLayoutPersistenceService(input, diagramPart);
    	}
    	else if (layoutStorage == LayoutStorage.SIDE_BY_SIDE)
    	{
    		layoutPersistentService = new SideBySideLayoutPersistenceService(input, diagramPart);
    	}
    	else if (layoutStorage == LayoutStorage.CUSTOM)
    	{
    		layoutPersistentService = rootElement.service(DiagramLayoutPersistenceService.class);
    	}
        return layoutPersistentService;     	
    }
        
}
