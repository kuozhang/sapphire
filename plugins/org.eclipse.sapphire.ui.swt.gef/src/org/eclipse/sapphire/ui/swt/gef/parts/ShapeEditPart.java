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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.ContainerShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;

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

//	@Override
//	public void activate() 
//	{
//		if (!isActive()) 
//		{
//			super.activate();
//			if (getModel() instanceof ShapeModel)
//			{
//				ShapeModel shapeModel = (ShapeModel)getModel();
//				shapeModel.getNodeModel().addPropertyChangeListener(this);
//			}
//		}
//	}
//    
//	@Override
//	public void deactivate() 
//	{
//		if (isActive()) 
//		{
//			if (getModel() instanceof ShapeModel)
//			{
//				ShapeModel shapeModel = (ShapeModel)getModel();
//				shapeModel.getNodeModel().removePropertyChangeListener(this);			
//				super.deactivate();
//			}
//		}
//	}
	
	@Override
	protected IFigure createFigure() 
	{
		ShapeModel shapeModel = (ShapeModel)getModel();
		ShapePart shapePart = (ShapePart)shapeModel.getSapphirePart();
		DiagramNodeEditPart nodeEditPart = getNodeEditPart();
		IFigure fig = ShapeUtil.createFigureForShape(shapePart, nodeEditPart.getPartFigureMap(), 
					nodeEditPart.getCastedModel().getDiagramModel().getResourceCache());
		return fig;
	}

	@Override
	protected void createEditPolicies() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		Object modelObj = getModel();
		if (modelObj instanceof ContainerShapeModel)
		{
			ContainerShapeModel containerModel = (ContainerShapeModel)modelObj;
			returnedModelChildren.addAll(collectActiveChildrenRecursively(containerModel));
		}
		else if (modelObj instanceof DiagramNodeModel)
		{
			DiagramNodeModel nodeModel = (DiagramNodeModel)getModel();
			ShapeModel shapeModel = nodeModel.getShapeModel();
			if (shapeModel instanceof ContainerShapeModel)
			{
				ContainerShapeModel containerModel = (ContainerShapeModel)shapeModel;
				returnedModelChildren.addAll(collectActiveChildrenRecursively(containerModel));
			}
		}		
		
		return returnedModelChildren;
	}

	public void propertyChange(PropertyChangeEvent evt) 
	{
		String prop = evt.getPropertyName();
		if (DiagramNodeModel.NODE_UPDATES.equals(prop)) 
		{
			refreshChildren();
			refreshVisuals();
		}
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

	private List<ShapeModel> collectActiveChildrenRecursively(ContainerShapeModel containerShapeModel) 
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
	
}
