/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Turn on anti-aliasing on diagram connection layer;
 *                   Move DiagramXYLayoutEditPolicy inner class to its own file.
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramXYLayoutEditPolicy;
import org.eclipse.swt.SWT;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorPageEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	@Override
	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new MarginBorder(3));
		f.setLayoutManager(new FreeformLayout());

		// Create the static router for the connection layer
		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(f));
		connLayer.setAntialias(SWT.ON);

		return f;
	}

	@Override
	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());

		// handles constraint changes (e.g. moving and/or resizing) of model
		// elements and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy(getCastedModel()));
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

	private DiagramModel getCastedModel() {
		return (DiagramModel)getModel();
	}
	
	@Override
	protected List<DiagramNodeModel> getModelChildren() {
		return getCastedModel().getNodes();
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (DiagramModel.NODE_ADDED.equals(prop)) {
			refreshChildren();
		} else if (DiagramModel.NODE_REMOVED.equals(prop)) {
			refreshChildren();
		} 
	}
}
