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

package org.eclipse.sapphire.ui.swt.gef.actions;

import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.swt.gef.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.gef.layout.HorizontalGraphLayout;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class HorizontalGraphLayoutActionHandler extends SapphireDiagramActionHandler 
{
	@Override
	public boolean canExecute(Object obj) 
	{
		return true;
	}

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramRenderingContext diagramCtx = (DiagramRenderingContext)context;
		SapphireDiagramEditor diagramEditor = diagramCtx.getDiagramEditor();
		new HorizontalGraphLayout().layout(diagramEditor);
		
		return null;
	}
	
}
