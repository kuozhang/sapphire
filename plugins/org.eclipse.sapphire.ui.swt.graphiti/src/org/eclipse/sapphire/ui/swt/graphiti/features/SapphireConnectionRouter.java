/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.features;

import java.util.ArrayList;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.internal.MultiValueMap;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ILayoutService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireConnectionRouter 
{
	private static SapphireConnectionRouter instance;
	private MultiValueMap connections = new MultiValueMap();
	private int separation = 30;
	
	private class HashKey 
	{
		private Anchor anchor1, anchor2;

		HashKey(Connection conn) 
		{
			anchor1 = conn.getStart();
			anchor2 = conn.getEnd();
		}

		public boolean equals(Object object) 
		{
			boolean isEqual = false;
			HashKey hashKey;

			if (object instanceof HashKey) 
			{
				hashKey = (HashKey) object;
				Anchor hkA1 = hashKey.getFirstAnchor();
				Anchor hkA2 = hashKey.getSecondAnchor();

				isEqual = (hkA1.equals(anchor1) && hkA2.equals(anchor2))
						|| (hkA1.equals(anchor2) && hkA2.equals(anchor1));
			}
			return isEqual;
		}

		public Anchor getFirstAnchor() 
		{
			return anchor1;
		}

		public Anchor getSecondAnchor() 
		{
			return anchor2;
		}

		public int hashCode() 
		{
			return anchor1.hashCode() ^ anchor2.hashCode();
		}
	}
	
	private SapphireConnectionRouter() {}
	
	public static SapphireConnectionRouter getInstance()
	{
		if (instance == null)
		{
			instance = new SapphireConnectionRouter();
		}
		return instance;
	}
	
	public int getSeparation() 
	{
		return separation;
	}
	
	public void setSeparation(int value) 
	{
		separation = value;
	}
	
	public void removeConnectionFromCache(Connection conn)
	{
		HashKey connectionKey = new HashKey(conn);
		connections.remove(connectionKey, conn);
	}
	
	public Point route(FreeFormConnection conn) 
	{		
		HashKey connectionKey = new HashKey(conn);
		ArrayList connectionList = connections.get(connectionKey);

		if (connectionList != null) 
		{
			Anchor startAnchor = conn.getStart();
			final ILayoutService layoutService = Graphiti.getLayoutService();
			ILocation startLocation = layoutService.getLocationRelativeToDiagram(startAnchor);
			Point startPoint = new Point(startLocation.getX(), startLocation.getY());

			Anchor endAnchor = conn.getEnd();
			ILocation endLocation = layoutService.getLocationRelativeToDiagram(endAnchor);
			Point endPoint = new Point(endLocation.getX(), endLocation.getY());
			
			PointList points = new PointList();
			points.addPoint(startPoint);
			points.addPoint(endPoint);

			int index;

			if (connectionList.contains(conn)) 
			{
				index = connectionList.indexOf(conn) + 1;
			} 
			else 
			{
				index = connectionList.size() + 1;
				connections.put(connectionKey, conn);
			}

			Point bendpoint = handleCollision(points, index);
			return bendpoint;
		}
		else 
		{
			connections.put(connectionKey, conn);
		}
		return null;
	}
	
	public void addConnection(FreeFormConnection conn)
	{
		HashKey connectionKey = new HashKey(conn);
		connections.put(connectionKey, conn);
	}
	
	private Point handleCollision(PointList points, int index) 
	{
		Point start = points.getFirstPoint();
		Point end = points.getLastPoint();

		if (start.equals(end))
			return null;

		Point midPoint = new Point((end.x + start.x) / 2, (end.y + start.y) / 2);
		int position = end.getPosition(start);
		Ray ray;
		if (position == PositionConstants.SOUTH
				|| position == PositionConstants.EAST)
			ray = new Ray(start, end);
		else
			ray = new Ray(end, start);
		double length = ray.length();

		double xSeparation = getSeparation() * ray.x / length;
		double ySeparation = getSeparation() * ray.y / length;

		Point bendPoint;

		if (index % 2 == 0) {
			bendPoint = new Point(
					midPoint.x + (index / 2) * (-1 * ySeparation), midPoint.y
							+ (index / 2) * xSeparation);
		} else {
			bendPoint = new Point(midPoint.x + (index / 2) * ySeparation,
					midPoint.y + (index / 2) * (-1 * xSeparation));
		}
		if (!bendPoint.equals(midPoint)) {
			return bendPoint;
		}
		
		return null;
	}
	

}
