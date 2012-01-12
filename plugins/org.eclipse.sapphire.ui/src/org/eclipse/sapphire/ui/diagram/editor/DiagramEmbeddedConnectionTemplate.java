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
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementDisposedEvent;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramEmbeddedConnectionTemplate extends DiagramConnectionTemplate
{
    private DiagramNodeTemplate nodeTemplate;
    private Map<IModelElement, List<DiagramConnectionPart>> diagramConnectionMap;
    private ModelElementListener modelElementListener;
    private ModelPath endpointPath;
        
    public DiagramEmbeddedConnectionTemplate(IDiagramExplicitConnectionBindingDef connBindingDef)
    {
        super(connBindingDef);
    }
    
    @Override
    public void init()
    {
        this.nodeTemplate = (DiagramNodeTemplate)getParentPart();
        this.diagramEditor = this.nodeTemplate.getDiagramEditorPart();
        this.modelElement = getModelElement();
        this.definition = (IDiagramConnectionDef)super.getDefinition();
        
        this.diagramConnectionMap = new HashMap<IModelElement, List<DiagramConnectionPart>>();
        
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        this.propertyName = this.bindingDef.getProperty().getContent();
        this.connListProperty = (ListProperty)nodeProperty.getType().getProperty(this.propertyName);
        
        this.connPartListener = new ConnectionPartListener();
        
        this.templateListeners = new CopyOnWriteArraySet<Listener>();
        
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
        
        String endpointPropStr = this.bindingDef.getEndpoint2().element().getProperty().getContent();
        this.endpointPath = new ModelPath(endpointPropStr);
        
        ModelElementList<IModelElement> srcNodeList = this.modelElement.read(nodeProperty);
        for (IModelElement srcNodeModel : srcNodeList)
        {
            ModelProperty connProp = ModelUtil.resolve(srcNodeModel, this.propertyName);
            if (connProp instanceof ListProperty)
            {
                ListProperty connListProperty = (ListProperty)connProp;
                ModelElementList<?> connList = srcNodeModel.read(connListProperty);
                for (IModelElement endpointModel : connList)
                {
                    createNewConnectionPart(endpointModel, srcNodeModel);
                }                
                addModelListener(srcNodeModel);
            }
        }                
    }
    
    public void refreshConnections(IModelElement srcNodeModel)
    {
    	ModelProperty connProp = ModelUtil.resolve(srcNodeModel, this.propertyName);
    	srcNodeModel.notifyPropertyChangeListeners(connProp);
    }
    
    @Override
    public boolean canStartNewConnection(DiagramNodePart srcNode)
    {
        IModelElement srcNodeModel = srcNode.getLocalModelElement();
        // check the source node type
        ModelElementType srcNodeType = srcNodeModel.getModelElementType();
        ModelElementType desiredsrcNodeType = this.nodeTemplate.getNodeType();
        
        if (!srcNodeType.equals(desiredsrcNodeType))
        {
            return false;
        }
        return true;
    }
        
    @Override
    public boolean canCreateNewConnection(DiagramNodePart srcNode, DiagramNodePart targetNode)
    {    
        if (!canStartNewConnection(srcNode))
        {
            return false;
        }
        IModelElement srcNodeModel = srcNode.getLocalModelElement();
        // check the target node type
        ModelElementType targetType = targetNode.getLocalModelElement().getModelElementType();
        
        ModelProperty connProp = ModelUtil.resolve(srcNodeModel, this.propertyName);        
        ModelElementType connType = connProp.getType();
        ModelProperty endpointProp = 
            connType.getProperty(this.bindingDef.getEndpoint2().element().getProperty().getContent());
        if (endpointProp.getType() == null && endpointProp.hasAnnotation(Reference.class))
        {
            return endpointProp.getAnnotation(Reference.class).target().isAssignableFrom(targetType.getModelElementClass());
        }
        return false;
    }
        
    @Override
    public void setSerializedEndpoint2(IModelElement connModelElement, String endpoint2Value)
    {
        IDiagramConnectionEndpointBindingDef endpointDef = this.bindingDef.getEndpoint2().element();
        String endpointProperty = endpointDef.getProperty().getContent();

        setModelProperty(connModelElement, endpointProperty, endpoint2Value);
    }
    
    @Override
    public DiagramConnectionPart createNewDiagramConnection(DiagramNodePart srcNode, 
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
        
        ModelProperty connProp = ModelUtil.resolve(srcNodeModel, this.propertyName);
        IModelElement newEndpoint = null;
        if (connProp instanceof ListProperty)
        {
            ListProperty listProperty = (ListProperty)connProp;
            ModelElementList<?> list = srcNodeModel.read(listProperty);
            newEndpoint = list.addNewElement();            
        }
        
        // Get serialized value of endpoint
        String endpointVal = getSerializedEndpoint2(targetNode);
        
        // Set the serialized value of endpoint
        setSerializedEndpoint2(newEndpoint, endpointVal);
        
        DiagramConnectionPart connPart = getConnectionPart(srcNodeModel, newEndpoint);
        if (connPart == null) 
        {
        	connPart = createNewConnectionPart(newEndpoint, null);
        }        
        return connPart;
    }
    
    @Override
    public DiagramConnectionPart createNewConnectionPart(IModelElement connElement, IModelElement srcNodeElement)
    {
        DiagramEmbeddedConnectionPart connPart = 
            new DiagramEmbeddedConnectionPart(this.bindingDef, srcNodeElement, this.endpointPath);
        connPart.init(this, connElement, this.definition, Collections.<String,String>emptyMap());
        connPart.addListener(this.connPartListener);
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
    public void addModelListener()
    {
        
    }
    
    @Override
    public void removeModelListener()
    {
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        ModelElementList<IModelElement> srcNodeList = this.modelElement.read(nodeProperty);
        for (IModelElement srcNodeModel : srcNodeList)
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
        List<DiagramConnectionPart> connParts = getDiagramConnections(null);
        
        for (DiagramConnectionPart connPart : connParts)
        {
            if (connPart.getEndpoint1() == element || connPart.getEndpoint2() == element)
            {
                notifyConnectionDelete(connPart);
                connPart.dispose();
            }
        }
        connParts.clear();
        removeModelListener(element);
    } 
    
}
