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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramEmbeddedConnectionPart extends DiagramConnectionPart 
{
	private IDiagramConnectionBindingDef localDefinition;
	private IModelElement srcNodeModel;
	private IModelElement endpointModel;
	private ModelPath endpointPath;
	private FunctionResult endpointFunctionResult;
	private IDiagramConnectionEndpointBindingDef endpointDef;
	
	public DiagramEmbeddedConnectionPart(DiagramEmbeddedConnectionTemplate connTemplate,
			IModelElement srcNodeModel, ModelPath endpointPath)
	{
		this.connectionTemplate = connTemplate;
		this.srcNodeModel = srcNodeModel;
		this.endpointPath = endpointPath;
	}
	
    @Override
    protected void init()
    {        
        this.localDefinition = (IDiagramConnectionBindingDef)super.definition;
        this.modelElement = super.getModelElement();
        this.labelFunctionResult = initExpression
        ( 
        	this.modelElement,
        	this.localDefinition.getLabel().element().getText(), 
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
            this.localDefinition.getInstanceId(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );        

        this.endpointDef = this.localDefinition.getEndpoint2().element();
        this.endpointModel = resolveEndpoint(this.modelElement, this.endpointPath);
        if (this.endpointModel != null)
        {
	        this.endpointFunctionResult = initExpression
	        (
	        	this.endpointModel, 
	        	this.endpointDef.getValue(), 
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
    
    @Override
    public IModelElement getEndpoint1()
    {
    	return this.srcNodeModel;
    }
    
    @Override
    public IModelElement getEndpoint2()
    {
    	return this.endpointModel;
    }

    @Override
	public void resetEndpoint1()
	{
	}
	
    @Override
	public void resetEndpoint2()
	{
		if (this.endpointFunctionResult != null)
		{
			Object value = this.endpointFunctionResult.value();
			String property = this.endpointDef.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}
        
    public DiagramNodePart getSourceNodePart()
    {
    	SapphireDiagramEditorPart diagramPart = (SapphireDiagramEditorPart)getParentPart();
    	return diagramPart.getDiagramNodePart(this.srcNodeModel);
    }
    
    @Override
    public void dispose()
    {
    	super.dispose();
    	if (this.endpointFunctionResult != null)
    	{
    		this.endpointFunctionResult.dispose();
    	}    	
    }
    
    @Override
    public void addModelListener()
    {
    	if (this.labelProperty != null)
    	{
	    	this.modelElement.addListener(this.modelPropertyListener, 
	    								this.labelProperty.getName());
    	}
    	this.modelElement.addListener(this.modelPropertyListener, 
    								this.endpointDef.getProperty().getContent());
    }
    
    @Override
    public void removeModelListener()
    {
    	if (this.labelProperty != null)
    	{
	    	this.modelElement.removeListener(this.modelPropertyListener, 
	    								this.labelProperty.getName());
    	}
    	this.modelElement.removeListener(this.modelPropertyListener, 
    								this.endpointDef.getProperty().getContent());
    }

    @Override
    protected void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final ModelProperty property = event.getProperty();
    	if (property.getName().equals(this.endpointDef.getProperty().getContent()))
    	{
			handleEndpointChange();
			notifyConnectionEndpointUpdate();
    	}    			
    }    
    
    private void handleEndpointChange()
    {
        this.endpointModel = resolveEndpoint(this.modelElement, this.endpointPath);
        if (this.endpointFunctionResult != null)
        {
        	this.endpointFunctionResult.dispose();
        	this.endpointFunctionResult = null;
        }
        if (this.endpointModel != null)
        {        	
	        this.endpointFunctionResult = initExpression
	        (
	        	this.endpointModel, 
	        	this.endpointDef.getValue(), 
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
