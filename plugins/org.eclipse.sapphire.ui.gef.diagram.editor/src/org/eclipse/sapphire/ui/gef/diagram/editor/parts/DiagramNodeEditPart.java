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

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramNodeEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.NodeEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.NodeLabelDirectEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart {

    public static final int DEFAULT_NODE_WIDTH = 100;
    public static final int DEFAULT_NODE_HEIGHT = 30;
    
    private NodeDirectEditManager manager;
    
    private ConnectionAnchor sourceAnchor;
    private ConnectionAnchor targetAnchor;

    @Override
	protected IFigure createFigure() {
		return new NodeFigure(getModelPart().getLabel());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DiagramNodeEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NodeLabelDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	private void performDirectEdit() {
		if (manager == null) {
			Label label = getNodeFigure().getLabelFigure();
			manager = new NodeDirectEditManager(this, TextCellEditor.class, new NodeCellEditorLocator(label), label);
		}
		manager.show();
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

	@Override
	protected List<DiagramConnectionPart> getModelSourceConnections() {
		List<DiagramConnectionPart> list = new ArrayList<DiagramConnectionPart>();
		SapphireDiagramEditorPagePart diagramPart = getModelPart().nearest(SapphireDiagramEditorPagePart.class);
		for (DiagramConnectionTemplate connTemplate : diagramPart.getConnectionTemplates()) {
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null)) {
				IModelElement endpoint1 = connPart.getEndpoint1();
				if (endpoint1 == getModelPart().getModelElement()) {
					list.add(connPart);
				}
			}
		}
		// Add embedded connections. This needs to be done after all the nodes have been added.
		for (DiagramNodeTemplate nodeTemplate : diagramPart.getNodeTemplates()) {
			DiagramEmbeddedConnectionTemplate embeddedConnTemplate = nodeTemplate.getEmbeddedConnectionTemplate();
			if (embeddedConnTemplate != null) {
				for (DiagramConnectionPart connPart : embeddedConnTemplate.getDiagramConnections(null)) {
					IModelElement endpoint1 = connPart.getEndpoint1();
					if (endpoint1 == getModelPart().getModelElement()) {
						list.add(connPart);
					}
				}
			}
		}
		
		// Add Implicit connections
		for (DiagramImplicitConnectionTemplate implicitConnTemplate : diagramPart.getImplicitConnectionTemplates()) {
			for (DiagramImplicitConnectionPart implicitConn : implicitConnTemplate.getImplicitConnections()) {
				IModelElement endpoint1 = implicitConn.getEndpoint1();
				if (endpoint1 == getModelPart().getModelElement()) {
					list.add(implicitConn);
				}
			}
		}
		return list;
	}

	@Override
	protected List<DiagramConnectionPart> getModelTargetConnections() {
		List<DiagramConnectionPart> list = new ArrayList<DiagramConnectionPart>();
		SapphireDiagramEditorPagePart diagramPart = getModelPart().nearest(SapphireDiagramEditorPagePart.class);
		for (DiagramConnectionTemplate connTemplate : diagramPart.getConnectionTemplates()) {
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null)) {
				IModelElement endpoint2 = connPart.getEndpoint2();
				if (endpoint2 == getModelPart().getModelElement()) {
					list.add(connPart);
				}
			}
		}
		// Add embedded connections. This needs to be done after all the nodes have been added.
		for (DiagramNodeTemplate nodeTemplate : diagramPart.getNodeTemplates()) {
			DiagramEmbeddedConnectionTemplate embeddedConnTemplate = nodeTemplate.getEmbeddedConnectionTemplate();
			if (embeddedConnTemplate != null) {
				for (DiagramConnectionPart connPart : embeddedConnTemplate.getDiagramConnections(null)) {
					IModelElement endpoint2 = connPart.getEndpoint2();
					if (endpoint2 == getModelPart().getModelElement()) {
						list.add(connPart);
					}
				}
			}
		}
		
		// Add Implicit connections
		for (DiagramImplicitConnectionTemplate implicitConnTemplate : diagramPart.getImplicitConnectionTemplates()) {
			for (DiagramImplicitConnectionPart implicitConn : implicitConnTemplate.getImplicitConnections()) {
				IModelElement endpoint2 = implicitConn.getEndpoint2();
				if (endpoint2 == getModelPart().getModelElement()) {
					list.add(implicitConn);
				}
			}
		}
		return list;
	}

	protected DiagramNodePart getModelPart() {
		return (DiagramNodePart)getModel();
	}
	
	protected NodeFigure getNodeFigure() {
		return (NodeFigure)getFigure();
	}

	@Override
	protected void refreshVisuals() {
		getNodeFigure().setText(getModelPart().getLabel());
		Bounds nb = getModelPart().getNodeBounds();
		Rectangle bounds = new Rectangle(nb.getX(), nb.getY(), DEFAULT_NODE_WIDTH, DEFAULT_NODE_HEIGHT);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,	getFigure(), bounds);
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		if (targetAnchor == null) {
			targetAnchor = new ChopboxAnchor(getFigure());
		}
		return targetAnchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// when moving or creating connections, the line should always end
		// directly at the mouse-pointer.
		return null;
	}
	
}
