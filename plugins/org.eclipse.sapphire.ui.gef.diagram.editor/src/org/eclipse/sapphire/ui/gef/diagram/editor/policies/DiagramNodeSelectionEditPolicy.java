package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramNodeEditPart;

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
