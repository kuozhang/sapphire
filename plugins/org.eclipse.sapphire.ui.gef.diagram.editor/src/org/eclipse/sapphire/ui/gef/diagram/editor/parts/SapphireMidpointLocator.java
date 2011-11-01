/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireMidpointLocator extends ConnectionLocator {

	private int index;

	public SapphireMidpointLocator(Connection connection, int i) {
		super(connection);
		index = i;
	}

	protected int getIndex() {
		return index;
	}

	@Override
	protected Point getReferencePoint() {
		Connection conn = getConnection();
		Point midPoint = Point.SINGLETON;
		Point p1 = conn.getPoints().getPoint(getIndex());
		Point p2 = conn.getPoints().getPoint(getIndex() + 1);
		conn.translateToAbsolute(p1);
		conn.translateToAbsolute(p2);
		midPoint.x = (p2.x - p1.x) / 2 + p1.x;
		midPoint.y = (p2.y - p1.y) / 2 + p1.y;
		
//			int deltaX, deltaY;
//
//			if (Math.signum(p2.x - midPoint.x) == Math.signum(p2.y - midPoint.y)) {
//				deltaX = 3;
//				deltaY = -10;
//			} else {
//				deltaX = 3;
//				deltaY = 0;
//			}
		// TODO calculate better deltas
		midPoint.x += 5;
		midPoint.y += -10;

		return midPoint;
	}
}
