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

import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapeFactoryPresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryModel extends ShapeModel 
{
	private List<ShapeModel> children;
	private ShapeFactoryPresentation shapeFactoryPresentation;
	public final static String SHAPE_ADD = "SHAPE_ADD";
	public final static String SHAPE_DELETE = "SHAPE_DELETE";
	public final static String SHAPE_REORDER = "SHAPE_REORDER";	
	
	public ShapeFactoryModel(DiagramNodeModel nodeModel, ShapeModel parent, ShapeFactoryPresentation presentation)
	{
		super(nodeModel, parent, presentation);
		this.shapeFactoryPresentation = presentation;
		children = new ArrayList<ShapeModel>();
		if (this.shapeFactoryPresentation.getPart().visible()) 
		{
			for (ShapePresentation shapePresentation : this.shapeFactoryPresentation.getChildren())
			{
				ShapeModel childModel = ShapeModelFactory.createShapeModel(nodeModel, this, shapePresentation);
	        	if (childModel != null)
	        	{        		
	        		this.children.add(childModel);
	        	}        				
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
		this.shapeFactoryPresentation.refreshChildren();
		List<ShapeModel> refreshedChildren = new ArrayList<ShapeModel>();
		if (this.shapeFactoryPresentation.getPart().visible()) 
		{
			for (ShapePresentation shapePresentation : this.shapeFactoryPresentation.getChildren())
			{
				ShapeModel childModel = ShapeModelFactory.createShapeModel(getNodeModel(), this, shapePresentation);
	        	if (childModel != null)
	        	{        		
	        		refreshedChildren.add(childModel);
	        	}        				
			}
		}
		children = refreshedChildren;
	}
	
	public List<ShapeModel> getChildren()
	{
		return this.children;
	}
	
}
