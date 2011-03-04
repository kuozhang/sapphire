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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.ui.diagram.DiagramDropTargetService;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeTemplate 
{
    public static abstract class Listener
    {
        public void handleNodeUpdate(final DiagramNodePart nodePart)
        {
        }       
        public void handleNodeAdd(final DiagramNodePart nodePart)
        {        	
        }
        public void handleNodeDelete(final DiagramNodePart nodePart)
        {        	
        }
    }
    
	private SapphireDiagramEditorPart diagramEditor;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;	
	private String propertyName;
	private ListProperty modelProperty;
	private String toolPaletteLabel;
	private String toolPaletteDesc;
	private DiagramEmbeddedConnectionTemplate embeddedConnTemplate;
	private ModelPropertyListener modelPropertyListener;
	private SapphireDiagramPartListener nodePartListener;
	private final Set<Listener> listeners;	
	private List<DiagramNodePart> diagramNodes;
	private DiagramDropTargetService dropService;
	    
    public DiagramNodeTemplate(final SapphireDiagramEditorPart diagramEditor, IDiagramNodeDef definition, IModelElement modelElement)
    {
    	this.diagramEditor = diagramEditor;
    	this.modelElement = modelElement;
    	this.definition = definition;
    	
        this.toolPaletteLabel = this.definition.getToolPaletteLabel().getContent();
        this.toolPaletteDesc = this.definition.getToolPaletteDesc().getContent();
        
        this.diagramNodes = new ArrayList<DiagramNodePart>();
        this.listeners = new CopyOnWriteArraySet<Listener>();
        
        this.propertyName = this.definition.getProperty().getContent();
        this.modelProperty = (ListProperty)resolve(this.modelElement, this.propertyName);
        this.nodePartListener = new SapphireDiagramPartListener() 
        {
        	@Override
        	 public void handleNodeUpdateEvent(final DiagramNodeEvent event)
        	 {
        		 notifyNodeUpdate((DiagramNodePart)event.getPart());
        	 }        	
		};
		
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
        	createNewNodePart(listEntryModelElement);
        }

        // handle drop target service
        final Class<?> serviceClass = this.definition.getDropTargetService().resolve();
        if (serviceClass != null)
        {
        	this.dropService = DiagramDropTargetService.create(serviceClass);
        }

        // handle embedded connections
        if (this.definition.getEmbeddedConnections().element() != null)
        {
        	IDiagramConnectionBindingDef embeddedConnDef = 
        				this.definition.getEmbeddedConnections().element();
        	this.embeddedConnTemplate = new DiagramEmbeddedConnectionTemplate(
        									this.diagramEditor,
        									this, 
        									embeddedConnDef, 
        									this.modelElement);
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
    
    public IDiagramNodeDef getDefinition()
    {
    	return this.definition;
    }
    
    public List<DiagramNodePart> getDiagramNodes()
    {
    	return this.diagramNodes;
    }
    
    public String getToolPaletteLabel()
    {
    	return this.toolPaletteLabel;
    }
    
    public String getToolPaletteDesc()
    {
    	return this.toolPaletteDesc;
    }
    
    public DiagramNodePart createNewDiagramNode()
    {
    	IModelElement newElement = null;
		ModelElementList<?> list = this.modelElement.read(this.modelProperty);
		newElement = list.addNewElement();
    	DiagramNodePart newNode = createNewNodePart(newElement);
    	return newNode;
    }
    
    public ModelProperty getModelProperty()
    {
    	return this.modelProperty;
    }
    
    public ModelElementType getNodeType()
    {
    	return this.modelProperty.getType();
    }
    
    public DiagramEmbeddedConnectionTemplate getEmbeddedConnectionTemplate()
    {
    	return this.embeddedConnTemplate;
    }
    
    public void addModelListener()
    {
    	this.modelElement.addListener(this.modelPropertyListener, this.propertyName);
    }
    
    public void removeModelLister()
    {
    	this.modelElement.removeListener(this.modelPropertyListener, this.propertyName);
    }
    
    public void addTemplateListener( final Listener listener )
    {
        this.listeners.add( listener );
    }
    
    public void removeTemplateListener( final Listener listener )
    {
        this.listeners.remove( listener );
    }
    
	public DiagramDropTargetService getDropTargetService()
	{
		return this.dropService;
	}
    
    private ModelProperty resolve(final IModelElement modelElement, 
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
    
    private void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final IModelElement element = event.getModelElement();
    	final ModelProperty property = event.getProperty();
    	ModelElementList<?> newList = (ModelElementList<?>)element.read(property);
    	if (newList.size() != getDiagramNodes().size())
    	{
    		List<DiagramNodePart> nodeParts = getDiagramNodes();
    		List<IModelElement> oldList = new ArrayList<IModelElement>(nodeParts.size());
    		for (DiagramNodePart nodePart : nodeParts)
    		{
    			oldList.add(nodePart.getLocalModelElement());
    		}
    		
	    	if (newList.size() > oldList.size())
	    	{
	    		// new nodes are added outside of the diagram editor
	    		List<IModelElement> newNodes = ListUtil.ListDiff(newList, oldList);
	    		for (IModelElement newNode : newNodes)
	    		{
	    			// If new model element is added through palette, we don't need to 
	    			// add its graphical representation to the diagram since the create
	    			// feature takes care of that. If the new model element is added through
	    			// other means (form editor or source editor, we need to add the PE to the
	    			// diagram
	    			DiagramNodePart nodePart = getNodePart(newNode);
	    			if (nodePart == null)
	    			{
	    		    	nodePart = createNewNodePart(newNode);
	    		    	notifyNodeAdd(nodePart);
	    			}
	    		}
	    	}
	    	else if (newList.size() < oldList.size())
	    	{
	    		// nodes are deleted
	    		List<IModelElement> deletedNodes = ListUtil.ListDiff(newList, oldList);
	    		for (IModelElement deletedNode : deletedNodes)
	    		{
	    			DiagramNodePart nodePart = getNodePart(deletedNode);
	    			if (nodePart != null)
	    			{
	    				notifyNodeDelete(nodePart);
    					nodePart.dispose();
    					this.diagramNodes.remove(nodePart);
	    			}
	    		}
	    	}
    	}
    }
    
    private DiagramNodePart getNodePart(IModelElement element)
    {
    	List<DiagramNodePart> nodeParts = getDiagramNodes();
    	for (DiagramNodePart nodePart : nodeParts)
    	{
    		if (nodePart.getLocalModelElement().equals(element))
    		{
    			return nodePart;
    		}
    	}
    	return null;
    }
    
    private DiagramNodePart createNewNodePart(IModelElement element)
    {
    	DiagramNodePart newNode = new DiagramNodePart(this);
    	newNode.init(this.diagramEditor, element, this.definition, 
    			Collections.<String,String>emptyMap());
    	newNode.addListener(this.nodePartListener);
    	this.diagramNodes.add(newNode);
    	if (this.embeddedConnTemplate != null)
    	{
    		this.embeddedConnTemplate.addModelListener(element);
    	}
    	return newNode;    	
    }
    	
	void dispose()
	{
		removeModelLister();
		
		List<DiagramNodePart> nodeParts = getDiagramNodes();
		for (DiagramNodePart nodePart : nodeParts)
		{
			nodePart.dispose();
		}
		
		if (this.embeddedConnTemplate != null)
		{
			this.embeddedConnTemplate.dispose();
		}
	}
    
	private void notifyNodeUpdate(DiagramNodePart nodePart)
	{
		for( Listener listener : this.listeners )
        {
            listener.handleNodeUpdate(nodePart);
        }		
	}
	
	private void notifyNodeAdd(DiagramNodePart nodePart)
	{
		for( Listener listener : this.listeners )
        {
            listener.handleNodeAdd(nodePart);
        }				
	}
	
	private void notifyNodeDelete(DiagramNodePart nodePart)
	{
		for( Listener listener : this.listeners )
        {
            listener.handleNodeDelete(nodePart);
        }				
	}
}
