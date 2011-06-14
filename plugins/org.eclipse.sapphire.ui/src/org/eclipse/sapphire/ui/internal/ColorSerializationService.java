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

package org.eclipse.sapphire.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.ui.Color;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ColorSerializationService 
        
    extends ValueSerializationService 
    
{
    private static Map<String, Color> namedColors;
    
    static
    {
        namedColors = new HashMap<String, Color>();
        namedColors.put("aqua", new Color(0, 255, 255));
        namedColors.put("black", new Color(0, 0, 0));
        namedColors.put("blue", new Color(0, 0, 255));
        namedColors.put("fuchsia", new Color(255, 0, 255));
        namedColors.put("gray", new Color(128, 128, 128));
        namedColors.put("green", new Color(0, 128, 0));
        namedColors.put("lime", new Color(0, 255, 0));
        namedColors.put("maroon", new Color(128, 0, 0));
        namedColors.put("navy", new Color(0, 0, 128));
        namedColors.put("olive", new Color(128, 128, 0));
        namedColors.put("orange", new Color(255, 165, 0));
        namedColors.put("purple", new Color(128, 0, 128));
        namedColors.put("red", new Color(255, 0, 0));
        namedColors.put("silver", new Color(192, 192, 192));
        namedColors.put("teal", new Color(0, 128, 128));
        namedColors.put("white", new Color(255, 255, 255));
        namedColors.put("yellow", new Color(255, 255, 0));
    }
    
    @Override
    protected Object decodeFromString(String value) 
    {        
        Color color = null;
        if (value.startsWith("#") && value.length() == 7)
        {
            int r = Integer.valueOf(value.substring(1, 3), 16);
            int g = Integer.valueOf(value.substring(3, 5), 16);
            int b = Integer.valueOf(value.substring(5, 7), 16);
            color = new Color(r, g, b);
        }
        else 
        {
            color = namedColors.get(value.toLowerCase());
        }
        return color;
    }

    @Override
    public String encode( final Object value )
    {
        if (value != null)
        {
            final Color color = (Color) value;
            final StringBuilder buf = new StringBuilder();
            
            buf.append("#");
            
            String temp = Integer.toHexString(color.getRed() % 256);
            if (temp.length() < 2)
            {
                buf.append("0");
            }
            buf.append(temp);
            
            temp = Integer.toHexString(color.getGreen() % 256);
            if (temp.length() < 2)
            {
                buf.append("0");
            }
            buf.append(temp);
            
            temp = Integer.toHexString(color.getBlue() % 256);
            if (temp.length() < 2)
            {
                buf.append("0");
            }
            buf.append(temp);
            
            return buf.toString();
        }
        return null;
    }    
}
