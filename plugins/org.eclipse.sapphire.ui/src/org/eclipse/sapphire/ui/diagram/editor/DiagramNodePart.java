/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.Collections;
import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeProblemDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.ImagePlacement;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodePart extends SapphirePart 
{
	private DiagramNodeTemplate nodeTemplate;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;
	private FunctionResult labelFunctionResult;
	private FunctionResult idFunctionResult;
	private FunctionResult imageFunctionResult;
	private ValueProperty labelProperty;
	private SapphireAction defaultAction;
	private SapphireActionHandler defaultActionHandler;
	private ModelPropertyListener modelPropertyListener;
		
    @Override
    protected void init()
    {
        super.init();
        this.nodeTemplate = (DiagramNodeTemplate)getParentPart();
        this.definition = (IDiagramNodeDef)super.definition;
        this.modelElement = getModelElement();
        this.labelFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.definition.getLabel().element().getText(), 
            new Runnable()
            {
                public void run()
                {
                	refreshLabel();
                }
            }
        );
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, this.labelFunctionResult);
        
        this.idFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.definition.getInstanceId(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
        
        if (this.definition.getImage().element() != null)
        {
	        this.imageFunctionResult = initExpression
	        ( 
	        	this.modelElement,
	            this.definition.getImage().element().getId(), 
	            new Runnable()
	            {
	                public void run()
	                {
	                	refreshImage();
	                }
	            }
	        );
        }
        
        // Default Action handler
        this.defaultAction = getAction("Sapphire.Diagram.Node.Default");
        this.defaultActionHandler = this.defaultAction.getFirstActiveHandler();
        
        // Add model property listener. It listens to all the properties so that the
        // validation status change would trigger node update
        this.modelPropertyListener =  new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
            	notifyNodeUpdate();
            }
        };
        this.modelElement.addListener(this.modelPropertyListener, "*");
    }
    
    public DiagramNodeTemplate getDiagramNodeTemplate()
    {
    	return this.nodeTemplate;
    }
    
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
    }    
        
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}

    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_DIAGRAM_NODE );
    }
	
    public SapphireAction getDefaultAction()
    {
    	return this.defaultAction;
    }
    
	public SapphireActionHandler getDefaultActionHandler()
	{		
		return this.defaultActionHandler;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		if (this.labelFunctionResult != null)
		{
			this.labelFunctionResult.dispose();
		}
		
		if (this.idFunctionResult != null)
		{
			this.idFunctionResult.dispose();
		}
		this.modelElement.removeListener(this.modelPropertyListener, "*");
	}
	
	public String getLabel()
	{
        String label = null;
        
        if( this.labelFunctionResult != null )
        {
            label = (String) this.labelFunctionResult.value();
        }
        
        if( label == null )
        {
            label = "#null#";
        }
        
        return label;
	}

	public void setLabel(String newValue)
	{
		if (this.labelProperty != null)
		{
			this.modelElement.write(this.labelProperty, newValue);
		}
	}
	
	public void refreshLabel()
	{
		notifyNodeUpdate();
	}
	
	public void refreshImage()
	{
		notifyNodeUpdate();
	}
	
	public void refreshDecorator()
	{
		notifyNodeUpdate();
	}

	public boolean canEditLabel()
	{
		return this.labelProperty != null;
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
	
	public int getNodeWidth()
	{
		if (this.definition.getWidth().getContent() != null)
		{
			return this.definition.getWidth().getContent();
		}
		return 0;
	}
	
	public int getNodeHeight()
	{
		if (this.definition.getHeight().getContent() != null)
		{
			return this.definition.getHeight().getContent();
		}
		return 0;
	}

	public int getHorizontalSpacing()
	{
		if (this.definition.getHorizontalSpacing().getContent() != null)
		{
			return this.definition.getHorizontalSpacing().getContent();
		}
		return 0;
	}
	
	public int getVerticalSpacing()
	{
		if (this.definition.getVerticalSpacing().getContent() != null)
		{
			return this.definition.getVerticalSpacing().getContent();
		}
		return 0;
	}

	public String getImageId()
	{
        if( this.imageFunctionResult != null )
        {
            String idStr = (String) this.imageFunctionResult.value();
            return idStr;
        }
        return null;		
	}
	
	public ImagePlacement getImagePlacement()
	{
		if (this.definition.getImage().element() != null)
		{
			return this.definition.getImage().element().getPlacement().getContent();
		}
		return null;
	}
	
	public int getImageWidth()
	{
		if (this.definition.getImage().element() != null)
		{
			if (this.definition.getImage().element().getWidth().getContent() != null)
			{
				this.definition.getImage().element().getWidth().getContent();
			}
		}
		return 0;
	}
	
	public int getImageHeight()
	{
		if (this.definition.getImage().element() != null)
		{
			if (this.definition.getImage().element().getHeight().getContent() != null)
			{
				this.definition.getImage().element().getHeight().getContent();
			}
		}
		return 0;
	}

	public int getLabelWidth()
	{
		if (this.definition.getLabel().element().getWidth().getContent() != null)
		{
			return this.definition.getLabel().element().getWidth().getContent();
		}
		return 0;
	}

	public int getLabelHeight()
	{
		if (this.definition.getLabel().element().getHeight().getContent() != null)
		{
			return this.definition.getLabel().element().getHeight().getContent();
		}
		return 0;
	}
	
	public IDiagramNodeProblemDecoratorDef getErrorIndicatorDef()
	{
		return this.definition.getProblemDecorator();
	}
		
	private void notifyNodeUpdate()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent nue = new DiagramNodeEvent(this);
				((SapphireDiagramPartListener)listener).handleNodeUpdateEvent(nue);
			}
		}
	}
	
}
