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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class MoveNodeCommand extends Command {
	
	private DiagramNodePart part;
	private Rectangle rectangle;

	public MoveNodeCommand(DiagramNodePart part, Rectangle rectangle) {
		this.part = part;
		this.rectangle = rectangle;
	}

	@Override
	public void execute() {
		part.setNodePosition(rectangle.x, rectangle.y);
	}
	
}