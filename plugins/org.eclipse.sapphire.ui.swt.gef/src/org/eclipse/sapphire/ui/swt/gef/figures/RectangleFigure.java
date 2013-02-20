/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientSegmentDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutOrientation;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SolidBackgroundDef;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireSequenceLayout;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayout;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.RectanglePresentation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Path;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class RectangleFigure extends ContainerShapeFigure implements IShapeFigure
{	
	private static final org.eclipse.sapphire.Color SELECTED_BACKGROUND = new org.eclipse.sapphire.Color(0xAC, 0xD2, 0xF4);
    private static final org.eclipse.sapphire.Color DEFAULT_BACKGROUND_START = new org.eclipse.sapphire.Color(0xFF, 0xFF, 0xFF);
    private static final org.eclipse.sapphire.Color DEFAULT_BACKGROUND_END = new org.eclipse.sapphire.Color(0xD4, 0xE7, 0xF8);
	
	private RectanglePresentation rectPresentation;
	private ShapeLayoutDef layout;
	private DiagramResourceCache resourceCache;
    private boolean selected;
	private boolean hasFocus;
	
	public RectangleFigure(RectanglePresentation rectPresentation, DiagramResourceCache resourceCache)
	{
		super(rectPresentation, resourceCache);
		this.rectPresentation = rectPresentation;
		this.layout = rectPresentation.getLayout();
		this.resourceCache = resourceCache;		
		
		if (this.layout instanceof SequenceLayoutDef)
		{
			if (((SequenceLayoutDef)this.layout).getOrientation().getContent() != SequenceLayoutOrientation.STACKED)
			{
				SapphireSequenceLayout sequenceLayout = new SapphireSequenceLayout((SequenceLayoutDef)layout);
				this.setLayoutManager(sequenceLayout);
			}
			else 
			{
				SapphireStackLayout sapphireStackLayout = new SapphireStackLayout();
				this.setLayoutManager(sapphireStackLayout);
			}
		}
		setBorder(new RectangleBorder(this.rectPresentation, this.resourceCache));
	}
	
	@Override
	protected void fillShape(Graphics graphics) 
	{
		BackgroundDef bg = this.rectPresentation.getBackground();
		if (bg != null)
		{
			final Color foregroundSave = graphics.getForegroundColor();
			final Color backgroundSave = graphics.getBackgroundColor();

			org.eclipse.draw2d.geometry.Rectangle fillRectangle = 
					new org.eclipse.draw2d.geometry.Rectangle(getBounds());

			if (this.rectPresentation.getCornerRadius() > 0)
			{
				Path path = createPath(fillRectangle, graphics, true, this.rectPresentation.getCornerRadius());
				graphics.clipPath(path);
			}
			
			if (selected) 
			{
				graphics.setBackgroundColor(resourceCache.getColor(SELECTED_BACKGROUND));
				graphics.fillRectangle(fillRectangle);
			} 
			else 
			{
				if (bg instanceof SolidBackgroundDef)
				{
					org.eclipse.sapphire.Color color = ((SolidBackgroundDef)bg).getColor().getContent();
					if (color != null)
					{
						graphics.setBackgroundColor(resourceCache.getColor(color));
					}
					else
					{
						graphics.setBackgroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_END));
					}
					graphics.fillRectangle(fillRectangle);
				}
				else if (bg instanceof GradientBackgroundDef)
				{
					boolean isVertical = ((GradientBackgroundDef)bg).isVertical().getContent();
					ModelElementList<GradientSegmentDef> segments = ((GradientBackgroundDef)bg).getGradientSegments();
					if (segments.size() == 0)
					{
						graphics.setForegroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_END));
						graphics.setBackgroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_START));
					}
					else
					{
						GradientSegmentDef segment0 = segments.get(0);
						GradientSegmentDef segment1 = segments.get(1);
						graphics.setForegroundColor(resourceCache.getColor(segment0.getColor().getContent()));
						graphics.setBackgroundColor(resourceCache.getColor(segment1.getColor().getContent()));
					}
					
					graphics.fillGradient(fillRectangle.x, fillRectangle.y, fillRectangle.width, fillRectangle.height, isVertical);
				}
			}
			
			graphics.setForegroundColor(foregroundSave);
			graphics.setBackgroundColor(backgroundSave);
		}
	}
	
	public void setSelected(boolean b) 
	{
		selected = b;
		repaint();
	}

	public void setFocus(boolean b) 
	{
		hasFocus = b;
		repaint();
	}
	
	private Path createPath(org.eclipse.draw2d.geometry.Rectangle outerBounds, Graphics graphics, boolean isFill,
			int cornerRadius)
	{
		Path path = new Path(null);
		
		float x = outerBounds.x;
		float y = outerBounds.y;
		float height = outerBounds.height;
		float width = outerBounds.width;
		float bottom = y + height;
		float right = x + width;		

		// the half cornersize is the length of the arc,
		// so two time the half cornersize must not be longer than the side
		// itself
		float cornerWidth = cornerRadius;
		float cornerHeight = cornerRadius;
		cornerWidth = (cornerWidth > width) ? width : cornerWidth;
		cornerHeight = (cornerHeight > height) ? height : cornerHeight;

		if (isFill) {
			// adjust corner for the inner path (formula found by experimenting)
			cornerHeight = Math.max(1, cornerHeight - (getLineWidth() + cornerHeight / 64));
			cornerWidth = Math.max(1, cornerWidth - (getLineWidth() + cornerWidth / 64));
		}

		// workaround: path must be usual rectangle, if corner=0
		// otherwise the path is not drawn at all (same happens
		// RoundedRectangles)
		if (cornerHeight <= 0 || cornerWidth <= 0) {
			path.addRectangle(x, y, width, height);
		} else {
			path.moveTo(x, y);
			path.addArc(x, y, cornerWidth, cornerHeight, 90, 90);
			path.addArc(x, bottom - cornerHeight, cornerWidth, cornerHeight, 180, 90);
			path.addArc(right - cornerWidth, bottom - cornerHeight, cornerWidth, cornerHeight, 270, 90);
			path.addArc(right - cornerWidth, y, cornerWidth, cornerHeight, 0, 90);
			path.close();
		}

		return path;
		
	}
}
