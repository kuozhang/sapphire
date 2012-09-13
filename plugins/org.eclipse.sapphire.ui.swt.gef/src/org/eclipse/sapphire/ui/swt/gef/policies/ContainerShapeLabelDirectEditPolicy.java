/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.swt.gef.commands.LabelEditCommand;
import org.eclipse.sapphire.ui.swt.gef.parts.ContainerShapeEditPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeLabelDirectEditPolicy extends DirectEditPolicy 
{

	@Override
	protected Command getDirectEditCommand(DirectEditRequest request) 
	{
		String labelText = (String) request.getCellEditor().getValue();
		ContainerShapeEditPart editPart = (ContainerShapeEditPart) getHost();
		ContainerShapePart containerPart = (ContainerShapePart)editPart.getCastedModel().getSapphirePart();
		LabelEditCommand command = new LabelEditCommand(containerPart.getTextPart(), labelText);
		return command;
	}

	@Override
	protected void showCurrentEditValue(DirectEditRequest request) 
	{
		// TODO Auto-generated method stub
		
	}

}
