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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

final public class NodeCellEditorLocator implements CellEditorLocator {

	private Label label;

	public NodeCellEditorLocator(Label stickyNote) {
		setLabel(stickyNote);
	}

	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Rectangle labelRect = label.getClientArea();
		label.translateToAbsolute(labelRect);
		Point pref = text.computeSize(-1, -1);
		Rectangle rect = label.getTextBounds().getCopy();
		pref.x = Math.min(pref.x, labelRect.width);
		if (text.getText().length() == 0) {
			pref.x = 10;
		}
		label.translateToAbsolute(rect);
		text.setBounds(rect.x, rect.y, pref.x + 1, pref.y + 1);
	}

	protected Label getLabel() {
		return label;
	}

	protected void setLabel(Label label) {
		this.label = label;
	}

}