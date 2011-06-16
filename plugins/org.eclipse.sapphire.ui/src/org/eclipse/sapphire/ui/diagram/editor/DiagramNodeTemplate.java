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
 *    Konstantin Komissarchik - [348813] Generalize Sapphire.Diagram.Drop action
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeTemplate extends SapphirePart
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
        public void handleNodeMove(final DiagramNodeEvent event)
        {        	
        }
    }
    
	private SapphireDiagramEditorPagePart diagramEditor;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;	
	private String propertyName;
	private ListProperty modelProperty;
	private JavaType modelElementType;
	private String toolPaletteLabel;
	private String toolPaletteDesc;
	private DiagramEmbeddedConnectionTemplate embeddedConnTemplate;
	private ModelPropertyListener modelPropertyListener;
	private SapphireDiagramPartListener nodePartListener;
	private Set<Listener> listeners;	
	private List<DiagramNodePart> diagramNodes;
	    
	@Override
    public void init()
    {
        this.diagramEditor = (SapphireDiagramEditorPagePart)getParentPart();
        this.modelElement = getModelElement();
        this.definition = (IDiagramNodeDef)super.definition;
        
        if (this.definition.getToolPaletteLabel().getContent() != null)
        {
            ValueProperty tpLabelProperty = IDiagramNodeDef.PROP_TOOL_PALETTE_LABEL;
            this.toolPaletteLabel = tpLabelProperty.getLocalizationService().text(
                            this.definition.getToolPaletteLabel().getContent(), CapitalizationType.TITLE_STYLE, false);
        }        
        if (this.definition.getToolPaletteDesc().getContent() != null)
        {
            ValueProperty tpDescProperty = IDiagramNodeDef.PROP_TOOL_PALETTE_LABEL;
            this.toolPaletteDesc = tpDescProperty.getLocalizationService().text(
                            this.definition.getToolPaletteDesc().getContent(), CapitalizationType.TITLE_STYLE, false);
        }
        
        this.diagramNodes = new ArrayList<DiagramNodePart>();
        this.listeners = new CopyOnWriteArraySet<Listener>();
        
        this.propertyName = this.definition.getProperty().getContent();
        this.modelProperty = (ListProperty)resolve(this.modelElement, this.propertyName);
        this.modelElementType = this.definition.getElementType().resolve();
        
        this.nodePartListener = new SapphireDiagramPartListener() 
        {
        	@Override
        	public void handleNodeUpdateEvent(final DiagramNodeEvent event)
        	{
        		notifyNodeUpdate((DiagramNodePart)event.getPart());
        	}        	
        	@Override
	    	public void handleNodeMoveEvent(final DiagramNodeEvent event)
	       	{
        		notifyNodeMoveEvent(event);
	       	}        	
		};
		
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
            if (this.modelElementType == null)
            {
                createNewNodePart(listEntryModelElement);
            }
            else 
            {
                final Class<?> cl = this.modelElementType.artifact();
                if( cl == null || cl.isAssignableFrom( listEntryModelElement.getClass() ) )
                {
                    createNewNodePart(listEntryModelElement);
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
    
    /*
     * We need to initialize all the node parts before we can initialize embedded connections.
     * Connections between "anonymous" nodes are represented using node index based mechanisms.
     */
    public void initEmbeddedConnections()
    {
        // handle embedded connections
        if (!this.definition.getEmbeddedConnections().isEmpty())
        {
            IDiagramExplicitConnectionBindingDef embeddedConnDef = 
                        this.definition.getEmbeddedConnections().get( 0 );
            this.embeddedConnTemplate = new DiagramEmbeddedConnectionTemplate(embeddedConnDef);
            IDiagramConnectionDef connDef = this.diagramEditor.getDiagramConnectionDef(embeddedConnDef.getConnectionId().getContent());
            this.embeddedConnTemplate.init(this, this.modelElement, connDef, Collections.<String,String>emptyMap());
        }
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
    
    public IDiagramImageChoice getToolPaletteImage()
    {
        return this.definition.getToolPaletteImage().element();
    }
    
    public String getNodeTypeId()
    {
        return this.definition.getId().getContent();
    }
    
    @Override
    public Set<String> getActionContexts()
    {
        Set<String> ret = new HashSet<String>();
        ret.add(SapphireActionSystem.CONTEXT_DIAGRAM_NODE);
        ret.add(SapphireActionSystem.CONTEXT_DIAGRAM);
        return ret;
    }
    
    public DiagramNodePart createNewDiagramNode()
    {
    	IModelElement newElement = null;
		ModelElementList<?> list = this.modelElement.read(this.modelProperty);
		if (this.modelElementType == null)
		{
			newElement = list.addNewElement();
		}
		else
		{
			final Class cl = this.modelElementType.artifact();
			if (cl != null)
			{
				newElement = list.addNewElement(cl);
			}
			else
			{
				newElement = list.addNewElement();
			}
		}
		DiagramNodePart newNodePart = getNodePart(newElement);
    	return newNodePart;
    }
    
    public ModelProperty getModelProperty()
    {
        return this.modelProperty;
    }
    
    public ModelElementType getNodeType()
    {
        if (this.modelElementType == null)
        {
            return this.modelProperty.getType();
        }
        else 
        {
            final Class<?> cl = this.modelElementType.artifact();
            return ModelElementType.getModelElementType(cl);
        }
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
    	
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}	
    
    private void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final IModelElement element = event.getModelElement();
    	final ModelProperty property = event.getProperty();
    	ModelElementList<?> tempList = (ModelElementList<?>)element.read(property);
    	
    	// filter the list property with specified element type
    	List<IModelElement> newList = new ArrayList<IModelElement>();
		newList = new ArrayList<IModelElement>();    	
    	for (IModelElement ele : tempList)
    	{
    		if (this.modelElementType == null)
    		{
    			newList.add(ele);
    		}
    		else
    		{
        		final Class<?> cl = this.modelElementType.artifact();
        		if( cl == null || cl.isAssignableFrom( ele.getClass()))
        		{
        			newList.add(ele);
        		}
    		}
    	}
	
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
    		    	DiagramNodePart nodePart = createNewNodePart(newNode);
    		    	notifyNodeAdd(nodePart);
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
    
    public DiagramNodePart getNodePart(IModelElement element)
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
    
    public DiagramNodePart createNewNodePart(IModelElement element)
    {
        DiagramNodePart newNode = new DiagramNodePart();
        newNode.init(this, element, this.definition, 
                Collections.<String,String>emptyMap());
        newNode.addListener(this.nodePartListener);
        this.diagramNodes.add(newNode);
        if (this.embeddedConnTemplate != null)
        {
            this.embeddedConnTemplate.addModelListener(element);
        }
        return newNode;        
    }
    
    @Override
    public void dispose()
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
    
    public SapphireDiagramEditorPagePart getDiagramEditorPart()
    {
        return this.diagramEditor;
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

	private void notifyNodeMoveEvent(DiagramNodeEvent event)
	{
		for( Listener listener : this.listeners )
        {
            listener.handleNodeMove(event);
        }				
	}
}
