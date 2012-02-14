/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.CompoundDirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireConnectionRouter;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramTypeProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DiagramGraphLayoutActionHandler extends
		SapphireDiagramActionHandler 
{
	private static final int PADDING = 36;
	
	@Override
	public boolean canExecute(Object obj) 
	{
		return true;
	}
	
	public abstract int getGraphDirection();

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramRenderingContext diagramCtx = (DiagramRenderingContext)context;
		SapphireDiagramTypeProvider tp = (SapphireDiagramTypeProvider)diagramCtx.getDiagramEditor().getDiagramTypeProvider();
		SapphireDiagramFeatureProvider fp = (SapphireDiagramFeatureProvider)tp.getFeatureProvider();
		Diagram diagram = tp.getDiagram();
		final CompoundDirectedGraph graph = mapDiagramToGraph(fp, diagram);
		graph.setDefaultPadding(new Insets(PADDING));
		new CompoundDirectedGraphLayout().visit(graph);
		mapGraphCoordinatesToDiagram(graph, fp, diagram);
		
		return null;
	}

	private CompoundDirectedGraph mapDiagramToGraph(SapphireDiagramFeatureProvider fp, Diagram d) 
	{
		Map<AnchorContainer, Node> shapeToNode = new HashMap<AnchorContainer, Node>();
		CompoundDirectedGraph dg = new CompoundDirectedGraph();
		if (getGraphDirection() == PositionConstants.EAST_WEST)
		{
			dg.setDirection(getGraphDirection());
		}
		EdgeList edgeList = new EdgeList();
		NodeList nodeList = new NodeList();
		EList<Shape> children = d.getChildren();
		for (Shape shape : children) 
		{
			Node node = new Node();
			GraphicsAlgorithm ga = shape.getGraphicsAlgorithm();
			node.x = ga.getX();
			node.y = ga.getY();
			node.width = ga.getWidth();
			node.height = ga.getHeight();
			node.data = shape;
			shapeToNode.put(shape, node);
			nodeList.add(node);
		}
		EList<Connection> connections = d.getConnections();
		for (Connection connection : connections) 
		{
			AnchorContainer source = connection.getStart().getParent();
			AnchorContainer target = connection.getEnd().getParent();
			Edge edge = new Edge(connection, shapeToNode.get(source), shapeToNode.get(target));
			edge.data = connection;
			edgeList.add(edge);
		}
		dg.nodes = nodeList;
		dg.edges = edgeList;
		return dg;
	}
	
	private void mapGraphCoordinatesToDiagram(final CompoundDirectedGraph graph,
			final SapphireDiagramFeatureProvider fp,
			final Diagram diagram) 
	{
		final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
		ted.getCommandStack().execute(new RecordingCommand(ted) 
		{
			@Override
			protected void doExecute() 
			{		
				mapGraphNodeCoordinatesToDiagram(graph, fp);
				removeDiagramManualBendpoints(fp, diagram);
				mapGraphEdgeCoordinatesToDiagram(graph, fp);
			}
		});
	}

	private void mapGraphNodeCoordinatesToDiagram(CompoundDirectedGraph graph, SapphireDiagramFeatureProvider fp)
	{
		NodeList myNodes = new NodeList();
		myNodes.addAll(graph.nodes);
		myNodes.addAll(graph.subgraphs);
		for (Object object : myNodes) 
		{
			Node node = (Node) object;
			Shape shape = (Shape) node.data;
			shape.getGraphicsAlgorithm().setX(node.x);
			shape.getGraphicsAlgorithm().setY(node.y);
			shape.getGraphicsAlgorithm().setWidth(node.width);
			shape.getGraphicsAlgorithm().setHeight(node.height);
						
			final Object bo = fp.getBusinessObjectForPictogramElement(shape);
			if (bo instanceof DiagramNodePart)
			{
				DiagramNodePart nodePart = (DiagramNodePart)bo;
				nodePart.setNodePosition(node.x, node.y);
			}				
		}		
	}
	
	private void removeDiagramManualBendpoints(final SapphireDiagramFeatureProvider fp, Diagram d)
	{		
		final Collection<Connection> cons = d.getConnections();
		for (Connection conn : cons)
		{
			if (conn instanceof FreeFormConnection)
			{
				FreeFormConnection freeConn = (FreeFormConnection)conn;
				freeConn.getBendpoints().clear();
				final Object bo = fp.getBusinessObjectForPictogramElement(conn);
				if (bo instanceof DiagramConnectionPart)
				{
					DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
					connPart.removeAllBendpoints();
				}
			}
		}				
	}
	
	private void mapGraphEdgeCoordinatesToDiagram(CompoundDirectedGraph graph, SapphireDiagramFeatureProvider fp)
	{
		// add bend points generated by the graph layout
		EdgeList myEdges = new EdgeList();
		myEdges.addAll(graph.edges);
		for (Object object : myEdges)
		{
			Edge edge = (Edge)object;
			Connection conn = (Connection)edge.data;
			NodeList nodes = edge.vNodes;
			final Object bo = fp.getBusinessObjectForPictogramElement(conn);
			DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
			if (conn instanceof FreeFormConnection && nodes != null)
			{
				FreeFormConnection freeConn = (FreeFormConnection)conn;
				int bpIndex = 0;
				for (int i = 0; i < nodes.size(); i++)
				{
					Node vn = nodes.getNode(i);
					int x = vn.x;
					int y = vn.y;
					if (getGraphDirection() == PositionConstants.EAST_WEST)
					{
						int offset = edge.isFeedback() ? -vn.width : vn.width;
						freeConn.getBendpoints().add(bpIndex, Graphiti.getGaCreateService().createPoint(x, y));
						freeConn.getBendpoints().add(bpIndex + 1, Graphiti.getGaCreateService().createPoint(x + offset, y));
						connPart.addBendpoint(bpIndex, x, y);
						connPart.addBendpoint(bpIndex + 1, x + offset, y);
					}
					else
					{
						int offset = edge.isFeedback() ? -vn.height : vn.height;
						freeConn.getBendpoints().add(bpIndex, Graphiti.getGaCreateService().createPoint(x, y));
						freeConn.getBendpoints().add(bpIndex + 1, Graphiti.getGaCreateService().createPoint(x, y + offset));
						connPart.addBendpoint(bpIndex, x, y);
						connPart.addBendpoint(bpIndex + 1, x, y + offset);
					}
					bpIndex += 2;
				}
			}
			else if (conn instanceof FreeFormConnection)
			{
	        	Point bendPoint = SapphireConnectionRouter.getInstance().route((FreeFormConnection)conn);
	        	if (bendPoint != null)
	        	{
	        		((FreeFormConnection)conn).getBendpoints().add(Graphiti.getCreateService().createPoint(bendPoint.x, bendPoint.y));
	        		connPart.addBendpoint(0, bendPoint.x, bendPoint.y);
	        	}				
			}
		}		
	}
	
}
