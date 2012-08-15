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
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeModel extends ShapeModel 
{
	private List<ShapeModel> children;
	
	public ContainerShapeModel(DiagramNodeModel nodeModel, ShapeModel parent, ContainerShapePart part)
	{
		super(nodeModel, parent, part);
		
		children = new ArrayList<ShapeModel>();
		for (ShapePart shapePart : part.getChildren())
		{
			ShapeModel childModel = null;
        	if (shapePart instanceof TextPart)
        	{
    	        childModel = new TextModel(nodeModel, this, (TextPart)shapePart);
        	}
        	else if (shapePart instanceof ImagePart)
        	{
        		childModel = new ImageModel(nodeModel, this, (ImagePart)shapePart);
        	}
        	else if (shapePart instanceof ValidationMarkerPart)
        	{
        		childModel = new ValidationMarkerModel(nodeModel, this, (ValidationMarkerPart)shapePart);
        	}
        	else if (shapePart instanceof RectanglePart)
        	{
        		childModel = new RectangleModel(nodeModel, this, (RectanglePart)shapePart);
        	}
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
