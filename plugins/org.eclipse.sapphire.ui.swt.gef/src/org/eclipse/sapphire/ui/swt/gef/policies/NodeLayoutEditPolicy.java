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

package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeBounds;
import org.eclipse.sapphire.ui.swt.gef.commands.MoveNodeCommand;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.parts.RectangleEditPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * This layout policy is needed for DiagramNodeEditPart for updating the edit part
 * for a request. It inherits "public EditPart getTargetEditPart(Request request)"
 * from LayoutEditPolicy parent class. It'll also be needed when we start to support
 * node containment.
 */

public class NodeLayoutEditPolicy extends XYLayoutEditPolicy 
{
	private DiagramNodeModel nodeModel;
	
	public NodeLayoutEditPolicy(DiagramNodeModel nodeModel)
	{
		this.nodeModel = nodeModel;		
	}
	
	@Override
	protected Command getCreateCommand(CreateRequest request) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) 
	{
		if (constraint instanceof Rectangle)
		{
			DiagramNodeBounds bounds = this.nodeModel.getModelPart().getNodeBounds();
			Rectangle rect = (Rectangle)constraint;
			bounds.setX(bounds.getX() + rect.x);
			bounds.setY(bounds.getY() + rect.y);
			return new MoveNodeCommand(this.nodeModel, new Rectangle(bounds.getX(), bounds.getY(), 
							bounds.getWidth(), bounds.getHeight()));
		}
		return null;
	}
	
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child)
	{
		if (child instanceof RectangleEditPart)
			return new RectangleSelectionEditPolicy();
		return new NonResizableEditPolicy();
	}
	
	@Override
	public EditPart getTargetEditPart(Request request)
	{
		if (REQ_CREATE.equals(request.getType()))
			return getHost();
		if (REQ_ADD.equals(request.getType()))
			return getHost();
		if (REQ_MOVE.equals(request.getType()))
		{
			return getHost();
		}
		return super.getTargetEditPart(request);
	}
	
}
