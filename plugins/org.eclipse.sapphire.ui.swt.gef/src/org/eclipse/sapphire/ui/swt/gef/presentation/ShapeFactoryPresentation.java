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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
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
	private Map<ShapePresentation, IFigure> separatorMap;
	private int index;
	
	public ShapeFactoryPresentation(ShapePresentation parent, ShapeFactoryPart shapeFactoryPart,
			DiagramConfigurationManager configManager)
	{
		super(parent, shapeFactoryPart, configManager);
		
		this.children = new ArrayList<ShapePresentation>();
		ShapePresentation childPresentation = null;
		for (ShapePart shapePart : shapeFactoryPart.getChildren())
		{
			childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart, configManager);
			this.children.add(childPresentation);
		}
		if (shapeFactoryPart.getSeparator() != null)
		{
			this.separator = ShapePresentationFactory.createShapePresentation(this, shapeFactoryPart.getSeparator(), configManager);
			this.separatorMap = new HashMap<ShapePresentation, IFigure>();
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
			childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart, getConfigurationManager());
			this.children.add(childPresentation);
		}		
	}
	
	public ShapePresentation getSeparator()
	{
		return this.separator;
	}
	
	public void addSeparatorFigure(ShapePresentation child, IFigure fig)
	{
		this.separatorMap.put(child, fig);
	}
	
	public void removeSeparatorFigure(ShapePresentation child)
	{
		this.separatorMap.remove(child);
	}
	
	public IFigure getSeparatorFigure(ShapePresentation child)
	{
		return this.separatorMap.get(child);
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
