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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.RGB;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ColorUtil 
{
	private static Map<String, RGB> namedColors;
	
	static
	{
		namedColors = new HashMap<String, RGB>();
		namedColors.put("aqua", new RGB(0, 255, 255));
		namedColors.put("black", new RGB(0, 0, 0));
		namedColors.put("blue", new RGB(0, 0, 255));
		namedColors.put("fuchsia", new RGB(255, 0, 255));
		namedColors.put("gray", new RGB(128, 128, 128));
		namedColors.put("green", new RGB(0, 128, 0));
		namedColors.put("lime", new RGB(0, 255, 0));
		namedColors.put("maroon", new RGB(128, 0, 0));
		namedColors.put("navy", new RGB(0, 0, 128));
		namedColors.put("olive", new RGB(128, 128, 0));
		namedColors.put("orange", new RGB(255, 165, 0));
		namedColors.put("purple", new RGB(128, 0, 128));
		namedColors.put("red", new RGB(255, 0, 0));
		namedColors.put("silver", new RGB(192, 192, 192));
		namedColors.put("teal", new RGB(0, 128, 128));
		namedColors.put("whie", new RGB(255, 255, 255));
		namedColors.put("yellow", new RGB(255, 255, 0));
	}
	
	public static RGB parseColor(String colorParam)
	{
		if (colorParam == null || colorParam.isEmpty())
			throw new IllegalArgumentException();
		
		RGB rgb = null;
		String colorStr = colorParam.trim().toLowerCase();
		if (colorStr.startsWith("#"))
		{
			if (colorStr.length() != 7)
			{
				throw new IllegalArgumentException();
			}
			int r = Integer.valueOf(colorStr.substring(1, 3), 16);
			int g = Integer.valueOf(colorStr.substring(3, 5), 16);
			int b = Integer.valueOf(colorStr.substring(5, 7), 16);
			rgb = new RGB(r, g, b);
		}
		else 
		{
			rgb = namedColors.get(colorStr);
		}
		return rgb;
	}
}
