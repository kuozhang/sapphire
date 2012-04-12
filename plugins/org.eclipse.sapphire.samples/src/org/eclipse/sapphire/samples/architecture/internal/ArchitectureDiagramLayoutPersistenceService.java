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
import java.util.List;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.samples.architecture.ConnectionBendpoint;
import org.eclipse.sapphire.samples.architecture.IArchitecture;
import org.eclipse.sapphire.samples.architecture.IComponent;
import org.eclipse.sapphire.samples.architecture.IComponentDependency;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ArchitectureDiagramLayoutPersistenceService extends DiagramLayoutPersistenceService 
{
	private SapphireDiagramEditorPagePart diagramPart;
	private IArchitecture architecture;
	private SapphireDiagramPartListener diagramPartListener;
	private ModelPropertyListener componentListener;	
	private ModelPropertyListener componentDependencyListener;
	private boolean needFlushoutLayout = false;
	
	@Override
    protected void init()
    {
    	super.init();    	
    	this.diagramPart = (SapphireDiagramEditorPagePart)context().find(ISapphirePart.class);
    	this.architecture = (IArchitecture)this.diagramPart.getLocalModelElement();
    	load();
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

	public Bounds read(DiagramNodePart nodePart)
	{
		
		IComponent component = (IComponent)nodePart.getLocalModelElement();
		if (!component.disposed())
		{
			if (isNodePersisted(nodePart))
			{
				if (component.getBounds().getX().getContent(false) != null && component.getBounds().getY().getContent() != null)
				{
					Bounds bounds = new Bounds(component.getBounds().getX().getContent(),
							component.getBounds().getY().getContent(),
							component.getBounds().getWidth().getContent(),
							component.getBounds().getHeight().getContent());
					return bounds;
				}
			}
		}
		return new Bounds();
	}
	
	private void write(DiagramNodePart nodePart) 
	{
		IComponent component = (IComponent)nodePart.getLocalModelElement();
		if (!component.disposed())
		{
			this.architecture.removeListener(this.componentListener, "/Components/Bounds/*");
			Bounds bounds = nodePart.getNodeBounds();
			component.getBounds().setX(bounds.getX());
			component.getBounds().setY(bounds.getY());
			if (nodePart.canResizeShape())
			{
				if (bounds.getWidth() > 0)
					component.getBounds().setWidth(bounds.getWidth());
				if (bounds.getHeight() > 0)
					component.getBounds().setHeight(bounds.getHeight());
			}
			this.architecture.addListener(this.componentListener, "/Components/Bounds/*");
		}
	}

	public List<Point> read(DiagramConnectionPart connectionPart)
	{
		List<Point> bendPoints = new ArrayList<Point>();
		IComponentDependency dependency = (IComponentDependency)connectionPart.getLocalModelElement();
		if (!dependency.disposed())
		{
			if (isConnectionPersisted(connectionPart))
			{
				if (dependency.getConnectionBendpoints().size() != 0 )
				{
					for (ConnectionBendpoint bendPoint : dependency.getConnectionBendpoints())
					{
						bendPoints.add(new Point(bendPoint.getX().getContent(), bendPoint.getY().getContent()));
					}					
				}
			}
		}
		return bendPoints;
	}
	
	public Point readConnectionLabelPosition(DiagramConnectionPart connectionPart)
	{
		// Architecture sample's dependency connections don't have labels
		return null;
	}
	
	private void write(DiagramConnectionPart connectionPart) 
	{
		IComponentDependency dependency = (IComponentDependency)connectionPart.getLocalModelElement();
		if (!dependency.disposed())
		{
			// Detect whether the connection bendpoints have been changed.
			List<Point> bendpoints = connectionPart.getConnectionBendpoints();	
			ModelElementList<ConnectionBendpoint> oldBendpoints = dependency.getConnectionBendpoints();
			boolean changed = false;
			if (bendpoints.size() != oldBendpoints.size())
			{
				changed = true;
			}
			else
			{
				for (int i = 0; i < bendpoints.size(); i++)
				{
					Point newPt = bendpoints.get(i);
					ConnectionBendpoint oldPt = oldBendpoints.get(i);
					if (oldPt.getX().getContent() == null || 
							oldPt.getY().getContent() == null ||
							newPt.getX() != oldPt.getX().getContent() || 
							newPt.getY() != oldPt.getY().getContent())
					{
						changed = true;
						break;
					}
				}
			}
			if (changed)
			{
				this.architecture.removeListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
				dependency.getConnectionBendpoints().clear();
				for (Point bendpoint : bendpoints)
				{
					ConnectionBendpoint bpLayout = dependency.getConnectionBendpoints().addNewElement();
					bpLayout.setX(bendpoint.getX());
					bpLayout.setY(bendpoint.getY());
				}
				this.architecture.addListener(this.componentDependencyListener, "/Components/Dependencies/ConnectionBendpoints/*");
			}
		}
	}

	@Override
	public void dispose()
	{
		if (this.diagramPartListener != null)
		{
			this.diagramPart.removeListener(this.diagramPartListener);
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
		this.diagramPart.setGridVisible(this.architecture.isShowGrid().getContent());
		this.diagramPart.setShowGuides(this.architecture.isShowGuides().getContent());
		
		ModelElementList<IComponent> components = this.architecture.getComponents();
		for (IComponent component : components)
		{
			DiagramNodePart nodePart = this.diagramPart.getDiagramNodePart(component);
			if (nodePart != null)
			{
				nodePart.setNodeBounds(component.getBounds().getX().getContent(), 
						component.getBounds().getY().getContent(), 
						component.getBounds().getWidth().getContent(), 
						component.getBounds().getHeight().getContent());
				
				// load the embedded connection layout
				ModelElementList<IComponentDependency> dependencies = component.getDependencies();
				for (IComponentDependency dependency : dependencies)
				{
					ModelElementList<ConnectionBendpoint> bendpoints = dependency.getConnectionBendpoints();
					if (bendpoints.size() > 0)
					{
						DiagramConnectionPart connPart = this.diagramPart.getDiagramConnectionPart(dependency);
						if (connPart != null)
						{
							int index = 0;
							for (ConnectionBendpoint bendpoint : bendpoints)
							{
								connPart.addBendpoint(index++, bendpoint.getX().getContent(), 
										bendpoint.getY().getContent());
							}
						}
					}
				}				
			}
		}		
	}
	
	private void flushoutLayout()
	{
		// If auto layout is applied when opening the diagram and user has made edits to the diagram,
		// we need to flush out the node/connection layouts to the model
		ModelElementList<IComponent> components = this.architecture.getComponents();
		for (IComponent component : components)
		{
			if (component.getBounds().getX().getContent(false) == null || 
					component.getBounds().getY().getContent(false) == null ||
					component.getBounds().getWidth().getContent(false) == null ||
					component.getBounds().getHeight().getContent(false) == null)
			{
				DiagramNodePart nodePart = this.diagramPart.getDiagramNodePart(component);
				write(nodePart);
			}
			
			ModelElementList<IComponentDependency> dependencies = component.getDependencies();
			for (IComponentDependency dependency : dependencies)
			{
				DiagramConnectionPart connPart = this.diagramPart.getDiagramConnectionPart(dependency);
				if (connPart.getConnectionBendpoints().size() > 0 &&
						dependency.getConnectionBendpoints().size() == 0  )
				{
					write(connPart);
				}
			}
		}
		this.needFlushoutLayout = false;
		
	}
	
	
	private void handleNodeLayoutChange(IComponent component)
	{
		DiagramNodePart nodePart = this.diagramPart.getDiagramNodePart(component);
		if (nodePart != null)
		{
			nodePart.setNodePosition(component.getBounds().getX().getContent(), component.getBounds().getY().getContent());
		}
	}
	
	private void handleConnectionBendpointChange(IComponentDependency componentDependency)
	{
		DiagramConnectionPart connPart = this.diagramPart.getDiagramConnectionPart(componentDependency);
		if (connPart != null)
		{
			List<Point> bendpoints = new ArrayList<Point>();
			for (ConnectionBendpoint bendpoint : componentDependency.getConnectionBendpoints())
			{
				if (bendpoint.getX().getContent() != null && bendpoint.getY().getContent() != null)
				{
					bendpoints.add(new Point(bendpoint.getX().getContent(), bendpoint.getY().getContent()));
				}
			}
			connPart.resetBendpoints(bendpoints);
		}
	}
	
	private void addDiagramPartListener()
	{
		this.diagramPartListener = new SapphireDiagramPartListener() 
		{
			@Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
				DiagramNodePart nodePart = (DiagramNodePart)event.getPart();
				read(nodePart);
            }
			
			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {
				if (!event.isFromAutoLayout())
				{
					if (needFlushoutLayout)
					{
						flushoutLayout();
					}
					else
					{
						write((DiagramNodePart)event.getPart());
					}
				}
				else
				{
					needFlushoutLayout = true;
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
				handleDiagramSave();
			}
		};
		this.diagramPart.addListener(this.diagramPartListener);
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
	
	private void handleDiagramSave()
	{
		refreshPersistedPartsCache();
	}
}
