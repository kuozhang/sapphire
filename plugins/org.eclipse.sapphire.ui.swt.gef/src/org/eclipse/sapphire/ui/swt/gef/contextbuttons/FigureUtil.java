/******************************************************************************
 * Copyright (c) 2013 SAP and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP - initial implementation
 *    Shenxue Zhou - adaptation for Sapphire and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.contextbuttons;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author SAP
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class FigureUtil 
{
	public static Rectangle getAdjustedRectangle(Rectangle rectangle, double zoom, int lw) {
		if (rectangle == null) {
			return null;
		}

		Rectangle ret = new Rectangle(rectangle);

		if (zoom != 1.0) {
			ret.x = (int) (Math.floor(rectangle.x * zoom));
			ret.y = (int) (Math.floor(rectangle.y * zoom));
			ret.width = (int) (Math.floor(((rectangle.x + rectangle.width) * zoom))) - ret.x;
			ret.height = (int) (Math.floor(((rectangle.y + rectangle.height) * zoom))) - ret.y;
		}

		int adjustmentTopLeft = lw / 2;
		int adjustmentBottomRight = lw;
		ret.x += adjustmentTopLeft;
		ret.y += adjustmentTopLeft;
		ret.width -= adjustmentBottomRight;
		ret.height -= adjustmentBottomRight;

		return ret;
	}

}
