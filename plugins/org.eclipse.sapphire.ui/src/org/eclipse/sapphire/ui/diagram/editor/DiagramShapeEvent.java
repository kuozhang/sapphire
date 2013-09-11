/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramShapeEvent extends DiagramPartEvent 
{
    public static enum ShapeEventType
    {
        ShapeUpdate,
        TextChange,
        ShapeVisibilityUpdate,
        ShapeAdd,
        ShapeDelete,
        ShapeReorder
    }

    private ShapePart shapePart;
    private ShapeEventType shapeEventType;
	
    public DiagramShapeEvent(final DiagramNodePart part, ShapePart shapePart)
    {
       super(part);
       this.shapePart = shapePart;
    }

	public ShapePart getShapePart() 
	{
		return this.shapePart;
	}

	public void setShapePart(ShapePart shapePart)
	{
		this.shapePart = shapePart;
	}

	public ShapeEventType getShapeEventType() {
		return shapeEventType;
	}

	public void setShapeEventType(ShapeEventType shapeEventType) {
		this.shapeEventType = shapeEventType;
	}
	
}
