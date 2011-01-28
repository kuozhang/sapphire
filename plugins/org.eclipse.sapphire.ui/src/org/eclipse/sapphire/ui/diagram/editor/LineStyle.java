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

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public enum LineStyle 
{
	SOLID,
	DASH,
	DOT,
	DASH_DOT;
	
	public static LineStyle getLineStyle(String name)
	{
		LineStyle style = null;
		if (name.equalsIgnoreCase("solid"))
			style = SOLID;
		else if (name.equalsIgnoreCase("dash"))
			style = DASH;
		else if (name.equalsIgnoreCase("dot"))
			style = DOT;
		else if (name.equalsIgnoreCase("dash-dot"))
			style = DASH_DOT;
		return style;
	}
}
