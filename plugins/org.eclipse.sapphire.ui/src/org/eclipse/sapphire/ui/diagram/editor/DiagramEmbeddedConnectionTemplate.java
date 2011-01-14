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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementDisposedEvent;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEmbeddedConnectionDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramEmbeddedConnectionTemplate extends DiagramConnectionTemplate
{
	private DiagramNodeTemplate nodeTemplate;
	private IDiagramEmbeddedConnectionDef definition;
	private Map<IModelElement, List<DiagramConnectionPart>> diagramConnectionMap;
	private ModelElementListener modelElementListener;
	private ModelElementList<IModelElement> srcNodeList;
		
    public DiagramEmbeddedConnectionTemplate(final SapphireDiagramEditorPart diagramEditor,
    										final DiagramNodeTemplate nodeTemplate, 
    										IDiagramEmbeddedConnectionDef definition, 
    										IModelElement modelElement)
    {
    	this.diagramEditor = diagramEditor;
    	this.nodeTemplate = nodeTemplate;
    	this.modelElement = modelElement;
    	this.definition = definition;
    	
        this.toolPaletteLabel = this.definition.getToolPaletteLabel().getContent();
        this.toolPaletteDesc = this.definition.getToolPaletteDesc().getContent();
        
        this.diagramConnectionMap = new HashMap<IModelElement, List<DiagramConnectionPart>>();
        
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        this.propertyName = this.definition.getProperty().getContent();
        
        this.modelPropertyListener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                handleModelPropertyChange( event );
            }
        };
        
        this.modelElementListener = new ModelElementListener() 
        {
        	@Override
        	public void handleElementDisposedEvent( final ModelElementDisposedEvent event )
        	{
        		handleModelElementDispose(event);
        	}
		};
		
    	this.srcNodeList = this.modelElement.read(nodeProperty);
        for (IModelElement srcNodeModel : this.srcNodeList)
        {
        	ModelProperty connProp = resolve(srcNodeModel, this.propertyName);
        	if (connProp instanceof ListProperty)
        	{
        		ListProperty connListProperty = (ListProperty)connProp;
        		ModelElementList<?> connList = srcNodeModel.read(connListProperty);
        		List<DiagramConnectionPart> partList = new ArrayList<DiagramConnectionPart>();
        		for (IModelElement endpointModel : connList)
        		{
        			DiagramEmbeddedConnectionPart connPart = 
        				new DiagramEmbeddedConnectionPart(this, srcNodeModel);
        			connPart.init(this.diagramEditor, endpointModel, this.definition, 
        					Collections.<String,String>emptyMap());
        			partList.add(connPart);
        		}
        		this.diagramConnectionMap.put(srcNodeModel, partList);
        		addModelListener(srcNodeModel);
        	}
        }            	
    }
        
    @Override
    public boolean canCreateNewConnection(DiagramNodePart srcNode, DiagramNodePart targetNode)
    {    	
    	IModelElement srcNodeModel = srcNode.getLocalModelElement();
    	
    	// check the source node type
    	ModelElementType srcNodeType = srcNodeModel.getModelElementType();
    	ModelElementType desiredsrcNodeType = this.nodeTemplate.getNodeType();
    	
    	if (!srcNodeType.equals(desiredsrcNodeType))
    	{
    		return false;
    	}
    	
    	// check the target node type
    	ModelElementType targetType = targetNode.getLocalModelElement().getModelElementType();
    	
    	ModelProperty connProp = resolve(srcNodeModel, this.propertyName);    	
        ModelElementType connType = connProp.getType();
        ModelProperty endpointProp = 
        	connType.getProperty(this.definition.getEndpoint().element().getProperty().getContent());
        if (endpointProp.getType() == null && endpointProp.hasAnnotation(Reference.class))
        {
        	return endpointProp.getAnnotation(Reference.class).target().isAssignableFrom(targetType.getModelElementClass());
        }
    	return false;
    }
        
    @Override
    public DiagramEmbeddedConnectionPart createNewDiagramConnection(DiagramNodePart srcNode, 
			DiagramNodePart targetNode)
    {
    	IModelElement srcNodeModel = srcNode.getLocalModelElement();
    	ModelProperty modelProperty = this.nodeTemplate.getModelProperty();
    	boolean found = false;
    	if (modelProperty instanceof ListProperty)
    	{
        	ListProperty listProperty = (ListProperty)modelProperty;
        	ModelElementList<?> list = this.modelElement.read(listProperty);
            for (IModelElement listEntryModelElement : list)
            {
            	if (listEntryModelElement.equals(srcNodeModel))
            	{
            		found = true;
            		break;
            	}            	
            }
    	}
        else if (modelProperty instanceof ElementProperty)
        {
        	ElementProperty elementProperty = (ElementProperty)modelProperty;
        	if (this.modelElement.read(elementProperty) != null)
        	{
	        	IModelElement localModelElement = this.modelElement.read(elementProperty).element();
	        	if (localModelElement == srcNodeModel)
	        	{
	        		found = true;
	        	}
        	}
        }
    	if (!found)
    	{
    		throw new RuntimeException( "Cannot locate the source node element");
    	}
    	
    	ModelProperty connProp = resolve(srcNodeModel, this.propertyName);
    	IModelElement newEndpoint = null;
    	if (connProp instanceof ListProperty)
    	{
    		ListProperty listProperty = (ListProperty)connProp;
    		ModelElementList<?> list = srcNodeModel.read(listProperty);
    		newEndpoint = list.addNewElement();    		
    	}
    	IDiagramConnectionEndpointDef endpointDef = this.definition.getEndpoint().element();
    	String endpointProperty = endpointDef.getProperty().getContent();
    	Value<Function> endpointFunc = endpointDef.getValue();
    	FunctionResult endpointFuncResult = getNodeReferenceFunction(targetNode, endpointFunc);
    	if (endpointFuncResult != null)
    	{
	    	setModelProperty(newEndpoint, endpointProperty, endpointFuncResult.value());
	    	endpointFuncResult.dispose();
    	}
    	
		DiagramEmbeddedConnectionPart connPart = 
			new DiagramEmbeddedConnectionPart(this, srcNodeModel);
		connPart.init(this.diagramEditor, newEndpoint, this.definition, 
				Collections.<String,String>emptyMap());               			
		addConnectionPart(srcNodeModel, connPart);                		
    	
    	return connPart;
    }
    
    @Override
    public DiagramConnectionPart createNewDiagramConnection(IModelElement connElement, IModelElement srcNodeElement)
    {
		DiagramEmbeddedConnectionPart connPart = 
			new DiagramEmbeddedConnectionPart(this, srcNodeElement);
		connPart.init(this.diagramEditor, connElement, this.definition, 
				Collections.<String,String>emptyMap());
		addConnectionPart(srcNodeElement, connPart);
    	return connPart;
    }
    
    public void addModelListener(IModelElement srcNodeModel)
    {
    	srcNodeModel.addListener(this.modelPropertyListener, this.propertyName);
    	srcNodeModel.addListener(this.modelElementListener);
    }
    
    public void removeModelListener(IModelElement srcNodeModel)
    {
    	srcNodeModel.removeListener(this.modelPropertyListener, this.propertyName);
    	srcNodeModel.removeListener(this.modelElementListener);
    } 
    
    @Override
    public void removeModelListener()
    {
    	for (IModelElement srcNodeModel : this.srcNodeList)
    	{
    		removeModelListener(srcNodeModel);
    	}
    }
    
    @Override
    public List<DiagramConnectionPart> getDiagramConnections(IModelElement srcNodeModel)
    {
    	List<DiagramConnectionPart> allConnParts = new ArrayList<DiagramConnectionPart>();
    	if (srcNodeModel != null)
    	{
    		if (this.diagramConnectionMap.get(srcNodeModel) != null)
    		{
    			allConnParts.addAll(this.diagramConnectionMap.get(srcNodeModel));
    		}
    	}
    	else
    	{    		
    		// return all the connection parts
    		Iterator<IModelElement> it = this.diagramConnectionMap.keySet().iterator();
    		while (it.hasNext())
    		{
    			allConnParts.addAll(this.diagramConnectionMap.get(it.next()));
    		}
    	}
		return allConnParts;
    }

    @Override
    protected void addConnectionPart(IModelElement srcNodeModel, DiagramConnectionPart connPart)
    {
    	List<DiagramConnectionPart> connParts = this.diagramConnectionMap.get(srcNodeModel);
    	if (connParts == null)
    	{
    		connParts = new ArrayList<DiagramConnectionPart>();
    		this.diagramConnectionMap.put(srcNodeModel, connParts);
    	}
    	connParts.add(connPart);
    }

    @Override
    protected void removeConnectionPart(IModelElement srcNodeModel, DiagramConnectionPart connPart)
    {
    	List<DiagramConnectionPart> connParts = this.diagramConnectionMap.get(srcNodeModel);
    	if (connParts != null)
    	{
    		connParts.remove(connPart);
    	}
    }
    
    private void handleModelElementDispose(final ModelElementDisposedEvent event)
    {
    	IModelElement element = event.getModelElement();
    	List<DiagramConnectionPart> connParts = getDiagramConnections(element);
		final IFeatureProvider fp = this.diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
		final Diagram diagram = this.diagramEditor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
		final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
    	
    	for (DiagramConnectionPart connPart : connParts)
    	{
			connPart.removeDiagramConnection(fp, ted);
			connPart.dispose();
    	}
    	connParts.clear();
    	removeModelListener(element);
    } 
    
}
