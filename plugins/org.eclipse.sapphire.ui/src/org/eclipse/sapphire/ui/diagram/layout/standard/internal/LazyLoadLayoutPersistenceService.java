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

package org.eclipse.sapphire.ui.diagram.layout.standard.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramBendPointLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramConnectionLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramNodeLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.StandardDiagramLayout;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class LazyLoadLayoutPersistenceService extends DiagramLayoutPersistenceService
{
	protected StandardDiagramLayout layoutModel;
	protected IEditorInput editorInput;
	protected SapphireDiagramEditorPagePart diagramPart;
	private SapphireDiagramPartListener diagramPartListener;

	public LazyLoadLayoutPersistenceService(IEditorInput editorInput, SapphireDiagramEditorPagePart diagramPart)
	{
		if (editorInput == null || diagramPart == null)
		{
			throw new IllegalArgumentException();
		}
		this.editorInput = editorInput;
		this.diagramPart = diagramPart;
		init();
		addDiagramPartListener();
	}
	
    @Override
    protected void init()
    {
        super.init();
		try
		{
			load();
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
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
			this.diagramPart.removeListener(this.diagramPartListener);
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
		this.diagramPart.setGridVisible(this.layoutModel.getGridLayout().isVisible().getContent());
		this.diagramPart.setShowGuides(this.layoutModel.getGuidesLayout().isVisible().getContent());
		ModelElementList<DiagramNodeLayout> nodes = this.layoutModel.getDiagramNodesLayout();
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
		
		ModelElementList<DiagramConnectionLayout> connList = this.layoutModel.getDiagramConnectionsLayout();
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

	public void save() 
	{
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
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				String id = IdUtil.computeNodeId(nodePart);
				DiagramNodeLayout diagramNode = this.layoutModel.getDiagramNodesLayout().addNewElement();
				diagramNode.setNodeId(id);
				Bounds nodeBounds = nodePart.getNodeBounds();
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
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				String id = IdUtil.computeConnectionId(connPart);
				DiagramConnectionLayout conn = null;
				if (connPart.getConnectionBendpoints().size() > 0)
				{					
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
			
		};
		this.diagramPart.addListener(this.diagramPartListener);
	}
	
	
}
