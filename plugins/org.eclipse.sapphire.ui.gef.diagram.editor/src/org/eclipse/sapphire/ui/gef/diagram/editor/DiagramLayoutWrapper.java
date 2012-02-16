/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - [bugzilla 365019] - SapphireDiagramEditor does not work on 
 *                   non-workspace files 
 *                 - [371576] - Add support for SapphireDigramEditor loading
 *    				 non-local files
 *                   
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.FileResourceStore;
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
import org.eclipse.sapphire.ui.diagram.layout.DiagramBendPointLayout;
import org.eclipse.sapphire.ui.diagram.layout.DiagramConnectionLayout;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayout;
import org.eclipse.sapphire.ui.diagram.layout.DiagramNodeLayout;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * Copied from org.eclipse.sapphire.ui.swt.graphiti
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramLayoutWrapper 
{
	private File file;
	private DiagramLayout geometryModel;
	private SapphireDiagramEditorPagePart diagramPart;
		
	public DiagramLayoutWrapper(File file, SapphireDiagramEditorPagePart diagramPart)
	{
		this.file = file;
		this.diagramPart = diagramPart;
		this.geometryModel = null;
	
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
		return this.geometryModel != null && this.geometryModel.getGridLayout().isVisible().getContent(false) != null;
	}
	
	public boolean isGridVisible()
	{
		return this.geometryModel != null && this.geometryModel.getGridLayout().isVisible().getContent();
	}
	
	public void setGridVisible(boolean visible)
	{
		if (this.geometryModel != null)
		{
			this.geometryModel.getGridLayout().setVisible(visible);
		}
	}
		
	public boolean isShowGuidesPropertySet()
	{
		return this.geometryModel != null && this.geometryModel.getGuidesLayout().isVisible().getContent(false) != null;
	}
	
	public boolean isShowGuides()
	{
		return this.geometryModel != null && this.geometryModel.getGuidesLayout().isVisible().getContent();
	}
	
	public void setShowGuides(boolean visible)
	{
		if (this.geometryModel != null)
		{
			this.geometryModel.getGuidesLayout().setVisible(visible);
		}
	}

	public void read() throws ResourceStoreException, CoreException
	{
		if (this.file == null)
		{
			return;
		}
		final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore(this.file ));
		this.geometryModel = DiagramLayout.TYPE.instantiate(new RootXmlResource( resourceStore ));

		ModelElementList<DiagramNodeLayout> nodes = this.geometryModel.getDiagramNodesLayout();
		for (DiagramNodeLayout node : nodes)
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
				
				ModelElementList<DiagramConnectionLayout> connList = node.getEmbeddedConnectionsLayout();
				for (DiagramConnectionLayout connGeometry : connList)
				{
					String connId = connGeometry.getConnectionId().getContent();
					DiagramConnectionPart connPart = IdUtil.getConnectionPart(nodePart, connId);
					if (connPart != null)
					{
						ModelElementList<DiagramBendPointLayout> bps = connGeometry.getConnectionBendpoints();
						int index = 0;
						for (DiagramBendPointLayout pt : bps)
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
		
		ModelElementList<DiagramConnectionLayout> connList = this.geometryModel.getDiagramConnectionsLayout();
		for (DiagramConnectionLayout connLayout : connList)
		{
			String connId = connLayout.getConnectionId().getContent();
			DiagramConnectionPart connPart = IdUtil.getConnectionPart(this.diagramPart, connId);
			if (connPart != null)
			{
				ModelElementList<DiagramBendPointLayout> bps = connLayout.getConnectionBendpoints();
				int index = 0;
				for (DiagramBendPointLayout pt : bps)
				{
					connPart.addBendpoint(index++, pt.getX().getContent(), pt.getY().getContent());
				}
				if (connLayout.getLabelX().getContent() != null && connLayout.getLabelY().getContent() != null)
				{
					connPart.setLabelPosition(connLayout.getLabelX().getContent(), 
							connLayout.getLabelY().getContent());
				}
				
			}			
		}
	}
	
	public void write() throws ResourceStoreException
	{		
		if (this.geometryModel == null)
		{
			return;
		}
		this.geometryModel.getGridLayout().setVisible(this.diagramPart.isGridVisible());
		this.geometryModel.getGuidesLayout().setVisible(this.diagramPart.isShowGuides());
		addNodeBoundsToModel();
		addConnectionsToModel();
		this.geometryModel.resource().save();
	}

	private void addNodeBoundsToModel()
	{
		this.geometryModel.getDiagramNodesLayout().clear();
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String id = IdUtil.computeNodeId(nodePart);
				DiagramNodeLayout diagramNode = this.geometryModel.getDiagramNodesLayout().addNewElement();
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
					diagramNode.getEmbeddedConnectionsLayout().clear();
					List<DiagramConnectionPart> connParts = embeddedConnTemplate.getDiagramConnections(nodePart.getLocalModelElement());
					for (DiagramConnectionPart connPart : connParts)
					{
						String connId = IdUtil.computeConnectionId(connPart);
						DiagramConnectionLayout conn = null;
						if (connPart.getConnectionBendpoints().size() > 0)
						{
							conn = diagramNode.getEmbeddedConnectionsLayout().addNewElement();
							conn.setConnectionId(connId);
							for (Point pt : connPart.getConnectionBendpoints())
							{
								DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().addNewElement();
								pt2.setX(pt.getX());
								pt2.setY(pt.getY());
							}
						}
						if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
						{
							if (conn == null)
							{
								conn = diagramNode.getEmbeddedConnectionsLayout().addNewElement();
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
		this.geometryModel.getDiagramConnectionsLayout().clear();
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String id = IdUtil.computeConnectionId(connPart);
				DiagramConnectionLayout conn = null;
				if (connPart.getConnectionBendpoints().size() > 0)
				{					
					conn = this.geometryModel.getDiagramConnectionsLayout().addNewElement();
					conn.setConnectionId(id);
					for (Point pt : connPart.getConnectionBendpoints())
					{
						DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().addNewElement();
						pt2.setX(pt.getX());
						pt2.setY(pt.getY());
					}					
				}
				if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
				{
					if (conn == null)
					{
						conn = this.geometryModel.getDiagramConnectionsLayout().addNewElement();
						conn.setConnectionId(id);
					}
					conn.setLabelX(connPart.getLabelPosition().getX());
					conn.setLabelY(connPart.getLabelPosition().getY());
				}
			}
		}
	}
			
}
