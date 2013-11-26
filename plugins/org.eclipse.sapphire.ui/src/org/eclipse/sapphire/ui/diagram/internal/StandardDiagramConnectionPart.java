/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [341856] NPE when a diagram connection doesn't define a label
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 *    Konstantin Komissarchik - [342775] Support EL in MasterDetailsTreeNodeDef.ImagePath
 *    Konstantin Komissarchik - [378756] Convert ModelElementListener and ModelPropertyListener to common listener infrastructure
 *    Ling Hao - [383924] Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelPath.ParentElementSegment;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramLabelDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent.ConnectionEventType;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.FunctionUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.forms.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.forms.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.forms.PropertiesViewContributorPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class StandardDiagramConnectionPart 

    extends DiagramConnectionPart
    implements PropertiesViewContributorPart
    
{
	protected DiagramConnectionTemplate connectionTemplate;
	protected IDiagramExplicitConnectionBindingDef bindingDef;
	protected Element modelElement;
	private ModelPath endpoint1Path;
	private ModelPath endpoint2Path;
	private IDiagramConnectionEndpointBindingDef endpoint1Def;
	private IDiagramConnectionEndpointBindingDef endpoint2Def;
	private Element srcNodeModel;
	private Element targetNodeModel;
	private FunctionResult endpoint1FunctionResult;
	private FunctionResult endpoint2FunctionResult;
	private PropertyDef endpoint1Property;
	private PropertyDef endpoint2Property;
	protected FunctionResult labelFunctionResult;
	protected Value<?> labelProperty;
	protected FunctionResult idFunctionResult;
	protected Listener modelPropertyListener;
	private PropertiesViewContributionManager propertiesViewContributionManager;
	private List<Point> bendPoints = new ArrayList<Point>();
	private Point labelPosition;
	
	protected static final String CONNECTION_ID_SEPARATOR = "&";
	
	public StandardDiagramConnectionPart() {}
	
	public StandardDiagramConnectionPart(IDiagramExplicitConnectionBindingDef bindingDef, ModelPath endpoint1Path, ModelPath endpoint2Path)
	{				
		this.bindingDef = bindingDef;
		this.endpoint1Path = endpoint1Path;
		this.endpoint2Path = endpoint2Path;
	}
	
	protected void initLabelId()
	{
    	this.connectionTemplate = (DiagramConnectionTemplate) parent();
        
        this.modelElement = getModelElement();
        
        final IDiagramLabelDef labelDef = this.bindingDef.getLabel().content();
        if (labelDef != null)
        {
            this.labelFunctionResult = initExpression
            ( 
                labelDef.getText().content(), 
                String.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                        refreshLabel();
                    }
                }
            );
        
            this.labelProperty = FunctionUtil.getFunctionProperty( this.modelElement, this.labelFunctionResult );
        }
        
        this.idFunctionResult = initExpression
        ( 
            this.bindingDef.getInstanceId().content(), 
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                }
            }
        ); 
        
    }
    
    @Override
    protected void init()
    {
        initLabelId();
        
        this.endpoint1Def = this.bindingDef.getEndpoint1().content();        
        this.srcNodeModel = resolveEndpoint(this.modelElement, this.endpoint1Path);
        if (this.srcNodeModel != null)
        {
            this.endpoint1FunctionResult = initExpression
            (
                this.srcNodeModel, 
                this.endpoint1Def.getValue().content(),
                String.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                    	resetEndpoint1();
                    }
                }
            );            
        }
        
        this.endpoint2Def = this.bindingDef.getEndpoint2().content();
        this.targetNodeModel = resolveEndpoint(this.modelElement, this.endpoint2Path);
        if (this.targetNodeModel != null)
        {
            this.endpoint2FunctionResult = initExpression
            (
                this.targetNodeModel, 
                this.endpoint2Def.getValue().content(),
                String.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                    	resetEndpoint2();
                    }
                }
            );
        }        
        
        this.endpoint1Property = this.modelElement.property(this.endpoint1Path).definition();
        this.endpoint2Property = this.modelElement.property(this.endpoint2Path).definition();
        
        // Add model property listener
        this.modelPropertyListener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
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
    
    @Override
    public Element getLocalModelElement()
    {
        return this.modelElement;
    }    
    
    public Element getEndpoint1()
    {
        return this.srcNodeModel;
    }
    
    public Element getEndpoint2()
    {
        return this.targetNodeModel;
    }

    public boolean canEditLabel()
    {
        return this.labelProperty != null;
    }
    
    public boolean removable()
    {
    	return true;
    }
    
    public void remove()
    {
    	getDiagramConnectionTemplate().deleteConnection(this);
    }
    
    public String getLabel()
    {
        String label = null;
        
        if( this.labelFunctionResult != null )
        {
            label = (String) this.labelFunctionResult.value();
        }
        
        return label;
    }
    
    public void setLabel(String newValue)
    {
        if (this.labelProperty != null)
        {
            this.labelProperty.write( newValue );
        }        
    }
    
    public IDiagramConnectionDef getConnectionDef()
    {
        return (IDiagramConnectionDef) definition();
    }
    
    public void refreshLabel()
    {
        notifyConnectionUpdate();
    }
    
    public String getConnectionTypeId()
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
    
    public String getId()
    {
        StringBuffer buffer = new StringBuffer(getConnectionTypeId());
        buffer.append(CONNECTION_ID_SEPARATOR);
        String instanceId = getInstanceId();
        if (instanceId != null && instanceId.length() > 0)
        {
            buffer.append(getInstanceId());
            buffer.append(CONNECTION_ID_SEPARATOR);
        }
        List<StandardDiagramConnectionPart> connParts = getDiagramConnectionTemplate().getDiagramConnections(null);
        int index = connParts.indexOf(this);
        buffer.append(index);                
        return buffer.toString();            	
    }
    
    @Override
    public Set<String> getActionContexts()
    {
        Set<String> contextSet = new HashSet<String>();
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION);
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION_HIDDEN);
        return contextSet;    	
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
        removeModelListener();
    }
    
    public void resetEndpoint1()
    {
        if (this.endpoint1FunctionResult != null)
        {
            String value = (String)this.endpoint1FunctionResult.value();
            if (value == null  || value.length() == 0)
            {
                SapphireDiagramEditorPagePart diagramPart = this.getDiagramConnectionTemplate().getDiagramEditor();
                DiagramNodePart nodePart = diagramPart.getDiagramNodePart(this.srcNodeModel);
                if (nodePart != null)
                {
                    value = nodePart.getId();
                }
            }
            setModelProperty(this.modelElement, this.endpoint1Path, value);
        }        
    }
    
    public void resetEndpoint2()
    {
        if (this.endpoint2FunctionResult != null)
        {
            String value = (String)this.endpoint2FunctionResult.value();
            if (value == null  || value.length() == 0)
            {
                SapphireDiagramEditorPagePart diagramPart = this.getDiagramConnectionTemplate().getDiagramEditor();
                DiagramNodePart nodePart = diagramPart.getDiagramNodePart(this.targetNodeModel);
                if (nodePart != null)
                {
                    value = nodePart.getId();
                }
            }            
            setModelProperty(this.modelElement, this.endpoint2Path, value);
        }        
    }

    protected void resetEndpoint1(DiagramNodePart newSrcNode)
    {
    	this.srcNodeModel = newSrcNode.getLocalModelElement();
    	String endpoint1Value = this.connectionTemplate.getSerializedEndpoint1(newSrcNode);
    	this.connectionTemplate.setSerializedEndpoint1(this.modelElement, endpoint1Value);
    }
    
    protected void resetEndpoint2(DiagramNodePart newTargetNode)
    {
    	this.targetNodeModel = newTargetNode.getLocalModelElement();
    	String endpoint2Value = this.connectionTemplate.getSerializedEndpoint2(newTargetNode);
    	this.connectionTemplate.setSerializedEndpoint2(this.modelElement, endpoint2Value);
    }

    protected Element resolveEndpoint(Element modelElement, ModelPath endpointPath)
    {
        if (endpointPath.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)endpointPath.head()).getPropertyName();
            PropertyDef modelProperty = resolve(modelElement, propertyName);
            if (!(modelProperty instanceof ValueProperty))
            {
                throw new RuntimeException( "Property " + propertyName + " not a ValueProperty");
            }
            ValueProperty property = (ValueProperty)modelProperty;
            Value<?> valObj = modelElement.property(property);
            if (!(valObj instanceof ReferenceValue))
            {
                throw new RuntimeException( "Property " + propertyName + " value not a reference");
            }
            ReferenceValue<?,?> refVal = (ReferenceValue<?,?>)valObj;
            Object targetObj = refVal.resolve();
            if (targetObj == null)
            {
                if (refVal.text() != null)
                {
                    SapphireDiagramEditorPagePart diagramEditorPart = this.getDiagramConnectionTemplate().getDiagramEditor();
                    DiagramNodePart targetNode = diagramEditorPart.getNode(refVal.text());
                    if (targetNode != null)
                    {
                        targetObj = targetNode.getLocalModelElement();
                    }
                }
            }
            return (Element)targetObj;
        }
        else
        {
            ModelPath.Segment head = endpointPath.head();
            if( head instanceof ParentElementSegment )
            {
                final Property parent = modelElement.parent();                
                if( parent == null )
                {
                    throw new RuntimeException("Invalid model path: " + endpointPath);
                }
                return resolveEndpoint(parent.element(), endpointPath.tail());                
            }
            else
            {
                throw new RuntimeException("Invalid model path: " + endpointPath);
            }
        }
    }
    
    protected void setModelProperty(final Element modelElement, 
            String propertyName, Object value)
    {
        if (propertyName != null)
        {
            final ElementType type = modelElement.type();
            final PropertyDef property = type.property( propertyName );
            if( property == null )
            {
                throw new RuntimeException( "Could not find property " + propertyName + " in " + type.getQualifiedName() );
            }
            if (!(property instanceof ValueProperty))
            {
                throw new RuntimeException( "Property " + propertyName + " not a ValueProperty");
            }
        
            modelElement.property( (ValueProperty) property ).write( value );
        }        
    }
    
    protected void setModelProperty(Element modelElement, ModelPath propertyPath, Object value)
    {
        if (propertyPath.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)propertyPath.head()).getPropertyName();
            setModelProperty(modelElement, propertyName, value);
        }
        else
        {
            if (propertyPath.head() instanceof ModelPath.ParentElementSegment)
            {
                final Property parent = modelElement.parent();
                setModelProperty(parent.element(), propertyPath.tail(), value);
            }
        }
    }
    
    public void addModelListener()
    {
        this.modelElement.attach(this.modelPropertyListener, this.endpoint1Path);
        this.modelElement.attach(this.modelPropertyListener, this.endpoint2Path);
    }
    
    public void removeModelListener()
    {
        this.modelElement.detach(this.modelPropertyListener, this.endpoint1Path);
        this.modelElement.detach(this.modelPropertyListener, this.endpoint2Path);
    }
    
    public StandardDiagramConnectionPart reconnect(DiagramNodePart newSrcNode, DiagramNodePart newTargetNode)
    {
        StandardDiagramConnectionPart newConnPart = getDiagramConnectionTemplate().createNewDiagramConnection(newSrcNode, newTargetNode);

        final Element oldConnElement = this.getLocalModelElement();
        newConnPart.getLocalModelElement().copy(oldConnElement);
        // Bug 382912 - Reconnecting an existing connection adds a bend point 
        // After the copy, connection endpoint event is triggered which causes SapphireConnectionRouter
        // to be called. Since the old connection hasn't been deleted, a default bend point will be added. 
        newConnPart.removeAllBendpoints();
		
		if (newSrcNode.getLocalModelElement() != getEndpoint1()) 
		{
			newConnPart.resetEndpoint1(newSrcNode);
		} 
		if (newTargetNode.getLocalModelElement() != getEndpoint2())
		{
			newConnPart.resetEndpoint2(newTargetNode);
		}
        final ElementList<?> list = (ElementList<?>) oldConnElement.parent();
        list.remove(oldConnElement);
        return newConnPart;
    }
    
    protected void handleModelPropertyChange(final PropertyEvent event)
    {
        final PropertyDef property = event.property().definition();
                
        if (property.name().equals(this.endpoint1Property.name()) || 
                property.name().equals(this.endpoint2Property.name()))
        {
            boolean sourceChange = property.name().equals(this.endpoint1Property.name()) ? true : false;
            handleEndpointChange(sourceChange);
            notifyConnectionEndpointUpdate();
        }
    }    
    
    private void handleEndpointChange(boolean sourceChange) 
    {
        if (sourceChange)
        {
            this.srcNodeModel = resolveEndpoint(this.modelElement, this.endpoint1Path);
            if (this.endpoint1FunctionResult != null)
            {
                this.endpoint1FunctionResult.dispose();
                this.endpoint1FunctionResult = null;
            }
            if (this.srcNodeModel != null)
            {
                this.endpoint1FunctionResult = initExpression
                (
                    this.srcNodeModel, 
                    this.endpoint1Def.getValue().content(), 
                    String.class,
                    null,
                    new Runnable()
                    {
                        public void run()
                        {
                        	resetEndpoint1();
                        }
                    }
                );                
            }            
        }
        else
        {
            this.targetNodeModel = resolveEndpoint(this.modelElement, this.endpoint2Path);
            if (this.endpoint2FunctionResult != null)
            {
                this.endpoint2FunctionResult.dispose();
                this.endpoint2FunctionResult = null;
            }
            if (this.targetNodeModel != null)
            {
                this.endpoint2FunctionResult = initExpression
                (
                    this.targetNodeModel, 
                    this.endpoint2Def.getValue().content(),
                    String.class,
                    null,
                    new Runnable()
                    {
                        public void run()
                        {
                        	resetEndpoint2();
                        }
                    }
                );
                
            }            
        }
    }
        
    protected void notifyConnectionUpdate()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionUpdate);
    	this.broadcast(event);
    }
    
    protected void notifyConnectionEndpointUpdate()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionEndpointUpdate);
    	this.broadcast(event);
    }
    
    protected void notifyAddBendpoint()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionAddBendpoint);
    	this.broadcast(event);
    }
    
    protected void notifyRemoveBendpoint()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionRemoveBendpoint);
    	this.broadcast(event);
    }

    protected void notifyMoveBendpoint()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionMoveBendpoint);
    	this.broadcast(event);
    }

    protected void notifyResetBendpoints()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionResetBendpoint);
    	this.broadcast(event);
    }

    protected void notifyMoveConnectionLabel()
    {
    	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
		event.setConnectionEventType(ConnectionEventType.ConnectionMoveLabel);
    	this.broadcast(event);
    }
    
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        if( this.propertiesViewContributionManager == null )
        {
            this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, getLocalModelElement(), getConnectionDef() );
        }
        
        return this.propertiesViewContributionManager.getPropertiesViewContribution();
    }
	
    public void addBendpoint(int index, int x, int y)
    {
    	this.bendPoints.add(index, new Point(x, y));
    	notifyAddBendpoint();
    }
    
    public void removeBendpoint(int index)
    {
    	this.bendPoints.remove(index);
    	notifyRemoveBendpoint();
    }
    
    public void removeAllBendpoints()
    {
    	this.bendPoints.clear();
    	notifyRemoveBendpoint();
    }
    
    public void updateBendpoint(int index, int x, int y)
    {
    	if (index < this.bendPoints.size())
    	{
    		this.bendPoints.set(index, new Point(x, y));
    	}
    	notifyMoveBendpoint();
    }
        
    public void resetBendpoints(List<Point> bendpoints)
    {
    	
    	boolean changed = false;
    	if (bendpoints.size() != this.bendPoints.size())
    	{
    		changed = true;
    	}
    	else
    	{
			for (int i = 0; i < bendpoints.size(); i++)
			{
				Point newPt = bendpoints.get(i);
				Point oldPt = this.bendPoints.get(i);
				if (newPt.getX() != oldPt.getX() || newPt.getY() != oldPt.getY())
				{
					changed = true;
					break;
				}
			}    		
    	}
    	if (changed)
    	{
    		this.bendPoints.clear();
    		this.bendPoints.addAll(bendpoints);
    		notifyResetBendpoints();
    	}
    }
    
    public List<Point> getBendpoints()
    {
    	List<Point> bendPoints = new ArrayList<Point>();
    	bendPoints.addAll(this.bendPoints);
    	return bendPoints;
    }
    
    public Point getLabelPosition()
    {
    	return this.labelPosition;
    }
    
    public void setLabelPosition(Point newPos)
    {
    	boolean changed = false;
    	
    	if (this.labelPosition == null && newPos != null)
    	{
    		this.labelPosition = new Point(newPos);
    		changed = true;
    	}
    	else if (this.labelPosition != null && newPos == null)
    	{
    		this.labelPosition = null;
    		changed = true;
    	}
    	else if (this.labelPosition != null && newPos != null && !this.labelPosition.equals(newPos))
    	{
    		this.labelPosition.setX(newPos.getX());
    		this.labelPosition.setY(newPos.getY());
   			changed = true;
    	}
    	if (changed)
    	{
    		notifyMoveConnectionLabel();
    	}
    }
}
