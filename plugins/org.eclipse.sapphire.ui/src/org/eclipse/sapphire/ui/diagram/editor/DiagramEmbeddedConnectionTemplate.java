/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [378756] Convert ModelElementListener and ModelPropertyListener to common listener infrastructure
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DiagramEmbeddedConnectionTemplate extends DiagramConnectionTemplate
{
    private DiagramNodeTemplate nodeTemplate;
    private Map<Element, List<DiagramConnectionPart>> diagramConnectionMap;
    private Listener modelElementListener;
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
        this.connectionDef = (IDiagramConnectionDef)super.definition();
        
        this.diagramConnectionMap = new HashMap<Element, List<DiagramConnectionPart>>();
        
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        this.propertyName = this.bindingDef.getProperty().content();
        this.connListProperty = (ListProperty)nodeProperty.getType().property(this.propertyName);
        
        this.connPartListener = new ConnectionPartListener();
        
        this.templateListeners = new CopyOnWriteArraySet<DiagramConnectionTemplateListener>();
        
        this.modelPropertyListener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                handleModelPropertyChange( event );
            }
        };
        
        this.modelElementListener = new FilteredListener<ElementDisposeEvent>() 
        {
            @Override
            protected void handleTypedEvent( final ElementDisposeEvent event )
            {
                handleModelElementDispose(event);
            }
        };
        
        String endpointPropStr = this.bindingDef.getEndpoint2().content().getProperty().content();
        this.endpointPath = new ModelPath(endpointPropStr);
        
        ElementList<Element> srcNodeList = getModelElement().property(nodeProperty);
        for (Element srcNodeModel : srcNodeList)
        {
            PropertyDef connProp = srcNodeModel.property(this.propertyName).definition();
            if (connProp instanceof ListProperty)
            {
                ListProperty connListProperty = (ListProperty)connProp;
                ElementList<?> connList = srcNodeModel.property(connListProperty);
                for (Element endpointModel : connList)
                {
                	createNewConnectionPart(endpointModel, srcNodeModel);                	
                }                
                addModelListener(srcNodeModel);
            }
        }                
    }
    
    public void refreshConnections(Element srcNodeModel)
    {
    	PropertyDef connProp = srcNodeModel.property(this.propertyName).definition();
    	handleConnectionListChange(srcNodeModel, (ListProperty)connProp);
    }
    
    @Override
    public boolean canStartNewConnection(DiagramNodePart srcNode)
    {
        Element srcNodeModel = srcNode.getLocalModelElement();
        // check the source node type
        ElementType srcNodeType = srcNodeModel.type();
        ElementType desiredsrcNodeType = this.nodeTemplate.getNodeType();
        
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
        Element srcNodeModel = srcNode.getLocalModelElement();
        // check the target node type
        ElementType targetType = targetNode.getLocalModelElement().type();
        
        PropertyDef connProp = srcNodeModel.property(this.propertyName).definition();        
        ElementType connType = connProp.getType();
        PropertyDef endpointProp = 
            connType.property(this.bindingDef.getEndpoint2().content().getProperty().content());
        if (endpointProp.getType() == null && endpointProp.hasAnnotation(Reference.class))
        {
            return endpointProp.getAnnotation(Reference.class).target().isAssignableFrom(targetType.getModelElementClass());
        }
        return false;
    }
        
    @Override
    public void setSerializedEndpoint2(Element connModelElement, String endpoint2Value)
    {
        IDiagramConnectionEndpointBindingDef endpointDef = this.bindingDef.getEndpoint2().content();
        String endpointProperty = endpointDef.getProperty().content();

        setModelProperty(connModelElement, endpointProperty, endpoint2Value);
    }
    
    @Override
    public DiagramConnectionPart createNewDiagramConnection(DiagramNodePart srcNode, 
            DiagramNodePart targetNode)
    {
        Element srcNodeModel = srcNode.getLocalModelElement();
        PropertyDef modelProperty = this.nodeTemplate.getModelProperty();
        boolean found = false;
        if (modelProperty instanceof ListProperty)
        {
            ListProperty listProperty = (ListProperty)modelProperty;
            ElementList<?> list = getModelElement().property(listProperty);
            for (Element listEntryModelElement : list)
            {
                if (listEntryModelElement == srcNodeModel)
                {
                    found = true;
                    break;
                }                
            }
        }
        else if (modelProperty instanceof ElementProperty)
        {
            ElementProperty elementProperty = (ElementProperty)modelProperty;
            if (getModelElement().property(elementProperty) != null)
            {
                Element localModelElement = getModelElement().property(elementProperty).content();
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
        
        PropertyDef connProp = srcNodeModel.property(this.propertyName).definition();
        Element newEndpoint = null;
        if (connProp instanceof ListProperty)
        {
            ListProperty listProperty = (ListProperty)connProp;
            ElementList<?> list = srcNodeModel.property(listProperty);
            newEndpoint = list.insert();            
        }
        
        // Get serialized value of endpoint
        String endpointVal = getSerializedEndpoint2(targetNode);
        
        // Set the serialized value of endpoint
        setSerializedEndpoint2(newEndpoint, endpointVal);
        
        DiagramConnectionPart connPart = getConnectionPart(srcNodeModel, newEndpoint);
        if (connPart == null) 
        {
        	connPart = createNewConnectionPart(newEndpoint, srcNodeModel);
        }        
        return connPart;
    }
    
    @Override
    public DiagramConnectionPart createNewConnectionPart(Element connElement, Element srcNodeElement)
    {
        DiagramEmbeddedConnectionPart connPart = 
            new DiagramEmbeddedConnectionPart(this.bindingDef, srcNodeElement, this.endpointPath);
        connPart.init(this, connElement, this.connectionDef, Collections.<String,String>emptyMap());
        connPart.addListener(this.connPartListener);
        addConnectionPart(srcNodeElement, connPart);
        return connPart;
    }
        
    public void addModelListener(Element srcNodeModel)
    {
        srcNodeModel.attach(this.modelPropertyListener, this.propertyName);
        srcNodeModel.attach(this.modelElementListener);
    }
    
    public void removeModelListener(Element srcNodeModel)
    {
        srcNodeModel.detach(this.modelPropertyListener, this.propertyName);
        srcNodeModel.detach(this.modelElementListener);
    } 
    
    @Override
    public void addModelListener()
    {
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        ElementList<Element> srcNodeList = getModelElement().property(nodeProperty);
        for (Element srcNodeModel : srcNodeList)
        {
            addModelListener(srcNodeModel);
        }        
    }
    
    @Override
    public void removeModelListener()
    {
        ListProperty nodeProperty = (ListProperty)this.nodeTemplate.getModelProperty();
        ElementList<Element> srcNodeList = getModelElement().property(nodeProperty);
        for (Element srcNodeModel : srcNodeList)
        {
            removeModelListener(srcNodeModel);
        }
    }
    
    @Override
    public List<DiagramConnectionPart> getDiagramConnections(Element srcNodeModel)
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
        	
            for (List<DiagramConnectionPart> connParts : this.diagramConnectionMap.values())
            {
            	allConnParts.addAll(connParts);
            }
        }
        return allConnParts;
    }

    @Override
    public void addConnectionPart(Element srcNodeModel, DiagramConnectionPart connPart)
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
    public void disposeConnectionPart(DiagramConnectionPart connPart)
    {
    	connPart.dispose();
    	Collection<List<DiagramConnectionPart>> allConnParts = this.diagramConnectionMap.values();
    	for (List<DiagramConnectionPart> connParts : allConnParts)
    	{
    		if (connParts.contains(connPart))
    		{
    			connParts.remove(connPart);
    			break;
    		}
    	}
    }
    
    public void removeConnectionParts(Element srcNodeModel)
    {
    	List<DiagramConnectionPart> connParts = new ArrayList<DiagramConnectionPart>();
    	connParts.addAll(this.getDiagramConnections(srcNodeModel));
    	for (DiagramConnectionPart connPart : connParts)
    	{
            notifyConnectionDelete(new DiagramConnectionEvent(connPart));
            disposeConnectionPart(connPart);    		
    	}
    }
    
    private void handleModelElementDispose(final ElementDisposeEvent event)
    {
        Element element = event.element();
        List<DiagramConnectionPart> connParts = getDiagramConnections(null);
        
        for (DiagramConnectionPart connPart : connParts)
        {
            if (connPart.getEndpoint1() == element || connPart.getEndpoint2() == element)
            {
                notifyConnectionDelete(new DiagramConnectionEvent(connPart));
                disposeConnectionPart(connPart);
            }
        }
        removeModelListener(element);
    } 
    
}
