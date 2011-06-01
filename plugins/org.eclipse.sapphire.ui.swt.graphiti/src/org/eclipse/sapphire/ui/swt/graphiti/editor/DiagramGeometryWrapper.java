/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.geometry.IBendPoint;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramConnectionGeometry;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramGeometry;
import org.eclipse.sapphire.ui.diagram.geometry.IDiagramNodeGeometry;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramGeometryWrapper 
{
	private IFile file;
	private IDiagramGeometry geometryModel;
	private SapphireDiagramEditorPagePart diagramPart;
	
	private HashMap<DiagramNodePart, Bounds> nodeGeometries;
	private HashMap<DiagramConnectionPart, List<Point>> connectionBendpoints;
	private HashMap<DiagramNodePart, HashMap<DiagramConnectionPart, List<Point>>> embeddedConnectionBendpoints;
	
	public DiagramGeometryWrapper(IFile file, SapphireDiagramEditorPagePart diagramPart)
	{
		if (file == null)
		{
			throw new IllegalArgumentException();
		}
		this.file = file;
		this.diagramPart = diagramPart;
		this.nodeGeometries = new HashMap<DiagramNodePart, Bounds>();
		this.connectionBendpoints = new HashMap<DiagramConnectionPart, List<Point>>();
		this.embeddedConnectionBendpoints = 
					new HashMap<DiagramNodePart, HashMap<DiagramConnectionPart, List<Point>>>();
		try
		{
			read();
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
		}
	}
	
	public boolean isGridPropertySet()
	{
		return this.geometryModel.getGridDefinition().isVisible().getContent(false) != null;
	}
	
	public boolean isGridVisible()
	{
		return this.geometryModel.getGridDefinition().isVisible().getContent();
	}
	
	public void setGridVisible(boolean visible)
	{
		this.geometryModel.getGridDefinition().setVisible(visible);
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
	
	public void read() throws ResourceStoreException, CoreException
	{
		this.file.refreshLocal(0, null);
		final XmlResourceStore resourceStore = new XmlResourceStore( new WorkspaceFileResourceStore(this.file ));
		this.geometryModel = IDiagramGeometry.TYPE.instantiate(new RootXmlResource( resourceStore ));

		ModelElementList<IDiagramNodeGeometry> nodes = this.geometryModel.getDiagramNodeGeometries();
		for (IDiagramNodeGeometry node : nodes)
		{
			String id = node.getNodeId().getContent();
			DiagramNodePart nodePart = IdUtil.getNodePart(this.diagramPart, id);
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
					DiagramConnectionPart connPart = IdUtil.getConnectionPart(nodePart, connId);
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
			DiagramConnectionPart connPart = IdUtil.getConnectionPart(this.diagramPart, connId);
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
			String id = IdUtil.computeNodeId(nodePart);
			
			if (bounds != null && id != null)
			{
				IDiagramNodeGeometry diagramNode = this.geometryModel.getDiagramNodeGeometries().addNewElement();
				diagramNode.setNodeId(id);
				diagramNode.setX(bounds.getX());
				diagramNode.setY(bounds.getY());
				if (nodePart.canResizeShape())
				{
					diagramNode.setWidth(bounds.getWidth());
					diagramNode.setHeight(bounds.getHeight());
				}
				
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
			String id = IdUtil.computeConnectionId(connPart);
			
			if (bps != null && id != null)
			{
				IDiagramConnectionGeometry conn = connGeometries.addNewElement();
				conn.setConnectionId(id);
				for (Point pt : bps)
				{
					IBendPoint pt2 = conn.getConnectionBendpoints().addNewElement();
					pt2.setX(pt.getX());
					pt2.setY(pt.getY());
				}
			}
		}		
	}
		
}
