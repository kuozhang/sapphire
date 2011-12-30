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

package org.eclipse.sapphire.ui.gef.diagram.editor.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.gef.diagram.editor.DiagramRenderingContext;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DndObjectCommand extends Command 
{
	private static final String SAPPHIRE_DROP_ACTION = "Sapphire.Drop";
	private DiagramModel diagramModel;
	private SapphireDiagramActionHandler dropHandler;
	private List<Object> droppedObjs;
	private Point location;
	
	public DndObjectCommand(DiagramModel diagramModel, ISelection sel, Point location)
	{
		this.diagramModel = diagramModel;
		this.location = location;
		
		droppedObjs = new ArrayList<Object>();
		
		IStructuredSelection s = (IStructuredSelection) sel;
		if (s == null) 
		{
			s = StructuredSelection.EMPTY;
		}

		for (Iterator<?> iter = s.iterator(); iter.hasNext();) 
		{
			Object next = iter.next();
			droppedObjs.add(next);
		}
	}
	
	@Override
	public boolean canExecute() 
	{
		SapphireAction dropAction = this.diagramModel.getSapphirePart().getAction(SAPPHIRE_DROP_ACTION);
		this.dropHandler = (SapphireDiagramActionHandler)dropAction.getFirstActiveHandler();
		for (Object droppedObj : this.droppedObjs)
		{
			if (dropHandler != null && dropHandler.canExecute(droppedObj))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void execute() 
	{
		int currX = this.location.x;
		int currY = this.location.y;
		for (Object droppedObj : this.droppedObjs)
		{
			DiagramRenderingContext diagramCtx = new DiagramRenderingContext(this.diagramModel.getSapphirePart());
			diagramCtx.setCurrentMouseLocation(currX, currY);
			currX += 20;
			currY += 20;
			diagramCtx.setObject(droppedObj);
			dropHandler.execute(diagramCtx);			
		}
	}
}
