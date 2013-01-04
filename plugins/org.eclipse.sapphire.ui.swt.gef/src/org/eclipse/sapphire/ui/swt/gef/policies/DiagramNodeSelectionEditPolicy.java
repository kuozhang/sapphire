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
import org.eclipse.sapphire.ui.swt.gef.figures.IShapeFigure;
import org.eclipse.sapphire.ui.swt.gef.parts.DiagramNodeEditPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeSelectionEditPolicy extends NonResizableEditPolicy {

	private IShapeFigure getNodeFigure() {
		DiagramNodeEditPart part = (DiagramNodeEditPart) getHost();
		if (part.getFigure() instanceof IShapeFigure)
		{
			return ((IShapeFigure) part.getFigure());
		}
		return null;
	}

	@Override
	protected void hideFocus() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setFocus(false);
		}
	}

	@Override
	protected void showFocus() {
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setFocus(true);
		}
	}

	@Override
	protected void hideSelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(false);
			shapeFigure.setFocus(false);
		}
	}

	@Override
	protected void showPrimarySelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(true);
			shapeFigure.setFocus(true);
		}
	}

	@Override
	protected void showSelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(true);
			shapeFigure.setFocus(false);
		}
	}

}
