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

import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryModel extends ShapeModel 
{
	private List<ShapeModel> children;
	private ShapeFactoryPart shapeFactoryPart;
	public final static String SHAPE_ADD = "SHAPE_ADD";
	public final static String SHAPE_DELETE = "SHAPE_DELETE";
	public final static String SHAPE_REORDER = "SHAPE_REORDER";	
	
	public ShapeFactoryModel(DiagramNodeModel nodeModel, ShapeModel parent, ShapeFactoryPart part)
	{
		super(nodeModel, parent, part);
		this.shapeFactoryPart = part;
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
	
	public void handleAddShape(ShapePart shapePart) 
	{
		refreshChildren();
		firePropertyChange(SHAPE_ADD, null, null);
	}
	
	public void handleDeleteShape(ShapePart shapePart) 
	{
		refreshChildren();
		firePropertyChange(SHAPE_DELETE, null, null);
	}

	public void handleReorderShapes(ShapeFactoryPart shapeFactory) 
	{
		refreshChildren();
		firePropertyChange(SHAPE_REORDER, null, null);
	}
	
	public void refreshChildren()
	{
		this.children.clear();
		List<ShapePart> children = new ArrayList<ShapePart>();
		children.addAll(this.shapeFactoryPart.getChildren());
		for (ShapePart shapePart : children)
		{
			ShapeModel childModel = ShapeModelFactory.createShapeModel(getNodeModel(), this, shapePart);
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
	
}
