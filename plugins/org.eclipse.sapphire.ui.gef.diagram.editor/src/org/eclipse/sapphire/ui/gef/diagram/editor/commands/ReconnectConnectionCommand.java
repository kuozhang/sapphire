/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;

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
		return true;
	}

	private boolean checkTargetReconnection() {
		// connection endpoints must be different 
		if (newTarget.equals(oldSource)) {
			return false;
		}
		return true;
	}

	@Override
	public void execute() {
		// TODO reconnect connectionPart
		//DiagramConnectionPart connectionPart = connection.getModelPart();
		if (newSource != null) {
			System.out.println("TODO reconnect source " + newSource.getLabel());
		} 
		if (newTarget != null) {
			System.out.println("TODO reconnect target " + newTarget.getLabel());
		}
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
