/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.parts.ShapeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class RectangleSelectionEditPolicy extends NonResizableEditPolicy 
{
	
	private RectangleFigure getRectangleFigure() {
		if (getHost() instanceof ShapeEditPart) {
			ShapeEditPart part = (ShapeEditPart) getHost();
			if (part.getFigure() instanceof RectangleFigure) {
				return ((RectangleFigure) part.getFigure());
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#hideFocus()
	 */
	protected void hideFocus() {
		RectangleFigure f = getRectangleFigure();
		if (f != null) {
			f.setFocus(false);
		}
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#hideSelection()
	 */
	protected void hideSelection() {
		RectangleFigure f = getRectangleFigure();
		if (f != null) {
			f.setSelected(false);
			f.setFocus(false);
		}
	}

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#showFocus()
	 */
	protected void showFocus() {
		RectangleFigure f = getRectangleFigure();
		if (f != null) {
			f.setFocus(true);
		}
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
	 */
	protected void showPrimarySelection() {
		RectangleFigure f = getRectangleFigure();
		if (f != null) {
			f.setSelected(true);
			f.setFocus(true);
		}
	}

	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
	 */
	protected void showSelection() {
		RectangleFigure f = getRectangleFigure();
		if (f != null) {
			f.setSelected(true);
			f.setFocus(false);
		}
	}

}
