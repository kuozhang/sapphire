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

package org.eclipse.sapphire.ui;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class Color 
{
    private final int red;
    private final int green;
    private final int blue;
    
    public Color(int r, int g, int b)
    {
        this.red = r;
        this.green = g;
        this.blue = b;
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
    
}
