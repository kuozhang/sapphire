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

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionLabel;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramConnectionBendpointEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramConnectionEndpointEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionEditPart extends AbstractConnectionEditPart {

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new DiagramConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new DiagramConnectionBendpointEditPolicy());

		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramConnectionLayoutEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setTargetDecoration(new PolygonDecoration());
		updateStyle(connection);
		
		return connection;
	}
	
	private DiagramConnectionPart getModelPart() {
		return (DiagramConnectionPart)getModel();
	}
	
	public void updateStyle(PolylineConnection connection) {
		IDiagramConnectionDef def = getModelPart().getConnectionDef();
		connection.setLineStyle(SapphireDiagramEditorUtil.getLinkStyle(def));
		connection.setLineWidth(def.getLineWidth().getContent());
		connection.setForegroundColor(SapphireDiagramEditorUtil.getLineColor(getModelPart()));
	}
	
	private void refreshBendpoints() {
		List<AbsoluteBendpoint> figureConstraint = new ArrayList<AbsoluteBendpoint>();
		for (Point point : getModelPart().getConnectionBendpoints()) {
			AbsoluteBendpoint bendpoint = new AbsoluteBendpoint(point.getX(), point.getY());
			figureConstraint.add(bendpoint);
		}
		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}
	
	@Override
	protected void refreshVisuals() {
		refreshBendpoints();
	}

	@Override
	protected List getModelChildren() {
		// add the label
		List list = new ArrayList(1);
		list.add(new DiagramConnectionLabel(getModelPart()));
		return list;
	}

	
	private class DiagramConnectionLayoutEditPolicy extends LayoutEditPolicy {

		@Override
		protected EditPolicy createChildEditPolicy(EditPart child) {
			return new NonResizableEditPolicy();
		}

		@Override
		protected Command getCreateCommand(CreateRequest request) {
			return null;
		}

		@Override
		protected Command getMoveChildrenCommand(Request request) {
			return null;
		}

	}
	
}
