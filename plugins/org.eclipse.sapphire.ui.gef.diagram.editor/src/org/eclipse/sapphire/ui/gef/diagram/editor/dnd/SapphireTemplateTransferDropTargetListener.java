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

package org.eclipse.sapphire.ui.gef.diagram.editor.dnd;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.swt.dnd.DND;


/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireTemplateTransferDropTargetListener extends
		TemplateTransferDropTargetListener 
{
	public SapphireTemplateTransferDropTargetListener(EditPartViewer viewer)
	{
		super(viewer);
	}

	@Override
	protected void handleDrop() 
	{
		updateTargetRequest();
		updateTargetEditPart();

		if (getTargetEditPart() != null) 
		{
			Command command = getCommand();
			if (command != null && command.canExecute())
				getViewer().getEditDomain().getCommandStack().execute(command);
			else
				getCurrentEvent().detail = DND.DROP_NONE;
		} 
		else
		{
			getCurrentEvent().detail = DND.DROP_NONE;
		}
		selectAddedObject();
	}

	private void selectAddedObject() 
	{
		Object model = getCreateRequest().getNewObject();
		if (model instanceof DiagramNodePart)
		{
			DiagramNodePart nodePart = (DiagramNodePart)model;
			EditPart parentPart = getTargetEditPart();
			DiagramModel diagramModel = (DiagramModel)parentPart.getModel();
			DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(nodePart);
			
			EditPartViewer viewer = getViewer();
			viewer.getControl().forceFocus();
			Object editpart = viewer.getEditPartRegistry().get(nodeModel);
			if (editpart instanceof EditPart) 
			{
				// Force a layout first.
				viewer.flush();
				viewer.select((EditPart) editpart);
			}
			
			diagramModel.handleDirectEditing(nodePart);
		}
	}
	
}
