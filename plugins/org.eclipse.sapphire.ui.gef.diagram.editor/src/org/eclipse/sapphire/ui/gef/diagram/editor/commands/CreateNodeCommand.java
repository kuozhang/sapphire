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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class CreateNodeCommand extends Command {
	
	private DiagramNodeTemplate nodeTemplate;
	private Point location;

	public CreateNodeCommand(DiagramModel diagramModel, DiagramNodeTemplate nodeTemplate, Point location) {
		this.nodeTemplate = nodeTemplate;
		this.location = location;
	}

	@Override
	public void execute() {
		DiagramNodePart newPart = this.nodeTemplate.createNewDiagramNode();
		newPart.setNodePosition(location.x, location.y);		
	}
	
	
}
