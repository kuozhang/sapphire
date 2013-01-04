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

package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * This layout policy is needed for DiagramNodeEditPart for updating the edit part
 * for a request. It inherits "public EditPart getTargetEditPart(Request request)"
 * from LayoutEditPolicy parent class. It'll also be needed when we start to support
 * node containment.
 */

public class NodeLayoutEditPolicy extends XYLayoutEditPolicy 
{

	@Override
	protected Command getCreateCommand(CreateRequest request) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) 
	{
		return null;
	}
	
}
