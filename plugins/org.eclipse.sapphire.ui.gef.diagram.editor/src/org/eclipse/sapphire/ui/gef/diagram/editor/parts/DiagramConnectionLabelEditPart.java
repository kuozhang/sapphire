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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionLabelModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.ConnectionLabelDirectEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionLabelEditPart extends AbstractGraphicalEditPart {
	
    private NodeDirectEditManager manager;

    @Override
	protected IFigure createFigure() {
    	return new Label();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ConnectionLabelDirectEditPolicy());
	}

	public DiagramConnectionPart getDiagramConnectionPart() {
		return ((DiagramConnectionLabelModel)getModel()).getModelPart();
	}
	
	private void performDirectEdit() {
		if (manager == null) {
			Label label = (Label)getFigure();
			manager = new NodeDirectEditManager(this, TextCellEditor.class, new NodeCellEditorLocator(label), label);
		}
		manager.show();
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

	@Override
	public void refresh() {
		super.refresh();
		
		((Label)getFigure()).setText(getDiagramConnectionPart().getLabel());

		PolylineConnection parent = (PolylineConnection)getFigure().getParent(); 
		Point position = getDiagramConnectionPart().getLabelPosition();
		SapphireMidpointLocator locator = position == null ? new SapphireMidpointLocator(parent) : new SapphireMidpointLocator(parent, position.getX(), position.getY());
		parent.getLayoutManager().setConstraint(getFigure(), locator);
	}

}
