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

package org.eclipse.sapphire.ui.swt.gef.actions;

import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.swt.gef.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramSelectAllNodesActionHandler extends
		SapphireDiagramActionHandler 
{

	@Override
	public boolean canExecute(Object obj) 
	{
		return true;
	}

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramRenderingContext diagramContext = (DiagramRenderingContext)context;
		SapphireDiagramEditor diagramEditor = diagramContext.getDiagramEditor();
		if (diagramEditor != null)
		{
			diagramEditor.selectAllNodes();
		}
		return null;
	}

}
