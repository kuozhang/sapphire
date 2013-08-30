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

import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderComponent;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectanglePresentation extends ContainerShapePresentation 
{
	public RectanglePresentation(ShapePresentation parent, RectanglePart rectanglePart, DiagramConfigurationManager configManager)
	{
		super(parent, rectanglePart, configManager);
	}

	@Override
	public RectanglePart part()
	{
		return (RectanglePart) super.part();
	}
	
	public BackgroundDef getBackground()
	{
		return part().getBackground();
	}
	
	public int getCornerRadius()
	{
		return part().getCornerRadius();
	}
	
	public BorderComponent getTopBorder() 
	{
		return part().getTopBorder();
	}
	
	public BorderComponent getBottomBorder() 
	{
		return part().getBottomBorder();
	}

	public BorderComponent getLeftBorder() 
	{
		return part().getLeftBorder();
	}
	
	public BorderComponent getRightBorder() 
	{
		return part().getRightBorder();
	}
		
}
