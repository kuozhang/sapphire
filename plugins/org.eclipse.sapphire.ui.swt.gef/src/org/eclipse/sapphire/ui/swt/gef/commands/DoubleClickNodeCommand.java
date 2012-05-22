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

package org.eclipse.sapphire.ui.swt.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.gef.parts.IConfigurationManagerHolder;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DoubleClickNodeCommand extends Command
{
	private DiagramNodePart nodePart;
	private IConfigurationManagerHolder configHolder;
	
	public DoubleClickNodeCommand(IConfigurationManagerHolder configHolder, DiagramNodePart nodePart)
	{
		this.configHolder = configHolder;
		this.nodePart = nodePart;
	}
	
	@Override
	public boolean canExecute()
	{
        SapphireActionHandler handler = (SapphireActionHandler)this.nodePart.getDefaultActionHandler();
        return (handler != null && handler.isEnabled());		
	}
	
	@Override
	public void execute() 
	{
        SapphireActionHandler handler = (SapphireActionHandler)this.nodePart.getDefaultActionHandler();
        if (handler != null && handler.isEnabled())
        {
        	DiagramConfigurationManager configManager = configHolder.getConfigurationManager();
        	DiagramRenderingContext context = configManager.getDiagramRenderingContextCache().get(this.nodePart);
            handler.execute(context);
        }            
	}
	
}
