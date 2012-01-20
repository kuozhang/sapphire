/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.CreateConnectionCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.ReconnectConnectionCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramConnectionEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeEditPolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		CreateConnectionCommand cmd = (CreateConnectionCommand) request.getStartCommand();
		cmd.setTarget((DiagramNodeModel) getHost().getModel());
		return cmd;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		DiagramNodeModel source = (DiagramNodeModel) getHost().getModel();
		Object def = request.getNewObjectType();
		CreateConnectionCommand cmd = new CreateConnectionCommand(source, (IDiagramConnectionDef)def);
		request.setStartCommand(cmd);
		return cmd;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		DiagramNodeModel newTarget = (DiagramNodeModel) getHost().getModel();
		DiagramConnectionEditPart editPart = (DiagramConnectionEditPart)request.getConnectionEditPart();
		ReconnectConnectionCommand cmd = new ReconnectConnectionCommand(editPart.getCastedModel());
		cmd.setNewTarget(newTarget);
		return cmd;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		DiagramNodeModel newSource = (DiagramNodeModel) getHost().getModel();
		DiagramConnectionEditPart editPart = (DiagramConnectionEditPart)request.getConnectionEditPart();
		ReconnectConnectionCommand cmd = new ReconnectConnectionCommand(editPart.getCastedModel());
		cmd.setNewSource(newSource);
		return cmd;
	}

}
