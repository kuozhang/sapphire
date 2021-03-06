/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and Other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Chriss Gross (schtoo@schtoo.com) - fix for 61670
 *******************************************************************************/
package org.eclipse.sapphire.ui.forms.swt.internal.text;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author IBM Corporation
 */

public interface IHyperlinkSegment extends IFocusSelectable {
    String getHref();
    String getText();
    void paintFocus(GC gc, Color bg, Color fg, boolean selected, Rectangle repaintRegion);
    boolean contains(int x, int y);
    boolean intersects(Rectangle rect);
}
