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
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class LineShapePresentation extends ShapePresentation 
{
	public LineShapePresentation(ShapePresentation parent, LinePart linePart, DiagramConfigurationManager configManager)
	{
		super(parent, linePart, configManager);
	}

	@Override
	public LinePart part()
	{
		return (LinePart) super.part();
	}
	
	public boolean isHorizontal()
	{
		return part().getOrientation() == Orientation.HORIZONTAL;
	}
	
	public int getWeight()
	{
		return part().getWeight();
	}
	
	public Color getColor()
	{
		return part().getColor();
	}
	
	public LineStyle getStyle()
	{
		return part().getStyle();
	}
	
}
