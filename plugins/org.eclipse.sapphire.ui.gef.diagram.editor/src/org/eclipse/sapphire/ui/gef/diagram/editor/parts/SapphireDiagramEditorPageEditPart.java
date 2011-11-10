/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.CreateNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.MoveNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramNodeSelectionEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorPageEditPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());

		// Create the static router for the connection layer
		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));

		return f;
	}

	@Override
	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());

		// handles constraint changes (e.g. moving and/or resizing) of model
		// elements and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
	}
	
	private SapphireDiagramEditorPagePart getModelPart() {
		return (SapphireDiagramEditorPagePart)getModel();
	}

	@Override
	protected List<DiagramNodePart> getModelChildren() {
		List<DiagramNodePart> list = new ArrayList<DiagramNodePart>();
		for (DiagramNodeTemplate nodeTemplate : getModelPart().getNodeTemplates()) {
			if (getModelPart().isNodeTemplateVisible(nodeTemplate)) {
				for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes()) {
					System.out.println(nodePart.getLabel());
					list.add(nodePart);
				}
			}
		}
		return list;
	}
	
	private DiagramNodeTemplate getDiagramNodeTemplate(ModelElementType type) {
		for (DiagramNodeTemplate nodeTemplate : getModelPart().getNodeTemplates()) {
			if (getModelPart().isNodeTemplateVisible(nodeTemplate)) {
				// TODO check for type
				return nodeTemplate;
			}
		}
		return null;
	}
	
	public void handleNodeRemoved(DiagramNodePart part) {
		removeChild(getEditPartForChild(part));
	}

	private EditPart getEditPartForChild(Object child) {
		return (EditPart) getViewer().getEditPartRegistry().get(child);
	}

	/**
	 * EditPolicy for the Figure used by this edit part. Children of
	 * XYLayoutEditPolicy can be used in Figures with XYLayout.
	 */
	private class DiagramXYLayoutEditPolicy extends XYLayoutEditPolicy {

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
				DiagramNodePart part = (DiagramNodePart) ((DiagramNodeEditPart)child).getModel();
				return new MoveNodeCommand(part, (Rectangle)constraint);
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
			ModelElementType type = (ModelElementType)request.getNewObjectType();
			DiagramNodeTemplate template = getDiagramNodeTemplate(type);
			return new CreateNodeCommand(template, request.getLocation());
		}

	}
}
