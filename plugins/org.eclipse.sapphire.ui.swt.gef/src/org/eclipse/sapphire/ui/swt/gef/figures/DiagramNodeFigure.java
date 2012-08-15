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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.SWT;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeFigure extends org.eclipse.draw2d.Shape
{
    private static final org.eclipse.sapphire.ui.Color OUTLINE_FOREGROUND = new org.eclipse.sapphire.ui.Color(0xFF, 0xA5, 0x00);	
    private boolean selected;
	private boolean hasFocus;
	private DiagramResourceCache resourceCache;
	
	public DiagramNodeFigure(DiagramNodePart nodePart, DiagramResourceCache resourceCache)
	{
		this.resourceCache = resourceCache;
		XYLayout layout = new XYLayout();
		this.setLayoutManager(layout);
	}		
	
	@Override
	protected void outlineShape(Graphics graphics) 
	{
		if (this.hasFocus || this.selected) 
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
			
			graphics.setForegroundColor(this.resourceCache.getColor(OUTLINE_FOREGROUND));
			org.eclipse.draw2d.geometry.Rectangle expanded = r.getExpanded(1, 1);
			graphics.setLineStyle(SWT.LINE_DASH);
			graphics.drawRoundRectangle(expanded, 8, 8);
		}
	}
	
	@Override
	protected void fillShape(Graphics graphics) 
	{
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
	
}
