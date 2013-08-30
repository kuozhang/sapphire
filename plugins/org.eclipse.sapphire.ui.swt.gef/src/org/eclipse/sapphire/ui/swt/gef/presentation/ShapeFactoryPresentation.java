/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924]  Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class ShapeFactoryPresentation extends ShapePresentation 
{
	private List<ShapePresentation> children;
	private ShapePresentation separator;
	private int index;
	
	public ShapeFactoryPresentation(ShapePresentation parent, ShapeFactoryPart shapeFactoryPart,
			DiagramConfigurationManager configManager)
	{
		super(parent, shapeFactoryPart, configManager);
		
		this.children = new ArrayList<ShapePresentation>();
		if (shapeFactoryPart.visible())
		{
			ShapePresentation childPresentation = null;
			for (ShapePart shapePart : shapeFactoryPart.getChildren())
			{
				childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart, configManager);
				this.children.add(childPresentation);
			}
		}
		if (shapeFactoryPart.getSeparator() != null)
		{
			this.separator = ShapePresentationFactory.createShapePresentation(this, shapeFactoryPart.getSeparator(), configManager);
			this.separator.setSeparator(true);
		}
	}

	public List<ShapePresentation> getChildren()
	{
		return this.children;
	}
	
	public void refreshChildren()
	{
		List<ShapePresentation> refreshedChildren = new ArrayList<ShapePresentation>();
		ShapeFactoryPart shapeFactoryPart = (ShapeFactoryPart) part();
		if (shapeFactoryPart.visible())
		{
			for (ShapePart shapePart : shapeFactoryPart.getChildren())
			{
				ShapePresentation childPresentation = getChildShapePresentation(shapePart);
				if (childPresentation == null) {
					childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart, getConfigurationManager());
				}
				refreshedChildren.add(childPresentation);
			}		
		}
		this.children = refreshedChildren;
	}
	
	private ShapePresentation getChildShapePresentation(ShapePart shapePart) {
		for (ShapePresentation presentation : getChildren()) {
			if (presentation.part() == shapePart) {
				return presentation;
			}
		}
		return null; 
	}

	public ShapePresentation getSeparator()
	{
		return this.separator;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		for (ShapePresentation shapePresentation : getChildren())
		{
			shapePresentation.dispose();
		}
	}	
}
