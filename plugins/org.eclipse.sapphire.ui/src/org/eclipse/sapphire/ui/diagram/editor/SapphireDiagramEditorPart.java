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
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPageDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramEditorPart extends SapphirePart
{
    private IModelElement modelElement;
    private IDiagramPageDef diagramPageDef = null;
    private List<DiagramNodeTemplate> nodeTemplates;
    private List<DiagramConnectionTemplate> connectionTemplates;
    private NodeTemplateListener nodeTemplateListener;
    private ConnectionTemplateListener connTemplateListener;
	    
    @Override
    protected void init()
    {
        super.init();
            
		this.modelElement = getModelElement();
		this.diagramPageDef = (IDiagramPageDef)super.definition;
        this.nodeTemplateListener = new NodeTemplateListener();
        this.connTemplateListener = new ConnectionTemplateListener();
        
        this.nodeTemplates = new ArrayList<DiagramNodeTemplate>();
        ModelElementList<IDiagramNodeDef> nodeDefs = this.diagramPageDef.getDiagramNodeDefs();
        
        for (IDiagramNodeDef nodeDef : nodeDefs)
        {
        	DiagramNodeTemplate nodeTemplate = new DiagramNodeTemplate(this, nodeDef, this.modelElement);
        	this.nodeTemplates.add(nodeTemplate);
        	nodeTemplate.addTemplateListener(this.nodeTemplateListener);
        	
        	if (nodeTemplate.getEmbeddedConnectionTemplate() != null)
        	{
        		nodeTemplate.getEmbeddedConnectionTemplate().addTemplateListener(this.connTemplateListener);
        	}
        }
                
        this.connectionTemplates = new ArrayList<DiagramConnectionTemplate>();
        ModelElementList<IDiagramConnectionDef> connectionDefs = this.diagramPageDef.getDiagramConnectionDefs();
        for (IDiagramConnectionDef connectionDef : connectionDefs)
        {
        	DiagramConnectionTemplate connectionTemplate = new DiagramConnectionTemplate(this, connectionDef, this.modelElement);
        	this.connectionTemplates.add(connectionTemplate);
        	connectionTemplate.addTemplateListener(this.connTemplateListener);
        }       
        
	}

	public List<DiagramNodeTemplate> getNodeTemplates()
	{
		return this.nodeTemplates;
	}
	
	public List<DiagramConnectionTemplate> getConnectionTemplates()
	{
		return this.connectionTemplates;
	}
			
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}
	
	public DiagramNodePart getDiagramNodePart(IModelElement nodeElement)
	{
		if (nodeElement == null)
			return null;
		
		List<DiagramNodeTemplate> nodeTemplates = this.getNodeTemplates();
		for (DiagramNodeTemplate nodeTemplate : nodeTemplates)
		{
			if (nodeTemplate.getNodeType().equals(nodeElement.getModelElementType()))
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
}
