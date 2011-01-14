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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.el.FailSafeFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionTemplate 
{
	protected SapphireDiagramEditorPart diagramEditor;
	private IDiagramConnectionDef definition;
	protected IModelElement modelElement;
	protected String propertyName;
	private ListProperty modelProperty;
	protected String toolPaletteLabel;
	protected String toolPaletteDesc;
	protected ModelPropertyListener modelPropertyListener;
	
	private List<DiagramConnectionPart> diagramConnections;
	
	public DiagramConnectionTemplate() {}
	
    public DiagramConnectionTemplate(final SapphireDiagramEditorPart diagramEditor, 
    		IDiagramConnectionDef definition, IModelElement modelElement)
    {
    	this.diagramEditor = diagramEditor;
    	this.modelElement = modelElement;
    	this.definition = definition;
    	
        this.toolPaletteLabel = this.definition.getToolPaletteLabel().getContent();
        this.toolPaletteDesc = this.definition.getToolPaletteDesc().getContent();
        
        this.diagramConnections = new ArrayList<DiagramConnectionPart>();
        
        this.propertyName = this.definition.getProperty().getContent();
        this.modelProperty = (ListProperty)resolve(this.modelElement, this.propertyName);
        	
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
        	DiagramConnectionPart connection = new DiagramConnectionPart(this);
        	connection.init(this.diagramEditor, listEntryModelElement, definition, 
        			Collections.<String,String>emptyMap());
        	addConnectionPart(null, connection);
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
    
    public List<DiagramConnectionPart> getDiagramConnections(IModelElement srcNodeModel)
    {
    	return this.diagramConnections;
    }
    
    public String getToolPaletteLabel()
    {
    	return this.toolPaletteLabel;
    }
    
    public String getToolPaletteDesc()
    {
    	return this.toolPaletteDesc;
    }
    
    public boolean canCreateNewConnection(DiagramNodePart srcNode, DiagramNodePart targetNode)
    {
    	boolean canCreate = false;
    	ModelElementType srcType = srcNode.getModelElement().getModelElementType();
    	ModelElementType targetType = targetNode.getModelElement().getModelElementType();
    	
        ModelElementType type = this.modelProperty.getType();
        ModelProperty prop1 = type.getProperty(this.definition.getEndpoint1().element().getProperty().getContent());
        if (prop1.getType() == null && prop1.hasAnnotation(Reference.class))
        {
        	canCreate = prop1.getAnnotation(Reference.class).target().isAssignableFrom(srcType.getModelElementClass());
        	if (!canCreate)
        		return false;
        }
        ModelProperty prop2 = type.getProperty(this.definition.getEndpoint2().element().getProperty().getContent());
        if (prop2.getType() == null && prop2.hasAnnotation(Reference.class))
        {
        	canCreate = prop2.getAnnotation(Reference.class).target().isAssignableFrom(targetType.getModelElementClass());
        }
    	return canCreate;
    }
    
    public void addModelListener()
    {
    	this.modelElement.addListener(this.modelPropertyListener, this.propertyName);
    }
    
    public void removeModelListener()
    {
    	this.modelElement.removeListener(this.modelPropertyListener, this.propertyName);
    }
    
    public SapphireDiagramEditorPart getDiagramEditor()
    {
    	return this.diagramEditor;
    }
    
    public DiagramConnectionPart createNewDiagramConnection(DiagramNodePart srcNode, 
    														DiagramNodePart targetNode)
    {
		ModelElementList<?> list = this.modelElement.read(this.modelProperty);
		IModelElement newElement = list.addNewElement();
		
    	IDiagramConnectionEndpointDef srcAnchorDef = this.definition.getEndpoint1().element();
    	String srcProperty = srcAnchorDef.getProperty().getContent();
    	Value<Function> srcFunc = srcAnchorDef.getValue();
    	FunctionResult srcFuncResult = getNodeReferenceFunction(srcNode, srcFunc);
    	if (srcFuncResult != null)
    	{
	    	setModelProperty(newElement, srcProperty, srcFuncResult.value());
	    	srcFuncResult.dispose();
    	}
    	
    	IDiagramConnectionEndpointDef targetAnchorDef = this.definition.getEndpoint2().element();
    	String targetProperty = targetAnchorDef.getProperty().getContent();
    	Value<Function> targetFunc = targetAnchorDef.getValue();;
    	FunctionResult targetFuncResult = getNodeReferenceFunction(targetNode, targetFunc);
    	if (targetFuncResult != null)
    	{
	    	setModelProperty(newElement, targetProperty, targetFuncResult.value());
	    	targetFuncResult.dispose();
    	}
    	
    	DiagramConnectionPart newConn = new DiagramConnectionPart(this);
    	newConn.init(this.diagramEditor, newElement, this.definition, 
    			Collections.<String,String>emptyMap());
    	addConnectionPart(null, newConn);
    	return newConn;
    }
    
    public DiagramConnectionPart createNewDiagramConnection(IModelElement connElement, IModelElement srcNodeElement)
    {
    	DiagramConnectionPart connPart = new DiagramConnectionPart(this);
    	connPart.init(this.diagramEditor, connElement, this.definition, 
    			Collections.<String,String>emptyMap());
    	addConnectionPart(null, connPart);
    	return connPart;
    }
    
    protected ModelProperty resolve(final IModelElement modelElement, 
    		String propertyName)
    {
    	if (propertyName != null)
    	{
	        final ModelElementType type = modelElement.getModelElementType();
	        final ModelProperty property = type.getProperty( propertyName );
	        if( property == null )
	        {
	            throw new RuntimeException( "Could not find property " + propertyName + " in " + type.getQualifiedName() );
	        }
	        return property;
    	}    
        return null;
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
    
    protected FunctionResult getNodeReferenceFunction(final DiagramNodePart nodePart,
    											final Value<Function> function)
    {
        Function f = null;
        FunctionResult fr = null;
        
        if( function != null )
        {
            f = function.getContent();
        }
        
        if( f != null )
        {
            
            f = FailSafeFunction.create( f, String.class );
            fr = f.evaluate( new ModelElementFunctionContext( nodePart.getLocalModelElement(), this.definition.adapt( LocalizationService.class ) ) );
        }
        return fr;
    }
    
    protected void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final IModelElement element = event.getModelElement();
    	final ModelProperty property = event.getProperty();
    	ModelElementList<?> newList = (ModelElementList<?>)element.read(property);
    	
    	if (newList.size() != getDiagramConnections(element).size())
    	{
    		List<DiagramConnectionPart> connParts = getDiagramConnections(element);
    		List<IModelElement> oldList = new ArrayList<IModelElement>(connParts.size());
    		for (DiagramConnectionPart connPart : connParts)
    		{
    			oldList.add(connPart.getLocalModelElement());
    		}
    		
    		final IFeatureProvider fp = this.diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
    		final Diagram diagram = this.diagramEditor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
			final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
    		
	    	if (newList.size() > oldList.size())
	    	{
	    		// new connections are added
	    		List<IModelElement> newConns = ListUtil.ListDiff(newList, oldList);
	    		for (IModelElement newConn : newConns)
	    		{	    			
	            	DiagramConnectionPart connPart = createNewDiagramConnection(newConn, element);
	            	connPart.addNewConnectionIfPossible(fp, ted, this.diagramEditor);
	    		}
	    	}
	    	else
	    	{
	    		// connections are deleted
	    		List<IModelElement> deletedConns = ListUtil.ListDiff(newList, oldList);
	    		for (IModelElement deletedConn : deletedConns)
	    		{
	    			DiagramConnectionPart connPart = getConnectionPart(element, deletedConn);
	    			if (connPart != null)
	    			{
	    				connPart.removeDiagramConnection(fp, ted);
		    			connPart.dispose();
		    			removeConnectionPart(element, connPart);
	    			}
	    		}
	    	}
    	}
    }
    
    protected void addConnectionPart(IModelElement srcNodeModel, DiagramConnectionPart connPart)
    {
    	this.diagramConnections.add(connPart);
    }
    
    protected void removeConnectionPart(IModelElement srcNodeModel, DiagramConnectionPart connPart)
    {
    	this.diagramConnections.remove(connPart);
    }
    
    protected DiagramConnectionPart getConnectionPart(IModelElement srcNodeModel, IModelElement connModel)
    {
    	List<DiagramConnectionPart> connParts = getDiagramConnections(srcNodeModel);
    	for (DiagramConnectionPart connPart : connParts)
    	{
    		if (connPart.getLocalModelElement().equals(connModel))
    		{
    			return connPart;
    		}
    	}
    	return null;
    }
    
    public void dispose()
    {
    	removeModelListener();
    	List<DiagramConnectionPart> connParts = getDiagramConnections(null);
    	for (DiagramConnectionPart connPart : connParts)
    	{
    		connPart.dispose();
    	}
    }
        
}
