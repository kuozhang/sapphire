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
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.DeleteNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class NodeEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object child = getHost().getModel();
		if (child instanceof DiagramNodeModel) {
			DiagramNodeModel nodeModel = (DiagramNodeModel)child;
			return new DeleteNodeCommand(nodeModel.getModelPart());
		}
		return super.createDeleteCommand(deleteRequest);
	}

}
