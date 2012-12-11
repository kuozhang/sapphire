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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryPresentation extends ShapePresentation 
{
	private List<ShapePresentation> children;
	
	public ShapeFactoryPresentation(ShapePresentation parent, ShapeFactoryPart shapeFactoryPart)
	{
		super(parent, shapeFactoryPart);
		
		this.children = new ArrayList<ShapePresentation>();
		ShapePresentation childPresentation = null;
		for (ShapePart shapePart : shapeFactoryPart.getChildren())
		{
			childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart);
			this.children.add(childPresentation);
		}		
	}

	public List<ShapePresentation> getChildren()
	{
		return this.children;
	}
	
	public void refreshChildren()
	{
		this.children.clear();
		List<ShapePart> children = new ArrayList<ShapePart>();
		ShapeFactoryPart shapeFactoryPart = (ShapeFactoryPart)this.getPart();
		children.addAll(shapeFactoryPart.getChildren());
		ShapePresentation childPresentation = null;
		for (ShapePart shapePart : children)
		{
			childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart);
			this.children.add(childPresentation);
		}		
	}
}
