/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - simplify the delete logic by calling Sapphire delete action
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DeleteNodeCommand extends Command 
{
	
    private static final String DELETE_ACTION_ID = "Sapphire.Delete";

    private DiagramNodePart nodePart;

	public DeleteNodeCommand(DiagramNodePart part) 
	{
		this.nodePart = part;
	}

	@Override
	public boolean canExecute() 
	{
		SapphireActionHandler deleteActionHandler = this.nodePart.getAction(DELETE_ACTION_ID).getFirstActiveHandler();
		return deleteActionHandler.isEnabled();
	}
		
	@Override
	public void execute() 
	{
        SapphireActionHandler deleteActionHandler = this.nodePart.getAction(DELETE_ACTION_ID).getFirstActiveHandler();
        SapphireRenderingContext renderingCtx = new SapphireRenderingContext(this.nodePart, null);
        deleteActionHandler.execute(renderingCtx);		
		
	}
	
}
