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

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.ConnectionLabelDirectEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionLabelEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
	
    private ConnectionDirectEditManager manager;

    @Override
	protected IFigure createFigure() {
    	Label label = new Label();
		
    	DiagramResourceCache resourceCache = getCastedModel().getDiagramModel().getResourceCache();
		DiagramConnectionPart connectionPart = getCastedModel().getModelPart();
		label.setForegroundColor(resourceCache.getLineColor(connectionPart));

		return label;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ConnectionLabelDirectEditPolicy());
	}

	public DiagramConnectionPart getDiagramConnectionPart() {
		return ((DiagramConnectionLabelModel)getModel()).getModelPart();
	}
	
	private DiagramConnectionLabelModel getCastedModel() {
		return (DiagramConnectionLabelModel)getModel();
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getCastedModel().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getCastedModel().removePropertyChangeListener(this);
		}
	}

	private void performDirectEdit() {
		if (manager == null) {
			Label label = (Label)getFigure();
			manager = new ConnectionDirectEditManager(this, TextCellEditor.class, new ConnectionEditorLocator(label), label);
		}
		manager.show();
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}
	
	private void refreshLabel() {
		((Label)getFigure()).setText(getDiagramConnectionPart().getLabel());
	}

	@Override
	public void refresh() {
		super.refresh();

		refreshLabel();

		PolylineConnection parent = (PolylineConnection)getFigure().getParent(); 
		Point position = getDiagramConnectionPart().getLabelPosition();
		SapphireMidpointLocator locator = position == null ? new SapphireMidpointLocator(parent) : new SapphireMidpointLocator(parent, position.getX(), position.getY());
		parent.getLayoutManager().setConstraint(getFigure(), locator);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (DiagramConnectionLabelModel.CONNECTION_LABEL.equals(prop)) {
			refreshLabel();
		} else if (DiagramConnectionLabelModel.CONNECTION_START_EDITING.equals(prop)) {
			performDirectEdit();
		}
	}

}
