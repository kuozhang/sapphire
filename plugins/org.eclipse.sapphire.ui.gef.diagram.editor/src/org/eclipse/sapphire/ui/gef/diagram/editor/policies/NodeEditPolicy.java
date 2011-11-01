package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class NodeEditPolicy extends ComponentEditPolicy {

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object parent = getHost().getParent().getModel();
		Object child = getHost().getModel();
		System.out.println("createDeleteCommand: " + parent + ", " + child);
		return super.createDeleteCommand(deleteRequest);
	}

}
