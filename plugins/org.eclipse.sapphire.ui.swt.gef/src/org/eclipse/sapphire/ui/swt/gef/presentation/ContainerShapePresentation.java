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

package org.eclipse.sapphire.ui.swt.gef.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarkerSize;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapePresentation extends ShapePresentation 
{
	private List<ShapePresentation> children;
	private ValidationMarkerPresentation validationMarker;
	
	public ContainerShapePresentation(ShapePresentation parent, ContainerShapePart containerShapePart,
			DiagramConfigurationManager configManager)
	{
		super(parent, containerShapePart, configManager);
		
		this.children = new ArrayList<ShapePresentation>();
		ShapePresentation childPresentation = null;
		for (ShapePart shapePart : containerShapePart.getChildren())
		{
			childPresentation = ShapePresentationFactory.createShapePresentation(this, shapePart, configManager);
			this.children.add(childPresentation);
			if (childPresentation instanceof ValidationMarkerPresentation)
			{
				this.validationMarker = (ValidationMarkerPresentation)childPresentation;
			}
		}
	}
	
	public List<ShapePresentation> getChildren()
	{
		return this.children;
	}
	
	public ContainerShapePart getContainerShapePart()
	{
		return (ContainerShapePart)getPart();
	}
	
	public boolean containsValidationMarker()
	{
		return getContainerShapePart().containsValidationMarker();
	}
	
	public int getValidationMarkerIndex()
	{
		return getContainerShapePart().getValidationMarkerIndex();
	}

	public ValidationMarkerSize getValidationMarkerSize()
	{
		ValidationMarkerSize size = null;
		if (this.validationMarker != null)
		{
			size = this.validationMarker.getSize();
		}
		return size;
	}
	
	public ValidationMarkerPresentation getValidationMarkerPresentation()
	{
		return this.validationMarker;
	}
	
	public ShapeLayoutDef getLayout()
	{
		return getContainerShapePart().getLayout();
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
	
	@Override
	public void refreshVisuals()
	{
		super.refreshVisuals();
		for (ShapePresentation shapePresentation : getChildren())
		{
			shapePresentation.refreshVisuals();
		}
	}

	public int getChildFigureIndex(ShapePresentation childShapePresentation)
	{
		int shapeIndex = getChildren().indexOf(childShapePresentation);
		if (shapeIndex == -1)
		{
			return -1;
		}

		int figureIndex = 0;
		for (int i = 0; i < shapeIndex; i++)
		{
			if (getChildren().get(i).getFigure() != null)
			{
				figureIndex++;
			}
		}
		return figureIndex;
	}
}

