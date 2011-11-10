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
	
	int deltaX = 5;
	int deltaY = -10;

	public SapphireMidpointLocator(Connection connection) {
		super(connection);
	}

	public SapphireMidpointLocator(Connection connection, int deltaX, int deltaY) {
		super(connection);
		this.deltaX = deltaX;
		this.deltaY = deltaY;
	}

	@Override
	protected Point getReferencePoint() {
		Connection conn = getConnection();
		Point midPoint = Point.SINGLETON;
		int size = conn.getPoints().size();
		if (size % 2 == 0) {
			int index = (size / 2) - 1;
			Point p1 = conn.getPoints().getPoint(index);
			Point p2 = conn.getPoints().getPoint(index + 1);
			conn.translateToAbsolute(p1);
			conn.translateToAbsolute(p2);
			midPoint.x = (p2.x - p1.x) / 2 + p1.x;
			midPoint.y = (p2.y - p1.y) / 2 + p1.y;
		} else {
			int index = size / 2;
			Point p1 = conn.getPoints().getPoint(index);
			conn.translateToAbsolute(p1);
			midPoint.x = p1.x;
			midPoint.y = p1.y;
		}
		
		
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
		midPoint.x += deltaX;
		midPoint.y += deltaY;

		return midPoint;
	}
}
