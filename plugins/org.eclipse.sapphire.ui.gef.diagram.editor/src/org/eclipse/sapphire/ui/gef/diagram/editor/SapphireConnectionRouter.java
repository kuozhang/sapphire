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

package org.eclipse.sapphire.ui.gef.diagram.editor;

import java.util.ArrayList;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.internal.MultiValueMap;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireConnectionRouter 
{
	private static SapphireConnectionRouter instance;
	private MultiValueMap connections = new MultiValueMap();
	private int separation = 30;
	
	private class HashKey 
	{
		private DiagramNodeModel sourceNode, targetNode;

		HashKey(DiagramConnectionModel conn) 
		{
			sourceNode = conn.getSourceNode();
			targetNode = conn.getTargetNode();
		}

		public boolean equals(Object object) 
		{
			boolean isEqual = false;
			HashKey hashKey;

			if (object instanceof HashKey) 
			{
				hashKey = (HashKey) object;
				DiagramNodeModel hkA1 = hashKey.getFirstNode();
				DiagramNodeModel hkA2 = hashKey.getSecondNode();

				isEqual = (hkA1.equals(sourceNode) && hkA2.equals(targetNode))
						|| (hkA1.equals(targetNode) && hkA2.equals(sourceNode));
			}
			return isEqual;
		}

		public DiagramNodeModel getFirstNode() 
		{
			return sourceNode;
		}

		public DiagramNodeModel getSecondNode() 
		{
			return targetNode;
		}

		public int hashCode() 
		{
			return sourceNode.hashCode() ^ targetNode.hashCode();
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
	
	public void removeConnectionFromCache(DiagramConnectionModel conn)
	{
		HashKey connectionKey = new HashKey(conn);
		connections.remove(connectionKey, conn);
	}
	
	private Point getNodeLocation(DiagramNodeModel node) {
		Bounds bounds = node.getNodeBounds();
		return new Point(bounds.getX() + (bounds.getWidth()/2), bounds.getY() + (bounds.getHeight()/2));
	}
	
	public Point route(DiagramConnectionModel conn) 
	{		
		HashKey connectionKey = new HashKey(conn);
		ArrayList connectionList = connections.get(connectionKey);

		if (connectionList != null) 
		{
			PointList points = new PointList();
			points.addPoint(getNodeLocation(conn.getSourceNode()));
			points.addPoint(getNodeLocation(conn.getTargetNode()));

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
	
	public void addConnection(DiagramConnectionModel conn)
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
