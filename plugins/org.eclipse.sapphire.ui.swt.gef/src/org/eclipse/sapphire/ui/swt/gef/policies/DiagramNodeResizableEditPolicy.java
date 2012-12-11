/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Maintain backward compatibility with Helios SR1
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.NonResizableHandleKit;
import org.eclipse.gef.handles.ResizableHandleKit;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef.tools.ResizeTracker;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeResizableEditPolicy extends ResizableEditPolicy 
{
	private int resizeDirections = PositionConstants.NSEW;
	private DiagramResourceCache resourceCache;
	
	public DiagramNodeResizableEditPolicy(DiagramResourceCache resourceCache) {
		this.resourceCache = resourceCache;
	}
	
	/**
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles()
	 */
	protected List createSelectionHandles() {
		if (resizeDirections == PositionConstants.NONE) {
			// non resizable, so delegate to super implementation
			return super.createSelectionHandles();
		}

		// resizable in at least one direction
		List list = new ArrayList();
		createMoveHandle(list);
		createResizeHandle(list, PositionConstants.NORTH);
		createResizeHandle(list, PositionConstants.EAST);
		createResizeHandle(list, PositionConstants.SOUTH);
		createResizeHandle(list, PositionConstants.WEST);
		createResizeHandle(list, PositionConstants.SOUTH_EAST);
		createResizeHandle(list, PositionConstants.SOUTH_WEST);
		createResizeHandle(list, PositionConstants.NORTH_WEST);
		createResizeHandle(list, PositionConstants.NORTH_EAST);
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	/**
	 * Returns the directions in which resizing should be allowed
	 * 
	 * Valid values are bit-wise combinations of:
	 * <UL>
	 * <LI>{@link PositionConstants#NORTH}
	 * <LI>{@link PositionConstants#SOUTH}
	 * <LI>{@link PositionConstants#EAST}
	 * <LI>{@link PositionConstants#WEST}
	 * </UL>
	 * or {@link PositionConstants#NONE}.
	 * 
	 */
	@Override
	public int getResizeDirections() {
		return resizeDirections;
	}
	
	/**
	 * Sets the directions in which handles should allow resizing. Valid values
	 * are bit-wise combinations of:
	 * <UL>
	 * <LI>{@link PositionConstants#NORTH}
	 * <LI>{@link PositionConstants#SOUTH}
	 * <LI>{@link PositionConstants#EAST}
	 * <LI>{@link PositionConstants#WEST}
	 * </UL>
	 * 
	 * @param newDirections
	 *            the direction in which resizing is allowed
	 */
	@Override
	public void setResizeDirections(int newDirections) {
		resizeDirections = newDirections;
	}

	// The following methods are copied from 3.7's NonResizableEditPolicy. We do so in order
	// to maintain compatibility with 3.6 (Helios SR1)
	/**
	 * Returns a resize tracker for the given direction to be used by a resize
	 * handle.
	 * 
	 * @param direction
	 *            the resize direction for the {@link ResizeTracker}.
	 * @return a new {@link ResizeTracker}
	 * @since 3.7
	 */
	protected ResizeTracker getResizeTracker(int direction) {
		return new ResizeTracker((GraphicalEditPart) getHost(), direction)
		{
			@Override
			protected Dimension getMaximumSizeFor(ChangeBoundsRequest request) {
				return new Dimension(250, 250);
			}

			/**
			 * Determines the <em>minimum</em> size that the specified child can be
			 * resized to.By default, a default value is returned. The value is
			 * interpreted to be a dimension in the host figure's coordinate system
			 * (i.e. relative to its bounds), so it is not affected by zooming effects.
			 * 
			 * @param request
			 *            the ChangeBoundsRequest
			 * @return the minimum size
			 * @since 3.7
			 */
			@Override
			protected Dimension getMinimumSizeFor(ChangeBoundsRequest request) {
				return new Dimension(100, 100);
			}
			
		};
	}
	
	/**
	 * Creates a 'resize'/'drag' handle, which uses a
	 * {@link DragEditPartsTracker} in case {@link #isDragAllowed()} returns
	 * true, and a {@link SelectEditPartTracker} otherwise.
	 * 
	 * @param handles
	 *            The list of handles to add the resize handle to
	 * @param direction
	 *            A position constant indicating the direction to create the
	 *            handle for
	 * @since 3.7
	 */
	protected void createDragHandle(List handles, int direction) {
		if (isDragAllowed()) {
			// display 'resize' handles to allow dragging (drag tracker)
			NonResizableHandleKit
					.addHandle((GraphicalEditPart) getHost(), handles,
							direction, getDragTracker(), SharedCursors.SIZEALL);
		} else {
			// display 'resize' handles to indicate selection only (selection
			// tracker)
			NonResizableHandleKit
					.addHandle((GraphicalEditPart) getHost(), handles,
							direction, getSelectTracker(), SharedCursors.ARROW);
		}
	}
	
	/**
	 * Returns a drag tracker to use by a resize handle.
	 * 
	 * @return a new {@link ResizeTracker}
	 * @since 3.7
	 */
	protected DragEditPartsTracker getDragTracker() {
		return new DragEditPartsTracker(getHost());
	}
	
	/**
	 * Returns a selection tracker to use by a selection handle.
	 * 
	 * @return a new {@link ResizeTracker}
	 * @since 3.7
	 */
	protected SelectEditPartTracker getSelectTracker() {
		return new SelectEditPartTracker(getHost());
	}
	
}
