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

package org.eclipse.sapphire.ui.swt.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.presentation.ContainerShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapeFactoryPresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeModelUtil 
{
	/*
	 * Collect all the active children of a given shape model in the shape model hierarchical tree.
	 * Shape model hierarchy is built on top of shape part hierarchy with one-to-one mapping between
	 * a shape model and a shape part. Shape models are used to feed GEF's edit part factory
	 * to create GEF edit part hierarchy.
	 * 
	 * We don't create a GEF edit part for all the shape parts(with shape models between them). 
	 * We only create a GEF edit part for "active" shape parts. Active shape parts can be selected, 
	 * moved or resized.
	 * 
	 * By default, only shape factory part and shape generated by shape factory are active.
	 * We could extend the shape definition language later to allow user to specify which shape
	 * part is active.
	 * 
	 * Since a GEF part will be created for shape factory part, that GEF part is the parent of
	 * all the shape edit parts generated from the factory. This method is used to collect all the
	 * shape models that correspond to active shape parts that are not generated by a shape factory.
	 * The returned "active" shape models will be used to create corresponding GEF parts.
	 */
	public static List<ShapeModel> collectActiveChildrenRecursively(ContainerShapeModel containerShapeModel) 
	{
		List<ShapeModel> activeChildren = new ArrayList<ShapeModel>();
		List<ShapeModel> modelChildren = containerShapeModel.getChildren();
		for (ShapeModel shapeModel : modelChildren)
		{
			ShapePart shapePart = (ShapePart)shapeModel.getSapphirePart();
			if (shapePart.isActive() || shapeModel instanceof RectangleModel)
			{
				activeChildren.add(shapeModel);
			}
		}
		return activeChildren;
	}
	
	public static ShapeModel getChildShapeModel(ShapeModel parent, ShapePart shapePart)
	{
		if (parent.getSapphirePart().equals(shapePart))
		{
			return parent;
		}
		else if (parent instanceof ContainerShapeModel)
		{
			ContainerShapeModel container = (ContainerShapeModel)parent;
			for (ShapeModel child :container.getChildren())
			{
				ShapeModel model = getChildShapeModel(child, shapePart);
				if (model != null)
				{
					return model;
				}
			}
		}
		else if (parent instanceof ShapeFactoryModel)
		{
			ShapeFactoryModel container = (ShapeFactoryModel)parent;
			for (ShapeModel child :container.getChildren())
			{
				ShapeModel model = getChildShapeModel(child, shapePart);
				if (model != null)
				{
					return model;
				}
			}
			
		}
		return null;
	}
	
	public static ContainerShapeModel getNearestContainerModel(ShapeModel shapeModel)
	{
		if (shapeModel instanceof ContainerShapeModel)
		{
			return (ContainerShapeModel)shapeModel;
		}
		ShapeModel parentModel = shapeModel.getParent();
		while (!(parentModel instanceof ContainerShapeModel) && parentModel != null)
		{
			parentModel = parentModel.getParent();
		}
		return (ContainerShapeModel)parentModel;
	}
	
	
	public static ShapePresentation getChildShapePresentation(ShapePresentation parent, ShapePart shapePart)
	{
		if (parent.part().equals(shapePart))
		{
			return parent;
		}
		else if (parent instanceof ContainerShapePresentation)
		{
			ContainerShapePresentation container = (ContainerShapePresentation)parent;
			for (ShapePresentation child :container.getChildren())
			{
				ShapePresentation presentation = getChildShapePresentation(child, shapePart);
				if (presentation != null)
				{
					return presentation;
				}
			}
		}
		else if (parent instanceof ShapeFactoryPresentation)
		{
			ShapeFactoryPresentation container = (ShapeFactoryPresentation)parent;
			for (ShapePresentation child : container.getChildren())
			{
				ShapePresentation presentation = getChildShapePresentation(child, shapePart);
				if (presentation != null)
				{
					return presentation;
				}
			}
			
		}
		return null;
	}
}
