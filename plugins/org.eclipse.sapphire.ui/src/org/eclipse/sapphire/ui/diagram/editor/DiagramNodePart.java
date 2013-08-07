/******************************************************************************
 * Copyright (c) 2013 Oracle
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
 *    Ling Hao - [383924]  Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.PartVisibilityEvent;
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
	private Element modelElement;
	private FunctionResult idFunctionResult;
	private SapphireAction defaultAction;
	private SapphireActionHandler defaultActionHandler;
	private PropertiesViewContributionManager propertiesViewContributionManager; 
	private DiagramNodeBounds nodeBounds = new DiagramNodeBounds();
	private ShapePart shapePart;

	@Override
    protected void init()
    {
        super.init();
        this.nodeTemplate = (DiagramNodeTemplate)getParentPart();
        this.definition = (IDiagramNodeDef)super.definition;
        this.modelElement = getModelElement();
        
        this.idFunctionResult = initExpression
        ( 
            this.definition.getInstanceId().content(), 
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
                 
        // create shape part
        
        createShapePart();
                
        // attach various shape listeners
        this.shapePart.attach
        (
            new FilteredListener<TextChangeEvent>()
            {
                @Override
                protected void handleTypedEvent( TextChangeEvent event )
                {
                    notifyTextChange(event.getPart());
                }
            }
        );
        this.shapePart.attach
        (
            new FilteredListener<ShapeUpdateEvent>()
            {
                @Override
                protected void handleTypedEvent( ShapeUpdateEvent event )
                {
                    notifyShapeUpdate(event.getPart());
                }
            }
        );        
        this.shapePart.attach
        (
             new FilteredListener<PartVisibilityEvent>()
             {
                @Override
                protected void handleTypedEvent( final PartVisibilityEvent event )
                {
                	notifyShapeVisibility((ShapePart)event.part());
                }
             }
        );        
        this.shapePart.attach
        (
             new FilteredListener<ShapeReorderEvent>()
             {
                @Override
                protected void handleTypedEvent( final ShapeReorderEvent event )
                {
                	notifyShapeReorder((ShapeFactoryPart)(event.getPart()));
                }
             }
        );        		        
        this.shapePart.attach
        (
             new FilteredListener<ShapeAddEvent>()
             {
                @Override
                protected void handleTypedEvent( final ShapeAddEvent event )
                {
                	notifyShapeAdd(event.getPart());
                }
             }
        );        		        
        this.shapePart.attach
        (
             new FilteredListener<ShapeDeleteEvent>()
             {
                @Override
                protected void handleTypedEvent( final ShapeDeleteEvent event )
                {
                	notifyShapeDelete(event.getPart());
                }
             }
        );        		        
        
    }
    
    public DiagramNodeTemplate getDiagramNodeTemplate()
    {
        return this.nodeTemplate;
    }
    
    @Override
    public Element getLocalModelElement()
    {
        return this.modelElement;
    }    
         
    public ShapePart getShapePart()
    {
    	return this.shapePart;
    }
    
    public List<TextPart> getContainedTextParts()
    {    	
    	ShapePart shapePart = getShapePart();
    	if (shapePart instanceof TextPart)
    	{
    		return Collections.singletonList((TextPart)shapePart);
    	}
    	else
    	{
    		return ShapePart.getContainedShapeParts(shapePart, TextPart.class);
    	}
    }
    
    public List<ImagePart> getContainedImageParts()
    {    	
    	ShapePart shapePart = getShapePart();
    	if (shapePart instanceof ImagePart)
    	{
    		return Collections.singletonList((ImagePart)shapePart);
    	}
    	else
    	{
    		return ShapePart.getContainedShapeParts(shapePart, ImagePart.class);
    	}
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
        this.shapePart.dispose();
        
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

    private void notifyTextChange(ShapePart shapePart)
    {
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramShapeEvent nue = new DiagramShapeEvent(this, shapePart);
				((SapphireDiagramPartListener)listener).handleTextChangeEvent(nue);
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
        return this.definition.getId().content();
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
		return this.definition.isResizable().content();
	}
	
	public DiagramNodeBounds getNodeBounds()
	{
		DiagramNodeBounds bounds = new DiagramNodeBounds(this.nodeBounds);
		if (bounds.getWidth() < 0 && this.definition.getWidth().content() != null )
		{
			bounds.setWidth(this.definition.getWidth().content());
		}
		if (bounds.getHeight() < 0 && this.definition.getHeight().content() != null)
		{
			bounds.setHeight(this.definition.getHeight().content());
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
        
    private void createShapePart()
    {
    	ShapeDef shape = this.definition.getShape().content();
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
    	
    	if (definition.getSelectionPresentation() != null)
    	{
    		this.shapePart.setSelectionPresentation(definition.getSelectionPresentation());
    	}
        this.shapePart.init(this, this.modelElement, shape, Collections.<String,String>emptyMap());
                
    }
}
