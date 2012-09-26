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

package org.eclipse.sapphire.ui.swt.gef.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeModelUtil 
{
	public static List<ShapeModel> collectActiveChildrenRecursively(ContainerShapeModel containerShapeModel) 
	{
		List<ShapeModel> activeChildren = new ArrayList<ShapeModel>();
		List<ShapeModel> modelChildren = containerShapeModel.getChildren();
		for (ShapeModel shapeModel : modelChildren)
		{
			ShapePart shapePart = (ShapePart)shapeModel.getSapphirePart();
			if (shapePart.isActive())
			{
				activeChildren.add(shapeModel);
			}
			else if (shapeModel instanceof ContainerShapeModel)
			{
				activeChildren.addAll(collectActiveChildrenRecursively((ContainerShapeModel)shapeModel));
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
		if (parent instanceof ContainerShapeModel)
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
		return null;
	}
	
}
