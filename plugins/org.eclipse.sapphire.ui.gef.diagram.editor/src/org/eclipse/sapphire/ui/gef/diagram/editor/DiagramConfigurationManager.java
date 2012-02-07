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

package org.eclipse.sapphire.ui.gef.diagram.editor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConfigurationManager
{
	private SapphireDiagramEditor diagramEditor;
	private DiagramRenderingContextCache drcCache;
	
	public DiagramConfigurationManager(SapphireDiagramEditor diagramEditor)
	{
		this.diagramEditor = diagramEditor;
		this.drcCache = new DiagramRenderingContextCache();
	}
	
	public SapphireDiagramEditor getDiagramEditor()
	{
		return this.diagramEditor;
	}
	
	public DiagramRenderingContextCache getDiagramRenderingContextCache()
	{
		return this.drcCache;
	}
}
