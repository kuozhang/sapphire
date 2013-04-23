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
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
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
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramLabelDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DiagramConnectionPart 

    extends SapphirePart
    implements IPropertiesViewContributorPart
    
{
	protected DiagramConnectionTemplate connectionTemplate;
	protected IDiagramExplicitConnectionBindingDef bindingDef;
	protected IDiagramConnectionDef definition;
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
	private DiagramConnectionBendPoints bendPoints = new DiagramConnectionBendPoints();
	private Point labelPosition;
	
	public DiagramConnectionPart() {}
	
	public DiagramConnectionPart(IDiagramExplicitConnectionBindingDef bindingDef, ModelPath endpoint1Path, ModelPath endpoint2Path)
	{				
		this.bindingDef = bindingDef;
		this.endpoint1Path = endpoint1Path;
		this.endpoint2Path = endpoint2Path;
	}
	
	protected void initLabelId()
	{
    	this.connectionTemplate = (DiagramConnectionTemplate)getParentPart();
        
        this.definition = (IDiagramConnectionDef)super.definition;
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
                    }
                }
            );
        }        
        
        this.endpoint1Property = ModelUtil.resolve(this.modelElement, this.endpoint1Path);
        this.endpoint2Property = ModelUtil.resolve(this.modelElement, this.endpoint2Path);
        
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
        return this.definition;
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
    
    @Override
    public Set<String> getActionContexts()
    {
        Set<String> contextSet = new HashSet<String>();
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION);
        contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION_HIDDEN);
        return contextSet;    	
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
                    value = IdUtil.computeNodeId(nodePart);
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
                    value = IdUtil.computeNodeId(nodePart);
                }
            }            
            setModelProperty(this.modelElement, this.endpoint2Path, value);
        }        
    }

    public void resetEndpoint1(DiagramNodePart newSrcNode)
    {
    	this.srcNodeModel = newSrcNode.getLocalModelElement();
    	String endpoint1Value = this.connectionTemplate.getSerializedEndpoint1(newSrcNode);
    	this.connectionTemplate.setSerializedEndpoint1(this.modelElement, endpoint1Value);
    }
    
    public void resetEndpoint2(DiagramNodePart newTargetNode)
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
                    DiagramNodePart targetNode = IdUtil.getNodePart(diagramEditorPart, refVal.text());
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
    
    protected void notifyAddBendpoint()
    {
        Set<SapphirePartListener> listeners = this.getListeners();
        for(SapphirePartListener listener : listeners)
        {
            if (listener instanceof SapphireDiagramPartListener)
            {
            	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
                ((SapphireDiagramPartListener)listener).handleConnectionAddBendpointEvent(event);
            }
        }    	
    }
    
    protected void notifyRemoveBendpoint()
    {
        Set<SapphirePartListener> listeners = this.getListeners();
        for(SapphirePartListener listener : listeners)
        {
            if (listener instanceof SapphireDiagramPartListener)
            {
                DiagramConnectionEvent cue = new DiagramConnectionEvent(this);
                ((SapphireDiagramPartListener)listener).handleConnectionRemoveBendpointEvent(cue);
            }
        }    	
    }

    protected void notifyMoveBendpoint()
    {
        Set<SapphirePartListener> listeners = this.getListeners();
        for(SapphirePartListener listener : listeners)
        {
            if (listener instanceof SapphireDiagramPartListener)
            {
                DiagramConnectionEvent cue = new DiagramConnectionEvent(this);
                ((SapphireDiagramPartListener)listener).handleConnectionMoveBendpointEvent(cue);
            }
        }    	
    }

    protected void notifyResetBendpoints()
    {
        Set<SapphirePartListener> listeners = this.getListeners();
        for(SapphirePartListener listener : listeners)
        {
            if (listener instanceof SapphireDiagramPartListener)
            {
            	DiagramConnectionEvent event = new DiagramConnectionEvent(this);
                ((SapphireDiagramPartListener)listener).handleConnectionResetBendpointsEvent(event);
            }
        }    	
    }

    protected void notifyMoveConnectionLabel()
    {
        Set<SapphirePartListener> listeners = this.getListeners();
        for(SapphirePartListener listener : listeners)
        {
            if (listener instanceof SapphireDiagramPartListener)
            {
                DiagramConnectionEvent cue = new DiagramConnectionEvent(this);
                ((SapphireDiagramPartListener)listener).handleConnectionMoveLabelEvent(cue);
            }
        }    	
    }
    
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        if( this.propertiesViewContributionManager == null )
        {
            this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, getLocalModelElement(), this.bindingDef );
        }
        
        return this.propertiesViewContributionManager.getPropertiesViewContribution();
    }
	
    public void addBendpoint(int index, int x, int y)
    {
    	this.bendPoints.getBendPoints().add(index, new Point(x, y));
    	notifyAddBendpoint();
    }
    
    public void removeBendpoint(int index)
    {
    	this.bendPoints.getBendPoints().remove(index);
    	notifyRemoveBendpoint();
    }
    
    public void removeAllBendpoints()
    {
    	this.bendPoints.getBendPoints().clear();
    	notifyRemoveBendpoint();
    }
    
    public void updateBendpoint(int index, int x, int y)
    {
    	if (index < this.bendPoints.getBendPoints().size())
    	{
    		this.bendPoints.getBendPoints().set(index, new Point(x, y));
    	}
    	notifyMoveBendpoint();
    }
    
    public void resetBendpoints(DiagramConnectionBendPoints bendPoints)
    {
    	resetBendpoints(bendPoints.getBendPoints(), bendPoints.isAutoLayout(), 
    			bendPoints.isDefault());
    }
    
    public void resetBendpoints(List<Point> bendpoints, boolean autoLayout , boolean isDefault)
    {
    	boolean changed = false;
    	if (bendpoints.size() != this.bendPoints.getBendPoints().size() || 
    			autoLayout != this.bendPoints.isAutoLayout() ||
    			isDefault != this.bendPoints.isDefault())
    	{
    		changed = true;
    	}
    	else
    	{
			for (int i = 0; i < bendpoints.size(); i++)
			{
				Point newPt = bendpoints.get(i);
				Point oldPt = this.bendPoints.getBendPoints().get(i);
				if (newPt.getX() != oldPt.getX() || newPt.getY() != oldPt.getY())
				{
					changed = true;
					break;
				}
			}    		
    	}
    	if (changed)
    	{
    		this.bendPoints.setBendPoints(bendpoints);
    		this.bendPoints.setDefault(isDefault);
    		this.bendPoints.setAutoLayout(autoLayout);
    		notifyResetBendpoints();
    	}
    }
    
    public DiagramConnectionBendPoints getConnectionBendpoints()
    {
    	return new DiagramConnectionBendPoints(this.bendPoints);
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
