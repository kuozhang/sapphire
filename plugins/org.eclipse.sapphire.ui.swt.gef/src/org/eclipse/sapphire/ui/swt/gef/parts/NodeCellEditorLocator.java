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
		labelRect.width = (int) (labelRect.width * zoom);
		
		Text text = (Text) celleditor.getControl();
		Point size = text.computeSize(-1, -1);
		
		size.x = Math.min(size.x, labelRect.width - 2);
		if (text.getText().length() == 0) {
			size.x = 10;
		}
		
		// calculate error image width
		int imageWidth = 0;
		for (Object object : label.getParent().getChildren()) {
			if (object instanceof DecoratorImageFigure) {
				Rectangle imageRect = ((DecoratorImageFigure)object).getBounds();
				if (labelRect.x == imageRect.x && labelRect.y == imageRect.y) {
					imageWidth = ((DecoratorImageFigure)object).getBounds().width + 2;
					imageWidth = (int) (imageWidth * zoom);
				}
			}
		}
		
		// center the cell editor
		int offset = 0;
		if (size.x < labelRect.width) {
			offset = (labelRect.width - size.x + 1) / 2;
			if (imageWidth > 0 && offset < imageWidth) {
				offset = imageWidth;
			}
		}
		size.x = Math.min(size.x, imageWidth == 0 ? labelRect.width : labelRect.width - imageWidth - 2);
		
		label.translateToAbsolute(labelRect);
		
		text.setBounds(labelRect.x + offset, labelRect.y + 1, size.x, size.y + 1);
	}
	
	protected Label getLabel() {
		return label;
	}

	protected void setLabel(Label label) {
		this.label = label;
	}

}