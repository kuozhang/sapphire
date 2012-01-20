/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Moved it out of SapphireDiagramEditorPageEditPart class;
 *                   Support DND from project explorer.
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.CreateNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.DndObjectCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.MoveNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramNodeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy 
{
	private DiagramModel model;
	
	public DiagramXYLayoutEditPolicy(DiagramModel model)
	{
		this.model = model;
	}

	@Override
	protected Rectangle getCurrentConstraintFor(GraphicalEditPart child) {
		if (child instanceof DiagramNodeEditPart) {
			return super.getCurrentConstraintFor(child);
		}
		return null;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof DiagramNodeEditPart)
			return new DiagramNodeSelectionEditPolicy();
		return new NonResizableEditPolicy();
	}

	@Override
	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child, Object constraint) {
		if (child instanceof DiagramNodeEditPart && constraint instanceof Rectangle) {
			DiagramNodeModel node = ((DiagramNodeEditPart)child).getCastedModel();
			return new MoveNodeCommand(node, (Rectangle)constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// not used in this example
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command cmd = UnexecutableCommand.INSTANCE;
		if (request.getNewObjectType() == DiagramNodeTemplate.class) {
			DiagramNodeTemplate nodeTemplate = (DiagramNodeTemplate)request.getNewObject();
			cmd = new CreateNodeCommand(this.model, nodeTemplate, request.getLocation());
		}
		else if (request.getNewObjectType() == ISelection.class) {
			// DND from project explorer
			ISelection selection = (ISelection)request.getNewObject();
			DiagramModel diagramModel = (DiagramModel)getHost().getModel();
			cmd = new DndObjectCommand(diagramModel, selection, request.getLocation());
		}
		return cmd;
	}
	
}
