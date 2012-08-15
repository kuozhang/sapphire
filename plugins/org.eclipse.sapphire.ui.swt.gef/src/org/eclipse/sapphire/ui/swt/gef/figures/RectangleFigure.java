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
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientSegmentDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SolidBackgroundDef;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectangleFigure extends org.eclipse.draw2d.Shape 
{	
	private static final org.eclipse.sapphire.ui.Color SELECTED_BACKGROUND = new org.eclipse.sapphire.ui.Color(0xAC, 0xD2, 0xF4);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_START = new org.eclipse.sapphire.ui.Color(0xFF, 0xFF, 0xFF);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_END = new org.eclipse.sapphire.ui.Color(0xD4, 0xE7, 0xF8);
    private static final org.eclipse.sapphire.ui.Color OUTLINE_FOREGROUND = new org.eclipse.sapphire.ui.Color(0xFF, 0xA5, 0x00);
	
	private RectanglePart rectPart;
	private int validationMarkerIndex;
	private IModelElement model;
	private ShapeLayoutDef layout;
	private DiagramResourceCache resourceCache;
	private Status validationStatus;
    private boolean selected;
	private boolean hasFocus;
	
	public RectangleFigure(RectanglePart rectPart, int numChildren, int validationMarkerIndex, IModelElement model,
			DiagramResourceCache resourceCache)
	{
		this.rectPart = rectPart;
		this.validationMarkerIndex = validationMarkerIndex;
		this.model = model;
		this.layout = rectPart.getLayout();
		this.resourceCache = resourceCache;
		
		this.validationStatus = model.validation();
		
		if (this.layout instanceof SequenceLayoutDef)
		{
			SequenceLayoutDef sequenceLayout = (SequenceLayoutDef)layout;
			GridLayout gridLayout = new GridLayout();
			if (sequenceLayout.getOrientation().getContent() == Orientation.HORIZONTAL)
			{
				gridLayout.numColumns = numChildren - (showValidationMarker() ? 0 : 1);
				gridLayout.horizontalSpacing = sequenceLayout.getSpacing().getContent();
			}
			else
			{
				gridLayout.numColumns = 1;
				gridLayout.verticalSpacing = sequenceLayout.getSpacing().getContent();
			}
			this.setLayoutManager(gridLayout);
			
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
		
		if (this.rectPart.hasBorder())
		{	
			graphics.setLineWidth(this.rectPart.getBorderWidth());
			if (this.rectPart.getBorderColor() != null)
				graphics.setForegroundColor(resourceCache.getColor(this.rectPart.getBorderColor()));
			if (this.rectPart.getBorderStyle() != LineStyle.SOLID)
			{
				graphics.setLineStyle(convertLineStyle(this.rectPart.getBorderStyle()));
			}
			graphics.drawRoundRectangle(r,
					Math.max(0, cornerDimension.width - (int) lineInset),
					Math.max(0, cornerDimension.height - (int) lineInset));
		}
		
//		if (hasFocus || selected) 
//		{
//			graphics.setForegroundColor(resourceCache.getColor(OUTLINE_FOREGROUND));
//			org.eclipse.draw2d.geometry.Rectangle expanded = r.getExpanded(1, 1);
//			graphics.setLineStyle(SWT.LINE_DASH);
//			graphics.drawRoundRectangle(expanded,
//					Math.max(0, cornerDimension.width - (int) lineInset),
//					Math.max(0, cornerDimension.height - (int) lineInset));				
//		}
		
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
	
	public void refreshValidationStatus()
	{
		if (this.validationMarkerIndex == -1)
		{
			return;
		}
		Status newStatus = this.model.validation();
		
		GridLayout gridLayout = null;
		boolean isHorizontalSequenceLayout = isHorizontalSequenceLayout(); 
		if (isHorizontalSequenceLayout)
		{
			gridLayout = (GridLayout)this.getLayoutManager();
		}
		if (!newStatus.equals(this.validationStatus))
		{			
			if (this.validationStatus.severity() != Status.Severity.OK)
			{
				if (isHorizontalSequenceLayout)
				{
					gridLayout.numColumns--;
				}
			}
			if (newStatus.severity() != Status.Severity.OK)
			{
				if (isHorizontalSequenceLayout)
				{
					gridLayout.numColumns++;
				}
			}
			this.validationStatus = newStatus;
			this.layout();
		}
	}
	
	private boolean showValidationMarker()
	{
		boolean show = false;
		if (this.validationMarkerIndex != -1)
		{
			Status status = this.model.validation();		
			show =  status.severity() != Status.Severity.OK;
		}
		return show;
	}
	
	private boolean isHorizontalSequenceLayout()
	{		
		if (this.layout instanceof SequenceLayoutDef)
		{
			SequenceLayoutDef sequenceLayout = (SequenceLayoutDef)layout;				
			if (sequenceLayout.getOrientation().getContent() == Orientation.HORIZONTAL)	
			{
				return true;
			}
		}
		return false;
		
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
