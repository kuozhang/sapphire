/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Implement the reconnect using sapphire diagram part api
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class ReconnectConnectionCommand extends Command {

	private final DiagramConnectionModel connection; 

	private final DiagramNodeModel oldSource;
	private final DiagramNodeModel oldTarget;

	private DiagramNodeModel newSource;
	private DiagramNodeModel newTarget;

	public ReconnectConnectionCommand(DiagramConnectionModel connection) {
		setLabel("connection creation");
		this.connection = connection;
		this.oldSource = connection.getSourceNode();
		this.oldTarget = connection.getTargetNode();
	}

	@Override
	public boolean canExecute() {
		// Don't allow reconnect on implicit connections
		DiagramConnectionPart connectionPart = this.connection.getModelPart();
		if (connectionPart instanceof DiagramImplicitConnectionPart) {
			return false;
		}
		if (newSource != null) {
			return checkSourceReconnection();
		} else if (newTarget != null) {
			return checkTargetReconnection();
		}
		return false;
	}

	private boolean checkSourceReconnection() {
		// connection endpoints must be different 
		if (newSource.equals(oldTarget)) {
			return false;
		}
		DiagramConnectionPart connectionPart = this.connection.getModelPart();
		
		if (!(connectionPart.getDiagramConnectionTemplate().canCreateNewConnection(
				newSource.getModelPart(), oldTarget.getModelPart())))
			return false;

		return true;
	}

	private boolean checkTargetReconnection() {
		// connection endpoints must be different 
		if (newTarget.equals(oldSource)) {
			return false;
		}
		DiagramConnectionPart connectionPart = this.connection.getModelPart();
		
		if (!(connectionPart.getDiagramConnectionTemplate().canCreateNewConnection(
				oldSource.getModelPart(), newTarget.getModelPart())))
			return false;
		
		return true;
	}

	@Override
	public void execute() 
	{
		// Tried to reset the endpoint but it turns out to be very complex. It's easier
		// to delete the connection and recreate a new one
		DiagramConnectionPart connectionPart = connection.getModelPart();
        
        DiagramNodePart srcNode = newSource != null ? newSource.getModelPart() : oldSource.getModelPart();
        DiagramNodePart targetNode = newTarget != null ? newTarget.getModelPart() : oldTarget.getModelPart();
        DiagramConnectionPart newConnPart = 
            connectionPart.getDiagramConnectionTemplate().createNewDiagramConnection(srcNode, targetNode); 

        final Element oldConnElement = connectionPart.getLocalModelElement();
        newConnPart.getLocalModelElement().copy(oldConnElement);
        // Bug 382912 - Reconnecting an existing connection adds a bend point 
        // After the copy, connection endpoint event is triggered which causes SapphireConnectionRouter
        // to be called. Since the old connection hasn't been deleted, a default bend point will be added. 
        newConnPart.removeAllBendpoints();
		
		if (newSource != null) 
		{
			newConnPart.resetEndpoint1(newSource.getModelPart());
		} 
		if (newTarget != null)
		{
			newConnPart.resetEndpoint2(newTarget.getModelPart());
		}
        final ElementList<?> list = (ElementList<?>) oldConnElement.parent();
        list.remove(oldConnElement);
	}

	public void setNewTarget(DiagramNodeModel target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.newTarget = target;
		this.newSource = null;
	}

	public void setNewSource(DiagramNodeModel source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		this.newSource = source;
		this.newTarget = null;
	}

}
