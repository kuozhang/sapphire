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
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphireEditorPagePart;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartEvent;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramEditorPagePart extends SapphireEditorPagePart
{
    private IModelElement modelElement;
    private IDiagramEditorPageDef diagramPageDef = null;
    private List<IDiagramConnectionDef> connectionDefs;
    private List<DiagramNodeTemplate> nodeTemplates;
    private List<DiagramConnectionTemplate> connectionTemplates;
    private List<DiagramImplicitConnectionTemplate> implicitConnectionTemplates;
    private NodeTemplateListener nodeTemplateListener;
    private ConnectionTemplateListener connTemplateListener;
    private PropertiesViewContributionManager propertiesViewContributionManager;
    private SapphirePart selection;
    private ImplicitConnectionTemplateListener implicitConnTemplateListener;
	    
    @Override
    protected void init()
    {
        super.init();
            
		this.diagramPageDef = (IDiagramEditorPageDef)super.definition;
		ImpliedElementProperty modelElementProperty = (ImpliedElementProperty)resolve(this.diagramPageDef.getProperty().getContent());
		if (modelElementProperty != null)
		{
			this.modelElement = getModelElement().read(modelElementProperty);
		}
        else
        {
            this.modelElement = getModelElement();
        }
		
        this.nodeTemplateListener = new NodeTemplateListener();
        this.connTemplateListener = new ConnectionTemplateListener();
        this.implicitConnTemplateListener = new ImplicitConnectionTemplateListener();
        
        this.nodeTemplates = new ArrayList<DiagramNodeTemplate>();
        ModelElementList<IDiagramNodeDef> nodeDefs = this.diagramPageDef.getDiagramNodeDefs();
        this.connectionDefs = this.diagramPageDef.getDiagramConnectionDefs();
        
        for (IDiagramNodeDef nodeDef : nodeDefs)
        {
        	DiagramNodeTemplate nodeTemplate = new DiagramNodeTemplate();
        	nodeTemplate.init(this, this.modelElement, nodeDef, Collections.<String,String>emptyMap());
        	this.nodeTemplates.add(nodeTemplate);
        	nodeTemplate.addTemplateListener(this.nodeTemplateListener);
        	
        }
        
        // Need to initialize the embedded connections after all the diagram node parts are created
        // For connections between "anonymous" nodes, we'd represent connections using node index based
        // mechanism.
        for (DiagramNodeTemplate nodeTemplate : this.nodeTemplates)
        {
        	nodeTemplate.initEmbeddedConnections();
        	if (nodeTemplate.getEmbeddedConnectionTemplate() != null)
        	{
        		nodeTemplate.getEmbeddedConnectionTemplate().addTemplateListener(this.connTemplateListener);
        	}
        }
                
        this.connectionTemplates = new ArrayList<DiagramConnectionTemplate>();
        ModelElementList<IDiagramExplicitConnectionBindingDef> connectionBindings = this.diagramPageDef.getDiagramConnectionBindingDefs();
        for (IDiagramExplicitConnectionBindingDef connBinding : connectionBindings)
        {
        	IDiagramConnectionDef connDef = getDiagramConnectionDef(connBinding.getConnectionId().getContent());
        	DiagramConnectionTemplate connectionTemplate = new DiagramConnectionTemplate(connBinding);
        	connectionTemplate.init(this, this.modelElement, connDef, Collections.<String,String>emptyMap());
        	this.connectionTemplates.add(connectionTemplate);
        	connectionTemplate.addTemplateListener(this.connTemplateListener);
        }
        
        // initialize implicit connections
        this.implicitConnectionTemplates = new ArrayList<DiagramImplicitConnectionTemplate>();
        ModelElementList<IDiagramImplicitConnectionBindingDef> implicitConnBindings = this.diagramPageDef.getImplicitConnectionBindingDefs();
        for (IDiagramImplicitConnectionBindingDef implicitConnBinding : implicitConnBindings)
        {
        	IDiagramConnectionDef connDef = getDiagramConnectionDef(implicitConnBinding.getConnectionId().getContent());
        	DiagramImplicitConnectionTemplate connectionTemplate = new DiagramImplicitConnectionTemplate(implicitConnBinding);
        	connectionTemplate.init(this, this.modelElement, connDef, Collections.<String,String>emptyMap());
        	this.implicitConnectionTemplates.add(connectionTemplate);
        	connectionTemplate.addTemplateListener(this.implicitConnTemplateListener);
        }
        
        this.selection = this;
        this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, this.modelElement );
                
        this.addListener
        (
            new SapphirePartListener()
            {
                @Override
                public void handleEvent( final SapphirePartEvent event )
                {
                    if( event instanceof SelectionChangedEvent )
                    {
                        refreshPropertiesViewContribution();
                    }
                }
            }
        );
        
        refreshPropertiesViewContribution();
	}

    public List<IDiagramImageChoice> getImageDecorators()
    {
    	return this.diagramPageDef.getDiagramImageDecorators();
    }
    
	public List<DiagramNodeTemplate> getNodeTemplates()
	{
		return this.nodeTemplates;
	}
	
	public List<IDiagramConnectionDef> getDiagramConnectionDefs()
	{
		return this.connectionDefs;
	}
	
	public IDiagramConnectionDef getDiagramConnectionDef(String connId)
	{
		if (connId == null)
		{
			throw new IllegalArgumentException();
		}
		
		IDiagramConnectionDef connDef = null;
		for (IDiagramConnectionDef def : this.connectionDefs)
		{
			String id = def.getId().getContent();
			if (id != null && id.equalsIgnoreCase(connId))
			{
				connDef = def;
				break;
			}
		}
		return connDef;
	}
	
	public List<DiagramConnectionTemplate> getConnectionTemplates()
	{
		return this.connectionTemplates;
	}
			
	public List<DiagramImplicitConnectionTemplate> getImplicitConnectionTemplates()
	{
		return this.implicitConnectionTemplates;
	}
	
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}
	
    @Override
    public Set<String> getActionContexts()
    {
    	Set<String> contextSet = new HashSet<String>();
    	contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM);
    	contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_EDITOR);
    	return contextSet;
    }
        
    public SapphirePart getSelection()
    {
        return this.selection;
    }
	
    public void setSelection( final SapphirePart selection )
    {
        if( this.selection != selection )
        {
            final SapphirePart oldSelection = this.selection;
            final SapphirePart newSelection = selection;
            
            this.selection = newSelection;
            
            notifyListeners( new SelectionChangedEvent( this, oldSelection, newSelection ) );
        }
    }
    
    private void refreshPropertiesViewContribution()
    {
        final SapphirePart selection = getSelection();
        
        PropertiesViewContributionPart propertiesViewContribution = null;
        
        if( selection == SapphireDiagramEditorPagePart.this )
        {
            propertiesViewContribution = this.propertiesViewContributionManager.getPropertiesViewContribution();
        }
        else if( selection instanceof IPropertiesViewContributorPart )
        {
            propertiesViewContribution = ( (IPropertiesViewContributorPart) selection ).getPropertiesViewContribution();
        }
        
        setPropertiesViewContribution( propertiesViewContribution );
    }
    
	public DiagramNodePart getDiagramNodePart(IModelElement nodeElement)
	{
		if (nodeElement == null)
			return null;
		
		List<DiagramNodeTemplate> nodeTemplates = this.getNodeTemplates();
		for (DiagramNodeTemplate nodeTemplate : nodeTemplates)
		{
			List<DiagramNodePart> nodeParts = nodeTemplate.getDiagramNodes();
			for (DiagramNodePart nodePart : nodeParts)
			{
				if (nodePart.getLocalModelElement().equals(nodeElement))
				{
					return nodePart;
				}
			}
		}
		return null;
	}
		
	@Override
	public void dispose() 
	{
		super.dispose();
		for (DiagramNodeTemplate nodeTemplate : this.nodeTemplates)
		{
			nodeTemplate.dispose();
		}
		for (DiagramConnectionTemplate connTemplate : this.connectionTemplates)
		{
			connTemplate.dispose();
		}
	}

	private void notifyNodeUpdate(DiagramNodePart nodePart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent nue = new DiagramNodeEvent(nodePart);
				((SapphireDiagramPartListener)listener).handleNodeUpdateEvent(nue);
			}
		}		
	}
	
	private void notifyNodeAdd(DiagramNodePart nodePart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent nue = new DiagramNodeEvent(nodePart);
				((SapphireDiagramPartListener)listener).handleNodeAddEvent(nue);
			}
		}
	}
	
	private void notifyNodeDelete(DiagramNodePart nodePart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent nue = new DiagramNodeEvent(nodePart);
				((SapphireDiagramPartListener)listener).handleNodeDeleteEvent(nue);
			}
		}
	}
	
	private void notifyConnectionUpdate(DiagramConnectionPart connPart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(connPart);
				((SapphireDiagramPartListener)listener).handleConnectionUpdateEvent(cue);
			}
		}		
	}
	
	private void notifyConnectionEndpointUpdate(DiagramConnectionPart connPart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(connPart);
				((SapphireDiagramPartListener)listener).handleConnectionEndpointEvent(cue);
			}
		}		
	}

	private void notifyConnectionAdd(DiagramConnectionPart connPart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(connPart);
				((SapphireDiagramPartListener)listener).handleConnectionAddEvent(cue);
			}
		}		
	}

	private void notifyConnectionDelete(DiagramConnectionPart connPart)
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramConnectionEvent cue = new DiagramConnectionEvent(connPart);
				((SapphireDiagramPartListener)listener).handleConnectionDeleteEvent(cue);
			}
		}		
	}
	
	// --------------------------------------------------------------------
	// Inner classes
	//---------------------------------------------------------------------
	
	private class NodeTemplateListener extends DiagramNodeTemplate.Listener
	{
        @Override
        public void handleNodeUpdate(final DiagramNodePart nodePart)
        {
            notifyNodeUpdate(nodePart);
        }
        
        @Override
        public void handleNodeAdd(final DiagramNodePart nodePart)
        {
        	notifyNodeAdd(nodePart);
        }

        @Override
        public void handleNodeDelete(final DiagramNodePart nodePart)
        {
        	notifyNodeDelete(nodePart);
        }		
	}
	
	private class ConnectionTemplateListener extends DiagramConnectionTemplate.Listener
	{
        @Override
        public void handleConnectionUpdate(final DiagramConnectionPart connPart)
        {
            notifyConnectionUpdate(connPart);
        }
        
        @Override
        public void handleConnectionEndpointUpdate(final DiagramConnectionPart connPart)
        {
            notifyConnectionEndpointUpdate(connPart);
        }

        @Override
        public void handleConnectionAdd(final DiagramConnectionPart connPart)
        {
        	notifyConnectionAdd(connPart);
        }

        @Override
        public void handleConnectionDelete(final DiagramConnectionPart connPart)
        {
        	notifyConnectionDelete(connPart);
        }
		
	}
	
	private class ImplicitConnectionTemplateListener extends DiagramImplicitConnectionTemplate.Listener
	{

        @Override
        public void handleConnectionAdd(final DiagramImplicitConnectionPart connPart)
        {
        	notifyConnectionAdd(connPart);
        }

        @Override
        public void handleConnectionDelete(final DiagramImplicitConnectionPart connPart)
        {
        	notifyConnectionDelete(connPart);
        }
		
	}
	
}
