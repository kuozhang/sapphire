/******************************************************************************
 * Copyright (c) 2012 Oracle
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

public class Color 
{
	/** pre-defined colors */
	public final static Color white = new Color(255, 255, 255);
	public final static Color lightGray = new Color(192, 192, 192);
	public final static Color gray = new Color(128, 128, 128);
	public final static Color darkGray = new Color(64, 64, 64);
	public final static Color black = new Color(0, 0, 0);
	public final static Color red = new Color(255, 0, 0);
	public final static Color orange = new Color(255, 196, 0);
	public final static Color yellow = new Color(255, 255, 0);
	public final static Color green = new Color(0, 255, 0);
	public final static Color lightGreen = new Color(96, 255, 96);
	public final static Color darkGreen = new Color(0, 127, 0);
	public final static Color cyan = new Color(0, 255, 255);
	public final static Color lightBlue = new Color(127, 127, 255);
	public final static Color blue = new Color(0, 0, 255);
	public final static Color darkBlue = new Color(0, 0, 127);

	private final int r;
    private final int g;
    private final int b;
    
    public Color(int r, int g, int b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public int getRed()
    {
        return this.r;
    }
        
    public int getGreen()
    {
        return this.g;
    }
    
    public int getBlue()
    {
        return this.b;
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
    
    
}
