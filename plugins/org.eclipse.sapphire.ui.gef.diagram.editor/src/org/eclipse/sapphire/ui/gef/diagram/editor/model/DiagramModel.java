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

package org.eclipse.sapphire.ui.gef.diagram.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireConnectionRouter;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramModel extends DiagramModelBase {
	
	public final static String NODE_ADDED = "NODE_ADDED";
	public final static String NODE_REMOVED = "NODE_REMOVED";

	private SapphireDiagramEditorPagePart part;
	private List<DiagramNodeModel> nodes = new ArrayList<DiagramNodeModel>();
	private List<DiagramConnectionModel> connections = new ArrayList<DiagramConnectionModel>();
	
	private DiagramImageCache imageCache;

	public DiagramModel(SapphireDiagramEditorPagePart part) {
		this.part = part;
		contructNodes();
		constructConnections();
		
		imageCache = new DiagramImageCache(part);
	}
	
	public SapphirePart getSapphirePart() {
		return getModelPart();
	}

	public SapphireDiagramEditorPagePart getModelPart() {
		return this.part;
	}
	
	public DiagramImageCache getImageCache() {
		return imageCache;
	}

	public List<DiagramNodeModel> getNodes() {
		return nodes;
	}
	
	public List<DiagramConnectionModel> getConnections() {
		return connections;
	}
	
	public DiagramNodeModel getDiagramNodeModel(DiagramNodePart nodePart) {
		for (DiagramNodeModel nodeModel : nodes) {
			if (nodeModel.getModelPart() == nodePart) {
				return nodeModel;
			}
		}
		return null;
	}
	
	public DiagramConnectionModel getDiagramConnectionModel(DiagramConnectionPart connectionPart) {
		for (DiagramConnectionModel connectionModel : connections) {
			if (connectionModel.getModelPart() == connectionPart) {
				return connectionModel;
			}
		}
		return null;
	}
	
	public void handleAddNode(DiagramNodePart nodePart) {
		DiagramNodeModel nodeModel = new DiagramNodeModel(this, nodePart); 
		nodes.add(nodeModel);
		firePropertyChange(NODE_ADDED, null, nodeModel);
		
	}
	
	public void handleDirectEditing(DiagramNodePart nodePart) {
		DiagramNodeModel nodeModel = getDiagramNodeModel(nodePart);
		if (nodeModel != null) {
			nodeModel.handleStartEditing();
		}
	}

	public void handleDirectEditing(DiagramConnectionPart connectionPart) {
		DiagramConnectionModel connectionModel = getDiagramConnectionModel(connectionPart);
		if (connectionModel != null) {
			connectionModel.handleStartEditing();
		}
	}

	public void handleRemoveNode(DiagramNodePart nodePart) {
		DiagramNodeModel nodeModel = getDiagramNodeModel(nodePart);
		if (nodeModel != null) {
			nodes.remove(nodeModel);
			firePropertyChange(NODE_REMOVED, null, nodePart);
		}
	}

	private void contructNodes() {
		for (DiagramNodeTemplate nodeTemplate : getModelPart().getNodeTemplates()) {
			if (getModelPart().isNodeTemplateVisible(nodeTemplate)) {
				for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes()) {
					System.out.println(nodePart.getLabel());
					nodes.add(new DiagramNodeModel(this, nodePart));
				}
			}
		}
	}
	
	private void constructConnections() {
		// add the top level connections back to the diagram
		for (DiagramConnectionTemplate connTemplate : this.part.getConnectionTemplates()) {
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null)) {
				addConnection(connPart);
			}
		}

		// Add embedded connections. This needs to be done after all the nodes
		// have been added.
		for (DiagramNodeTemplate nodeTemplate : this.part.getNodeTemplates()) {
			DiagramEmbeddedConnectionTemplate embeddedConnTemplate = nodeTemplate.getEmbeddedConnectionTemplate();
			if (embeddedConnTemplate != null) {
				for (DiagramConnectionPart connPart : embeddedConnTemplate.getDiagramConnections(null)) {
					addConnection(connPart);
				}
			}
		}

		// Add Implicit connections
		for (DiagramImplicitConnectionTemplate implicitConnTemplate : this.part.getImplicitConnectionTemplates()) {
			for (DiagramImplicitConnectionPart implicitConn : implicitConnTemplate.getImplicitConnections()) {
				addConnection(implicitConn);
			}
		}
	}
	
	public void addConnection(DiagramConnectionPart connPart) {
		if (getDiagramConnectionModel(connPart) != null) {
			return;
		}
		
		IModelElement endpoint1 = connPart.getEndpoint1();
		IModelElement endpoint2 = connPart.getEndpoint2();
		DiagramNodePart nodePart1 = this.part.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.part.getDiagramNodePart(endpoint2);
		if (nodePart1 != null && nodePart2 != null) {
			DiagramConnectionModel connectionModel = new DiagramConnectionModel(this, connPart);
			connections.add(connectionModel);

			DiagramNodeModel sourceNode = getDiagramNodeModel(nodePart1);
			DiagramNodeModel targetNode = getDiagramNodeModel(nodePart2);
			if (sourceNode != null && targetNode != null) {
				sourceNode.addSourceConnection(connectionModel);
				targetNode.addTargetConnection(connectionModel);
				
				connectionModel.setSourceNode(sourceNode);
				connectionModel.setTargetNode(targetNode);

				// add bendpoint if collision
				Point bendPoint = SapphireConnectionRouter.getInstance().route(connectionModel);
	        	if (bendPoint != null) {
	        		connectionModel.getModelPart().addBendpoint(0, bendPoint.x, bendPoint.y);
	        	}
			}
		}
	}
	
	public void removeConnection(DiagramConnectionPart connPart) {
		DiagramConnectionModel connectionModel = getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			IModelElement endpoint1 = connPart.getEndpoint1();
			IModelElement endpoint2 = connPart.getEndpoint2();
			DiagramNodePart nodePart1 = this.part.getDiagramNodePart(endpoint1);
			DiagramNodePart nodePart2 = this.part.getDiagramNodePart(endpoint2);
			if (nodePart1 != null && nodePart2 != null) {
				DiagramNodeModel sourceNode = getDiagramNodeModel(nodePart1);
				DiagramNodeModel targetNode = getDiagramNodeModel(nodePart2);
				if (sourceNode != null && targetNode != null) {
					sourceNode.removeSourceConnection(connectionModel);
					targetNode.removeTargetConnection(connectionModel);
					
					connectionModel.setSourceNode(null);
					connectionModel.setTargetNode(null);
				}
			}
			connections.remove(connectionModel);
		}
	}

}
