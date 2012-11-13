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

import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeModel extends ShapeModel 
{
	public final static String SHAPE_VALIDATION = "SHAPE_VALIDATION";
	private List<ShapeModel> children;
	
	public ContainerShapeModel(DiagramNodeModel nodeModel, ShapeModel parent, ContainerShapePart part)
	{
		super(nodeModel, parent, part);
		
		children = new ArrayList<ShapeModel>();
		for (ShapePart shapePart : part.getChildren())
		{
			ShapeModel childModel = ShapeModelFactory.createShapeModel(nodeModel, this, shapePart);
        	if (childModel != null)
        	{        		
        		this.children.add(childModel);
        	}        				
		}
	}
		
	public List<ShapeModel> getChildren()
	{
		return this.children;
	}
		
	public void handleShapeValidation() 
	{
		firePropertyChange(SHAPE_VALIDATION, null, null);
	}

}
