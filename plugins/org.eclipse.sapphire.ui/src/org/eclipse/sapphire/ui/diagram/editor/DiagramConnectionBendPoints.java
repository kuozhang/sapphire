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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Point;


/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionBendPoints 
{
	private List<Point> bendPoints;
	private boolean isAutoLayout;
	private boolean isDefault;
	
	public DiagramConnectionBendPoints()
	{
		this.bendPoints = new ArrayList<Point>();
		this.isAutoLayout = false;
		this.isDefault = false;
	}
	
	public DiagramConnectionBendPoints(List<Point> bendPoints)
	{
		this(bendPoints, false, false);
	}
	
	public DiagramConnectionBendPoints(List<Point> bendPoints, boolean isAutoLayout, boolean isDefault)
	{
		this.bendPoints = new ArrayList<Point>(bendPoints.size());
		this.bendPoints.addAll(bendPoints);
		this.isAutoLayout = isAutoLayout;
		this.isDefault = isDefault;
	}
	
	public DiagramConnectionBendPoints(DiagramConnectionBendPoints bendPoints)
	{
		this(bendPoints.getBendPoints(), bendPoints.isAutoLayout(), bendPoints.isDefault());
	}
	
	public List<Point> getBendPoints()
	{
		return this.bendPoints;
	}
	
	public void setBendPoints(List<Point> bendPoints)
	{
		this.bendPoints.clear();
		this.bendPoints.addAll(bendPoints);
	}
	
	public boolean isEmpty()
	{
		return this.bendPoints.isEmpty();
	}
	
	public int size()
	{
		return this.bendPoints.size();
	}
	
	public Point get(int index)
	{
		return this.bendPoints.get(index);
	}
	
	public boolean isAutoLayout()
	{
		return this.isAutoLayout;
	}
	
	public void setAutoLayout(boolean isAutoLayout)
	{
		this.isAutoLayout = isAutoLayout;
	}
	
	public boolean isDefault()
	{
		return this.isDefault;
	}
	
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (obj instanceof DiagramConnectionBendPoints)
		{
			DiagramConnectionBendPoints bendPoints = (DiagramConnectionBendPoints)obj;
			if (bendPoints.getBendPoints().size() == getBendPoints().size() && 
					bendPoints.isAutoLayout() == isAutoLayout() &&
					bendPoints.isDefault() == isDefault())
			{
				isEqual = true;
				for (int i = 0; i < bendPoints.size(); i++)
				{
					Point point1 = bendPoints.get(i);
					Point point2 = get(i);
					if (!point1.equals(point2))
					{
						isEqual = false;
						break;
					}
				}
				
			}
		}
		return isEqual;
	}
}
