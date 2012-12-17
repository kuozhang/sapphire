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

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderComponent;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.RectanglePresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectangleBorder extends AbstractBorder 
{
	private RectanglePresentation rectPresentation;
	private DiagramResourceCache resourceCache;
	private boolean hasUniformBorders;
	
	public RectangleBorder(RectanglePresentation rectPresentation, DiagramResourceCache resourceCache)
	{
		this.rectPresentation = rectPresentation;
		this.resourceCache = resourceCache;
		this.hasUniformBorders = hasUniformBorders();
	}
	
	public Insets getInsets(IFigure figure) 
	{
		int tw, bw, lw, rw;
		tw = this.rectPresentation.getTopBorder().getWeight().getContent();
		bw = this.rectPresentation.getBottomBorder().getWeight().getContent();
		lw = this.rectPresentation.getLeftBorder().getWeight().getContent();
		rw = this.rectPresentation.getRightBorder().getWeight().getContent();
		
		return new Insets(tw, lw, bw, rw);
	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) 
	{
		tempRect.setBounds(getPaintRectangle(figure, insets));
		BorderComponent borderDef = this.rectPresentation.getTopBorder();
		if (this.hasUniformBorders && borderDef.getWeight().getContent() > 0)
		{
			float lineInset = Math.max(1.0f, (float)borderDef.getWeight().getContent()) / 2.0f;
			int inset1 = (int) Math.floor(lineInset) + 1;
			int inset2 = (int) Math.ceil(lineInset) + 1;
			tempRect.x += inset1;
			tempRect.y += inset1;
			tempRect.width -= inset1 + inset2;
			tempRect.height -= inset1 + inset2;
			
			int cornerRadius = this.rectPresentation.getCornerRadius();
			final Dimension cornerDimension = new Dimension(cornerRadius, cornerRadius); 
			
			graphics.setLineWidth(borderDef.getWeight().getContent());
			graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
			graphics.setLineStyle(FigureUtil.convertLineStyle(borderDef.getStyle().getContent()));
			// Shrink the border to leave 1 pixel space for the selection border
			tempRect.shrink(1, 1);
			graphics.drawRoundRectangle(tempRect,
					Math.max(0, cornerDimension.width - (int) lineInset),
					Math.max(0, cornerDimension.height - (int) lineInset));
			
		}
		else
		{
			if( borderDef.getWeight().getContent() > 0 )
			{
				int w = borderDef.getWeight().getContent();
				graphics.setLineWidth(w);
				graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
				graphics.setLineStyle(FigureUtil.convertLineStyle(borderDef.getStyle().getContent()));		
				int inset = Math.max(1, w / 2);
				int x = tempRect.x + inset;
				int y = tempRect.y - inset;
				int x2 = tempRect.x + tempRect.width - inset - inset;				
				graphics.drawLine(x, y, x2, y);
			}
			
			borderDef = this.rectPresentation.getBottomBorder();			
			if( borderDef.getWeight().getContent() > 0 )
			{	
				int w = borderDef.getWeight().getContent();
				graphics.setLineWidth(w);
				graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
				graphics.setLineStyle(FigureUtil.convertLineStyle(borderDef.getStyle().getContent()));
				int inset = Math.max(1, w / 2);
				int x = tempRect.x + inset;
				int y = tempRect.y + tempRect.height - inset;
				int x2 = tempRect.x + tempRect.width - inset - inset;
				graphics.drawLine(x, y, x2, y);
			}
	
			borderDef = this.rectPresentation.getRightBorder();			
			if( borderDef.getWeight().getContent() > 0 )
			{	
				int w = borderDef.getWeight().getContent();
				graphics.setLineWidth(w);
				graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
				graphics.setLineStyle(FigureUtil.convertLineStyle(borderDef.getStyle().getContent()));
				int inset = Math.max(1, w / 2);
				int x = tempRect.x + tempRect.width - inset;
				int y = tempRect.y + inset;
				int y2 = tempRect.y + tempRect.height - inset - inset;				
				graphics.drawLine(x, y, x, y2);
			}
	
			borderDef = this.rectPresentation.getLeftBorder();			
			if( borderDef.getWeight().getContent() > 0 )
			{	
				int w = borderDef.getWeight().getContent();
				graphics.setLineWidth(w);
				graphics.setForegroundColor(resourceCache.getColor(borderDef.getColor().getContent()));
				graphics.setLineStyle(FigureUtil.convertLineStyle(borderDef.getStyle().getContent()));
				int inset = Math.max(1, w / 2);
				int x = tempRect.x - inset;
				int y = tempRect.y + inset;
				int y2 = tempRect.y + tempRect.height - inset - inset;				
				graphics.drawLine(x, y, x, y2);
			}
			
		}
	}
		
	private boolean hasUniformBorders()
	{
		org.eclipse.sapphire.ui.Color tc, bc, lc, rc;
		tc = this.rectPresentation.getTopBorder().getColor().getContent();
		bc = this.rectPresentation.getBottomBorder().getColor().getContent();
		lc = this.rectPresentation.getLeftBorder().getColor().getContent();
		rc = this.rectPresentation.getRightBorder().getColor().getContent();
		if (!(tc.equals(bc) && tc.equals(lc) && tc.equals(rc)))
		{
			return false;
		}
		int tw, bw, lw, rw;
		tw = this.rectPresentation.getTopBorder().getWeight().getContent();
		bw = this.rectPresentation.getBottomBorder().getWeight().getContent();
		lw = this.rectPresentation.getLeftBorder().getWeight().getContent();
		rw = this.rectPresentation.getRightBorder().getWeight().getContent();
		if (!(tw == bw && tw == lw && tw == rw))
		{
			return false;
		}
		LineStyle ts, bs, ls, rs;
		ts = this.rectPresentation.getTopBorder().getStyle().getContent();
		bs = this.rectPresentation.getBottomBorder().getStyle().getContent();
		ls = this.rectPresentation.getLeftBorder().getStyle().getContent();
		rs = this.rectPresentation.getRightBorder().getStyle().getContent();
		if (!(ts == bs && ts == ls && ts == rs))
		{
			return false;
		}
		return true;
	}
	

}
