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

import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeResizableEditPolicy extends ResizableEditPolicy {
	
	private DiagramResourceCache resourceCache;
	
	public DiagramNodeResizableEditPolicy(DiagramResourceCache resourceCache) {
		this.resourceCache = resourceCache;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void createResizeHandle(List handles, int direction) {
		if ((getResizeDirections() & direction) == direction) {
			ResizeHandle handle = new ResizeHandle((GraphicalEditPart) getHost(), direction) {
				@Override
				protected Color getBorderColor() {
					return (isPrimary()) ? ColorConstants.white : resourceCache.getOutlineColor();
				}
				@Override
				protected Color getFillColor() {
					return (isPrimary()) ? resourceCache.getOutlineColor() : ColorConstants.white;
				}
			};
			handle.setDragTracker(getResizeTracker(direction));
			handle.setCursor( Cursors.getDirectionalCursor(direction, getHostFigure().isMirrored()));
			handles.add(handle);
		} else {
			// display 'resize' handle to allow dragging or indicate selection only
			createDragHandle(handles, direction);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void createMoveHandle(List handles) {
		if (isDragAllowed()) {
			// display 'move' handle to allow dragging
			MoveHandle moveHandle = new MoveHandle((GraphicalEditPart) getHost());
			moveHandle.setDragTracker(getDragTracker());
			moveHandle.setCursor(Cursors.SIZEALL);
			Border border = new LineBorder(resourceCache.getOutlineColor(), 1, Graphics.LINE_DASH);
			moveHandle.setBorder(border);
			handles.add(moveHandle);
		} else {
			// display 'move' handle only to indicate selection
			ResizableHandleKit.addMoveHandle((GraphicalEditPart) getHost(),
					handles, getSelectTracker(), SharedCursors.ARROW);
		}
	}


}
