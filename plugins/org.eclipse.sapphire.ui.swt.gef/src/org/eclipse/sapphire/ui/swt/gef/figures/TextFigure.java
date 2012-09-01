/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Extend Sapphire Diagram Framework to support SQL Schema diagram like editors
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.shape.def.FontDef;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextFigure extends Label 
{
	private static final org.eclipse.sapphire.ui.Color DEFAULT_TEXT_FOREGROUND = new org.eclipse.sapphire.ui.Color(51, 51, 153);
	private DiagramResourceCache resourceCache;
	
	public TextFigure(DiagramResourceCache resourceCache, String value, Color textColor, FontDef fontDef)
	{
		this.resourceCache = resourceCache;
		
		Color color =  textColor != null ? textColor : DEFAULT_TEXT_FOREGROUND;
		setForegroundColor(resourceCache.getColor(color));
		setLabelAlignment(PositionConstants.CENTER);
		setFont(this.resourceCache.getFont(fontDef));
		setText(value);
	}
}
