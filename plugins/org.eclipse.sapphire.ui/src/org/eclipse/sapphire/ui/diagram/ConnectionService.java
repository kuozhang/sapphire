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

import java.util.List;

import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class ConnectionService extends Service 
{
	/**
	 * Returns whether it's valid or not to build a connection between two nodes
	 * 
	 * @param srcNode Source node
	 * @param targetNode Target node
	 * @param connectionType Connection type
	 * @return True if connection is valid, false otherwise
	 */
	public abstract boolean valid(DiagramNodePart srcNode, DiagramNodePart targetNode, String connectionType);
	
	/**
	 * Establish connection between two nodes
	 * @param srcNode Source node
	 * @param targetNode Target node
	 * @param connectionType Connection type
	 * @return The connection part that has been newly established
	 */
	public abstract DiagramConnectionPart connect(DiagramNodePart srcNode, DiagramNodePart targetNode, String connectionType);
		
	public abstract List<DiagramConnectionPart> list();
				
}
