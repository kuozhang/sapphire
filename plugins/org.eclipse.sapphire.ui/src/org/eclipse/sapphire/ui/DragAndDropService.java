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

package org.eclipse.sapphire.ui;

import org.eclipse.sapphire.services.Service;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DragAndDropService extends Service 
{
	public abstract boolean canDrop(Object obj);
	
	public abstract Object handleDrop(DropContext context);
	
	public static class DropContext
	{
		private final Object droppedObj;
		private final Point dropPosition;
		
		public DropContext(Object droppedObj, Point dropPosition)
		{
			this.droppedObj = droppedObj;
			this.dropPosition = new Point(dropPosition);
		}
		
		public Object getDroppedObject()
		{
			return this.droppedObj;
		}
		
		public Point getDropPosition()
		{
			return this.dropPosition;
		}
	}
}
