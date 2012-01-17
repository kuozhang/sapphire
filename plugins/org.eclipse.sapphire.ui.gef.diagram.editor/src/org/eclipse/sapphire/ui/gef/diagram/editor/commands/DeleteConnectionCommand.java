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

package org.eclipse.sapphire.ui.gef.diagram.editor.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DeleteConnectionCommand extends Command 
{
    private static final String DELETE_ACTION_ID = "Sapphire.Delete";

    private DiagramConnectionPart connectionPart;
    
	public DeleteConnectionCommand(DiagramConnectionPart part) 
	{
		this.connectionPart = part;
	}

	@Override
	public void execute() 
	{
        SapphireActionHandler deleteActionHandler = this.connectionPart.getAction(DELETE_ACTION_ID).getFirstActiveHandler();
        SapphireRenderingContext renderingCtx = new SapphireRenderingContext(this.connectionPart, null);
        deleteActionHandler.execute(renderingCtx);		
	}
}
