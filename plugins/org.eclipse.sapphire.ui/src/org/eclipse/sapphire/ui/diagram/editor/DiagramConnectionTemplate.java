/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 *    Konstantin Komissarchik - [342775] Support EL in IMasterDetailsTreeNodeDef.ImagePath
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.el.FailSafeFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionTemplate extends SapphirePart
{
    public static abstract class Listener
    {
        public void handleConnectionUpdate(final DiagramConnectionPart connPart)
        {
        }  
        public void handleConnectionEndpointUpdate(final DiagramConnectionPart connPart)
        {        	
        }
        public void handleConnectionAdd(final DiagramConnectionPart connPart)
        {        	
        }
        public void handleConnectionDelete(final DiagramConnectionPart connPart)
        {        	
        }
    }
	
	protected SapphireDiagramEditorPagePart diagramEditor;
	protected IDiagramConnectionDef definition;
	protected IDiagramExplicitConnectionBindingDef bindingDef;
	protected IModelElement modelElement;
	protected String propertyName;
	private ListProperty modelProperty;
	protected ListProperty connListProperty;
	protected ModelPropertyListener modelPropertyListener;
	protected SapphireDiagramPartListener connPartListener;
	protected Set<Listener> templateListeners;
	// The model path specified in the sdef file
	private ModelPath originalEndpoint2Path;
	// The converted model path for the endpoints. The path is based on the connection element
	private ModelPath endpoint1Path;
	private ModelPath endpoint2Path;
	private ModelProperty endpoint1Property;
	private ModelProperty endpoint2Property;
	
	private List<DiagramConnectionPart> diagramConnections;
	
	public DiagramConnectionTemplate() {}
	
	public DiagramConnectionTemplate(IDiagramExplicitConnectionBindingDef bindingDef)
	{
		this.bindingDef = bindingDef;
	}
	
	@Override
    public void init()
    {
    	this.diagramEditor = (SapphireDiagramEditorPagePart)getParentPart();
    	this.modelElement = getModelElement();
    	this.definition = (IDiagramConnectionDef)super.definition;;
    	
        this.diagramConnections = new ArrayList<DiagramConnectionPart>();
        
        this.propertyName = this.bindingDef.getProperty().getContent();
        this.modelProperty = (ListProperty)ModelUtil.resolve(this.modelElement, this.propertyName);
        
        this.connPartListener = new SapphireDiagramPartListener() 
        {
        	@Override
	       	 public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
	       	 {
	       		 notifyConnectionUpdate((DiagramConnectionPart)event.getPart());
	       	 }  
        	 @Override
	       	 public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
	       	 {
	       		 notifyConnectionEndpointUpdate((DiagramConnectionPart)event.getPart());
	       	 }        	
        	
		};
		
		this.templateListeners = new CopyOnWriteArraySet<Listener>();
        	    	
    	String endpt1PropStr = this.bindingDef.getEndpoint1().element().getProperty().getContent();
    	String endpt2PropStr = this.bindingDef.getEndpoint2().element().getProperty().getContent();
    	this.originalEndpoint2Path = new ModelPath(endpt2PropStr);
    	
        ModelElementType type = this.modelProperty.getType();
        this.endpoint1Property = type.getProperty(endpt1PropStr);
        this.endpoint2Property = ModelUtil.resolve(type, this.originalEndpoint2Path);
                
        if (getConnectionType() == ConnectionType.OneToOne)
        {
    		this.endpoint1Path = new ModelPath(endpt1PropStr);
    		this.endpoint2Path = new ModelPath(endpt2PropStr);
    		this.connListProperty = this.modelProperty;
        }
        else 
        {
			ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
			ModelProperty prop = type.getProperty(head.getPropertyName());
			if (prop instanceof ListProperty)
			{
				this.endpoint1Path = new ModelPath("../" + endpt1PropStr);
				this.endpoint2Path = this.originalEndpoint2Path.tail();
				this.connListProperty = (ListProperty)prop;
			}
			else 
			{
				throw new RuntimeException("Invaid Model Path:" + this.originalEndpoint2Path);
			}
        }
        
        // initialize the connection parts
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
        	// check the type of connection: 1x1 connection versus 1xn connection        	
        	if (getConnectionType() == ConnectionType.OneToOne)
        	{    
        		// The connection model element specifies a 1x1 connection
        		createNewConnectionPart(listEntryModelElement, null);
        	}
        	else
        	{
    			ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
    			ModelProperty connProp = ModelUtil.resolve(listEntryModelElement, head.getPropertyName());
    			if (!(connProp instanceof ListProperty))
    			{
    				throw new RuntimeException("Expecting " + connProp.getName() + " to be a list property");
    			}
				// the connection is of type 1xn
				ModelElementList<?> connList = listEntryModelElement.read((ListProperty)connProp);        				
				for (IModelElement connElement : connList)
				{
					createNewConnectionPart(connElement, null);
				}
        	}
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
    
    public String getConnectionId()
    {
    	return this.bindingDef.getConnectionId().getContent();
    }
    
    public String getConnectionTypeId()
    {
    	return this.definition.getId().getContent();
    }
    
    public List<DiagramConnectionPart> getDiagramConnections(IModelElement connListParent)
    {
    	if (connListParent == null || getConnectionType() == ConnectionType.OneToOne)
    	{
    		return this.diagramConnections;
    	}
    	else
    	{
    		List<DiagramConnectionPart> connList = new ArrayList<DiagramConnectionPart>();
    		for (DiagramConnectionPart connPart : this.diagramConnections)
    		{
    			IModelElement connModel = connPart.getLocalModelElement();
    			if (connModel.parent().parent() == connListParent)
    			{
    				connList.add(connPart);
    			}
    		}
    		return connList;
    	}
    }
        
    public IModelElement getConnectionParentElement(IModelElement srcNodeModel)
    {
    	if (getConnectionType() == ConnectionType.OneToMany)
    	{
        	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
            for( IModelElement listEntryModelElement : list )
            {
				Object valObj = listEntryModelElement.read(this.endpoint1Property);
				if (valObj instanceof ReferenceValue)
				{
					ReferenceValue<?,?> reference = (ReferenceValue<?,?>)valObj;
					IModelElement model = (IModelElement)reference.resolve();
					if (model == null)
					{
						if (reference.getText() != null)
						{
							SapphireDiagramEditorPagePart diagramEditorPart = getDiagramEditor();
							DiagramNodePart targetNode = IdUtil.getNodePart(diagramEditorPart, reference.getText());
							if (targetNode != null)
							{
								model = targetNode.getLocalModelElement();
							}
						}						
					}
					if (srcNodeModel == model)
					{
						return listEntryModelElement;
					}
				}
            }
    	}
    	return null;
    }
    
    public boolean canStartNewConnection(DiagramNodePart srcNode)
    {
       	boolean canStart = false;
    	ModelElementType srcType = srcNode.getModelElement().getModelElementType();
        if (this.endpoint1Property.getType() == null && this.endpoint1Property.hasAnnotation(Reference.class))
        {
        	canStart = this.endpoint1Property.getAnnotation(Reference.class).target().isAssignableFrom(srcType.getModelElementClass());
        }
    	return canStart;
    }
    
    public boolean canCreateNewConnection(DiagramNodePart srcNode, DiagramNodePart targetNode)
    {
    	boolean canCreate = false;
    	
    	canCreate = canStartNewConnection(srcNode);
    	if (!canCreate)
    		return false;
    	
    	ModelElementType targetType = targetNode.getModelElement().getModelElementType();
    	
        if (this.endpoint2Property.getType() == null && this.endpoint2Property.hasAnnotation(Reference.class))
        {
        	canCreate = this.endpoint2Property.getAnnotation(Reference.class).target().isAssignableFrom(targetType.getModelElementClass());
        }
    	return canCreate;
    }
    
    public void addModelListener()
    {
    	this.modelElement.addListener(this.modelPropertyListener, this.propertyName);
    	if (getConnectionType() == ConnectionType.OneToMany)
    	{
    		// it's a 1xn connection type; need to listen to each connection list property
    		ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    		ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
    		for( IModelElement listEntryModelElement : list )
    		{    			
    			listEntryModelElement.addListener(this.modelPropertyListener, head.getPropertyName());    			
    		}
    	}
    }
    
    public void removeModelListener()
    {
    	this.modelElement.removeListener(this.modelPropertyListener, this.propertyName);
    	if (getConnectionType() == ConnectionType.OneToMany)
    	{
    		// it's a 1xn connection type; need to listen to each connection list property
    		ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    		for( IModelElement listEntryModelElement : list )
    		{
    			ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
    			listEntryModelElement.removeListener(this.modelPropertyListener, head.getPropertyName());    			
    		}
    	}
    }
    
    public void addTemplateListener( final Listener listener )
    {
        this.templateListeners.add( listener );
    }
    
    public void removeTemplateListener( final Listener listener )
    {
        this.templateListeners.remove( listener );
    }
    
    public SapphireDiagramEditorPagePart getDiagramEditor()
    {
    	return this.diagramEditor;
    }
    
    public ConnectionType getConnectionType()
    {
    	if (this.originalEndpoint2Path.length() > 1)
    	{
    		return ConnectionType.OneToMany;
    	}
    	else
    	{
    		return ConnectionType.OneToOne;
    	}
    }
        
    public DiagramConnectionPart createNewDiagramConnection(DiagramNodePart srcNode, 
    														DiagramNodePart targetNode)
    {    	
    	// Get the serialized value of endpoint1
    	String endpoint1Value = null;
    	IDiagramConnectionEndpointBindingDef srcAnchorDef = this.bindingDef.getEndpoint1().element();
    	String srcProperty = srcAnchorDef.getProperty().getContent();
    	Value<Function> srcFunc = srcAnchorDef.getValue();
    	FunctionResult srcFuncResult = getNodeReferenceFunction(srcNode, srcFunc, 
    							this.bindingDef.adapt( LocalizationService.class ));
    	if (srcFuncResult != null)
    	{
    		endpoint1Value = (String)srcFuncResult.value();
    		srcFuncResult.dispose();    		
    	}
		if (endpoint1Value == null || endpoint1Value.length() == 0)
		{
			endpoint1Value = IdUtil.computeNodeId(srcNode);
		}
    	
    	// get the serialized value of endpoint2
    	String endpoint2Value = null;
    	IDiagramConnectionEndpointBindingDef targetAnchorDef = this.bindingDef.getEndpoint2().element();
    	Value<Function> targetFunc = targetAnchorDef.getValue();;
    	FunctionResult targetFuncResult = getNodeReferenceFunction(targetNode, targetFunc,
    							this.bindingDef.adapt( LocalizationService.class ));
    	
    	if (targetFuncResult != null)
    	{
    		endpoint2Value = (String)targetFuncResult.value();
    		targetFuncResult.dispose();
    	}
		if (endpoint2Value == null || endpoint2Value.length() == 0)
		{
			endpoint2Value = IdUtil.computeNodeId(targetNode);
		}
    	
    	if (endpoint1Value != null && endpoint2Value != null)
    	{
    		if (getConnectionType() == ConnectionType.OneToOne)
    		{
    			ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    			IModelElement newElement = list.addNewElement();
    			setModelProperty(newElement, ((ModelPath.PropertySegment)this.endpoint1Path.head()).getPropertyName(), endpoint1Value);
    			setModelProperty(newElement, ((ModelPath.PropertySegment)this.endpoint2Path.head()).getPropertyName(), endpoint2Value);
    	    	DiagramConnectionPart newConn = createNewConnectionPart(newElement, null);
    	    	return newConn;    			
    		}
    		else
    		{
    			ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    			IModelElement srcElement = null;
    			for (IModelElement element : list)
    			{
    				Object valObj = element.read(this.endpoint1Property);
    				String val = null;
    				if (valObj instanceof ReferenceValue)
    				{
    					val = (((ReferenceValue<?,?>)valObj).getText());
    				}
    				else
    				{
    					val = (String)element.read(this.endpoint1Property);
    				}
    				if (val.equals(endpoint1Value))
    				{
    					srcElement = element;
    					break;
    				}
    			}
    			if (srcElement == null)
    			{
    				srcElement = list.addNewElement();
    				setModelProperty(srcElement, srcProperty, endpoint1Value);
    			}
    			
    			ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
    			ModelProperty connProp = ModelUtil.resolve(srcElement, head.getPropertyName());
    			if (!(connProp instanceof ListProperty))
    			{
    				throw new RuntimeException("Expecting " + connProp.getName() + " to be a list property");
    			}
				// the connection is of type 1xn
    			ModelElementList<?> connList = srcElement.read((ListProperty)connProp);
    			IModelElement newElement = connList.addNewElement();
    			setModelProperty(newElement, ((ModelPath.PropertySegment)this.endpoint2Path.head()).getPropertyName(), endpoint2Value);
    	    	DiagramConnectionPart newConn = createNewConnectionPart(newElement, null);
				return newConn;
    		}
    	} 
    	return null;
    }
    
    public DiagramConnectionPart createNewConnectionPart(IModelElement connElement, IModelElement srcNodeElement)
    {
    	DiagramConnectionPart connPart = new DiagramConnectionPart(this.bindingDef, this.endpoint1Path, this.endpoint2Path);
    	connPart.init(this, connElement, this.definition, 
    			Collections.<String,String>emptyMap());
    	connPart.addListener(this.connPartListener);
    	addConnectionPart(srcNodeElement, connPart);
    	return connPart;
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
    											final Value<Function> function,
    											LocalizationService ls)
    {
        Function f = null;
        FunctionResult fr = null;
        
        if( function != null )
        {
            f = function.getContent();
        }
        
        if( f != null )
        {
            
            f = FailSafeFunction.create( f, Literal.create( String.class ) );
            fr = f.evaluate( new ModelElementFunctionContext( nodePart.getLocalModelElement(), ls ));
        }
        return fr;
    }
    
    protected void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final IModelElement element = event.getModelElement();
    	final ListProperty property = (ListProperty)event.getProperty();
    	ModelElementList<?> newList = element.read(property);
    	
    	if (property == this.connListProperty)
    	{
    		List<DiagramConnectionPart> connParts = getDiagramConnections(element);
	    	if (newList.size() != connParts.size())
	    	{	    		
	    		List<IModelElement> oldList = new ArrayList<IModelElement>(connParts.size());
	    		for (DiagramConnectionPart connPart : connParts)
	    		{
	    			oldList.add(connPart.getLocalModelElement());
	    		}
	    		    		
		    	if (newList.size() > oldList.size())
		    	{
		    		// new connections are added
		    		List<IModelElement> newConns = ListUtil.ListDiff(newList, oldList);
		    		for (IModelElement newConn : newConns)
		    		{	    			
		            	DiagramConnectionPart connPart = createNewConnectionPart(newConn, element);
		            	if (connPart.getEndpoint1() != null && connPart.getEndpoint2() != null)
		            	{
		            		notifyConnectionAdd(connPart);
		            	}
		            	else
		            	{
			    			connPart.dispose();
			    			removeConnectionPart(element, connPart);
		            	}
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
		    				notifyConnectionDelete(connPart);
			    			connPart.dispose();
			    			removeConnectionPart(element, connPart);
		    			}
		    		}
		    	}
	    	}
    	}
    	else if (property == this.modelProperty)
    	{
    		// 1xn type connection and we are dealing with events on connection list parent
    		List<DiagramConnectionPart> connParts = getDiagramConnections(null);
    		List<IModelElement> oldList = new ArrayList<IModelElement>();
    		Set<IModelElement> oldConnParents = new HashSet<IModelElement>(); 
    		for (DiagramConnectionPart connPart : connParts)
    		{
    			IModelElement connElement = connPart.getLocalModelElement();    			
    			IModelElement connParentElement = (IModelElement)connElement.parent().parent();
    			if (!(oldConnParents.contains(connParentElement)))
    			{
    				oldConnParents.add(connParentElement);
    			}    			
    		}
    		Iterator <IModelElement> it = oldConnParents.iterator();
    		while(it.hasNext())
    		{
    			oldList.add(it.next());
    		}
    		if (newList.size() > oldList.size())
    		{
    			// new connection parents are added and we need to listen on their connection list property
    			List<IModelElement> newConnParents = ListUtil.ListDiff(newList, oldList);
    			ModelPath.PropertySegment head = (ModelPath.PropertySegment)this.originalEndpoint2Path.head();
    			for (IModelElement newConnParent : newConnParents)
    			{
    				newConnParent.addListener(this.modelPropertyListener, head.getPropertyName());
    			}
    		}
    		else if (newList.size() < oldList.size())
    		{
    			// connection parents are deleted and we need to dispose any connections associated with them
    			List<IModelElement> deletedConnParents = ListUtil.ListDiff(newList, oldList);
    			List<DiagramConnectionPart> connPartsCopy = new ArrayList<DiagramConnectionPart>(connParts.size());
    			connPartsCopy.addAll(connParts);
    			for (DiagramConnectionPart connPart : connPartsCopy)
    			{
        			IModelElement connElement = connPart.getLocalModelElement();    			
        			IModelElement connParentElement = (IModelElement)connElement.parent().parent();
        			if (deletedConnParents.contains(connParentElement))
        			{
	    				notifyConnectionDelete(connPart);
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
    
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}	
    
    protected void notifyConnectionUpdate(DiagramConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionUpdate(connPart);
        }    	
    }

    protected void notifyConnectionEndpointUpdate(DiagramConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionEndpointUpdate(connPart);
        }    	
    }
    
    protected void notifyConnectionAdd(DiagramConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionAdd(connPart);
        }    	
    }

    protected void notifyConnectionDelete(DiagramConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionDelete(connPart);
        }    	
    }
    
    // ******************************************************************
    // Inner classes
    //*******************************************************************
    
    public static enum ConnectionType
    {
    	OneToOne,
    	OneToMany
    }
}
