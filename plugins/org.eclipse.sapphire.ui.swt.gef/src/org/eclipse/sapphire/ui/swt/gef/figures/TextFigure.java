/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Extend Sapphire Diagram Framework to support SQL Schema diagram like editors
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.TextPresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextFigure extends Label 
{
	private DiagramResourceCache resourceCache;
	private TextPresentation textPresentation;
	private Rectangle availableArea;
	private int horizontalAlignment;
	
	public TextFigure(DiagramResourceCache resourceCache, TextPresentation textPresentation)
	{
		this.resourceCache = resourceCache;
		this.textPresentation = textPresentation;
		setForegroundColor(resourceCache.getColor(textPresentation.getTextColor()));
		this.horizontalAlignment = getSwtTextAlignment();
		setLabelAlignment(this.horizontalAlignment);
		setFont(this.resourceCache.getFont(textPresentation.getFontDef()));
		setText(textPresentation.getContent());
	}
	
	public Rectangle getAvailableArea() {
		IFigure nodeFigure = this.textPresentation.getNodeFigure();
		Rectangle nodeBounds = nodeFigure.getBounds();
		return new Rectangle(this.availableArea.x + nodeBounds.x, this.availableArea.y + nodeBounds.y,
				this.availableArea.width, this.availableArea.height);
	}

	public void setAvailableArea(Rectangle availableArea) {
		// Translate the available area to relative to the node. We don't need to
		// adjust the available area when node is moved.
		IFigure nodeFigure = this.textPresentation.getNodeFigure();
		Rectangle nodeBounds = nodeFigure.getBounds();
		this.availableArea = new Rectangle(availableArea.x - nodeBounds.x, availableArea.y - nodeBounds.y,
				availableArea.width, availableArea.height);
		
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	public TextPresentation getTextPresentation()
	{
		return this.textPresentation;
	}
	
	/**
	 * @see IFigure#getMinimumSize(int, int)
	 */
	public Dimension getMinimumSize(int w, int h) 
	{
		if (minSize != null)
			return minSize;
		minSize = new Dimension();
		if (getLayoutManager() != null)
			minSize.setSize(getLayoutManager().getMinimumSize(this, w, h));

		Dimension labelSize;
		if (getTextPresentation().truncatable())
		{
			labelSize = calculateLabelSize(getTextUtilities()
				.getTextExtents(getTruncationString(), getFont())
				.intersect(
						getTextUtilities().getTextExtents(getText(), getFont())));
		}
		else
		{
			labelSize = calculateLabelSize(getTextUtilities().getTextExtents(getText(), getFont()));
		}
		Insets insets = getInsets();
		labelSize.expand(insets.getWidth(), insets.getHeight());
		minSize.union(labelSize);
		return minSize;
	}
	
	private int getSwtTextAlignment()
	{
		int alignment = PositionConstants.CENTER;
		LayoutConstraintDef constraint = this.textPresentation.getLayoutConstraint();
		HorizontalAlignment sapphireAlign = constraint.getHorizontalAlignment().getContent();
		switch (sapphireAlign) 
		{
			case LEFT:
				alignment = PositionConstants.LEFT;
				break;
			case RIGHT:
				alignment = PositionConstants.RIGHT;
				break;
			default:			
				break;
		}
		return alignment;
	}
	
}
