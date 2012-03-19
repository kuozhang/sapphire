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

package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.figures.NodeFigure;
import org.eclipse.sapphire.ui.swt.gef.parts.DiagramNodeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeSelectionEditPolicy extends NonResizableEditPolicy {

	private NodeFigure getLabel() {
		DiagramNodeEditPart part = (DiagramNodeEditPart) getHost();
		return ((NodeFigure) part.getFigure());
	}

	@Override
	protected void hideFocus() {
		getLabel().setFocus(false);
	}

	@Override
	protected void showFocus() {
		getLabel().setFocus(true);
	}

	@Override
	protected void hideSelection() {
		getLabel().setSelected(false);
		getLabel().setFocus(false);

	}

	@Override
	protected void showPrimarySelection() {
		getLabel().setSelected(true);
		getLabel().setFocus(true);
	}

	@Override
	protected void showSelection() {
		getLabel().setSelected(true);
		getLabel().setFocus(false);
	}

}
