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

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
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
		return null;
	}

	@Override
	protected void createEditPolicies() 
	{
		// TODO Auto-generated method stub
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
		
	public IModelElement getModelElement()
	{
		ShapeModel shapeModel = (ShapeModel)getModel();
		return shapeModel.getNodeModel().getModelPart().getLocalModelElement();
	}
	
}
