package org.eclipse.sapphire.ui.swt.gef.policies;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.parts.RectangleEditPart;

public class RectangleSelectionEditPolicy extends NonResizableEditPolicy 
{

	private RectangleFigure getRectangleFigure() 
	{
		RectangleEditPart part = (RectangleEditPart) getHost();
		return ((RectangleFigure) part.getFigure());
	}

	@Override
	protected void hideFocus() 
	{
		getRectangleFigure().setFocus(false);
	}

	@Override
	protected void showFocus() 
	{
		getRectangleFigure().setFocus(true);
	}

	@Override
	protected void hideSelection() 
	{
		getRectangleFigure().setSelected(false);
		getRectangleFigure().setFocus(false);

	}

	@Override
	protected void showPrimarySelection() 
	{
		getRectangleFigure().setSelected(true);
		getRectangleFigure().setFocus(true);
	}

	@Override
	protected void showSelection() 
	{
		getRectangleFigure().setSelected(true);
		getRectangleFigure().setFocus(false);
	}

}
