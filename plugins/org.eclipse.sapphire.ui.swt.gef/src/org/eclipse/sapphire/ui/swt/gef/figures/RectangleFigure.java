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

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientSegmentDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SolidBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireSequenceLayout;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayout;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectangleFigure extends ContainerShapeFigure implements IShapeFigure
{	
	private static final org.eclipse.sapphire.ui.Color SELECTED_BACKGROUND = new org.eclipse.sapphire.ui.Color(0xAC, 0xD2, 0xF4);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_START = new org.eclipse.sapphire.ui.Color(0xFF, 0xFF, 0xFF);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_END = new org.eclipse.sapphire.ui.Color(0xD4, 0xE7, 0xF8);
    private static final org.eclipse.sapphire.ui.Color OUTLINE_FOREGROUND = new org.eclipse.sapphire.ui.Color(0xFF, 0xA5, 0x00);
	
	private RectanglePart rectPart;
	private ShapeLayoutDef layout;
	private DiagramResourceCache resourceCache;
    private boolean selected;
	private boolean hasFocus;
	
	public RectangleFigure(RectanglePart rectPart, DiagramResourceCache resourceCache)
	{
		super(rectPart, resourceCache);
		this.rectPart = rectPart;
		this.layout = rectPart.getLayout();
		this.resourceCache = resourceCache;		
		
		if (this.layout instanceof SequenceLayoutDef)
		{
			SequenceLayoutDef sequenceLayoutDef = (SequenceLayoutDef)layout;
			SapphireSequenceLayout sequenceLayout = new SapphireSequenceLayout(sequenceLayoutDef.getOrientation().getContent() == Orientation.HORIZONTAL);
			sequenceLayout.setSpacing(sequenceLayoutDef.getSpacing().getContent());
			this.setLayoutManager(sequenceLayout);
		}
		else if (this.layout instanceof StackLayoutDef)
		{
			SapphireStackLayout sapphireStackLayout = new SapphireStackLayout();
			this.setLayoutManager(sapphireStackLayout);
		}
	}
	
	@Override
	protected void fillShape(Graphics graphics) 
	{
		BackgroundDef bg = this.rectPart.getBackground();
		if (bg != null)
		{
			final Dimension cornerDimension = new Dimension(1, 1);
			org.eclipse.draw2d.geometry.Rectangle fillRectangle = 
					new org.eclipse.draw2d.geometry.Rectangle(getBounds());
			fillRectangle = fillRectangle.shrink(cornerDimension.width, cornerDimension.height);
			
			final Color foregroundSave = graphics.getForegroundColor();
			final Color backgroundSave = graphics.getBackgroundColor();
			
			if (selected) 
			{
				graphics.setBackgroundColor(resourceCache.getColor(SELECTED_BACKGROUND));
				graphics.fillRectangle(fillRectangle);
			} 
			else 
			{
				if (bg instanceof SolidBackgroundDef)
				{
					org.eclipse.sapphire.ui.Color color = ((SolidBackgroundDef)bg).getColor().getContent();
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
	
	@Override
	protected void outlineShape(Graphics graphics) 
	{
		float lineInset = Math.max(1.0f, getLineWidthFloat()) / 2.0f;
		int inset1 = (int) Math.floor(lineInset) + 1;
		int inset2 = (int) Math.ceil(lineInset) + 1;

		org.eclipse.draw2d.geometry.Rectangle r = 
				org.eclipse.draw2d.geometry.Rectangle.SINGLETON.setBounds(getBounds());
		r.x += inset1;
		r.y += inset1;
		r.width -= inset1 + inset2;
		r.height -= inset1 + inset2;

		// Save existing graphics attributes
		final int oldLineWidth = graphics.getLineWidth();
		final Color oldColor = graphics.getForegroundColor();
		final int oldLineStyle = graphics.getLineStyle();
		
		int cornerRadius = this.rectPart.getCornerRadius();
		final Dimension cornerDimension = new Dimension(cornerRadius, cornerRadius); 
		
		BorderDef borderDef = this.rectPart.getBorderDef(); 
		if (borderDef != null)
		{	
			graphics.setLineWidth(borderDef.getWidth().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(convertLineStyle(borderDef.getStyle().getContent()));
			graphics.drawRoundRectangle(r,
					Math.max(0, cornerDimension.width - (int) lineInset),
					Math.max(0, cornerDimension.height - (int) lineInset));
		}
		
		borderDef = this.rectPart.getTopBorderDef(); 
		if (borderDef != null)
		{	
			graphics.setLineWidth(borderDef.getWidth().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(convertLineStyle(borderDef.getStyle().getContent()));
			graphics.drawLine(r.x, r.y, r.x + r.width, r.y);
		}

		borderDef = this.rectPart.getBottomBorderDef(); 
		if (borderDef != null)
		{	
			graphics.setLineWidth(borderDef.getWidth().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(convertLineStyle(borderDef.getStyle().getContent()));
			graphics.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
		}

		borderDef = this.rectPart.getRightBorderDef(); 
		if (borderDef != null)
		{	
			graphics.setLineWidth(borderDef.getWidth().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(convertLineStyle(borderDef.getStyle().getContent()));
			graphics.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
		}

		borderDef = this.rectPart.getLeftBorderDef(); 
		if (borderDef != null)
		{	
			graphics.setLineWidth(borderDef.getWidth().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(convertLineStyle(borderDef.getStyle().getContent()));
			graphics.drawLine(r.x, r.y, r.x, r.y + r.height);
		}

		if (hasFocus || selected) 
		{
			graphics.setForegroundColor(resourceCache.getColor(OUTLINE_FOREGROUND));
			org.eclipse.draw2d.geometry.Rectangle expanded = r.getExpanded(1, 1);
			graphics.setLineStyle(SWT.LINE_DASH);
			graphics.drawRoundRectangle(expanded,
					Math.max(0, cornerDimension.width - (int) lineInset),
					Math.max(0, cornerDimension.height - (int) lineInset));				
		}
		
		// Restore previous graphics attributes
		graphics.setLineWidth(oldLineWidth);
		graphics.setForegroundColor(oldColor);
		graphics.setLineStyle(oldLineStyle);
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
	
	private int convertLineStyle(LineStyle style)
	{
		int swtStyle = SWT.LINE_SOLID;
		if (style == LineStyle.DASH)
		{
			swtStyle = SWT.LINE_DASH;
		}
		else if (style == LineStyle.DASH_DOT)
		{
			swtStyle = SWT.LINE_DASHDOT;
		}
		else if (style == LineStyle.DOT)
		{
			swtStyle = SWT.LINE_DOT;
		}
		return swtStyle;
	}
	
}
