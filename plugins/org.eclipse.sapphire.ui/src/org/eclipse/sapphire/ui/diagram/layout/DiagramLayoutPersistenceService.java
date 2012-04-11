/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.layout;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DiagramLayoutPersistenceService extends Service
{
	private SapphireDiagramEditorPagePart diagramPart;
	private Set<String> persistedNodes;
	private Set<String> persistedConnections;
	
	@Override
    protected void init()
    {
		super.init();
		this.diagramPart = (SapphireDiagramEditorPagePart)context().find(ISapphirePart.class);
    	this.persistedNodes = new HashSet<String>();
    	this.persistedConnections = new HashSet<String>();
    	refreshPersistedPartsCache();
    }	
	
	public abstract void read(DiagramNodePart nodePart);
	
	public abstract void read(DiagramConnectionPart connectionPart);
	
	public SapphireDiagramEditorPagePart getDiagramEditorPagePart()
	{
		return this.diagramPart;
	}
	
	/**
	 * When a node part is created, diagram layout persistence service is consulted
	 * to see whether the node has been persisted in the persistence service.
	 * 
	 * @param nodePart the node part
	 * @return true if the node was persisted in the persistence service; false otherwise
	 * 
	 */
	public boolean isNodePersisted(DiagramNodePart nodePart)
	{
		String nodeId = IdUtil.computeNodeId(nodePart); 
		if (this.persistedNodes.contains(nodeId))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * When a connection part is created, diagram layout persistence service is consulted
	 * to see whether the connection has been persisted in the persistence service.
	 * 
	 * @param connectionPart the connection part
	 * @return true if the connection was persisted in the persistence service; false otherwise
	 * 
	 */	
	public boolean isConnectionPersisted(DiagramConnectionPart connPart)
	{
		String connId = IdUtil.computeConnectionId(connPart); 
		if (this.persistedConnections.contains(connId))
		{
			return true;
		}
		return false;
	}

	protected void refreshPersistedPartsCache()
	{
		this.persistedConnections.clear();
		this.persistedNodes.clear();
		for (DiagramConnectionPart connPart : this.diagramPart.getConnections())
		{
			String connId = IdUtil.computeConnectionId(connPart);
			this.persistedConnections.add(connId);
		}
		for (DiagramNodePart nodePart : this.diagramPart.getNodes())
		{
			String nodeId = IdUtil.computeNodeId(nodePart);
			this.persistedNodes.add(nodeId);
		}		
	}
	
}
