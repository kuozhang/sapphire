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

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.sapphire.Color;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.LinePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class LineShapePresentation extends ShapePresentation 
{
	public LineShapePresentation(ShapePresentation parent, LinePart linePart)
	{
		super(parent, linePart);
	}

	public LinePart getLinePart()
	{
		return (LinePart)getPart();
	}
	
	public boolean isHorizontal()
	{
		return getLinePart().getOrientation() == Orientation.HORIZONTAL;
	}
	
	public int getWeight()
	{
		return getLinePart().getWeight();
	}
	
	public Color getColor()
	{
		return getLinePart().getColor();
	}
	
	public LineStyle getStyle()
	{
		return getLinePart().getStyle();
	}
	
}
