/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.DecoratorImageFigure;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

final public class NodeCellEditorLocator implements CellEditorLocator {

	private Label label;
	private DiagramConfigurationManager manager;

	public NodeCellEditorLocator(DiagramConfigurationManager manager, Label label) {
		this.manager = manager;
		setLabel(label);
	}

	public void relocate(CellEditor celleditor) {
		double zoom = manager.getDiagramEditor().getZoomLevel();
		Rectangle labelRect = label.getClientArea();
		// shrink horizontal by 2
		labelRect.x += 2;
		labelRect.width -= 4;
		// zoom
		labelRect.width = (int) (labelRect.width * zoom);
		
		Text text = (Text) celleditor.getControl();
		Point size = text.computeSize(-1, -1);
		
		size.x = Math.min(size.x, labelRect.width - 2);
		if (text.getText().length() == 0) {
			size.x = 10;
		}
		
		// calculate error image width
		int rightOffset = 0;
		for (Object object : label.getParent().getChildren()) {
			if (object instanceof DecoratorImageFigure) {
				Rectangle imageRect = ((DecoratorImageFigure)object).getBounds();
				int imageWidth = ((DecoratorImageFigure)object).getBounds().width;
				imageWidth = (int) (imageWidth * zoom);
				int newX = imageRect.x + imageWidth + 4;
				if (newX < labelRect.x + (labelRect.width / 2)) {
					// right aligned image
					rightOffset = newX - labelRect.x; 
				}
			}
		}
		
		// center the cell editor
		int offset = 0;
		if (size.x < labelRect.width) {
			offset = (labelRect.width - size.x + 1) / 2;
			if (rightOffset > 0 && offset < rightOffset) {
				offset = rightOffset;
			}
		}
		size.x = Math.min(size.x, rightOffset == 0 ? labelRect.width : labelRect.width - rightOffset - 1);
		
		label.translateToAbsolute(labelRect);
		
		text.setBounds(labelRect.x + offset, labelRect.y + 1, size.x - 1, size.y + 1);
	}
	
	protected Label getLabel() {
		return label;
	}

	protected void setLabel(Label label) {
		this.label = label;
	}

}