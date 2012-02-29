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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.samples.architecture.ConnectionBendpoint;
import org.eclipse.sapphire.samples.architecture.IArchitecture;
import org.eclipse.sapphire.samples.architecture.IComponent;
import org.eclipse.sapphire.samples.architecture.IComponentDependency;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class InPlaceLayoutPersistenceService extends DiagramLayoutPersistenceService 
{
	private SapphireDiagramEditorPagePart diagramPart;
	private IArchitecture architecture;
	private SapphireDiagramPartListener diagramPartListener;
	private ModelElementListener componentListener;
	private ModelPropertyListener componentDependencyListener;
	private boolean needFlushoutLayout = false;
	
	@Override
    protected void init()
    {
    	super.init();
    	this.architecture = (IArchitecture)context(IModelElement.class);
    	SapphireEditor sapphireEditor = context(IModelElement.class).adapt(SapphireEditor.class);
    	SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)sapphireEditor.getPage("Diagram");
    	this.diagramPart = diagramEditor.getPart();
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


	private void write(DiagramNodePart nodePart) 
	{
		IComponent component = (IComponent)nodePart.getLocalModelElement();
		Bounds bounds = nodePart.getNodeBounds();
		component.setX(bounds.getX());
		component.setY(bounds.getY());
		if (nodePart.canResizeShape())
		{
			if (bounds.getWidth() > 0)
				component.setWidth(bounds.getWidth());
			if (bounds.getHeight() > 0)
				component.setHeight(bounds.getHeight());
		}
	}

	private void write(DiagramConnectionPart connectionPart) 
	{
		IComponentDependency dependency = (IComponentDependency)connectionPart.getLocalModelElement();
		
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
			dependency.removeListener(this.componentDependencyListener, "ConnectionBendpoints");
			dependency.removeListener(this.componentDependencyListener, "ConnectionBendpoints/*");
			dependency.getConnectionBendpoints().clear();
			for (Point bendpoint : bendpoints)
			{
				ConnectionBendpoint bpLayout = dependency.getConnectionBendpoints().addNewElement();
				bpLayout.setX(bendpoint.getX());
				bpLayout.setY(bendpoint.getY());
			}
			dependency.addListener(componentDependencyListener, "ConnectionBendpoints");
			dependency.addListener(componentDependencyListener, "ConnectionBendpoints/*");			
		}
	}

	@Override
	public void dispose()
	{
		if (this.diagramPartListener != null)
		{
			this.diagramPart.addListener(this.diagramPartListener);
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
				nodePart.setNodeBounds(component.getX().getContent(), component.getY().getContent(), 
						component.getWidth().getContent(), component.getHeight().getContent());				
				
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
	
	private void save()
	{
		// If auto layout is applied when opening the diagram and user has made edits to the diagram,
		// we need to flush out the node/connection layouts to the model
		if (!needFlushoutLayout)
			return;
		
		ModelElementList<IComponent> components = this.architecture.getComponents();
		for (IComponent component : components)
		{
			if (component.getX().getContent(false) == null || 
					component.getY().getContent(false) == null ||
					component.getWidth().getContent(false) == null ||
					component.getHeight().getContent(false) == null)
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
	}
	
	private void handleNodeLayoutChange(IComponent component)
	{
		DiagramNodePart nodePart = this.diagramPart.getDiagramNodePart(component);
		if (nodePart == null)
		{
			throw new RuntimeException("DiagramNodePart is null");
		}
		nodePart.setNodePosition(component.getX().getContent(), component.getY().getContent());
	}
	
	private void handleConnectionBendpointChange(IComponentDependency componentDependency)
	{
		DiagramConnectionPart connPart = this.diagramPart.getDiagramConnectionPart(componentDependency);
		if (connPart == null)
		{
			throw new RuntimeException("DiagramConnectionPart is null");
		}
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
	
	private void addDiagramPartListener()
	{
		this.diagramPartListener = new SapphireDiagramPartListener() 
		{
			@Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
				DiagramNodePart nodePart = (DiagramNodePart)event.getPart();
				write(nodePart);
				nodePart.getLocalModelElement().addListener(componentListener);                
            }
			
			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {
				if (!event.isFromAutoLayout())
				{
					write((DiagramNodePart)event.getPart());
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
				connPart.getLocalModelElement().addListener(componentDependencyListener, "ConnectionBendpoints");
				connPart.getLocalModelElement().addListener(componentDependencyListener, "ConnectionBendpoints/*");
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
			
		    public void handleDiagramSaveEvent(final DiagramPageEvent event)
		    {
		    	save();
		    }			
			
		    		
		};
		this.diagramPart.addListener(this.diagramPartListener);
	}
	
	private void addModelListeners()
	{
		this.componentListener = new ModelElementListener() 
		{
		    public void propertyChanged( final ModelPropertyChangeEvent event )
		    {
		        String propertyName = event.getProperty().getName();
		        if (propertyName.equals(IComponent.PROP_X.getName()) ||
		        		propertyName.equals(IComponent.PROP_Y.getName()) ||
		        		propertyName.equals(IComponent.PROP_WIDTH.getName()) ||
		        		propertyName.equals(IComponent.PROP_HEIGHT.getName()))
		        {
		        	handleNodeLayoutChange((IComponent)event.getModelElement());
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
					handleConnectionBendpointChange(componentDependency);
				}
			}
		};
		
		ModelElementList<IComponent> components = this.architecture.getComponents();
		for (IComponent component : components)
		{
			component.addListener(this.componentListener);
			
			ModelElementList<IComponentDependency> dependencies = component.getDependencies();
			for (IComponentDependency dependency : dependencies)
			{
				dependency.addListener(this.componentDependencyListener, "ConnectionBendpoints");
				dependency.addListener(this.componentDependencyListener, "ConnectionBendpoints/*");
			}
		}
	}
}
