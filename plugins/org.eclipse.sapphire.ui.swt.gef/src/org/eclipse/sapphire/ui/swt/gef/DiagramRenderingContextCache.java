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

package org.eclipse.sapphire.ui.swt.gef;

import java.util.HashMap;

import org.eclipse.sapphire.ui.ISapphirePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramRenderingContextCache 
{
	private HashMap<ISapphirePart, DiagramRenderingContext> partContextMap;
	
	public DiagramRenderingContextCache()
	{
		this.partContextMap = new HashMap<ISapphirePart, DiagramRenderingContext>();
	}
		
    public void put(ISapphirePart part, DiagramRenderingContext ctx)
    {
        this.partContextMap.put(part, ctx);
    }
    
    public DiagramRenderingContext get(ISapphirePart part)
    {
        return this.partContextMap.get(part);
    }
    
    public void remove(ISapphirePart part)
    {
    	this.partContextMap.remove(part);
    }
    
    public void clear()
    {
    	this.partContextMap.clear();
    }
}
