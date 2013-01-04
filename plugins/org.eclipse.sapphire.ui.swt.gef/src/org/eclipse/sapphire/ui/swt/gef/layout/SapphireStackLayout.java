/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireStackLayout extends AbstractLayout 
{
	private Map<IFigure, Object> constraints = new HashMap<IFigure, Object>();
	
	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) 
	{
		List children = container.getChildren();
		if (children.size() > 0)
		{
			IFigure firstChild = (IFigure)children.get(0);
			return firstChild.getPreferredSize().getCopy();
		}
		return null;
	}

	/**
	 * Returns the origin for the given figure.
	 * 
	 * @param parent
	 *            the figure whose origin is requested
	 * @return the origin
	 */
	public Point getOrigin(IFigure parent)
	{
		return parent.getClientArea().getLocation();
	}
	
	public void layout(IFigure parent) 
	{
		List children = parent.getChildren();
		if (children.size() == 0)
		{
			return;
		}
		Point offset = getOrigin(parent);
		IFigure base = (IFigure)children.get(0);
		Dimension baseSize = base.getPreferredSize();
		Rectangle bounds = new Rectangle(0, 0, baseSize.width, baseSize.height);
		bounds = bounds.getTranslated(offset);
		base.setBounds(bounds);
		
		for (int i = 1; i < children.size(); i++)
		{
			IFigure child = (IFigure)children.get(i);
			Dimension childSize = child.getPreferredSize();
			SapphireStackLayoutConstraint constraint = (SapphireStackLayoutConstraint)getConstraint(child);
			int offsetX = 0; 
			int offsetY = 0;
			
			HorizontalAlignment horizontalAlign = constraint.getHorizontalAlignment();
			if (horizontalAlign == HorizontalAlignment.LEFT)
			{
				offsetX = constraint.getLeftMargin();
			}
			else if (horizontalAlign == HorizontalAlignment.RIGHT)
			{
				offsetX = baseSize.width - constraint.getRightMargin() - childSize.width;
			}
			else if (horizontalAlign == HorizontalAlignment.CENTER)
			{
				offsetX = (baseSize.width - childSize.width) >> 1;
			}
			
			VerticalAlignment verticalAlign = constraint.getVerticalAlignment();
			if (verticalAlign == VerticalAlignment.TOP)
			{
				offsetY = constraint.getTopMargin();
			}
			else if (verticalAlign == VerticalAlignment.BOTTOM)
			{
				offsetY = baseSize.height - constraint.getBottomMargin() - childSize.height;
			}
			else if (verticalAlign == VerticalAlignment.CENTER)
			{
				offsetY = (baseSize.height - childSize.height) >> 1;
			}
			Rectangle childBounds = new Rectangle(offsetX, offsetY, childSize.width, childSize.height);
			childBounds = childBounds.getTranslated(offset);
			child.setBounds(childBounds);
		}
	}
	
	/**
	 * @see LayoutManager#getConstraint(IFigure)
	 */
	public Object getConstraint(IFigure figure) 
	{
		return constraints.get(figure);
	}
	
	/**
	 * @see LayoutManager#remove(IFigure)
	 */
	public void remove(IFigure figure) 
	{
		super.remove(figure);
		constraints.remove(figure);
	}

	/**
	 * Sets the layout constraint of the given figure. The constraints can only
	 * be of type {@link Rectangle}.
	 * 
	 * @see LayoutManager#setConstraint(IFigure, Object)
	 * @since 2.0
	 */
	public void setConstraint(IFigure figure, Object newConstraint) 
	{
		super.setConstraint(figure, newConstraint);
		if (newConstraint != null)
			constraints.put(figure, newConstraint);
	}

}
