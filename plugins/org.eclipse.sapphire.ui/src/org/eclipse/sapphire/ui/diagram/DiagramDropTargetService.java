/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram;

import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DiagramDropTargetService 
{

    public static DiagramDropTargetService create(final Class<?> serviceClass)
    {
    	try
    	{
    		final DiagramDropTargetService service = (DiagramDropTargetService) serviceClass.newInstance();
    		return service;
    	}
    	catch( Exception e )
    	{
    		SapphireUiFrameworkPlugin.log( e );
    	}

    	return null;
    }
	
	public boolean accept(Object obj)
	{
		return false;
	}
	
	public Object createModel(DiagramNodeTemplate nodeTemplate, Object obj)
	{
		return null;
	}
}
