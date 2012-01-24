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
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.DiagramRenderingContext;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DoubleClickNodeCommand extends Command
{
	private DiagramNodePart nodePart;
	
	public DoubleClickNodeCommand(DiagramNodePart nodePart)
	{
		this.nodePart = nodePart;
	}
	
	@Override
	public boolean canExecute()
	{
        SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)this.nodePart.getDefaultActionHandler();
        return (handler != null && handler.canExecute(this.nodePart));		
	}
	
	@Override
	public void execute() 
	{
        SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)this.nodePart.getDefaultActionHandler();
        if (handler != null && handler.canExecute(this.nodePart))
        {
        	DiagramRenderingContext context = new DiagramRenderingContext(this.nodePart);
            handler.execute(context);
        }            
	}
	
}
