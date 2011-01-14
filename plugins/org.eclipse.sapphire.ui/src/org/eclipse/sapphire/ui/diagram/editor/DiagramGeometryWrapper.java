/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.WorkspaceFileResourceStore;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.diagram.geometry.IBendPoint;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramConnectionGeometry;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramGeometry;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramNodeGeometry;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramGeometryWrapper 
{
	private IFile file;
	private IDiagramGeometry geometryModel;
	private SapphireDiagramEditorPart diagramPart;
	
	private HashMap<DiagramNodePart, Bounds> nodeGeometries;
	private HashMap<DiagramConnectionPart, List<Point>> connectionBendpoints;
	private HashMap<DiagramNodePart, HashMap<DiagramConnectionPart, List<Point>>> embeddedConnectionBendpoints;
	
	public DiagramGeometryWrapper(IFile file, SapphireDiagramEditorPart diagramPart)
		throws ResourceStoreException
	{
		this.file = file;
		this.diagramPart = diagramPart;
		this.nodeGeometries = new HashMap<DiagramNodePart, Bounds>();
		this.connectionBendpoints = new HashMap<DiagramConnectionPart, List<Point>>();
		this.embeddedConnectionBendpoints = 
					new HashMap<DiagramNodePart, HashMap<DiagramConnectionPart, List<Point>>>();
		read();
	}
	
	public void addNode(DiagramNodePart nodePart, int x, int y, int w, int h)
	{
		Bounds bounds = new Bounds(x, y, w, h);
		this.nodeGeometries.put(nodePart, bounds);
	}
	
	public void removeNode(DiagramNodePart nodePart)
	{
		this.nodeGeometries.remove(nodePart);
	}
	
	public void updateNode(DiagramNodePart nodePart, int x, int y)
	{
		Bounds bounds = this.nodeGeometries.get(nodePart);
		if (bounds != null)
		{
			bounds.setX(x);
			bounds.setY(y);
		}
	}

	public void updateNode(DiagramNodePart nodePart, int x, int y, int w, int h)
	{
		Bounds bounds = this.nodeGeometries.get(nodePart);
		if (bounds != null)
		{
			bounds.setX(x);
			bounds.setY(y);
			bounds.setHeight(h);
			bounds.setWidth(w);
		}
	}
	
	public Bounds getNode(DiagramNodePart nodePart)
	{
		return this.nodeGeometries.get(nodePart);
	}
		
	public void addConnectionBendpoint(DiagramConnectionPart connPart, int index, int x, int y)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = 
			getConnectionBenpointsMap(connPart, true);
		List<Point> bendpoints = connBendpointsMap.get(connPart);
		if (bendpoints == null)
		{
			bendpoints = new ArrayList<Point>();
			connBendpointsMap.put(connPart, bendpoints);
		}
		Point newPt = new Point(x, y);
		bendpoints.add(index, newPt);
	}
	
	public void removeConnectionBendpoint(DiagramConnectionPart connPart, int index)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = 
			getConnectionBenpointsMap(connPart, false);
		if (connBendpointsMap == null)
		{
			throw new RuntimeException("Could not locate connection benpoints map for the embedded connection");
		}
		List<Point> bendpoints = connBendpointsMap.get(connPart);
		if (bendpoints != null)
		{
			bendpoints.remove(index);
		}
	}
	
	public boolean updateConnectionBendpoint(DiagramConnectionPart connPart, int index, int x, int y)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = 
			getConnectionBenpointsMap(connPart, false);
		if (connBendpointsMap == null)
		{
			throw new RuntimeException("Could not locate connection benpoints map for the embedded connection");
		}
		List<Point> bendpoints = connBendpointsMap.get(connPart);
		if (bendpoints != null && index < bendpoints.size())
		{
			bendpoints.set(index, new Point(x, y));
			return true;
		}
		return false;
	}
	

	public List<Point> getConnectionBendpoints(DiagramConnectionPart connPart)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = 
			getConnectionBenpointsMap(connPart, false);		
		if (connBendpointsMap != null)
		{
			return connBendpointsMap.get(connPart);
		}
		return null;
	}
		
	public void removeConnectionBendpoints(DiagramConnectionPart connPart)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = 
			getConnectionBenpointsMap(connPart, false);		
		if (connBendpointsMap != null)
		{
			connBendpointsMap.remove(connPart);
		}		
	}
	
	public void read() throws ResourceStoreException
	{
		final XmlResourceStore resourceStore = new XmlResourceStore( new WorkspaceFileResourceStore(this.file ));
		this.geometryModel = IDiagramGeometry.TYPE.instantiate(new RootXmlResource( resourceStore ));

		ModelElementList<IDiagramNodeGeometry> nodes = this.geometryModel.getDiagramNodeGeometries();
		for (IDiagramNodeGeometry node : nodes)
		{
			String id = node.getNodeId().getContent();
			DiagramNodePart nodePart = getNodePart(id);
			if (nodePart != null)
			{
				int x = node.getX().getContent() != null ? node.getX().getContent() : -1;
				int y = node.getY().getContent() != null ? node.getY().getContent() : -1;
				int width = node.getWidth().getContent() != null ? node.getWidth().getContent() : -1;
				int height = node.getHeight().getContent() != null ? node.getHeight().getContent() : -1;
				this.addNode(nodePart, x, y, width, height);
				
				ModelElementList<IDiagramConnectionGeometry> connList = node.getEmbeddedConnectionGeometries();
				for (IDiagramConnectionGeometry connBend : connList)
				{
					String connId = connBend.getConnectionId().getContent();
					DiagramConnectionPart connPart = getConnectionPart(nodePart, connId);
					if (connPart != null)
					{
						ModelElementList<IBendPoint> bps = connBend.getConnectionBendpoints();
						int index = 0;
						for (IBendPoint pt : bps)
						{
							this.addConnectionBendpoint(connPart, index++, pt.getX().getContent(), pt.getY().getContent());
						}
					}			
				}
				
			}
		}
		
		ModelElementList<IDiagramConnectionGeometry> connList = this.geometryModel.getDiagramConnectionGeometries();
		for (IDiagramConnectionGeometry connBend : connList)
		{
			String connId = connBend.getConnectionId().getContent();
			DiagramConnectionPart connPart = getConnectionPart(connId);
			if (connPart != null)
			{
				ModelElementList<IBendPoint> bps = connBend.getConnectionBendpoints();
				int index = 0;
				for (IBendPoint pt : bps)
				{
					this.addConnectionBendpoint(connPart, index++, pt.getX().getContent(), pt.getY().getContent());
				}
			}			
		}
	}
	
	public void write() throws ResourceStoreException
	{
		this.geometryModel.getDiagramNodeGeometries().clear();
		Iterator<DiagramNodePart> it = this.nodeGeometries.keySet().iterator();
		while (it.hasNext())
		{
			DiagramNodePart nodePart = it.next();
			Bounds bounds = this.nodeGeometries.get(nodePart);
			String id = nodePart.getInstanceId();
			
			if (bounds != null && id != null)
			{
				IDiagramNodeGeometry diagramNode = this.geometryModel.getDiagramNodeGeometries().addNewElement();
				diagramNode.setNodeId(id);
				diagramNode.setX(bounds.getX());
				diagramNode.setY(bounds.getY());
				diagramNode.setWidth(bounds.getWidth());
				diagramNode.setHeight(bounds.getHeight());
				
				// save the embedded connection bendpoints
				HashMap<DiagramConnectionPart, List<Point>> embeddedConnBendpointsMap = 
					this.embeddedConnectionBendpoints.get(nodePart);
				if (embeddedConnBendpointsMap != null)
				{
					diagramNode.getEmbeddedConnectionGeometries().clear();
					addConnectionBenpointsToModel(embeddedConnBendpointsMap, 
							diagramNode.getEmbeddedConnectionGeometries());					
				}				
			}			
		}
		
		this.geometryModel.getDiagramConnectionGeometries().clear();
		addConnectionBenpointsToModel(this.connectionBendpoints, 
				this.geometryModel.getDiagramConnectionGeometries());
		this.geometryModel.resource().save();
	}

	private DiagramNodePart getNodePart(String nodeId)
	{
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String nodeId2 = nodePart.getInstanceId();
				if (nodeId != null && nodeId2 != null && nodeId.equals(nodeId2))
				{
					return nodePart;
				}
			}
		}
		return null;
	}
	
	private DiagramConnectionPart getConnectionPart(String connId)
	{
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String connId2 = connPart.getInstanceId();
				if (connId != null && connId2 != null && connId.equals(connId2))
				{
					return connPart;
				}
			}
		}
		return null;
	}

	private DiagramConnectionPart getConnectionPart(DiagramNodePart nodePart, String connId)
	{
		DiagramNodeTemplate nodeTemplate = nodePart.getDiagramNodeTemplate();
		DiagramEmbeddedConnectionTemplate connTemplate = 
			nodeTemplate.getEmbeddedConnectionTemplate();
		if (connTemplate != null)
		{
			List<DiagramConnectionPart> connParts = connTemplate.getDiagramConnections(nodePart.getLocalModelElement());
			for (DiagramConnectionPart connPart : connParts)
			{
				String connId2 = connPart.getInstanceId();
				if (connId != null && connId2 != null && connId.equals(connId2))
				{
					return connPart;
				}				
			}
		}
		return null;
	}

	private HashMap<DiagramConnectionPart, List<Point>> getConnectionBenpointsMap(DiagramConnectionPart connPart, boolean create)
	{
		HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap = null;
		if (connPart instanceof DiagramEmbeddedConnectionPart)
		{
			DiagramNodePart srcNodePart = ((DiagramEmbeddedConnectionPart)connPart).getSourceNodePart();
			if (srcNodePart == null)
			{
				throw new RuntimeException("Could not locate the source node for the embedded connection");
			}
			connBendpointsMap = this.embeddedConnectionBendpoints.get(srcNodePart);
			if (connBendpointsMap == null)
			{
				if (create)
				{
					connBendpointsMap = new HashMap<DiagramConnectionPart, List<Point>>();
					this.embeddedConnectionBendpoints.put(srcNodePart, connBendpointsMap);
				}
			}
		}
		else
		{
			connBendpointsMap = this.connectionBendpoints;
		}
		return connBendpointsMap;
	}
	
	private void addConnectionBenpointsToModel(HashMap<DiagramConnectionPart, List<Point>> connBendpointsMap, 
			ModelElementList<IDiagramConnectionGeometry> connGeometries)
	{
		Iterator<DiagramConnectionPart> connIt = connBendpointsMap.keySet().iterator();
		while (connIt.hasNext())
		{
			DiagramConnectionPart connPart = connIt.next();
			List<Point> bps = connBendpointsMap.get(connPart);
			String id = connPart.getInstanceId();
			
			if (bps != null && id != null)
			{
				IDiagramConnectionGeometry conn = connGeometries.addNewElement();
				conn.setConnectionId(id);
				for (Point pt : bps)
				{
					IBendPoint pt2 = conn.getConnectionBendpoints().addNewElement();
					pt2.setX(pt.x);
					pt2.setY(pt.y);
				}
			}
		}
		
	}
	
	// -------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------
	
	public static class Point
	{
		private int x;
		private int y;
		
		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int getX() 
		{ 
			return this.x; 
		}
		
		public void setX(int x) 
		{
			this.x = x;
		}
		
		public int getY()
		{
			return this.y;
		}
		
		public void setY(int y)
		{
			this.y = y;
		}
	}
	
	public static final class Bounds extends Point
	{
		private int width;
		private int height;
		
		public Bounds(int x, int y, int width, int height)
		{
			super(x, y);
			this.width = width;
			this.height = height;
		}
		
		public int getWidth()
		{
			return this.width;
		}
		
		public void setWidth(int w)
		{
			this.width = w;
		}
		
		public int getHeight()
		{
			return this.height;
		}
		
		public void setHeight(int h)
		{
			this.height = h;
		}
	}
}
