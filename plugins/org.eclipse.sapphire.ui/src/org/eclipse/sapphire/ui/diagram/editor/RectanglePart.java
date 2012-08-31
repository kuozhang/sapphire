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

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.BorderDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectanglePart extends ContainerShapePart
{
	private RectangleDef rectangleDef;
	
	@Override
    protected void init()
    {
        super.init();
        this.rectangleDef = (RectangleDef)super.definition;
    }

	public boolean hasBorder()
	{
		return this.rectangleDef.getBorder().element() != null;
	}
	
	public BorderDef getBorderDef() 
	{
		return this.rectangleDef.getBorder().element();
	}
	
	public BorderDef getTopBorderDef() 
	{
		return this.rectangleDef.getTopBorder().element();
	}
	
	public BorderDef getBottomBorderDef() 
	{
		return this.rectangleDef.getBottomBorder().element();
	}

	public BorderDef getLeftBorderDef() 
	{
		return this.rectangleDef.getLeftBorder().element();
	}
	
	public BorderDef getRightBorderDef() 
	{
		return this.rectangleDef.getRightBorder().element();
	}
	
	public BackgroundDef getBackground()
	{
		return this.rectangleDef.getBackground().element();
	}
	
	public int getCornerRadius()
	{
		return this.rectangleDef.getCornerRadius().getContent();
	}
	
}
