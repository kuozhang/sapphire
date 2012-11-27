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

package org.eclipse.sapphire.ui.swt.gef.layout;

import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireStackLayoutConstraint 
{
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;
	private int horizontalMargin, verticalMargin;
	
	public SapphireStackLayoutConstraint()
	{
		this(HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 0, 0);
	}
	
	public SapphireStackLayoutConstraint(HorizontalAlignment horizontalAlignment, 
										VerticalAlignment verticalAlignment)
	{
		this(horizontalAlignment, verticalAlignment, 0, 0);
	}

	public SapphireStackLayoutConstraint(HorizontalAlignment horizontalAlignment, 
										VerticalAlignment verticalAlignment, 
										int horizontalMargin, int verticalMargin)
	{
		this.horizontalAlignment = horizontalAlignment;
		this.verticalAlignment = verticalAlignment;
		this.horizontalMargin = horizontalMargin;
		this.verticalMargin = verticalMargin;
	}

	public HorizontalAlignment getHorizontalAlignment() 
	{
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) 
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public VerticalAlignment getVerticalAlignment() 
	{
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment) 
	{
		this.verticalAlignment = verticalAlignment;
	}

	public int getHorizontalMargin() 
	{
		return horizontalMargin;
	}

	public void setHorizontalMargin(int horizontalMargin) 
	{
		this.horizontalMargin = horizontalMargin;
	}

	public int getVerticalMargin() 
	{
		return verticalMargin;
	}

	public void setVerticalMargin(int verticalMargin) 
	{
		this.verticalMargin = verticalMargin;
	}
		
}
