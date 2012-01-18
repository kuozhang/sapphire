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

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.handles.BendpointHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.BendpointCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.CreateBendpointCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.DeleteBendpointCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.MoveBendpointCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionBendpointEditPolicy extends org.eclipse.gef.editpolicies.BendpointEditPolicy {

	@SuppressWarnings("rawtypes")
	private static final List NULL_CONSTRAINT = new ArrayList();

	private Map<Shape, Integer> shapeToLineStyle = new HashMap<Shape, Integer>();

	protected Command getCreateBendpointCommand(BendpointRequest request) {
		CreateBendpointCommand com = new CreateBendpointCommand();
		Point p = request.getLocation();
		Connection conn = getConnection();

		conn.translateToRelative(p);

		com.setLocation(p);
		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

		conn.translateToRelative(ref1);
		conn.translateToRelative(ref2);

		com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
		com.setDiagramConnectionModel((DiagramConnectionModel) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	protected Command getMoveBendpointCommand(BendpointRequest request) {
		MoveBendpointCommand com = new MoveBendpointCommand();
		Point p = request.getLocation();
		Connection conn = getConnection();

		conn.translateToRelative(p);

		com.setLocation(p);

		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

		conn.translateToRelative(ref1);
		conn.translateToRelative(ref2);

		com.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
		com.setDiagramConnectionModel((DiagramConnectionModel) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		BendpointCommand com = new DeleteBendpointCommand();
		Point p = request.getLocation();
		com.setLocation(p);
		com.setDiagramConnectionModel((DiagramConnectionModel) request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List createSelectionHandles() {
		List<BendpointHandle> list = new ArrayList<BendpointHandle>();
		boolean automaticallyBending = isAutomaticallyBending();
		if (automaticallyBending) {
			list = createHandlesForAutomaticBendpoints();
		} else {
			list = createHandlesForUserBendpoints();
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List createHandlesForAutomaticBendpoints() {
		List list = new ArrayList();
		ConnectionEditPart connEP = (ConnectionEditPart) getHost();
		PointList points = getConnection().getPoints();
		for (int i = 0; i < points.size() - 1; i++)
			list.add(new DiagramBendpointCreationHandle(connEP, 0, i));

		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List createHandlesForUserBendpoints() {
		List list = new ArrayList();
		ConnectionEditPart connEP = (ConnectionEditPart) getHost();
		PointList points = getConnection().getPoints();
		List bendPoints = (List) getConnection().getRoutingConstraint();
		int bendPointIndex = 0;
		Point currBendPoint = null;

		if (bendPoints == null)
			bendPoints = NULL_CONSTRAINT;
		else if (!bendPoints.isEmpty())
			currBendPoint = ((Bendpoint) bendPoints.get(0)).getLocation();

		for (int i = 0; i < points.size() - 1; i++) {
			// Put a create handle on the middle of every segment
			list.add(new DiagramBendpointCreationHandle(connEP, bendPointIndex, i));

			// If the current user bendpoint matches a bend location, show a
			// move handle
			if (i < points.size() - 1 && bendPointIndex < bendPoints.size()
					&& currBendPoint.equals(points.getPoint(i + 1))) {
				list.add(new DiagramBendpointMoveHandle(connEP, bendPointIndex, i + 1));

				// Go to the next user bendpoint
				bendPointIndex++;
				if (bendPointIndex < bendPoints.size())
					currBendPoint = ((Bendpoint) bendPoints.get(bendPointIndex)).getLocation();
			}
		}

		return list;
	}

	@SuppressWarnings("rawtypes")
	private boolean isAutomaticallyBending() {
		List constraint = (List) getConnection().getRoutingConstraint();
		PointList points = getConnection().getPoints();
		return ((points.size() > 2) && (constraint == null || constraint.isEmpty()));
	}
	
	@Override
	protected void addSelectionHandles() {
		super.addSelectionHandles();

		showHighlight();
	}

	@Override
	protected void removeSelectionHandles() {
		super.removeSelectionHandles();

		removeHighlight();
	}

	protected void showHighlight() {
		// remove previous highlight
		removeHighlight();

		// determine new highlight-values
		int newLineStyle = Graphics.LINE_DASH;

		// store old highlight-values and set new highlight-values
		shapeToLineStyle.put(getConnectionFigure(), getConnectionFigure().getLineStyle());
		getConnectionFigure().setLineStyle(newLineStyle);
	}

	protected void removeHighlight() {
		Set<Shape> lineStyleShapes = shapeToLineStyle.keySet();
		for (Shape lineStyleShape : lineStyleShapes) {
			int lineStyle = shapeToLineStyle.get(lineStyleShape);
			lineStyleShape.setLineStyle(lineStyle);
		}

		shapeToLineStyle.clear();
	}
	
	private Shape getConnectionFigure() {
		return (Shape) ((GraphicalEditPart) getHost()).getFigure();
	}
}
