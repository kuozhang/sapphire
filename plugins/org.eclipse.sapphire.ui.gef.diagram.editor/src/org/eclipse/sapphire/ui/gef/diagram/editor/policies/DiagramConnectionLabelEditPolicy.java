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

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramConnectionLabelEditPart;
import org.eclipse.swt.graphics.Cursor;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionLabelEditPolicy extends NonResizableEditPolicy {

    @SuppressWarnings("rawtypes")
	@Override
	protected List createSelectionHandles() {
		List list = new ArrayList();
		createMoveHandle(list);
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void createMoveHandle(List handles) {
		if (isDragAllowed()) {
			// display 'move' handle to allow dragging
			// Replace ResizableHandleKit.addMoveHandle to customize color and style
			handles.add(moveHandle((GraphicalEditPart) getHost(), getDragTracker(), Cursors.SIZEALL));
		} else {
			// display 'move' handle only to indicate selection
			// Replace ResizableHandleKit.addMoveHandle to customize color and style
			handles.add(moveHandle((GraphicalEditPart) getHost(), getDragTracker(), SharedCursors.ARROW));
		}
	}

	public static Handle moveHandle(GraphicalEditPart owner, DragTracker tracker, Cursor cursor) {
		MoveHandle moveHandle = new MoveHandle(owner);
		moveHandle.setDragTracker(tracker);
		moveHandle.setCursor(cursor);

    	DiagramResourceCache resourceCache = ((DiagramConnectionLabelEditPart)owner).getCastedModel().getDiagramModel().getResourceCache();
		moveHandle.setBorder(new LineBorder(resourceCache.getColor(NodeFigure.OUTLINE_FOREGROUND), 1, Graphics.LINE_DASH));

		return moveHandle;
	}
}
