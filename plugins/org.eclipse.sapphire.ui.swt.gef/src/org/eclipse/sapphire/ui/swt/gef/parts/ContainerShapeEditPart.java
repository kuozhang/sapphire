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

package org.eclipse.sapphire.ui.swt.gef.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.ContainerShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeEditPart extends ShapeEditPart 
{
	
	public ContainerShapeEditPart(DiagramConfigurationManager configManager) 
	{
    	super(configManager);    	
    }

	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		// TODO need to recursively collect all the active children
		ContainerShapeModel containerModel = (ContainerShapeModel)getModel();
		List<ShapeModel> modelChildren = containerModel.getChildren();
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		for (ShapeModel shapeModel : modelChildren)
		{
			ShapePart shapePart = (ShapePart)shapeModel.getSapphirePart();
			if (shapePart.isActive())
			{
				returnedModelChildren.add(shapeModel);
			}
		}
		return returnedModelChildren;
	}
	
	@Override
	protected IFigure createFigure() 
	{
		return null;
	}
		
}
