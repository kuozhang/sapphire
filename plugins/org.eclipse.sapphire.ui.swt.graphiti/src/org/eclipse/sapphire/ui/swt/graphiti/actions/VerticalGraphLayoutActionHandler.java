/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.draw2d.PositionConstants;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class VerticalGraphLayoutActionHandler extends
		DiagramGraphLayoutActionHandler 
{

	@Override
	public int getGraphDirection() 
	{
		return PositionConstants.NORTH_SOUTH;
	}

}
