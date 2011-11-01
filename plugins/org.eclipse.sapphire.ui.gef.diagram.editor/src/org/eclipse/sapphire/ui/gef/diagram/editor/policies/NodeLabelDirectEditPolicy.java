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

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.LabelNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramNodeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class NodeLabelDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		DiagramNodeEditPart editPart = (DiagramNodeEditPart) getHost();
		LabelNodeCommand command = new LabelNodeCommand((DiagramNodePart)editPart.getModel(), labelText);
		return command;
	}

	/**
	 * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
		String value = (String) request.getCellEditor().getValue();
		((NodeFigure) getHostFigure()).setText(value);
		// hack to prevent async layout from placing the cell editor twice.
		getHostFigure().getUpdateManager().performUpdate();

	}

}
