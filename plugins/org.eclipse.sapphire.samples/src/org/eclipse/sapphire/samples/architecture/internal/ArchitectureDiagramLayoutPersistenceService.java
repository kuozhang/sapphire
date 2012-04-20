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

package org.eclipse.sapphire.samples.architecture.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.samples.architecture.ConnectionBendpoint;
import org.eclipse.sapphire.samples.architecture.IArchitecture;
import org.eclipse.sapphire.samples.architecture.IComponent;
import org.eclipse.sapphire.samples.architecture.IComponentDependency;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionBendPoints;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeBounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.ConnectionHashKey;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceServiceListener;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ArchitectureDiagramLayoutPersistenceService extends DiagramLayoutPersistenceService 
{
	private IArchitecture architecture;
	private SapphireDiagramPartListener diagramPartListener;
	private ModelPropertyListener componentListener;	
	private ModelPropertyListener componentDependencyListener;
	private Map<String, DiagramNodeBounds> nodeBounds;
	private Map<ConnectionHashKey, DiagramConnectionBendPoints> connectionBendPoints;
	
	@Override
    protected void init()
    {
    	super.init();    	
    	this.architecture = (IArchitecture)getDiagramEditorPagePart().getLocalModelElement();
    	this.nodeBounds = new HashMap<String, DiagramNodeBounds>();
    	this.connectionBendPoints = new HashMap<ConnectionHashKey, DiagramConnectionBendPoints>();
    	load();
    	refreshPersistedPartsCache();
    	addDiagramPartListener();
    	addModelListeners();
    }

	private void setGridVisible(boolean visible)
	{
		this.architecture.setShowGrid(visible);
	}
	
	private void setGuidesVisible(boolean visible)
	{
		this.architecture.setShowGuides(visible);
	}

	private void read(DiagramNodePart nodePart)
	{
		
		IComponent component = (IComponent)nodePart.getLocalModelElement();
		if (!component.disposed())
		{
			String nodeId = IdUtil.computeNodeId(nodePart);
	    	if (this.nodeBounds.containsKey(nodeId) && this.nodeBounds.get(nodeId) != null)
	    	{
	    		nodePart.setNodeBounds(this.nodeBounds.get(nodeId)); 		
	    	}
		}
	}
	
	private void write(DiagramNodePart nodePart) 
	{
		IComponent component = (IComponent)nodePart.getLocalModelElement();
		if (!component.disposed())
		{
			if (isNodeLayoutChanged(nodePart))
			{				
				this.architecture.removeListener(this.componentListener, "/Components/Bounds/*");
				writeComponentBounds(component, nodePart);
				this.architecture.addListener(this.componentListener, "/Components/Bounds/*");
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
	}

	private void read(DiagramConnectionPart connPart)
	{
    	ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
    	if (this.connectionBendPoints.containsKey(key) && this.connectionBendPoints.get(key) != null)
    	{    		
    		connPart.resetBendpoints(this.connectionBendPoints.get(key));
    	}		
	}
	
	private void write(DiagramConnectionPart connPart) 
	{
		IComponentDependency dependency = (IComponentDependency)connPart.getLocalModelElement();
		if (!dependency.disposed())
		{
			if (isConnectionLayoutChanged(connPart))
			{
				this.architecture.removeListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
				writeDependencyBendPoints(dependency, connPart);
				this.architecture.addListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
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
	}
	
	@Override
	public void dispose()
	{
		if (this.diagramPartListener != null)
		{
			getDiagramEditorPagePart().removeListener(this.diagramPartListener);
		}
		if (this.componentListener != null)
		{
			this.architecture.removeListener(this.componentListener, "/Components/Bounds/*");
		}
		if (this.componentDependencyListener != null)
		{
			this.architecture.removeListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
		}
	}

	private void load()
	{
		getDiagramEditorPagePart().setGridVisible(this.architecture.isShowGrid().getContent());
		getDiagramEditorPagePart().setShowGuides(this.architecture.isShowGuides().getContent());
		
		ModelElementList<IComponent> components = this.architecture.getComponents();
		for (IComponent component : components)
		{
			DiagramNodePart nodePart = getDiagramEditorPagePart().getDiagramNodePart(component);
			if (nodePart != null)
			{
				String nodeId = IdUtil.computeNodeId(nodePart);
				DiagramNodeBounds bounds = null;
				if (component.getBounds().getX().getContent(false) != null && component.getBounds().getX().getContent(false) != null)
				{
					bounds = new DiagramNodeBounds(component.getBounds().getX().getContent(), 
							component.getBounds().getY().getContent(), 
							component.getBounds().getWidth().getContent(), 
							component.getBounds().getHeight().getContent(),
							false, false);
					nodePart.setNodeBounds(bounds);					
					
				}
				
				// load the embedded connection layout
				ModelElementList<IComponentDependency> dependencies = component.getDependencies();
				for (IComponentDependency dependency : dependencies)
				{
					DiagramConnectionPart connPart = getDiagramEditorPagePart().getDiagramConnectionPart(dependency);
					if (connPart != null)
					{
						String connId = IdUtil.computeConnectionId(connPart);
						ConnectionHashKey connKey = ConnectionHashKey.createKey(nodeId, connId);
						List<Point> pts = new ArrayList<Point>();
						
						ModelElementList<ConnectionBendpoint> bendpoints = dependency.getConnectionBendpoints();
						if (bendpoints.size() > 0)
						{
							int index = 0;
							for (ConnectionBendpoint bendpoint : bendpoints)
							{
								connPart.addBendpoint(index++, bendpoint.getX().getContent(), 
										bendpoint.getY().getContent());
								pts.add(new Point(bendpoint.getX().getContent(), bendpoint.getY().getContent()));
							}							
						}
					}
				}				
			}
		}		
	}
	
	private void handleNodeLayoutChange(IComponent component)
	{
		DiagramNodePart nodePart = getDiagramEditorPagePart().getDiagramNodePart(component);
		if (nodePart != null && component.getBounds().getX().getContent(false) != null && 
				component.getBounds().getY().getContent(false) != null)
		{
			DiagramNodeBounds nodeBounds = new DiagramNodeBounds(component.getBounds().getX().getContent(),
					component.getBounds().getY().getContent());
			nodePart.setNodeBounds(nodeBounds);
		}
	}
	
	private void handleConnectionBendpointChange(IComponentDependency componentDependency)
	{
		DiagramConnectionPart connPart = getDiagramEditorPagePart().getDiagramConnectionPart(componentDependency);
		if (connPart != null)
		{
			List<Point> bendpoints = new ArrayList<Point>();
			for (ConnectionBendpoint bendpoint : componentDependency.getConnectionBendpoints())
			{
				if (bendpoint.getX().getContent(false) != null && bendpoint.getY().getContent(false) != null)
				{
					bendpoints.add(new Point(bendpoint.getX().getContent(), bendpoint.getY().getContent()));
				}
			}
			connPart.resetBendpoints(bendpoints, false, false);
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
	}

	private void addDiagramPartListener()
	{
		this.diagramPartListener = new SapphireDiagramPartListener() 
		{
			@Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
				read((DiagramNodePart)event.getPart());
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
			public void handleDiagramSaveEvent(final DiagramPageEvent event)
			{
				doSave();
			}
						
		};
		getDiagramEditorPagePart().addListener(this.diagramPartListener);
	}
	
	private void addModelListeners()
	{
		this.componentListener = new ModelPropertyListener() 
		{
		    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
		    {
		    	if (event != null && event.getModelElement() != null)
		    	{
		    		IComponent component = event.getModelElement().nearest(IComponent.class);
		    		if (component != null)
		    		{
		    			handleNodeLayoutChange(component);
		    		}
		    	}
		    }
			
		};
		
		this.componentDependencyListener = new ModelPropertyListener()
		{
			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
			{
				if (event != null && event.getModelElement() != null)
				{
					IComponentDependency componentDependency = event.getModelElement().nearest(IComponentDependency.class);
					if (componentDependency != null)
					{
						handleConnectionBendpointChange(componentDependency);
					}
				}
			}
		};
		
		this.architecture.addListener(this.componentListener, "/Components/Bounds/*");
		this.architecture.addListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
		
	}
	
	private void writeComponentBounds(IComponent component, DiagramNodePart node)
	{
		Bounds bounds = node.getNodeBounds();
		component.getBounds().setX(bounds.getX());
		component.getBounds().setY(bounds.getY());
		if (node.canResizeShape())
		{
			if (bounds.getWidth() > 0)
				component.getBounds().setWidth(bounds.getWidth());
			if (bounds.getHeight() > 0)
				component.getBounds().setHeight(bounds.getHeight());
		}		
	}
	
	private void writeDependencyBendPoints(IComponentDependency dependency, DiagramConnectionPart connPart)
	{
		dependency.getConnectionBendpoints().clear();
		List<Point> bendpoints = connPart.getConnectionBendpoints().getBendPoints();
		for (Point bendpoint : bendpoints)
		{
			ConnectionBendpoint bpLayout = dependency.getConnectionBendpoints().insert();
			bpLayout.setX(bendpoint.getX());
			bpLayout.setY(bendpoint.getY());
		}
		
	}
		
	private void doSave()
	{
		refreshPersistedPartsCache();
		// For nodes that are placed using default node positions and connection bend points that
		// are calculated using connection router, we don't modify the corresponding model properties
		// in order to allow "revert" in source editor to work correctly.
		// So we need to do an explicit save of the node bounds and connection bend points here.
		this.architecture.removeListener(this.componentListener, "/Components/Bounds/*");
		this.architecture.removeListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
		
		for (DiagramNodePart nodePart : getDiagramEditorPagePart().getNodes())
		{
			IComponent component = (IComponent)nodePart.getLocalModelElement();
			if (!component.disposed())
			{
				writeComponentBounds(component, nodePart);
			}
		}
		
		for (DiagramConnectionPart connPart : getDiagramEditorPagePart().getConnections())
		{
			IComponentDependency dependency = (IComponentDependency)connPart.getLocalModelElement();
			if (!dependency.disposed())
			{
				writeDependencyBendPoints(dependency, connPart);
			}
		}
		
		this.architecture.addListener(this.componentListener, "/Components/Bounds/*");
		this.architecture.addListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
		
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
		// Detect whether the connection bendpoints have been changed.
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
		}
		else
		{
			changed = true;
		}
    	return changed;
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
	
}
