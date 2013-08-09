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
package org.eclipse.sapphire.ui.swt.gef.tools;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.swt.SWT;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDragEditPartsTracker extends DragEditPartsTracker {
	
	private final static String LAST_EDIT_PART = "LAST_EDIT_PART";

	public SapphireDragEditPartsTracker(EditPart sourceEditPart) {
		super(sourceEditPart);
	}

	@SuppressWarnings("rawtypes")
	protected void performSelection() {
		if (hasSelectionOccurred())
			return;
		setFlag(FLAG_SELECTION_PERFORMED, true);
		EditPartViewer viewer = getCurrentViewer();
		List selectedObjects = viewer.getSelectedEditParts();

		if (getCurrentInput().isModKeyDown(SWT.MOD1)) {
			if (selectedObjects.contains(getSourceEditPart()))
				viewer.deselect(getSourceEditPart());
			else
				viewer.appendSelection(getSourceEditPart());

			viewer.setProperty(LAST_EDIT_PART, getSourceEditPart());
		} else if (getCurrentInput().isShiftKeyDown()) {
			EditPart lastEditPart = (EditPart)viewer.getProperty(LAST_EDIT_PART);
			if (sameParent(getSourceEditPart(), lastEditPart)) {
				viewer.deselectAll();
				
				EditPart fromEditPart = null;
				EditPart toEditPart = null;
				for (Object child : getSourceEditPart().getParent().getChildren()) {
					if (child instanceof EditPart) {
						EditPart part = (EditPart)child;
						if (part.isSelectable()) {
							if (part.equals(getSourceEditPart()) || part.equals(lastEditPart)) {
								if (fromEditPart == null) {
									fromEditPart = part;
									if (getSourceEditPart().equals(lastEditPart)) {
										toEditPart = part;
									}
								} else if (toEditPart == null) {
									toEditPart = part;
								}
							}
							if (fromEditPart != null) {
								viewer.appendSelection(part);
							}
							if (toEditPart != null) {
								break;
							}
						}
					}
				}
			} else {
				viewer.appendSelection(getSourceEditPart());

				viewer.setProperty(LAST_EDIT_PART, getSourceEditPart());
			}
		}
		else {
			viewer.select(getSourceEditPart());

			viewer.setProperty(LAST_EDIT_PART, getSourceEditPart());
		}
	}
	
	private boolean sameParent(EditPart part, EditPart lastPart) {
		if (part != null && lastPart != null) {
			return part.getParent().equals(lastPart.getParent());
		}
		return false;
	}
}
