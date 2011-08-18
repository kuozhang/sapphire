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
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
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
		
	public DiagramGeometryWrapper(IFile file, SapphireDiagramEditorPagePart diagramPart)
	{
		if (file == null)
		{
			throw new IllegalArgumentException();
		}
		this.file = file;
		this.diagramPart = diagramPart;
	
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
		
	public boolean isShowGuidesPropertySet()
	{
		return this.geometryModel.isShowGuides().getContent(false) != null;
	}
	
	public boolean isShowGuides()
	{
		return this.geometryModel.isShowGuides().getContent();
	}
	
	public void setShowGuides(boolean visible)
	{
		this.geometryModel.setShowGuides(visible);
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
				nodePart.setNodeBounds(x, y, width, height);
				
				ModelElementList<IDiagramConnectionGeometry> connList = node.getEmbeddedConnectionGeometries();
				for (IDiagramConnectionGeometry connGeometry : connList)
				{
					String connId = connGeometry.getConnectionId().getContent();
					DiagramConnectionPart connPart = IdUtil.getConnectionPart(nodePart, connId);
					if (connPart != null)
					{
						ModelElementList<IBendPoint> bps = connGeometry.getConnectionBendpoints();
						int index = 0;
						for (IBendPoint pt : bps)
						{
							connPart.addBendpoint(index++, pt.getX().getContent(), pt.getY().getContent());
						}
						
						if (connGeometry.getLabelX().getContent() != null && connGeometry.getLabelY().getContent() != null)
						{
							connPart.setLabelPosition(connGeometry.getLabelX().getContent(), 
									connGeometry.getLabelY().getContent());
						}
					}			
				}
				
			}
		}
		
		ModelElementList<IDiagramConnectionGeometry> connList = this.geometryModel.getDiagramConnectionGeometries();
		for (IDiagramConnectionGeometry connGeometry : connList)
		{
			String connId = connGeometry.getConnectionId().getContent();
			DiagramConnectionPart connPart = IdUtil.getConnectionPart(this.diagramPart, connId);
			if (connPart != null)
			{
				ModelElementList<IBendPoint> bps = connGeometry.getConnectionBendpoints();
				int index = 0;
				for (IBendPoint pt : bps)
				{
					connPart.addBendpoint(index++, pt.getX().getContent(), pt.getY().getContent());
				}
				if (connGeometry.getLabelX().getContent() != null && connGeometry.getLabelY().getContent() != null)
				{
					connPart.setLabelPosition(connGeometry.getLabelX().getContent(), 
							connGeometry.getLabelY().getContent());
				}
				
			}			
		}
	}
	
	public void write() throws ResourceStoreException
	{		
		this.geometryModel.getGridDefinition().setVisible(this.diagramPart.isGridVisible());
		this.geometryModel.setShowGuides(this.diagramPart.isShowGuides());
		addNodeBoundsToModel();
		addConnectionsToModel();
		this.geometryModel.resource().save();
	}

	private void addNodeBoundsToModel()
	{
		this.geometryModel.getDiagramNodeGeometries().clear();
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String id = IdUtil.computeNodeId(nodePart);
				IDiagramNodeGeometry diagramNode = this.geometryModel.getDiagramNodeGeometries().addNewElement();
				diagramNode.setNodeId(id);
				Bounds nodeBounds = nodePart.getNodeBounds();
				diagramNode.setX(nodeBounds.getX());
				diagramNode.setY(nodeBounds.getY());
				if (nodePart.canResizeShape())
				{
					diagramNode.setWidth(nodeBounds.getWidth());
					diagramNode.setHeight(nodeBounds.getHeight());
				}
				// save the embedded connection bendpoints
				DiagramEmbeddedConnectionTemplate embeddedConnTemplate = 
						nodePart.getDiagramNodeTemplate().getEmbeddedConnectionTemplate();
				if (embeddedConnTemplate != null)
				{
					diagramNode.getEmbeddedConnectionGeometries().clear();
					List<DiagramConnectionPart> connParts = embeddedConnTemplate.getDiagramConnections(nodePart.getLocalModelElement());
					for (DiagramConnectionPart connPart : connParts)
					{
						String connId = IdUtil.computeConnectionId(connPart);
						IDiagramConnectionGeometry conn = null;
						if (connPart.getConnectionBendpoints().size() > 0)
						{
							conn = diagramNode.getEmbeddedConnectionGeometries().addNewElement();
							conn.setConnectionId(connId);
							for (Point pt : connPart.getConnectionBendpoints())
							{
								IBendPoint pt2 = conn.getConnectionBendpoints().addNewElement();
								pt2.setX(pt.getX());
								pt2.setY(pt.getY());
							}
						}
						if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
						{
							if (conn == null)
							{
								conn = diagramNode.getEmbeddedConnectionGeometries().addNewElement();
								conn.setConnectionId(connId);
							}
							conn.setLabelX(connPart.getLabelPosition().getX());
							conn.setLabelY(connPart.getLabelPosition().getY());
						}
						
					}
				}
			}
		}
	}
	
	private void addConnectionsToModel()
	{
		this.geometryModel.getDiagramConnectionGeometries().clear();
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String id = IdUtil.computeConnectionId(connPart);
				IDiagramConnectionGeometry conn = null;
				if (connPart.getConnectionBendpoints().size() > 0)
				{					
					conn = this.geometryModel.getDiagramConnectionGeometries().addNewElement();
					conn.setConnectionId(id);
					for (Point pt : connPart.getConnectionBendpoints())
					{
						IBendPoint pt2 = conn.getConnectionBendpoints().addNewElement();
						pt2.setX(pt.getX());
						pt2.setY(pt.getY());
					}					
				}
				if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
				{
					if (conn == null)
					{
						conn = this.geometryModel.getDiagramConnectionGeometries().addNewElement();
						conn.setConnectionId(id);
					}
					conn.setLabelX(connPart.getLabelPosition().getX());
					conn.setLabelY(connPart.getLabelPosition().getY());
				}
			}
		}
	}
			
}
