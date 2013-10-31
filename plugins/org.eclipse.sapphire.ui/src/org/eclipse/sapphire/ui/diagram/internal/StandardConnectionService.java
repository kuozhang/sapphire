/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.diagram.ConnectionService;
import org.eclipse.sapphire.ui.diagram.ConnectionServiceEvent;
import org.eclipse.sapphire.ui.diagram.def.ConnectionServiceType;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramExplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImplicitConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent.ConnectionEventType;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate.DiagramConnectionTemplateListener;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionTemplate.DiagramImplicitConnectionTemplateListener;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent.NodeEventType;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.NodeTemplateVisibilityEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class StandardConnectionService extends ConnectionService 
{
	private SapphireDiagramEditorPagePart diagramPagePart;
	private IDiagramEditorPageDef diagramPageDef;
	private List<IDiagramConnectionDef> connectionDefs;
	private Map<DiagramNodeTemplate, DiagramEmbeddedConnectionTemplate> embeddedConnectionTemplateMap;
    private List<DiagramConnectionTemplate> connectionTemplates;
    private List<DiagramImplicitConnectionTemplate> implicitConnectionTemplates;
    private ConnectionTemplateListener connTemplateListener;
    private ImplicitConnectionTemplateListener implicitConnTemplateListener;
    private Listener diagramNodeListener;
    private Listener diagramNodeTemplateListener;
	
    @Override
    protected void init()
    {
    	this.diagramPagePart = context(SapphireDiagramEditorPagePart.class);
    	this.diagramPageDef = (IDiagramEditorPageDef)this.diagramPagePart.getPageDef();
    	this.connectionDefs = this.diagramPageDef.getDiagramConnectionDefs();
    	this.embeddedConnectionTemplateMap = new HashMap<DiagramNodeTemplate, DiagramEmbeddedConnectionTemplate>();
    	this.connTemplateListener = new ConnectionTemplateListener();
    	this.implicitConnTemplateListener = new ImplicitConnectionTemplateListener();
    	
        // Need to initialize the embedded connections after all the diagram node templates are initialized.
        // For connections between "anonymous" nodes, we'd represent connections using node index based
        // mechanism.
        for (DiagramNodeTemplate nodeTemplate : this.diagramPagePart.getNodeTemplates())
        {
            nodeTemplate.initEmbeddedConnections();
            if (nodeTemplate.getEmbeddedConnectionTemplate() != null)
            {
                nodeTemplate.getEmbeddedConnectionTemplate().addTemplateListener(this.connTemplateListener);
                this.embeddedConnectionTemplateMap.put(nodeTemplate, nodeTemplate.getEmbeddedConnectionTemplate());
            }
        }
    	
        // Initialize connection templates
        this.connectionTemplates = new ArrayList<DiagramConnectionTemplate>();
        ElementList<IDiagramExplicitConnectionBindingDef> connectionBindings = this.diagramPageDef.getDiagramConnectionBindingDefs();
        for (IDiagramExplicitConnectionBindingDef connBinding : connectionBindings)
        {
            IDiagramConnectionDef connDef = getDiagramConnectionDef(connBinding.getConnectionId().content());
            DiagramConnectionTemplate connectionTemplate = new DiagramConnectionTemplate(connBinding);
            connectionTemplate.init(this.diagramPagePart, this.diagramPagePart.getLocalModelElement(), 
            		connDef, Collections.<String,String>emptyMap());
            connectionTemplate.initialize();
            this.connectionTemplates.add(connectionTemplate);
            connectionTemplate.addTemplateListener(this.connTemplateListener);
        }

        // initialize implicit connections
        this.implicitConnectionTemplates = new ArrayList<DiagramImplicitConnectionTemplate>();
        ElementList<IDiagramImplicitConnectionBindingDef> implicitConnBindings = this.diagramPageDef.getImplicitConnectionBindingDefs();
        for (IDiagramImplicitConnectionBindingDef implicitConnBinding : implicitConnBindings)
        {
            IDiagramConnectionDef connDef = getDiagramConnectionDef(implicitConnBinding.getConnectionId().content());
            DiagramImplicitConnectionTemplate connectionTemplate = new DiagramImplicitConnectionTemplate(implicitConnBinding);
            connectionTemplate.init(this.diagramPagePart, this.diagramPagePart.getLocalModelElement(), 
            		connDef, Collections.<String,String>emptyMap());
            connectionTemplate.initialize();
            this.implicitConnectionTemplates.add(connectionTemplate);
            connectionTemplate.addTemplateListener(this.implicitConnTemplateListener);
        }
        
        // Listen to "node about to be deleted" event to remove the connection parent element for 1 x n
        // connection type
		this.diagramNodeListener = new FilteredListener<DiagramNodeEvent>() 
		{
			@Override
			protected void handleTypedEvent(DiagramNodeEvent event) 
			{
		    	if (event.getNodeEventType() == NodeEventType.NodeAboutToBeDeleted)
		    	{
		    		handleNodeAboutToBeDeleted((DiagramNodePart)event.getPart());
		    	}
			}
		};
		this.diagramNodeTemplateListener = new FilteredListener<NodeTemplateVisibilityEvent>() 
		{
			@Override
			protected void handleTypedEvent(NodeTemplateVisibilityEvent event) 
			{
		    	if (event.getNodeTemplate().visible())
		    	{
		    		showAllAttachedConnections(event.getNodeTemplate());
		    	}
		    	else
		    	{
		    		hideAllAttachedConnections(event.getNodeTemplate());
		    	}
			}
		};
		this.diagramPagePart.attach(this.diagramNodeListener);
		this.diagramPagePart.attach(this.diagramNodeTemplateListener);
        
    }

	@Override
	public boolean valid(DiagramNodePart srcNode, DiagramNodePart targetNode, String connectionType) 
	{
		DiagramConnectionTemplate connectionTemplate = getConnectionTemplate(srcNode, connectionType);
		if (connectionTemplate != null)
		{
			return connectionTemplate.canCreateNewConnection(srcNode, targetNode);
		}
		return false;
	}

	@Override
	public StandardDiagramConnectionPart connect(DiagramNodePart srcNode, DiagramNodePart targetNode, String connectionType) 
	{
		DiagramConnectionTemplate connectionTemplate = getConnectionTemplate(srcNode, connectionType);
		if (connectionTemplate != null)
		{
			StandardDiagramConnectionPart connection = connectionTemplate.createNewDiagramConnection(srcNode, targetNode);
			return connection;
		}
		return null;
	}
		
	@Override
	public List<DiagramConnectionPart> list()
	{
        final ListFactory<DiagramConnectionPart> connections = ListFactory.start();
        
        for( DiagramConnectionTemplate template : getAllConnectionTemplates() )
        {
        	for (StandardDiagramConnectionPart connPart : template.getDiagramConnections(null))
        	{
        		connections.add(connPart);
        	}
        }
        
        for (DiagramConnectionTemplate embeddedConnectionTemplate : this.embeddedConnectionTemplateMap.values())
        {
        	for (StandardDiagramConnectionPart connPart : embeddedConnectionTemplate.getDiagramConnections(null))
        	{
        		connections.add(connPart);
        	}
        }
        for( DiagramImplicitConnectionTemplate template : this.implicitConnectionTemplates )
        {
        	for (StandardImplicitConnectionPart connPart : template.getImplicitConnections())
        	{
        		connections.add( connPart );
        	}
        }

        return connections.result();
	}
					
	private void showAllAttachedConnections(DiagramNodeTemplate nodeTemplate)
	{
		if (nodeTemplate == null)
			return;
    	if (nodeTemplate != null)
    	{
	    	List<DiagramConnectionTemplate> connTemplates = getAllConnectionTemplates();
	    	for (DiagramConnectionTemplate connTemplate : connTemplates)
	    	{
	    		connTemplate.showAllConnectionParts(nodeTemplate);
	    	}
	    	DiagramEmbeddedConnectionTemplate embeddedConnTemplate = this.embeddedConnectionTemplateMap.get(nodeTemplate);
	        if (embeddedConnTemplate != null)
	        {
	        	embeddedConnTemplate.showAllConnectionParts(nodeTemplate);
	        }
	    	
	    	refreshImplicitConnections();
    	}
	}
	    
	private void hideAllAttachedConnections(DiagramNodeTemplate nodeTemplate)
	{		
    	if (nodeTemplate == null)
    		return;
    	List<DiagramConnectionTemplate> connTemplates = getAllConnectionTemplates();
    	for (DiagramConnectionTemplate connTemplate : connTemplates)
    	{
    		connTemplate.hideAllConnectionParts(nodeTemplate);
    	}
    	DiagramEmbeddedConnectionTemplate embeddedConnTemplate = this.embeddedConnectionTemplateMap.get(nodeTemplate);
        if (embeddedConnTemplate != null)
        {
        	embeddedConnTemplate.hideAllConnectionParts(nodeTemplate);
        }
    	
    	refreshImplicitConnections();		
	}
    
	private List<DiagramConnectionTemplate> getAllConnectionTemplates()
	{
		return this.connectionTemplates;
	}
    
    private IDiagramConnectionDef getDiagramConnectionDef(String connId)
    {
        if (connId == null)
        {
            throw new IllegalArgumentException();
        }
        
        IDiagramConnectionDef connDef = null;
        for (IDiagramConnectionDef def : this.connectionDefs)
        {
            String id = def.getId().content();
            if (id != null && id.equalsIgnoreCase(connId))
            {
                connDef = def;
                break;
            }
        }
        return connDef;
    }
    
    private DiagramConnectionTemplate getConnectionTemplate(DiagramNodePart srcNode, String connectionType)
    {
		DiagramConnectionTemplate connectionTemplate = null;
		DiagramEmbeddedConnectionTemplate embeddedConnTemplate = 
				this.embeddedConnectionTemplateMap.get(srcNode.getDiagramNodeTemplate());
		if (embeddedConnTemplate != null && 
				embeddedConnTemplate.getConnectionTypeId().equalsIgnoreCase(connectionType))
		{
			connectionTemplate = embeddedConnTemplate;
		}
		else
		{
			for (DiagramConnectionTemplate connectionTemplate2 : getAllConnectionTemplates())
			{
				if (connectionTemplate2.getConnectionTypeId().equalsIgnoreCase(connectionType))
				{
					connectionTemplate = connectionTemplate2;
					break;
				}
			}
		}
		return connectionTemplate;
    }
	
	private void refreshImplicitConnections()
	{		
        for( DiagramImplicitConnectionTemplate template : this.implicitConnectionTemplates )
        {
            template.refreshImplicitConnections();
        }
	}
	    
	private void notifyConnectionUpdate(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionUpdate);
    	this.broadcast(serviceEvent);
	}
	
	private void notifyConnectionEndpointUpdate(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionEndpointUpdate);
    	this.broadcast(serviceEvent);
	}

    private void notifyConnectionAdd(final DiagramConnectionEvent event)
    {
    	ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionAdd);
    	this.broadcast(serviceEvent);
    }

	private void notifyConnectionDelete(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionDelete);
    	this.broadcast(serviceEvent);
	}
	
	private void notifyConnectionAddBendpoint(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionAddBendpoint);
    	this.broadcast(serviceEvent);
	}

	private void notifyConnectionRemoveBendpoint(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionRemoveBendpoint);
    	this.broadcast(serviceEvent);
	}

	private void notifyConnectionMoveBendpoint(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionMoveBendpoint);
    	this.broadcast(serviceEvent);
	}
	
	private void notifyConnectionResetBendpoints(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionResetBendpoint);
    	this.broadcast(serviceEvent);
	}

	private void notifyConnectionMoveLabel(final DiagramConnectionEvent event)
	{
		ConnectionServiceEvent serviceEvent = new ConnectionServiceEvent(this, 
				(StandardDiagramConnectionPart)event.getPart(), ConnectionEventType.ConnectionMoveLabel);
    	this.broadcast(serviceEvent);
	}
	
	private void handleNodeAboutToBeDeleted(DiagramNodePart nodePart)
	{
		Element nodeModel = nodePart.getLocalModelElement();
        // Check top level connections to see whether we need to remove the connection parent element
        for (DiagramConnectionTemplate connTemplate : getAllConnectionTemplates())
        {
            if (connTemplate.getConnectionType() == DiagramConnectionTemplate.ConnectionType.OneToMany)
            {
                Element connParentElement = connTemplate.getConnectionParentElement(nodeModel);
                if (connParentElement != null)
                {
                    ElementList<?> connParentList = (ElementList<?>) connParentElement.parent();
                    connParentList.remove(connParentElement);
                }
            }
        }
		
	}
    
	public class ConnectionTemplateListener extends DiagramConnectionTemplateListener
	{
        @Override
        public void handleConnectionUpdate(final DiagramConnectionEvent event)
        {
            notifyConnectionUpdate(event);
        }
        
        @Override
        public void handleConnectionEndpointUpdate(final DiagramConnectionEvent event)
        {
            notifyConnectionEndpointUpdate(event);
        }

        @Override
        public void handleConnectionAdd(final DiagramConnectionEvent event)
        {
            notifyConnectionAdd(event);
        }

        @Override
        public void handleConnectionDelete(final DiagramConnectionEvent event)
        {
            notifyConnectionDelete(event);
        }
        
        @Override
        public void handleAddBendpoint(final DiagramConnectionEvent event)
        {
            notifyConnectionAddBendpoint(event);
        }

        @Override
        public void handleRemoveBendpoint(final DiagramConnectionEvent event)
        {
            notifyConnectionRemoveBendpoint(event);
        }

        @Override
        public void handleMoveBendpoint(final DiagramConnectionEvent event)
        {
            notifyConnectionMoveBendpoint(event);
        }

        @Override
        public void handleResetBendpoints(final DiagramConnectionEvent event)
        {
            notifyConnectionResetBendpoints(event);
        }

        @Override
        public void handleMoveLabel(final DiagramConnectionEvent event)
        {
            notifyConnectionMoveLabel(event);
        }
	}
    
    public class ImplicitConnectionTemplateListener extends DiagramImplicitConnectionTemplateListener
    {

        @Override
        public void handleConnectionAdd(final DiagramConnectionEvent event)
        {
            notifyConnectionAdd(event);
        }

        @Override
        public void handleConnectionDelete(final DiagramConnectionEvent event)
        {
            notifyConnectionDelete(event);
        }        
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
        	ISapphirePart part = context.find(ISapphirePart.class);
        	if (part instanceof SapphireDiagramEditorPagePart)
        	{
        		SapphireDiagramEditorPagePart diagramPagePart = (SapphireDiagramEditorPagePart)part;
        		IDiagramEditorPageDef pageDef = diagramPagePart.getPageDef();
        		if (pageDef.getConnectionServiceType().content() == ConnectionServiceType.STANDARD)
        		{
        			return true;
        		}
        	}
        	return false;
        }
    }
    	
}
