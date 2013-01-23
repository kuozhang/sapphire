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

package org.eclipse.sapphire.ui.swt.gef.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModelUtil;
import org.eclipse.sapphire.ui.swt.gef.presentation.ContainerShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeEditPart extends AbstractGraphicalEditPart 
			implements IConfigurationManagerHolder, PropertyChangeListener
{
	
	private DiagramConfigurationManager configManager;

	public ShapeEditPart(DiagramConfigurationManager configManager) 
	{
    	this.configManager = configManager;
    }
    
    public DiagramConfigurationManager getConfigurationManager() 
    {
    	return this.configManager;
    }

	@Override
	public void activate() 
	{
		if (!isActive()) 
		{
			super.activate();
			if (getModel() instanceof ShapeModel)
			{
				ShapeModel shapeModel = (ShapeModel)getModel();
				//shapeModel.getNodeModel().addPropertyChangeListener(this);
				shapeModel.addPropertyChangeListener(this);
			}
		}
	}
    
	@Override
	public void deactivate() 
	{
		if (isActive()) 
		{
			if (getModel() instanceof ShapeModel)
			{
				ShapeModel shapeModel = (ShapeModel)getModel();
				//shapeModel.getNodeModel().removePropertyChangeListener(this);
				shapeModel.removePropertyChangeListener(this);
				super.deactivate();
			}
		}
	}
	
	@Override
	protected IFigure createFigure() 
	{
		ShapeModel shapeModel = (ShapeModel)getModel();
		ShapePresentation shapePresentation = shapeModel.getShapePresentation();
		DiagramNodeEditPart nodeEditPart = getNodeEditPart();
		IFigure fig = ShapeUtil.createFigureForShape(shapePresentation,  
					nodeEditPart.getCastedModel().getDiagramModel().getResourceCache());
		return fig;
	}

	@Override
	protected void createEditPolicies() 
	{
		// TODO Auto-generated method stub
	}

	public void propertyChange(PropertyChangeEvent evt) 
	{
	}
		
	public DiagramNodeEditPart getNodeEditPart()
	{
		EditPart parent = this;
		while (!(parent instanceof DiagramNodeEditPart))
		{
			parent = parent.getParent();
		}
		return (DiagramNodeEditPart)parent;
	}
	
	public ShapePresentation getShapePresentation()
	{
		ShapeModel shapeModel = (ShapeModel)getModel();
		ShapePresentation shapePresentation = shapeModel.getShapePresentation();
		return shapePresentation;
	}
	
	protected ContainerShapePresentation getParentContainer(ShapePresentation shapePresentation)
	{
		ShapePresentation parentPresentation = shapePresentation.getParent();
		while (!(parentPresentation instanceof ContainerShapePresentation) && parentPresentation != null)
		{
			parentPresentation = parentPresentation.getParent();
		}
		return (ContainerShapePresentation)parentPresentation;
	}
	
	protected IFigure getPartFigure(ShapePart shapePart)
	{
		ShapePresentation shapePresentation = ShapeModelUtil.getChildShapePresentation(
				getNodeEditPart().getCastedModel().getShapePresentation(), shapePart);
		return shapePresentation.getFigure();
		
	}
	
}
