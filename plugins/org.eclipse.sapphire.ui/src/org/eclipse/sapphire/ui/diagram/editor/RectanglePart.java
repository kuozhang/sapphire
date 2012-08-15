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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.LineStyle;
import org.eclipse.sapphire.ui.diagram.shape.def.Background;
import org.eclipse.sapphire.ui.diagram.shape.def.Rectangle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectanglePart extends ContainerShapePart
{
	private Rectangle rectangleDef;
	private IModelElement modelElement;
	private FunctionResult borderWidth;	
	private FunctionResult borderColor;
	
	@Override
    protected void init()
    {
        super.init();
        this.rectangleDef = (Rectangle)super.definition;
        this.modelElement = getModelElement();
        
        this.borderWidth = initExpression
        ( 
            this.modelElement,
            this.rectangleDef.getBorder().element().getWidth().getContent(),
            Integer.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshBorder();
                }
            }
        );
        
        this.borderColor = initExpression
        ( 
            this.modelElement,
            this.rectangleDef.getBorder().element().getColor().getContent(),
            Color.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshBorder();
                }
            }
        );
        
    }

	public boolean hasBorder()
	{
		return this.rectangleDef.getBorder().element() != null;
	}
	
	public int getBorderWidth()
	{
		int width = 0;
        if( this.borderWidth != null )
        {
            width = (Integer) this.borderWidth.value();
        }
        
        return width;
	}
	
	public Color getBorderColor()
	{
		Color borderColor = new Color(0, 0, 0);
        if( this.borderColor != null )
        {
            borderColor = (Color) this.borderColor.value();
        }
        
        return borderColor;
	}
	
	public LineStyle getBorderStyle()
	{
		LineStyle style = this.rectangleDef.getBorder().element().getStyle().getContent();
		return style;
	}

	public Background getBackground()
	{
		return this.rectangleDef.getBackground().element();
	}
	
	public int getCornerRadius()
	{
		return this.rectangleDef.getCornerRadius().getContent();
	}
	
	@Override
    public void dispose()
    {
        super.dispose();
        if (this.borderWidth != null)
        {
            this.borderWidth.dispose();
        }
        if (this.borderColor != null)
        {
            this.borderColor.dispose();
        }
    }
    
	private void refreshBorder()
	{
		
	}
}
