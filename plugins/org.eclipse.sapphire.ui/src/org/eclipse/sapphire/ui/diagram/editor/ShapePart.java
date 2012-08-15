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
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapePart extends SapphirePart 
{
	private ShapeDef shapeDef;
	private IModelElement modelElement;

	@Override
    protected void init()
    {
        super.init();
        this.shapeDef = (ShapeDef)super.definition;
        this.modelElement = getModelElement();
    }

	public LayoutConstraintDef getLayoutConstraint()
	{
		return this.shapeDef.getLayoutConstraint().element();
	}
	
	@Override
	public void render(SapphireRenderingContext context) 
	{
		// TODO Auto-generated method stub

	}

}
