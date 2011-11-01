/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.SapphireDiagramEditorUtil;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class NodeFigure extends RoundedRectangle { 
	
    private static final org.eclipse.sapphire.ui.Color DEFAULT_TEXT_FOREGROUND = new org.eclipse.sapphire.ui.Color(51, 51, 153);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_NODE_FOREGROUND = new org.eclipse.sapphire.ui.Color(51, 51, 153);
    //EEF6FD ACD2F4 - 81B9EA, 9ABFE0
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_START = new org.eclipse.sapphire.ui.Color(0xFF, 0xFF, 0xFF);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_END = new org.eclipse.sapphire.ui.Color(0xAC, 0xD2, 0xF4);

    private static final org.eclipse.sapphire.ui.Color SELECTED_BACKGROUND = new org.eclipse.sapphire.ui.Color(0x9A, 0xBF, 0xE0);
    
    private static final int DEFAULT_TEXT_HEIGHT = 20;

    private Label labelFigure;

    private boolean selected;
	private boolean hasFocus;

	public NodeFigure(final String text) {
		this.setForegroundColor(SapphireDiagramEditorUtil.getColor(DEFAULT_NODE_FOREGROUND));
		setLayoutManager(new XYLayout());

		labelFigure = new Label("BB");
		labelFigure.setForegroundColor(SapphireDiagramEditorUtil.getColor(DEFAULT_TEXT_FOREGROUND));
		this.add(labelFigure);

		this.addFigureListener(new FigureListener() {

			public void figureMoved(IFigure source) {

				Rectangle bounds = getBounds();
				Rectangle labelFigureConstraint = new Rectangle(0, 5, bounds.width, DEFAULT_TEXT_HEIGHT);
				getLayoutManager().setConstraint(labelFigure, labelFigureConstraint);
			}
		});
	}
	
	public Label getLabelFigure() {
		return this.labelFigure;
	}
	
	public void setText(String text) {
		labelFigure.setText(text);
	}
	
	public String getText() {
		return labelFigure.getText();
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
		final Dimension cornerDimension = new Dimension(1, 1); //this.getCornerDimensions();
		final Rectangle fillRectangle = getBounds().getShrinked(cornerDimension.width, cornerDimension.height);
		
		final Color foregroundSave = graphics.getForegroundColor();
		final Color backgroundSave = graphics.getBackgroundColor();
		
		if (selected) {
			graphics.setBackgroundColor(SapphireDiagramEditorUtil.getColor(SELECTED_BACKGROUND));
			graphics.fillRectangle(fillRectangle);
		} else {
			graphics.setForegroundColor(SapphireDiagramEditorUtil.getColor(DEFAULT_BACKGROUND_END));
			graphics.setBackgroundColor(SapphireDiagramEditorUtil.getColor(DEFAULT_BACKGROUND_START));
			
			graphics.fillGradient(fillRectangle.x, fillRectangle.y, fillRectangle.width, fillRectangle.height, true/*vertical*/);
		}
		
		graphics.setForegroundColor(foregroundSave);
		graphics.setBackgroundColor(backgroundSave);
	}

	@Override
	protected void outlineShape(Graphics graphics) {

		float lineInset = Math.max(1.0f, getLineWidthFloat()) / 2.0f;
		int inset1 = (int) Math.floor(lineInset) + 1;
		int inset2 = (int) Math.ceil(lineInset) + 1;

		Rectangle r = Rectangle.SINGLETON.setBounds(getBounds());
		r.x += inset1;
		r.y += inset1;
		r.width -= inset1 + inset2;
		r.height -= inset1 + inset2;

		graphics.drawRoundRectangle(r,
				Math.max(0, getCornerDimensions().width - (int) lineInset),
				Math.max(0, getCornerDimensions().height - (int) lineInset));
		
		if (hasFocus) {
			final Color foregroundSave = graphics.getForegroundColor();
			graphics.setForegroundColor(ColorConstants.orange);
			Rectangle expanded = r.getExpanded(1, 1);
			graphics.drawRoundRectangle(expanded,
					Math.max(0, getCornerDimensions().width - (int) lineInset),
					Math.max(0, getCornerDimensions().height - (int) lineInset));
			graphics.setForegroundColor(foregroundSave);
		}
	}

	public void setSelected(boolean b) {
		selected = b;
		repaint();
	}

	public void setFocus(boolean b) {
		hasFocus = b;
		repaint();
	}
}
