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

package org.eclipse.sapphire.ui.swt.gef.actions;

import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DeleteAllBendpointsActionHandler extends SapphireDiagramActionHandler 
{

	@Override
	public boolean canExecute(Object obj) 
	{
		return isEnabled();
	}
    
	@Override
    public boolean isEnabled()
    {
		DiagramConnectionPart connPart = (DiagramConnectionPart)this.getAction().getPart();
		return connPart.getConnectionBendpoints().size() > 0;
    }

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramConnectionPart connPart = (DiagramConnectionPart)context.getPart();
		connPart.removeAllBendpoints();
		return null;
	}

}
