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
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.utils.MultiValueMap;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireConnectionRouter 
{
	private MultiValueMap connections = new MultiValueMap();
	private MultiValueMap connectionIndices = new MultiValueMap();
	private HashMap<DiagramConnectionModel, Integer> connectionIndexMap = 
					new HashMap<DiagramConnectionModel, Integer>();
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
	

	public SapphireConnectionRouter() {}
		
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
		if (connectionIndexMap.containsKey(conn))
		{
			int index = connectionIndexMap.get(conn);
			connectionIndices.remove(connectionKey, index);
		}
	}
	
	public void clear()
	{
		this.connectionIndexMap.clear();
		this.connections.clear();
		this.connectionIndices.clear();
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

			int index  = getNextConnectionIndex(connectionKey);
			connections.put(connectionKey, conn);
			connectionIndices.put(connectionKey, index);
			connectionIndexMap.put(conn, index);

			Point bendpoint = handleCollision(points, index);
			return bendpoint;
		}
		else 
		{
			connections.put(connectionKey, conn);
			connectionIndices.put(connectionKey, -1);
			connectionIndexMap.put(conn, -1);
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
	
	private int getNextConnectionIndex(HashKey connectionKey)
	{
		ArrayList indices = connectionIndices.get(connectionKey);
		int[] intArr = new int[indices.size()];
		int i = 0;
		for (Object obj : indices)
		{
			Integer intObj = (Integer)obj;
			intArr[i++] = intObj;
		}
		Arrays.sort(intArr, 0, indices.size());
		
		int nextInt = -1;
		for (int j = 0; j < intArr.length; j++)
		{
			int temp = intArr[j];
			if (temp > 1 && temp != j + 1)
			{
				nextInt = j + 1;
				break;
			}
		}
		if (nextInt == -1)
		{
			nextInt = indices.size() + 1;
		}
		return nextInt;
	}

}
