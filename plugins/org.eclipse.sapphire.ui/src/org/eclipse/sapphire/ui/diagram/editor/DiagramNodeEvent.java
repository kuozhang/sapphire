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

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeEvent extends DiagramPartEvent 
{
	
	private ShapePart shapePart;
    
    public DiagramNodeEvent(final DiagramNodePart part)
    {
       super(part);
    }

    public DiagramNodeEvent(final DiagramNodePart part, final ShapePart shapePart)
    {
       super(part);
       this.shapePart = shapePart;
    }

	public ShapePart getShapePart() {
		return this.shapePart;
	}

	public void setShapePart(ShapePart shapePart) {
		this.shapePart = shapePart;
	}
    
}
