/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [376245] Revert action in StructuredTextEditor does not revert diagram nodes and connections in SapphireDiagramEditor
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.layout;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.services.Service;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class DiagramLayoutPersistenceService extends Service
{
	public boolean dirty()
	{
	    return false;
	}
	
	public static class DirtyStateEvent extends Event
	{
	    private final boolean before;
	    private final boolean after;
	    
	    public DirtyStateEvent( final boolean before, final boolean after )
	    {
	        this.before = before;
	        this.after = after;
	    }
	    
	    public boolean before()
	    {
	        return this.before;
	    }
	    
	    public boolean after()
	    {
	        return this.after;
	    }
	}
	
}
