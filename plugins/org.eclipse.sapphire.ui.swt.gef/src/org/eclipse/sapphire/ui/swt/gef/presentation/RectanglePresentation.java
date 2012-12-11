/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderComponent;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectanglePresentation extends ContainerShapePresentation 
{
	public RectanglePresentation(ShapePresentation parent, RectanglePart rectanglePart)
	{
		super(parent, rectanglePart);
	}

	public RectanglePart getRectanglePart()
	{
		return (RectanglePart)getPart();
	}
	
	public BackgroundDef getBackground()
	{
		return getRectanglePart().getBackground();
	}
	
	public int getCornerRadius()
	{
		return getRectanglePart().getCornerRadius();
	}
	
	public BorderComponent getTopBorder() 
	{
		return getRectanglePart().getTopBorder();
	}
	
	public BorderComponent getBottomBorder() 
	{
		return getRectanglePart().getBottomBorder();
	}

	public BorderComponent getLeftBorder() 
	{
		return getRectanglePart().getLeftBorder();
	}
	
	public BorderComponent getRightBorder() 
	{
		return getRectanglePart().getRightBorder();
	}
	
}
