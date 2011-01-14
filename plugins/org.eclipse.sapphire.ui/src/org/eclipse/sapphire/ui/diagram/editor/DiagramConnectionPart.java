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

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
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
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionPart extends SapphirePart 
{
	protected DiagramConnectionTemplate connectionTemplate;
	private IDiagramConnectionDef localDefinition;
	protected IModelElement modelElement;
	private IDiagramConnectionEndpointDef endpoint1Def;
	private IDiagramConnectionEndpointDef endpoint2Def;
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
        
        this.localDefinition = (IDiagramConnectionDef)super.definition;
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
        this.endpoint1Model = processEndpoint(this.endpoint1Def);
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
		            	refreshEndpoint1();
		            }
	        	}
	        );
        }
        
        this.endpoint2Def = this.localDefinition.getEndpoint2().element();
        this.endpoint2Model = processEndpoint(this.endpoint2Def);
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
		            	refreshEndpoint2();
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
		final SapphireDiagramEditorPart diagramEditor = (SapphireDiagramEditorPart)getParentPart();
		final IFeatureProvider fp = 
						diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
		final PictogramElement pe = getConnection(fp, this);
		if (pe != null)
		{
			UpdateContext context = new UpdateContext(pe);
			fp.updateIfPossible(context);
		}		
    	
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
    
	public void refreshEndpoint1()
	{
		if (this.endpoint1FunctionResult != null)
		{
			Object value = this.endpoint1FunctionResult.value();
			String property = this.endpoint1Def.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}
	
	public void refreshEndpoint2()
	{
		if (this.endpoint2FunctionResult != null)
		{
			Object value = this.endpoint2FunctionResult.value();
			String property = this.endpoint2Def.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}

	protected IModelElement processEndpoint(IDiagramConnectionEndpointDef endpointDef)
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
        	SapphireDiagramEditorPart diagramEditor = (SapphireDiagramEditorPart)getParentPart();
    		final IFeatureProvider fp = diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
    		final Diagram diagram = diagramEditor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
    		final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);

    		removeDiagramConnection(fp, ted);
    		
    		boolean sourceChange = property.getName().equals(this.endpoint1Def.getProperty().getContent()) ? true : false;
    		handleEndpointChange(sourceChange);
			// add new connection to the diagram
    		addNewConnectionIfPossible(fp, ted, diagramEditor);
    	}
    }    
    
    private void handleEndpointChange(boolean sourceChange) 
    {
		if (sourceChange)
		{
			this.endpoint1Model = processEndpoint(this.endpoint1Def);
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
			            	refreshEndpoint1();
			            }
		        	}
		        );
	        }			
		}
		else
		{
			this.endpoint2Model = processEndpoint(this.endpoint2Def);
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
			            	refreshEndpoint2();
			            }
		        	}
		        );
	        }			
		}
    }
    
    protected void removeDiagramConnection(IFeatureProvider fp, TransactionalEditingDomain ted)
    {
		// remove the existing connection pe from the diagram
		if (this.getEndpoint1() != null && this.getEndpoint2() != null)
		{
			PictogramElement pe = getConnection(fp, this);
			if (pe != null)
			{
				final IRemoveContext rc = new RemoveContext(pe);
				final IRemoveFeature removeFeature = fp.getRemoveFeature(rc);
				if (removeFeature != null) 
				{
					ted.getCommandStack().execute(new RecordingCommand(ted) 
					{
						protected void doExecute() 
						{			    					
							removeFeature.remove(rc);
						}
					});
				}
			}
		}    	
    }
    
    protected void addNewConnectionIfPossible(IFeatureProvider fp, TransactionalEditingDomain ted,
    						SapphireDiagramEditorPart diagramEditor)
    {
		// add new connection to the diagram
		if (this.getEndpoint1() != null && this.getEndpoint2() != null)
		{
			DiagramNodePart srcNodePart = diagramEditor.getDiagramNodePart(this.getEndpoint1());
			DiagramNodePart targetNodePart = diagramEditor.getDiagramNodePart(this.getEndpoint2());
			ContainerShape srcNode = getContainerShape(fp, srcNodePart);
			ContainerShape targetNode = getContainerShape(fp, targetNodePart);
			final AddConnectionContext addContext = 
					new AddConnectionContext(srcNode.getAnchors().get(0), targetNode.getAnchors().get(0));
			addContext.setNewObject(this);
			final IAddFeature addFeature = fp.getAddFeature(addContext);
			if (addFeature != null) 
			{
				ted.getCommandStack().execute(new RecordingCommand(ted) 
				{
					protected void doExecute() 
					{			    					
						addFeature.add(addContext);
					}
				});
			}			
		}     	
    }
    
	protected Connection getConnection(IFeatureProvider fp, Object bo)
	{
		PictogramElement [] pictograms = fp.getAllPictogramElementsForBusinessObject(bo);
		for (PictogramElement pictogram : pictograms)
		{
			if (pictogram instanceof Connection)
			{
				return (Connection)pictogram;
			}
		}
		return null;
	}
    
	protected ContainerShape getContainerShape(IFeatureProvider fp, Object bo)
	{
		ContainerShape containerShape = null;
		PictogramElement [] pictograms = fp.getAllPictogramElementsForBusinessObject(bo);
		for (PictogramElement pictogram : pictograms)
		{
			if (pictogram instanceof ContainerShape)
			{
				containerShape = (ContainerShape)pictogram;
				break;
			}
		}
		return containerShape;
	}
	
}
