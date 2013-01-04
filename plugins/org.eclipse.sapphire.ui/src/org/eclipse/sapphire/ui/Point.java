/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class Point 
{
    private int x;
    private int y;
    
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Point(Point another)
    {
    	this.x = another.getX();
    	this.y = another.getY();
    }
    
    public int getX() 
    { 
        return this.x; 
    }
    
    public void setX(int x) 
    {
        this.x = x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    @Override
    public boolean equals(Object another)
    {
    	boolean isEqual = false;
    	if (another instanceof Point)
    	{
    		Point pt = (Point)another;
    		if (pt.getX() == getX() && pt.getY() == getY())
    		{
    			isEqual = true;
    		}
    	}
    	return isEqual;
    }
}
