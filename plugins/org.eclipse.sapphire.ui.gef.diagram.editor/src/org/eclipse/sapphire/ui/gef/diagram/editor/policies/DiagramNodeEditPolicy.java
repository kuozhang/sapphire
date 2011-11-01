package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.CreateConnectionCommand;

public class DiagramNodeEditPolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		CreateConnectionCommand cmd = (CreateConnectionCommand) request.getStartCommand();
		cmd.setTarget((DiagramNodePart) getHost().getModel());
		return cmd;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		DiagramNodePart source = (DiagramNodePart) getHost().getModel();
		Object def = request.getNewObjectType();
		CreateConnectionCommand cmd = new CreateConnectionCommand(source, (IDiagramConnectionDef)def);
		request.setStartCommand(cmd);
		return cmd;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
