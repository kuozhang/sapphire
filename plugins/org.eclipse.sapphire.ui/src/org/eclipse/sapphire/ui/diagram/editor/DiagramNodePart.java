/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 *    Konstantin Komissarchik - [342775] Support EL in MasterDetailsTreeNodeDef.ImagePath
 *    Ling Hao - [44319] Image specification for diagram parts inconsistent with the rest of sdef 
 *    Konstantin Komissarchik - [378756] Convert ModelElementListener and ModelPropertyListener to common listener infrastructure
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.modeling.ElementValidationEvent;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;
import org.eclipse.sapphire.ui.swt.renderer.SwtUtil;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DiagramNodePart 

    extends SapphirePart
    implements IPropertiesViewContributorPart
    
{
	
	private static final String DEFAULT_ACTION_ID = "Sapphire.Diagram.Node.Default";
	
	private DiagramNodeTemplate nodeTemplate;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;
	private FunctionResult idFunctionResult;
	private SapphireAction defaultAction;
	private SapphireActionHandler defaultActionHandler;
	private FilteredListener<ElementValidationEvent> elementValidationListener;
	private PropertiesViewContributionManager propertiesViewContributionManager; 
	private DiagramNodeBounds nodeBounds = new DiagramNodeBounds();
	private ShapePart shapePart;
	private SapphireDiagramPartListener shapeListener;

	@Override
    protected void init()
    {
        super.init();
        this.nodeTemplate = (DiagramNodeTemplate)getParentPart();
        this.definition = (IDiagramNodeDef)super.definition;
        this.modelElement = getModelElement();
        
        this.idFunctionResult = initExpression
        ( 
            this.definition.getInstanceId().getContent(), 
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
         
        createShapeListener();
        
        // create shape part
        
        createShapePart();
                
        this.elementValidationListener = new FilteredListener<ElementValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( final ElementValidationEvent event )
            {
                notifyNodeValidation();
            }
        };
        this.modelElement.attach(this.elementValidationListener);        
    }
    
    public DiagramNodeTemplate getDiagramNodeTemplate()
    {
        return this.nodeTemplate;
    }
    
    @Override
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
    }    
         
    public ShapePart getShapePart()
    {
    	return this.shapePart;
    }
    
    @Override
    public void render(SapphireRenderingContext context)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getActionContexts()
    {
        Set<String> contextSet = new HashSet<String>();
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_NODE);
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_NODE_HIDDEN);
        return contextSet;    	
    }
        
    public SapphireActionHandler getDefaultActionHandler()
    {        
    	if (this.defaultAction == null)
    	{
            // Default Action handler
            this.defaultAction = getAction(DEFAULT_ACTION_ID);
            this.defaultActionHandler = this.defaultAction.getFirstActiveHandler();    		
    	}
        return this.defaultActionHandler;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.idFunctionResult != null)
        {
            this.idFunctionResult.dispose();
        }
        
        this.modelElement.detach(this.elementValidationListener);
    }
    
    public void addShape(ShapePart shapePart)
    {
    	notifyShapeAdd(shapePart);
    }
    
    private void notifyShapeUpdate(ShapePart shapePart)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleShapeUpdateEvent(nue);
			}
		}    	
    }

    private void notifyShapeVisibility(ShapePart shapePart)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleShapeVisibilityEvent(nue);
			}
		}    	
    }
    
    private void notifyShapeValidation(ShapePart shapePart)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleShapeValidationEvent(nue);
			}
		}    	
    }

    private void notifyShapeAdd(ShapePart shapePart)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleShapeAddEvent(nue);
			}
		}    	    	
    }
    
    private void notifyShapeDelete(ShapePart shapePart)
    {    	
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleShapeDeleteEvent(nue);
			}
		}    	    	
    }
    
    private void notifyShapeReorder(ShapeFactoryPart shapeFactory)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapeFactory);
				((SapphireDiagramPartListener)listener).handleShapeReorderEvent(nue);
			}
		}    	
    }
    
    public String getNodeTypeId()
    {
        return this.definition.getId().getContent();
    }
    
    public String getInstanceId()
    {
        String id = null;
        
        if( this.idFunctionResult != null )
        {
            id = (String) this.idFunctionResult.value();
        }
                
        return id;		
	}
	
	public boolean canResizeShape()
	{
		return this.definition.isResizable().getContent();
	}
	
	public DiagramNodeBounds getNodeBounds()
	{
		DiagramNodeBounds bounds = new DiagramNodeBounds(this.nodeBounds);
		if (bounds.getWidth() < 0 && this.definition.getWidth().getContent() != null )
		{
			bounds.setWidth(this.definition.getWidth().getContent());
		}
		if (bounds.getHeight() < 0 && this.definition.getHeight().getContent() != null)
		{
			bounds.setHeight(this.definition.getHeight().getContent());
		}
		return bounds;
	}
	
	public void setNodeBounds(int x, int y)
	{
		setNodeBounds(new DiagramNodeBounds(x, y, -1, -1, false, false));
	}
	
	public void setNodeBounds(int x, int y, int width, int height)
	{
		setNodeBounds(new DiagramNodeBounds(x, y, width, height, false, false));
	}

	public void setNodeBounds(int x, int y, boolean autoLayout, boolean defaultPosition)
	{
		setNodeBounds(new DiagramNodeBounds(x, y, -1, -1, autoLayout, defaultPosition));
	}
	
	public void setNodeBounds(Bounds bounds)
	{
		DiagramNodeBounds nodeBounds = new DiagramNodeBounds(bounds);
		setNodeBounds(nodeBounds);
	}
	
	public void setNodeBounds(DiagramNodeBounds bounds)
	{
		// TODO handle node resizing events - rename move?
		if (!this.nodeBounds.equals(bounds))
		{
			this.nodeBounds.setX(bounds.getX());
			this.nodeBounds.setY(bounds.getY());
			this.nodeBounds.setWidth(bounds.getWidth());
			this.nodeBounds.setHeight(bounds.getHeight());
			this.nodeBounds.setAutoLayout(bounds.isAutoLayout());
			this.nodeBounds.setDefaultPosition(bounds.isDefaultPosition());
			notifyNodeMove();
		}			
	}

	
	private void notifyNodeValidation()
	{
        SwtUtil.runOnDisplayThread
        (
            new Runnable()
            {
                public void run()
                {
            		Set<SapphirePartListener> listeners = getListeners();
            		for(SapphirePartListener listener : listeners)
            		{
            			if (listener instanceof SapphireDiagramPartListener)
            			{
            				DiagramNodeEvent nue = new DiagramNodeEvent(DiagramNodePart.this);
            				((SapphireDiagramPartListener)listener).handleNodeValidationEvent(nue);
            			}
            		}
                }
            }
        );
		
	}
	
	private void notifyNodeMove()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent ne = new DiagramNodeEvent(this);
				((SapphireDiagramPartListener)listener).handleNodeMoveEvent(ne);
			}
		}		
	}
	
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        if( this.propertiesViewContributionManager == null )
        {
            this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, getLocalModelElement() );
        }
        
        return this.propertiesViewContributionManager.getPropertiesViewContribution();
    }
    
    private void createShapeListener()
    {
    	this.shapeListener = new SapphireDiagramPartListener()
    	{
    	    public void handleShapeVisibilityEvent(final DiagramShapeEvent event)
    	    {
    	    	notifyShapeVisibility(event.getShapePart());
    	    }

    	    public void handleShapeUpdateEvent(final DiagramShapeEvent event)
    	    {
    	    	notifyShapeUpdate(event.getShapePart());
    	    }
    		
    	    public void handleShapeValidationEvent(final DiagramShapeEvent event)
    	    {
    	    	notifyShapeValidation(event.getShapePart());
    	    }

    	    public void handleShapeAddEvent(final DiagramShapeEvent event)
    	    {
    	        notifyShapeAdd(event.getShapePart());
    	    }

    	    public void handleShapeReorderEvent(final DiagramShapeEvent event)
    	    {
    	        notifyShapeReorder((ShapeFactoryPart)event.getShapePart());
    	    }

    	    public void handleShapeDeleteEvent(final DiagramShapeEvent event)
    	    {
    	        notifyShapeDelete(event.getShapePart());
    	    }
    	    
    	};
    }
    
    private void createShapePart()
    {
    	ShapeDef shape = this.definition.getShape().element();
    	if (shape instanceof TextDef)
    	{
	        this.shapePart = new TextPart();
    	}
    	else if (shape instanceof ImageDef)
    	{
    		this.shapePart = new ImagePart();
    	}
    	else if (shape instanceof RectangleDef)
    	{
    		this.shapePart = new RectanglePart();
    	}
        this.shapePart.init(this, this.modelElement, shape, Collections.<String,String>emptyMap());
        
        this.shapePart.addListener(this.shapeListener);
    }
}
