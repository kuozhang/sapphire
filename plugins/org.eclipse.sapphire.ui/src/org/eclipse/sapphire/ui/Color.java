/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - Bug 383924 - Extend Sapphire Diagram Framework to support SQL Schema diagram like editors
 ******************************************************************************/

package org.eclipse.sapphire.ui;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public final class Color 
{
	private final int red;
    private final int green;
    private final int blue;
    
    public Color( final int red, 
                  final int green, 
                  final int blue )
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public int getRed()
    {
        return this.red;
    }
        
    public int getGreen()
    {
        return this.green;
    }
    
    public int getBlue()
    {
        return this.blue;
    }
    
    @Override
    public boolean equals(Object another)
    {
    	boolean isEqual = false;
    	if (another instanceof Color)
    	{
    		Color color = (Color)another;
    		if (color.getRed() == getRed() && color.getGreen() == getGreen() &&
    				color.getBlue() == getBlue())
    		{
    			isEqual = true;
    		}
    	}
    	return isEqual;
    }
    
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        
        buf.append('#');
        
        String temp = Integer.toHexString(this.red % 256);
        if (temp.length() < 2)
        {
            buf.append('0');
        }
        buf.append(temp);
        
        temp = Integer.toHexString(this.green % 256);
        if (temp.length() < 2)
        {
            buf.append('0');
        }
        buf.append(temp);
        
        temp = Integer.toHexString(this.blue % 256);
        if (temp.length() < 2)
        {
            buf.append('0');
        }
        buf.append(temp);
        
        return buf.toString();
    }
    
}
