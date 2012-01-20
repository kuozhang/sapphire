/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Add constructor which takes only ISapphirePart
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramRenderingContext extends SapphireRenderingContext 
{
	private SapphireDiagramEditor diagramEditor;
	private Point currentMouseLocation = new Point(0, 0);
	private Object object;
	
	public DiagramRenderingContext(ISapphirePart part) {
		super(part, null);
	}

	public DiagramRenderingContext(ISapphirePart part, SapphireDiagramEditor diagramEditor) {
		super(part, null);
		this.diagramEditor = diagramEditor;
	}
	
	public SapphireDiagramEditor getDiagramEditor()
	{
		return this.diagramEditor;
	}
	
	public void setCurrentMouseLocation(int x, int y)
	{
		this.currentMouseLocation.setX(x);
		this.currentMouseLocation.setY(y);
	}
	
	public Point getCurrentMouseLocation()
	{
		return new Point(this.currentMouseLocation.getX(), 
				this.currentMouseLocation.getY());
	}
	
	public Object getObject()
	{
		return this.object;
	}
	
	public void setObject(Object object)
	{
		this.object = object;
	}
}
