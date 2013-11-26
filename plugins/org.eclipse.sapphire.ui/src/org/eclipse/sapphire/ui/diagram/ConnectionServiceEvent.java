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

package org.eclipse.sapphire.ui.diagram;

import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent.ConnectionEventType;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ConnectionServiceEvent extends ServiceEvent 
{	
    private DiagramConnectionPart connPart;
    private ConnectionEventType type;
    
	public ConnectionServiceEvent(Service service, DiagramConnectionPart connPart, ConnectionEventType type)
	{
		super(service);
		this.connPart = connPart;
		this.type = type;
	}
	
	public DiagramConnectionPart getConnectionPart()
	{
		return this.connPart;
	}
	
    public ConnectionEventType getConnectionEventType() 
    {
		return type;
	}

	public void setConnectionEventType(ConnectionEventType connectionEventType) 
	{
		this.type = connectionEventType;
	}
	
}
