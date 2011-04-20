/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342775] Support EL in IMasterDetailsTreeNodeDef.ImagePath
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
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IModelElementTypeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramImplicitConnectionTemplate extends SapphirePart 
{
    public static abstract class Listener
    {
        public void handleConnectionAdd(final DiagramImplicitConnectionPart connPart)
        {        	
        }
        public void handleConnectionDelete(final DiagramImplicitConnectionPart connPart)
        {        	
        }
    }
	
	private IDiagramImplicitConnectionBindingDef bindingDef;
	private IDiagramConnectionDef definition;
	protected IModelElement modelElement;
	private SapphireDiagramEditorPagePart diagramEditor;
	private String propertyName;
	private ModelPath allDescendentsPath;
	private ListProperty modelProperty;
	private List<Class<?>> modelElementTypes;
	private List<IModelElement> filteredElementList;
	private List<DiagramImplicitConnectionPart> implicitConnections;
	private ModelPropertyListener modelPropertyListener;
	private Set<Listener> templateListeners;
		
	public DiagramImplicitConnectionTemplate(IDiagramImplicitConnectionBindingDef bindingDef)
	{
		this.bindingDef = bindingDef;
	}

	@Override
    public void init()
    {
    	this.diagramEditor = (SapphireDiagramEditorPagePart)getParentPart();
    	this.modelElement = getModelElement();
    	this.definition = (IDiagramConnectionDef)super.definition;
        this.propertyName = this.bindingDef.getProperty().getContent();
        this.modelProperty = (ListProperty)ModelUtil.resolve(this.modelElement, this.propertyName);
        
    	this.modelElementTypes = new ArrayList<Class<?>>();
    	ModelElementList<IModelElementTypeDef> types = this.bindingDef.getModelElementTypes();
    	for (IModelElementTypeDef typeDef : types)
    	{
    		this.modelElementTypes.add(typeDef.getType().resolve().artifact());
    	}
    	initImplicitConnectionParts();
    	
    	this.templateListeners = new CopyOnWriteArraySet<Listener>();
    	
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
	
	public SapphireDiagramEditorPagePart getDiagramEditorPart()
	{
		return this.diagramEditor;
	}
	
    public void addModelListener()
    {
    	this.modelElement.addListener(this.modelPropertyListener, this.propertyName);
    	String temp = this.propertyName + "/*";
    	this.allDescendentsPath = new ModelPath(temp);
    	this.modelElement.addListener(this.modelPropertyListener, this.allDescendentsPath);
    }
    
    public void removeModelListener()
    {
    	this.modelElement.removeListener(this.modelPropertyListener, this.propertyName);
    	this.modelElement.removeListener(this.modelPropertyListener, this.allDescendentsPath);
    }
    
    private void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	List<IModelElement> newFilteredList = getFilteredModelElementList();
    	
    	// compare the new element list with the old element list. If the size is different or
    	// the list order has changed, we need to refresh the implicit connections
    	boolean changed = false;
    	if (newFilteredList.size() == this.filteredElementList.size())
    	{
    		for (int i = 0; i < newFilteredList.size(); i++)
    		{
    			IModelElement element1 = newFilteredList.get(i);
    			IModelElement element2 = this.filteredElementList.get(i);
    			if (element1 != element2)
    			{
    				changed = true;
    				break;
    			}
    		}
    	}
    	else
    	{
    		changed = true;
    	}
    	
    	if (changed)
    	{
    		for (DiagramImplicitConnectionPart connPart : this.implicitConnections)
    		{
    			notifyConnectionDelete(connPart);
    			connPart.dispose();
    		}
    		this.implicitConnections.clear();
    		this.filteredElementList.clear();
    		this.filteredElementList = newFilteredList;
            for (int i = 0; i < this.filteredElementList.size() - 1; i++)
            {
            	DiagramImplicitConnectionPart connPart = 
            			createNewConnectionPart(this.filteredElementList.get(i), this.filteredElementList.get(i+1));
            	this.implicitConnections.add(connPart);
            	notifyConnectionAdd(connPart);            	
            }	
    		
    	}
    }
    
	public List<DiagramImplicitConnectionPart> getImplicitConnections()
	{
		return this.implicitConnections;
	}
	
	private void initImplicitConnectionParts()
	{
		this.filteredElementList = getFilteredModelElementList();
		
		this.implicitConnections = new ArrayList<DiagramImplicitConnectionPart>();
        for (int i = 0; i < this.filteredElementList.size() - 1; i++)
        {
        	DiagramImplicitConnectionPart connPart = 
        			createNewConnectionPart(this.filteredElementList.get(i), this.filteredElementList.get(i+1));
        	this.implicitConnections.add(connPart);
        }	
		
	}
	
	private List<IModelElement> getFilteredModelElementList()
	{
    	ModelElementList<?> list = this.modelElement.read(this.modelProperty);
    	List<IModelElement> filteredList = new ArrayList<IModelElement>();
        for( IModelElement listEntryModelElement : list )
        {
        	if (isRightEntry(listEntryModelElement))
        	{
        		filteredList.add(listEntryModelElement);
        	}
        }
        return filteredList;
	}
		
	private DiagramImplicitConnectionPart createNewConnectionPart(IModelElement srcNodeModel, IModelElement targetNodeModel)
	{
		DiagramImplicitConnectionPart connPart = new DiagramImplicitConnectionPart(srcNodeModel, targetNodeModel);
		connPart.init(this, srcNodeModel, this.definition, Collections.<String,String>emptyMap());
		return connPart;
	}
	
	private boolean isRightEntry(IModelElement entryModelElement)
	{
		boolean isRightType = true;
		if (this.modelElementTypes.size() > 0)
		{
			isRightType = false;
			for (Class<?> eleType : this.modelElementTypes)
			{
				if (eleType.isAssignableFrom(entryModelElement.getClass()))
				{
					isRightType = true;
					break;
				}
			}
		}
		if (isRightType && this.bindingDef.getCondition() != null)
		{
			isRightType = false;
			// apply the condition
			FunctionResult fr = initExpression
			( 
				entryModelElement,
			    this.bindingDef.getCondition().getContent(), 
			    String.class,
			    null,
			    new Runnable()
			    {
					public void run()
					{
			        }	
			    }
			);
			if (fr != null && ((String)fr.value()).equals("true"))
			{
				isRightType = true;
			}
		}
		return isRightType;
	}
	
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();		
	}
	
    public void addTemplateListener( final Listener listener )
    {
        this.templateListeners.add( listener );
    }
    
    public void removeTemplateListener( final Listener listener )
    {
        this.templateListeners.remove( listener );
    }	

    public void notifyConnectionAdd(DiagramImplicitConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionAdd(connPart);
        }    	
    }

    public void notifyConnectionDelete(DiagramImplicitConnectionPart connPart)
    {
		for( Listener listener : this.templateListeners )
        {
            listener.handleConnectionDelete(connPart);
        }    	
    }
	
}
