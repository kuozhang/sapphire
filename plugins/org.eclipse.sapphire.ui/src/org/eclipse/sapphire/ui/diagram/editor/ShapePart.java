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
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraint;
import org.eclipse.sapphire.ui.diagram.shape.def.Shape;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapePart extends SapphirePart 
{
	private Shape shapeDef;
	private IModelElement modelElement;

	@Override
    protected void init()
    {
        super.init();
        this.shapeDef = (Shape)super.definition;
        this.modelElement = getModelElement();
    }

	public LayoutConstraint getLayoutConstraint()
	{
		return this.shapeDef.getLayoutConstraint().element();
	}
	
	@Override
	public void render(SapphireRenderingContext context) 
	{
		// TODO Auto-generated method stub

	}

}
