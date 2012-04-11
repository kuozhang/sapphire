/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.layout.standard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.StatusException;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class StandardDiagramLayoutPersistenceService extends DiagramLayoutPersistenceService
{
	protected StandardDiagramLayout layoutModel;
	protected IEditorInput editorInput;
	private SapphireDiagramPartListener diagramPartListener;
	private Map<String, Bounds> nodeBounds;
	private Map<String, List<Point>> connectionBendPoints;
	
    @Override
    protected void init()
    {
        super.init();
    	this.editorInput = getDiagramEditorPagePart().getLocalModelElement().adapt(IEditorInput.class);
    	this.nodeBounds = new HashMap<String, Bounds>();
    	this.connectionBendPoints = new HashMap<String, List<Point>>();
		try
		{
			load();
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
		}
		addDiagramPartListener();
    }		
	
    public void read(DiagramNodePart nodePart)
    {
    	String id = IdUtil.computeNodeId(nodePart);
    	if (isNodePersisted(nodePart))
    	{
    		Bounds bounds = this.nodeBounds.get(id);
    		if (bounds != null)
    		{
    			nodePart.setNodeBounds(bounds.getX(), bounds.getY(), bounds.getHeight(), bounds.getWidth());
    		}
    	}
    }
    
    public void read(DiagramConnectionPart connPart)
    {
    	if (isConnectionPersisted(connPart))
    	{
    		String id = IdUtil.computeConnectionId(connPart);
    		List<Point> bendPoints = this.connectionBendPoints.get(id);
    		if (bendPoints != null)
    		{
    			connPart.resetBendpoints(bendPoints);
    		}
    	}
    }
    
    private void setGridVisible(boolean visible)
    {
    	if (this.layoutModel != null)
    	{
    		this.layoutModel.getGridLayout().setVisible(visible);
    	}
    }
	
	private void setGuidesVisible(boolean visible)
    {    	
    	if (this.layoutModel != null)
    	{
    		this.layoutModel.getGuidesLayout().setVisible(visible);
    	}
    }
	
	
	@Override
	public void dispose()
	{
		if (this.diagramPartListener != null)
		{
			getDiagramEditorPagePart().removeListener(this.diagramPartListener);
		}
	}
	protected abstract StandardDiagramLayout initLayoutModel();
	
	public void load() throws ResourceStoreException, CoreException, IOException, StatusException
	{
		this.layoutModel = initLayoutModel();
		if (this.layoutModel == null)
		{
			return;
		}
		getDiagramEditorPagePart().setGridVisible(this.layoutModel.getGridLayout().isVisible().getContent());
		getDiagramEditorPagePart().setShowGuides(this.layoutModel.getGuidesLayout().isVisible().getContent());
		
		
		ModelElementList<DiagramNodeLayout> nodes = this.layoutModel.getDiagramNodesLayout();
		for (DiagramNodeLayout node : nodes)
		{
			String id = node.getNodeId().getContent();
			DiagramNodePart nodePart = IdUtil.getNodePart(getDiagramEditorPagePart(), id);
			int x = node.getX().getContent() != null ? node.getX().getContent() : -1;
			int y = node.getY().getContent() != null ? node.getY().getContent() : -1;
			int width = node.getWidth().getContent() != null ? node.getWidth().getContent() : -1;
			int height = node.getHeight().getContent() != null ? node.getHeight().getContent() : -1;
			
			if (nodePart != null)
			{
				nodePart.setNodeBounds(x, y, width, height);
			}
			this.nodeBounds.put(id, new Bounds(x, y, width, height));
			ModelElementList<DiagramConnectionLayout> connList = node.getEmbeddedConnectionsLayout();
			for (DiagramConnectionLayout connGeometry : connList)
			{
				String connId = connGeometry.getConnectionId().getContent();
				ModelElementList<DiagramBendPointLayout> bps = connGeometry.getConnectionBendpoints();
				DiagramConnectionPart connPart = IdUtil.getConnectionPart(nodePart, connId);
				if (connPart != null)
				{					
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
				addConnectionBendPointsToCache(connId, bps);
			}
				
		}
		
		ModelElementList<DiagramConnectionLayout> connList = this.layoutModel.getDiagramConnectionsLayout();
		for (DiagramConnectionLayout connLayout : connList)
		{
			String connId = connLayout.getConnectionId().getContent();
			DiagramConnectionPart connPart = IdUtil.getConnectionPart(getDiagramEditorPagePart(), connId);
			ModelElementList<DiagramBendPointLayout> bps = connLayout.getConnectionBendpoints();
			if (connPart != null)
			{
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
			addConnectionBendPointsToCache(connId, bps);
		}
		
	}
	
	private void addConnectionBendPointsToCache(String connId, List<DiagramBendPointLayout> bendPoints)
	{
		if (bendPoints != null && bendPoints.size() > 0)
		{
			List<Point> bendPoints2 = new ArrayList<Point>(bendPoints.size());
			for (DiagramBendPointLayout pt : bendPoints)
			{
				bendPoints2.add(new Point(pt.getX().getContent(), pt.getY().getContent()));
			}
			this.connectionBendPoints.put(connId, bendPoints2);
		}
	}

	public void save() 
	{
		refreshPersistedPartsCache();
		if (this.layoutModel == null)
		{
			return;
		}
		addNodeBoundsToModel();
		addConnectionsToModel();
		try
		{
			this.layoutModel.resource().save();
		}
		catch (ResourceStoreException rse)
		{
			SapphireUiFrameworkPlugin.log( rse );
		}
	}
	
	private void addAutoLayoutToCache()
	{
		for (DiagramNodePart nodePart : getDiagramEditorPagePart().getNodes())
		{
			String nodeId = IdUtil.computeNodeId(nodePart);
			Bounds bounds = nodePart.getNodeBounds();
			this.nodeBounds.put(nodeId, new Bounds(bounds));
		}
		for (DiagramConnectionPart connPart : getDiagramEditorPagePart().getConnections())
		{
			if (connPart.getConnectionBendpoints().size() > 0)
			{
				String connId = IdUtil.computeConnectionId(connPart);
				ArrayList<Point> bps = new ArrayList<Point>(connPart.getConnectionBendpoints().size());
				bps.addAll(connPart.getConnectionBendpoints());
				this.connectionBendPoints.put(connId, bps);
			}
		}
	}
	
	protected String computeLayoutFileName(IEditorInput editorInput) throws CoreException, IOException
	{
		// Compute a unique path for the layout file based on a hash associated with the editor input
		String uniquePath = null;
    	if (editorInput instanceof FileEditorInput)
    	{
    		FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
    		IFile ifile = fileEditorInput.getFile();
    		uniquePath = ifile.getLocation().toPortableString();
    	}
    	else if (editorInput instanceof FileStoreEditorInput)
    	{
    		FileStoreEditorInput fileStoreInput = (FileStoreEditorInput)editorInput;
        	IFileStore store = EFS.getStore(fileStoreInput.getURI());
        	File localFile = store.toLocalFile(EFS.NONE, null);
    		//if no local file is available, obtain a cached file
    		if (localFile == null)
    			localFile = store.toLocalFile(EFS.CACHE, null);
    		if (localFile == null)
    			throw new IllegalArgumentException();
    		uniquePath = localFile.getCanonicalPath();
    	}
    	else if (editorInput instanceof IStorageEditorInput)
    	{
    		IStorageEditorInput storageEditorInput = (IStorageEditorInput) editorInput;
    		IPath storagePath = storageEditorInput.getStorage().getFullPath();
    		if (storagePath != null)
    		{
    			uniquePath = storagePath.toPortableString();
    		}    		
    	}
		return uniquePath != null ? MiscUtil.createStringDigest(uniquePath) : null;
	}
		
	protected void addNodeBoundsToModel()
	{
		this.layoutModel.getDiagramNodesLayout().clear();
		for (DiagramNodeTemplate nodeTemplate : getDiagramEditorPagePart().getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String id = IdUtil.computeNodeId(nodePart);
				DiagramNodeLayout diagramNode = this.layoutModel.getDiagramNodesLayout().addNewElement();
				diagramNode.setNodeId(id);
				Bounds nodeBounds = nodePart.getNodeBounds();
				this.nodeBounds.put(id, nodeBounds);
				diagramNode.setX(nodeBounds.getX());
				diagramNode.setY(nodeBounds.getY());
				if (nodePart.canResizeShape())
				{
					if (nodeBounds.getHeight() != -1)
					{
						diagramNode.setHeight(nodeBounds.getHeight());
					}
					if (nodeBounds.getWidth() != -1)
					{
						diagramNode.setWidth(nodeBounds.getWidth());
					}
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
							this.connectionBendPoints.put(connId, connPart.getConnectionBendpoints());
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
	
	protected void addConnectionsToModel()
	{
		this.layoutModel.getDiagramConnectionsLayout().clear();
		for (DiagramConnectionTemplate connTemplate : getDiagramEditorPagePart().getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String id = IdUtil.computeConnectionId(connPart);
				DiagramConnectionLayout conn = null;
				if (connPart.getConnectionBendpoints().size() > 0)
				{		
					this.connectionBendPoints.put(id, connPart.getConnectionBendpoints());
					conn = this.layoutModel.getDiagramConnectionsLayout().addNewElement();
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
						conn = this.layoutModel.getDiagramConnectionsLayout().addNewElement();
						conn.setConnectionId(id);
					}
					conn.setLabelX(connPart.getLabelPosition().getX());
					conn.setLabelY(connPart.getLabelPosition().getY());
				}
			}
		}
	}
	
	private void addDiagramPartListener()
	{
		this.diagramPartListener = new SapphireDiagramPartListener() 
		{
		    @Override
			public void handleGridStateChangeEvent(final DiagramPageEvent event)
			{
		    	SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)event.getPart();
		    	setGridVisible(diagramPart.isGridVisible());
			}
			
			@Override
			public void handleGuideStateChangeEvent(final DiagramPageEvent event)
			{
				SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)event.getPart();
		    	setGuidesVisible(diagramPart.isShowGuides());
			}
			
		    public void handleDiagramSaveEvent(final DiagramPageEvent event)
		    {
		    	save();
		    }			
			
		    public void handleDiagramAutoLayout(final DiagramPageEvent event)
		    {
		    	addAutoLayoutToCache();
		    }			
		};
		getDiagramEditorPagePart().addListener(this.diagramPartListener);
	}
		
}
