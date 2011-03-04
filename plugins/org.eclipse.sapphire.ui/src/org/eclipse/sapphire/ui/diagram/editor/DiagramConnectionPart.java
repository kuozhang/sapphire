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

import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionPart extends SapphirePart 
{
	protected DiagramConnectionTemplate connectionTemplate;
	private IDiagramConnectionBindingDef localDefinition;
	protected IModelElement modelElement;
	private IDiagramConnectionEndpointBindingDef endpoint1Def;
	private IDiagramConnectionEndpointBindingDef endpoint2Def;
	private IModelElement endpoint1Model;
	private IModelElement endpoint2Model;
	protected FunctionResult labelFunctionResult;
	protected ValueProperty labelProperty;
	protected FunctionResult idFunctionResult;
	private FunctionResult endpoint1FunctionResult;
	private FunctionResult endpoint2FunctionResult;
	protected ModelPropertyListener modelPropertyListener;
	
	public DiagramConnectionPart() {}
	
	public DiagramConnectionPart(DiagramConnectionTemplate connectionTemplate)
	{
		this.connectionTemplate = connectionTemplate;
	}
	
    @Override
    protected void init()
    {
        super.init();
        
        this.localDefinition = (IDiagramConnectionBindingDef)super.definition;
        this.modelElement = getModelElement();
        this.labelFunctionResult = initExpression
        ( 
        	this.modelElement,
        	this.localDefinition.getLabel().element().getContent(), 
            new Runnable()
            {
                public void run()
                {
                	refreshLabel();
                }
            }
        );
        
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, 
        		this.labelFunctionResult);
        
        this.idFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.localDefinition.getInstanceId(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
        
        this.endpoint1Def = this.localDefinition.getEndpoint1().element();        
        this.endpoint1Model = resolveEndpoint(this.endpoint1Def);
        if (this.endpoint1Model != null)
        {
	        this.endpoint1FunctionResult = initExpression
	        (
	        	this.endpoint1Model, 
	        	this.endpoint1Def.getValue(), 
	            new Runnable()
	        	{
		            public void run()
		            {
		            }
	        	}
	        );
        }
        
        this.endpoint2Def = this.localDefinition.getEndpoint2().element();
        this.endpoint2Model = resolveEndpoint(this.endpoint2Def);
        if (this.endpoint2Model != null)
        {
	        this.endpoint2FunctionResult = initExpression
	        (
	        	this.endpoint2Model, 
	        	this.endpoint2Def.getValue(), 
	            new Runnable()
	        	{
		            public void run()
		            {
		            }
	        	}
	        );
        }        
        
        // Add model property listener
        this.modelPropertyListener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                handleModelPropertyChange( event );
            }
        };
        addModelListener();        
    }
    
    public DiagramConnectionTemplate getDiagramConnectionTemplate()
    {
    	return this.connectionTemplate;
    }
    
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
    }    
    
    public IModelElement getEndpoint1()
    {
    	return this.endpoint1Model;
    }
    
    public IModelElement getEndpoint2()
    {
    	return this.endpoint2Model;
    }

    public boolean canEditLabel()
    {
    	return this.labelProperty != null;
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
    	notifyConnectionUpdate();
    }
    
    public String getInstanceId()
	{
        String id = null;
        
        if( this.idFunctionResult != null )
        {
            id = (String) this.idFunctionResult.value();
        }
        
        if( id == null )
        {
            id = "#null#";
        }
        
        return id;
	}

    @Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
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
        if (this.endpoint1FunctionResult != null)
        {
        	this.endpoint1FunctionResult.dispose();
        }
        if (this.endpoint2FunctionResult != null)
        {
        	this.endpoint2FunctionResult.dispose();
        }
    }
    
	public void resetEndpoint1()
	{
		if (this.endpoint1FunctionResult != null)
		{
			Object value = this.endpoint1FunctionResult.value();
			String property = this.endpoint1Def.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}
	
	public void resetEndpoint2()
	{
		if (this.endpoint2FunctionResult != null)
		{
			Object value = this.endpoint2FunctionResult.value();
			String property = this.endpoint2Def.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}

	protected IModelElement resolveEndpoint(IDiagramConnectionEndpointBindingDef endpointDef)
	{
		String propertyName = endpointDef.getProperty().getContent();
		ModelProperty modelProperty = resolve(this.modelElement, propertyName);
        if (!(modelProperty instanceof ValueProperty))
        {
        	throw new RuntimeException( "Property " + propertyName + " not a ValueProperty");
        }
        ValueProperty property = (ValueProperty)modelProperty;
		Value<?> valObj = this.modelElement.read(property);
		if (!(valObj instanceof ReferenceValue))
		{
			throw new RuntimeException( "Property " + propertyName + " value not a reference");
		}
		ReferenceValue<?> refVal = (ReferenceValue<?>)valObj;
		Object targetObj = refVal.resolve();
		return (IModelElement)targetObj;
	}
	
    protected void setModelProperty(final IModelElement modelElement, 
			String propertyName, Object value)
	{
		if (propertyName != null)
		{
			final ModelElementType type = modelElement.getModelElementType();
			final ModelProperty property = type.getProperty( propertyName );
			if( property == null )
			{
				throw new RuntimeException( "Could not find property " + propertyName + " in " + type.getQualifiedName() );
			}
			if (!(property instanceof ValueProperty))
			{
				throw new RuntimeException( "Property " + propertyName + " not a ValueProperty");
			}
		
			modelElement.write((ValueProperty)property, value);
		}    	
	}
	
    public void addModelListener()
    {
    	this.modelElement.addListener(this.modelPropertyListener, 
    								this.endpoint1Def.getProperty().getContent());
    	this.modelElement.addListener(this.modelPropertyListener, 
									this.endpoint2Def.getProperty().getContent());
    }
    
    public void removeModelListener()
    {
    	this.modelElement.removeListener(this.modelPropertyListener, 
    								this.endpoint1Def.getProperty().getContent());
    	this.modelElement.removeListener(this.modelPropertyListener, 
									this.endpoint2Def.getProperty().getContent());
    }
    
    protected void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final ModelProperty property = event.getProperty();
    	    	
    	if (property.getName().equals(this.endpoint1Def.getProperty().getContent()) || 
    			property.getName().equals(this.endpoint2Def.getProperty().getContent()))
    	{
    		boolean sourceChange = property.getName().equals(this.endpoint1Def.getProperty().getContent()) ? true : false;
    		handleEndpointChange(sourceChange);
    		notifyConnectionEndpointUpdate();
    	}
    }    
    
    private void handleEndpointChange(boolean sourceChange) 
    {
		if (sourceChange)
		{
			this.endpoint1Model = resolveEndpoint(this.endpoint1Def);
			if (this.endpoint1FunctionResult != null)
			{
				this.endpoint1FunctionResult.dispose();
				this.endpoint1FunctionResult = null;
			}
	        if (this.endpoint1Model != null)
	        {
		        this.endpoint1FunctionResult = initExpression
		        (
		        	this.endpoint1Model, 
		        	this.endpoint1Def.getValue(), 
		            new Runnable()
		        	{
			            public void run()
			            {
			            }
		        	}
		        );
	        }			
		}
		else
		{
			this.endpoint2Model = resolveEndpoint(this.endpoint2Def);
			if (this.endpoint2FunctionResult != null)
			{
				this.endpoint2FunctionResult.dispose();
				this.endpoint2FunctionResult = null;
			}
	        if (this.endpoint2Model != null)
	        {
		        this.endpoint2FunctionResult = initExpression
		        (
		        	this.endpoint2Model, 
		        	this.endpoint2Def.getValue(), 
		            new Runnable()
		        	{
			            public void run()
			            {
			            }
		        	}
		        );
	        }			
		}
    }
        
	protected void notifyConnectionUpdate()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(this);
				((SapphireDiagramPartListener)listener).handleConnectionUpdateEvent(cue);
			}
		}
	}
	
	protected void notifyConnectionEndpointUpdate()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(this);
				((SapphireDiagramPartListener)listener).handleConnectionEndpointEvent(cue);
			}
		}
	}
}
