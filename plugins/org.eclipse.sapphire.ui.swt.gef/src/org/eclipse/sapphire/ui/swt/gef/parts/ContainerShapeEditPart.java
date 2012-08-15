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
import org.eclipse.draw2d.Label;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.ContainerShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ValidationMarkerModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeEditPart extends ShapeEditPart 
{
	private int validationMarkerIndex = -1;
	private Label childLabel;
	
	public ContainerShapeEditPart(DiagramConfigurationManager configManager) 
	{
    	super(configManager);    	
    }

	@Override
	public void setModel(Object model)
	{
		super.setModel(model);
		ContainerShapeModel containerShapeModel = (ContainerShapeModel)getModel();
		ContainerShapePart containerShapePart = (ContainerShapePart)containerShapeModel.getSapphirePart();
		int index = 0;
		for (ShapePart shapePart : containerShapePart.getChildren())
		{
			if (shapePart instanceof ValidationMarkerPart)
			{
				this.validationMarkerIndex = index;
				break;
			}
			index++;
		}
	}
	
	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		ContainerShapeModel containerModel = (ContainerShapeModel)getModel();
		List<ShapeModel> modelChildren = containerModel.getChildren();
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		for (ShapeModel shapeModel : modelChildren)
		{
			if (shapeModel instanceof ValidationMarkerModel)
			{
				if (showValidationMarker())
				{
					returnedModelChildren.add(shapeModel);
				}
			}
			else
			{
				returnedModelChildren.add(shapeModel);
			}
		}
		return returnedModelChildren;
	}
	
	@Override
	protected IFigure createFigure() 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getValidationMarkerIndex()
	{
		return this.validationMarkerIndex;
	}
	
	public boolean showValidationMarker()
	{
		if (this.validationMarkerIndex != -1)
		{
			IModelElement model = this.getModelElement();
			Status status = model.validation();		
			return status.severity() != Status.Severity.OK;			
		}
		return false;
	}
	
	public Label getChildLabel()
	{
		return this.childLabel;
	}
	
	public void setChildLabel(Label label)
	{
		this.childLabel = label;
	}
	
}
