/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.sapphire.ui.swt.gef.parts.ContainerShapeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class ContainerShapeEditPolicy extends XYLayoutEditPolicy {
	

	public ContainerShapeEditPolicy() {
	}
	
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof ContainerShapeEditPart) {
			return new ContainerShapeEditPolicy();
		} else {
			return new NonResizableEditPolicy();
		}
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		if (REQ_CREATE.equals(request.getType()))
			return getHost();
		if (REQ_ADD.equals(request.getType()))
			return getHost();
		if (REQ_MOVE.equals(request.getType())) {
			return getHost().getParent();
		}
		if (REQ_SELECTION.equals(request.getType())) {
			return getHost().getParent();
		}
		return super.getTargetEditPart(request);
	}

}
