/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.sapphire.ui.diagram.shape.def.SelectionPresentation;
import org.eclipse.sapphire.ui.swt.gef.figures.FigureUtil;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.parts.ShapeEditPart;
import org.eclipse.sapphire.ui.swt.gef.utils.SapphireSurroundingHandle;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
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
	
	@Override
	protected List<?> createSelectionHandles() 
	{
		List<AbstractHandle> list = new ArrayList<AbstractHandle>();
		GraphicalEditPart owner = (GraphicalEditPart) getHost();
		ShapeEditPart shapeEditPart = (ShapeEditPart)owner;
		SelectionPresentation selectionPresentation = shapeEditPart.getShapePresentation().getSelectionPresentation();
		DiagramResourceCache resourceCache = shapeEditPart.getNodeEditPart().getCastedModel().getDiagramModel().getResourceCache();
		SapphireSurroundingHandle selectionHandle = new SapphireSurroundingHandle(owner, shapeEditPart.getConfigurationManager(),
				resourceCache, isDragAllowed());
		if (selectionPresentation != null)
		{
			selectionHandle.setLineInset(selectionPresentation.getInset().getContent());
			selectionHandle.setLineWidth(selectionPresentation.getWeight().getContent());
			selectionHandle.setLineStyle(FigureUtil.convertLineStyle(selectionPresentation.getStyle().getContent()));
			selectionHandle.setLineColor(selectionPresentation.getColor().getContent());
		}
		list.add(selectionHandle);
		return list;
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
			removeSelectionHandles();
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
			addSelectionHandles();
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
			addSelectionHandles();
		}
	}

}
