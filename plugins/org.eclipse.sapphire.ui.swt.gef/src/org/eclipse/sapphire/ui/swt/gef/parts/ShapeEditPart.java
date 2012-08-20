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

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutConstraint;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.FigureUtil;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.swt.SWT;

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
	
	private DiagramResourceCache getResourceCache()
	{
		if (getModel() instanceof DiagramNodeModel)
		{
			return ((DiagramNodeModel)getModel()).getDiagramModel().getResourceCache();
		}
		ShapeModel shapeModel = (ShapeModel)getModel();
		return shapeModel.getNodeModel().getDiagramModel().getResourceCache();
	}
	
	public IFigure createFigureForShape(ShapePart shapePart)
	{
		IFigure figure = null;
		if (shapePart instanceof TextPart)
		{
			TextPart textPart = (TextPart)shapePart;
			figure = new TextFigure(getResourceCache(), textPart.getText(), textPart.getTextColor());
		}
		else if (shapePart instanceof ValidationMarkerPart)
		{
			ValidationMarkerPart markerPart = (ValidationMarkerPart)shapePart;
			DiagramNodePart nodePart = markerPart.nearest(DiagramNodePart.class);
			figure = FigureUtil.createValidationMarkerFigure(markerPart.getSize(), nodePart.getLocalModelElement(), nodePart.getImageCache());
		}
		else if (shapePart instanceof RectanglePart)
		{
			RectanglePart rectPart = (RectanglePart)shapePart;
			figure = new RectangleFigure(rectPart, getResourceCache());
		}
		
		if (shapePart instanceof ContainerShapePart)
		{
			ContainerShapePart containerPart = (ContainerShapePart)shapePart;
			ShapeLayoutDef layoutDef = containerPart.getLayout();
			for (ShapePart childShapePart : containerPart.getChildren())
			{
				if (!childShapePart.isActive())
				{
					IFigure childFigure = createFigureForShape(childShapePart);
					if (childFigure != null)
					{
						Object layoutConstraint = null;
						if (layoutDef instanceof SequenceLayoutDef)
						{
							SequenceLayoutDef sequenceLayout = (SequenceLayoutDef)layoutDef;
							SequenceLayoutConstraint sequenceLayoutConstraint = (SequenceLayoutConstraint)childShapePart.getLayoutConstraint();
							GridData gd = new GridData();
							if (childFigure instanceof Label)
							{
								if (sequenceLayout.getOrientation().getContent() == Orientation.HORIZONTAL)
								{
									gd.grabExcessHorizontalSpace = sequenceLayoutConstraint.isExpandCellHorizontally().getContent();
									gd.horizontalAlignment = SWT.CENTER;
									gd.verticalAlignment = SWT.CENTER;
									gd.grabExcessVerticalSpace = true;
								}
								else
								{
									gd.grabExcessVerticalSpace = sequenceLayoutConstraint.isExpandCellVertically().getContent();
								}
							}
							else
							{
								if (sequenceLayout.getOrientation().getContent() == Orientation.HORIZONTAL)
								{
									if (sequenceLayoutConstraint != null)
									{
										gd.horizontalIndent = sequenceLayoutConstraint.getHorizontalMargin().getContent();
									}
								}								
							}
							layoutConstraint = gd;
						}
						if (layoutConstraint != null)
						{
							figure.add(childFigure, layoutConstraint);
						}
						else
						{
							figure.add(childFigure);
						}
					}
				}
			}
		}
		return figure;
	}
	
}
