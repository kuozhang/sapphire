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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

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
	protected Rectangle getNewBounds(Dimension size, Point center) {
		return new Rectangle(center, size);
	}

	@Override
	protected Point getReferencePoint() {
		Connection conn = getConnection();
		PointList points = conn.getPoints();
		Point p1;
		Point p2;
		int size = points.size();
		if (size % 2 == 0) {
			int i = points.size() / 2;
			int j = i - 1;
			p1 = points.getPoint(j);
			p2 = points.getPoint(i);
		} else {
			int index = size / 2;
			p1 = points.getPoint(index + 1);
			p2 = points.getPoint(index);
		}
		Dimension d = p2.getDifference(p1);
		Point midPoint = Point.SINGLETON.setLocation(p1.x + d.width / 2, p1.y	+ d.height / 2);

		double value = Math.atan2(p1.y-p2.y, p1.x-p2.x);
		double angle = Math.toDegrees(value);
		if ((angle > 0 && angle < 90) || (angle > -180 && angle < -90)) {
			midPoint.y -= 12; 
		}
		midPoint.x += 3;
		
		return midPoint;
	}
}
