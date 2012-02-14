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

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.def.SapphireActionType;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ToggleDiagramGridActionHandler extends SapphireDiagramActionHandler 
{		
    @Override
    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
    	super.init(action, def);
    	if (action.getType() == SapphireActionType.TOGGLE)
    	{
    		SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)this.getPart();
    		setChecked(diagramPart.isGridVisible());
    	}
    }
    
	@Override
	public boolean canExecute(Object obj) 
	{		
		return true;
	}
		
	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)this.getPart();
		boolean visible = !diagramPart.isGridVisible();
		diagramPart.setGridVisible(visible);
		return null;
	}
}
