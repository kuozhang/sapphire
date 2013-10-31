/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;


/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionEvent extends DiagramPartEvent 
{
    public static enum ConnectionEventType
    {
        ConnectionUpdate,
        ConnectionEndpointUpdate,
        ConnectionAdd,
        ConnectionDelete,
        ConnectionAddBendpoint,
        ConnectionRemoveBendpoint,
        ConnectionMoveBendpoint,
        ConnectionResetBendpoint,
        ConnectionMoveLabel
    }
    
    private ConnectionEventType connectionEventType;

	public DiagramConnectionEvent(final DiagramConnectionPart part)
    {
         super(part);
    }

    public ConnectionEventType getConnectionEventType() {
		return connectionEventType;
	}

	public void setConnectionEventType(ConnectionEventType connectionEventType) {
		this.connectionEventType = connectionEventType;
	}

}
