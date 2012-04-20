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
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionBendPoints;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeBounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.ConnectionHashKey;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceServiceListener;
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
	private Map<String, DiagramNodeBounds> nodeBounds;
	private Map<ConnectionHashKey, DiagramConnectionBendPoints> connectionBendPoints;
	private Map<ConnectionHashKey, Point> connectionLabelPositions;
	
    @Override
    protected void init()
    {
        super.init();
    	this.editorInput = getDiagramEditorPagePart().getLocalModelElement().adapt(IEditorInput.class);
    	this.nodeBounds = new HashMap<String, DiagramNodeBounds>();
    	this.connectionBendPoints = new HashMap<ConnectionHashKey, DiagramConnectionBendPoints>();
    	this.connectionLabelPositions = new HashMap<ConnectionHashKey, Point>();
		try
		{
			load();
			refreshPersistedPartsCache();
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
		}
		addDiagramPartListener();
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
			String nodeId = node.getNodeId().getContent();
			DiagramNodePart nodePart = IdUtil.getNodePart(getDiagramEditorPagePart(), nodeId);
			int x = node.getX().getContent();
			int y = node.getY().getContent();
			int width = node.getWidth().getContent();
			int height = node.getHeight().getContent();
			
			if (nodePart != null)
			{
				nodePart.setNodeBounds(new DiagramNodeBounds(x, y, width, height, false, false));
			}
			
			ModelElementList<DiagramConnectionLayout> connList = node.getEmbeddedConnectionsLayout();
			for (DiagramConnectionLayout connLayout : connList)
			{
				String connId = connLayout.getConnectionId().getContent();
				ModelElementList<DiagramBendPointLayout> bps = connLayout.getConnectionBendpoints();
				DiagramConnectionPart connPart = IdUtil.getConnectionPart(nodePart, connId);
				if (connPart != null)
				{					
					ConnectionHashKey hashKey = ConnectionHashKey.createKey(nodeId, connId);
					int index = 0;
					for (DiagramBendPointLayout pt : bps)
					{
						connPart.addBendpoint(index++, pt.getX().getContent(), pt.getY().getContent());
					}
					
					if (connLayout.getLabelX().getContent(false) != null && connLayout.getLabelY().getContent(false) != null)
					{
						Point labelPos = new Point(connLayout.getLabelX().getContent(), connLayout.getLabelY().getContent());
						connPart.setLabelPosition(labelPos);
					}
				}
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
				ConnectionHashKey hashKey = ConnectionHashKey.createKey(null, connId);
				int index = 0;
				for (DiagramBendPointLayout pt : bps)
				{
					connPart.addBendpoint(index++, pt.getX().getContent(), pt.getY().getContent());
				}
				List<Point> bendPoints = new ArrayList<Point>();
				bendPoints.addAll(connPart.getConnectionBendpoints().getBendPoints());
				
				if (connLayout.getLabelX().getContent(false) != null && 
						connLayout.getLabelY().getContent(false) != null)
				{
					Point labelPos = new Point(connLayout.getLabelX().getContent(), 
							connLayout.getLabelY().getContent());
					connPart.setLabelPosition(labelPos);
				}
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

	public void save() 
	{
		if (this.layoutModel == null)
		{
			return;
		}
		addNodeBoundsToModel();
		addConnectionsToModel();
		refreshPersistedPartsCache();
		try
		{
			this.layoutModel.resource().save();
		}
		catch (ResourceStoreException rse)
		{
			SapphireUiFrameworkPlugin.log( rse );
		}
	}
	
    private void read(DiagramNodePart nodePart)
    {
    	String id = IdUtil.computeNodeId(nodePart);
    	if (this.nodeBounds.containsKey(id) && this.nodeBounds.get(id) != null)
    	{
    		nodePart.setNodeBounds(this.nodeBounds.get(id)); 		
    	}
    }
        
	private void write(DiagramNodePart nodePart) 
	{
		if (isNodeLayoutChanged(nodePart))
		{
			markDirty();
		}
		else
		{
			if (isDiagramLayoutChanged())
			{
				markDirty();
			}
			else
			{
				markClean();
			}
		}
	}
    
    private void read(DiagramConnectionPart connPart)
    {
    	ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
    	if (this.connectionBendPoints.containsKey(key))
    	{    		
    		if (this.connectionBendPoints.get(key) != null)
    		{
    			connPart.resetBendpoints(this.connectionBendPoints.get(key));
    		}
    		Point labelPos = this.connectionLabelPositions.get(key);
    		if (labelPos != null)
    		{
    			connPart.setLabelPosition(new Point(labelPos.getX(), labelPos.getY()));
    		}
    	}
    }
        
	private void write(DiagramConnectionPart connPart)
	{
		if (isConnectionLayoutChanged(connPart))
		{
			markDirty();
		}
		else
		{
			if (isDiagramLayoutChanged())
			{
				markDirty();
			}
			else
			{
				markClean();
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
		
	private void addNodeBoundsToModel()
	{
		this.layoutModel.getDiagramNodesLayout().clear();
		for (DiagramNodeTemplate nodeTemplate : getDiagramEditorPagePart().getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String nodeId = IdUtil.computeNodeId(nodePart);
				DiagramNodeLayout diagramNode = this.layoutModel.getDiagramNodesLayout().insert();
				diagramNode.setNodeId(nodeId);
				DiagramNodeBounds bounds = nodePart.getNodeBounds();
				diagramNode.setX(bounds.getX());
				diagramNode.setY(bounds.getY());
				if (nodePart.canResizeShape())
				{
					if (bounds.getHeight() != -1)
					{
						diagramNode.setHeight(bounds.getHeight());
					}
					if (bounds.getWidth() != -1)
					{
						diagramNode.setWidth(bounds.getWidth());
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
						DiagramConnectionBendPoints connBendPoints = connPart.getConnectionBendpoints();
						if (connBendPoints.size() > 0)
						{							
							conn = diagramNode.getEmbeddedConnectionsLayout().insert();
							conn.setConnectionId(connId);
							for (Point pt : connBendPoints.getBendPoints())
							{
								DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().insert();
								pt2.setX(pt.getX());
								pt2.setY(pt.getY());
							}
						}
						if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
						{
							if (conn == null)
							{
								conn = diagramNode.getEmbeddedConnectionsLayout().insert();
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
		this.layoutModel.getDiagramConnectionsLayout().clear();
		for (DiagramConnectionTemplate connTemplate : getDiagramEditorPagePart().getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String id = IdUtil.computeConnectionId(connPart);
				DiagramConnectionLayout conn = null;
				
				DiagramConnectionBendPoints connBendPoints = connPart.getConnectionBendpoints();
				if (connBendPoints.size() > 0)
				{		
					conn = this.layoutModel.getDiagramConnectionsLayout().insert();
					conn.setConnectionId(id);
					for (Point pt : connBendPoints.getBendPoints())
					{
						DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().insert();
						pt2.setX(pt.getX());
						pt2.setY(pt.getY());
					}					
				}
				if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
				{
					if (conn == null)
					{
						conn = this.layoutModel.getDiagramConnectionsLayout().insert();
						conn.setConnectionId(id);
					}
					conn.setLabelX(connPart.getLabelPosition().getX());
					conn.setLabelY(connPart.getLabelPosition().getY());
				}
			}
		}
	}
	
	private void addNodeToPersistenceCache(DiagramNodePart nodePart)
	{
		String nodeId = IdUtil.computeNodeId(nodePart);
		this.nodeBounds.put(nodeId, nodePart.getNodeBounds());
	}
	
	private void addConnectionToPersistenceCache(DiagramConnectionPart connPart)
	{
		ConnectionHashKey connKey = ConnectionHashKey.createKey(connPart);
		this.connectionBendPoints.put(connKey, connPart.getConnectionBendpoints());
		if (connPart.getLabel() != null && connPart.getLabelPosition() != null)
		{
			this.connectionLabelPositions.put(connKey, new Point(connPart.getLabelPosition()));
		}		
	}
	
	private void refreshPersistedPartsCache()
	{
		this.nodeBounds.clear();
		this.connectionBendPoints.clear();
		for (DiagramConnectionPart connPart : getDiagramEditorPagePart().getConnections())
		{
			addConnectionToPersistenceCache(connPart);
		}
		for (DiagramNodePart nodePart : getDiagramEditorPagePart().getNodes())
		{
			addNodeToPersistenceCache(nodePart);
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
			
			@Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
				DiagramNodePart nodePart = (DiagramNodePart)event.getPart();
				read(nodePart);
            }
			
			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {
				DiagramNodePart nodePart = (DiagramNodePart)event.getPart();
				DiagramNodeBounds nodeBounds = nodePart.getNodeBounds();
				if (nodeBounds.isAutoLayout())
				{
					// need to add the node bounds to the persistence cache so that "revert" could work
					addNodeToPersistenceCache(nodePart);
					if (isDiagramLayoutChanged())
					{
						markDirty();
					}
					else
					{
						markClean();
					}
				}
				else if (!nodeBounds.isDefaultPosition())
				{
					write((DiagramNodePart)event.getPart());
				}
		    }
			
			@Override
		    public void handleNodeDeleteEvent(final DiagramNodeEvent event)
			{
				if (isDiagramLayoutChanged())
				{
					markDirty();
				}
				else
				{
					markClean();
				}				
			}
			
			@Override
	        public void handleConnectionAddEvent(final DiagramConnectionEvent event)
			{
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
				read(connPart);
			}
			
			@Override
			public void handleConnectionAddBendpointEvent(final DiagramConnectionEvent event)
			{
				write((DiagramConnectionPart)event.getPart());
			}
			
			@Override
		    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
		    {
				write((DiagramConnectionPart)event.getPart());
			}

		    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
		    {
		    	write((DiagramConnectionPart)event.getPart());
		    }
			
			@Override
		    public void handleConnectionResetBendpointsEvent(final DiagramConnectionEvent event)
		    {
		    	DiagramConnectionPart connPart = (DiagramConnectionPart)event.getPart();
		    	DiagramConnectionBendPoints bendPoints = connPart.getConnectionBendpoints();
		    	if (bendPoints.isAutoLayout())
		    	{
		    		addConnectionToPersistenceCache(connPart);
					if (isDiagramLayoutChanged())
					{
						markDirty();
					}
					else
					{
						markClean();
					}
		    		
		    	}
		    	else
		    	{
		    		if (bendPoints.isDefault())
		    		{
		    			// Both the SapphireDiagramEditor and this class listen on connection
		    			// events and we don't control who receives the events first.
						// During "revert", t=if the default bend point is added after the connection 
						// was read, we need to re-read the connection to ensure "revert" works.		    			
		    			read(connPart);
		    		}
		    		else
		    		{
		    			write((DiagramConnectionPart)event.getPart());
		    		}
		    	}
		    }
			
			@Override
		    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
			{
				write((DiagramConnectionPart)event.getPart());
			}
		    
		    public void handleDiagramSaveEvent(final DiagramPageEvent event)
		    {
		    	save();
		    }			
			
		};
		getDiagramEditorPagePart().addListener(this.diagramPartListener);
	}
		
	private void markClean()
	{
        for( DiagramLayoutPersistenceServiceListener listener : getListeners() )
        {
            listener.markClean();
        }		
	}
	
	private void markDirty()
	{
        for( DiagramLayoutPersistenceServiceListener listener : getListeners() )
        {
            listener.markDirty();
        }                		
	}
	
    private boolean isNodeLayoutChanged(DiagramNodePart nodePart)
    {
		DiagramNodeBounds newBounds = nodePart.getNodeBounds();
		boolean changed = false;
		String nodeId = IdUtil.computeNodeId(nodePart);
		if (this.nodeBounds.containsKey(nodeId))
		{
			DiagramNodeBounds oldBounds = this.nodeBounds.get(nodeId);
			if (!newBounds.equals(oldBounds))
			{
				changed = true;
			}
		}
		else
		{
			changed = true;
		}
    	return changed;
    }
	
    private boolean isConnectionLayoutChanged(DiagramConnectionPart connPart)
    {
		// Detect whether the connection bendpoints or connection label have been changed.
    	DiagramConnectionBendPoints bendpoints = connPart.getConnectionBendpoints();
		ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
		boolean changed = false;
		if (this.connectionBendPoints.containsKey(key))
		{		
			DiagramConnectionBendPoints oldBendpoints = this.connectionBendPoints.get(key);
			if (!bendpoints.equals(oldBendpoints))
			{
				changed = true;
			}
			if (!changed)
			{
				if (connPart.getLabel() != null)
				{
					Point newPos = connPart.getLabelPosition();
					Point oldPos = this.connectionLabelPositions.get(key);
					if ((newPos == null && oldPos != null) ||
							(newPos != null && oldPos == null) ||
							(newPos != null && oldPos != null && !newPos.equals(oldPos)))
					{
						changed = true;
					}
				}
			}
		}
		else
		{
			changed = true;
		}
    	return changed;
    }

    private boolean isDiagramLayoutChanged()
    {
    	boolean changed = false;
		for (DiagramNodePart nodePart : getDiagramEditorPagePart().getNodes())
		{
			if (!nodePart.getLocalModelElement().disposed() && isNodeLayoutChanged(nodePart))
			{
				changed = true;
				break;
			}
		}
		for (DiagramConnectionPart connPart : getDiagramEditorPagePart().getConnections())
		{
			if (!connPart.getLocalModelElement().disposed() && isConnectionLayoutChanged(connPart))
			{
				changed = true;
				break;
			}
		}
		
    	return changed;
    }
}
