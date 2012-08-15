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

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraint;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayout;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.model.RectangleModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.swt.SWT;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class RectangleEditPart extends ContainerShapeEditPart 
{
	
	public RectangleEditPart(DiagramConfigurationManager configManager)
	{
		super(configManager);
	}

	@Override
	protected IFigure createFigure() 
	{
		RectangleModel rectModel = (RectangleModel)getModel();
		RectanglePart rectPart = (RectanglePart)rectModel.getSapphirePart();
		DiagramNodePart nodePart = rectPart.nearest(DiagramNodePart.class);
		DiagramResourceCache resourceCache = rectModel.getNodeModel().getDiagramModel().getResourceCache();
		return new RectangleFigure(rectPart, rectModel.getChildren().size(), getValidationMarkerIndex(), 
				nodePart.getLocalModelElement(), resourceCache);
	}

	@Override
	protected void refreshVisuals() 
	{
//		IFigure parentFigure = ((GraphicalEditPart)getParent()).getFigure();
//		org.eclipse.draw2d.geometry.Rectangle rect = 
//				(org.eclipse.draw2d.geometry.Rectangle)parentFigure.getParent().getLayoutManager().getConstraint(parentFigure);		
//		org.eclipse.draw2d.geometry.Rectangle newRect = new org.eclipse.draw2d.geometry.Rectangle(0, 0, rect.width, rect.height);
//		((GraphicalEditPart)getParent()).setLayoutConstraint(this, this.getFigure(), newRect);
		
		RectangleFigure rectFigure = (RectangleFigure)getFigure();
		rectFigure.refreshValidationStatus();
	}	
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) 
	{
		super.addChildVisual(childEditPart, index);
		RectangleModel rectModel = (RectangleModel)getModel();
		RectanglePart rectPart = (RectanglePart)rectModel.getSapphirePart();
		if (rectPart.getLayout() instanceof SequenceLayout)
		{
			SequenceLayout sequenceLayout = (SequenceLayout)rectPart.getLayout();
			GridLayout layoutManager = (GridLayout)getContentPane().getLayoutManager();
			if (childEditPart instanceof GraphicalEditPart)
			{
				IFigure childFigure = ((GraphicalEditPart)childEditPart).getFigure();
				ShapeModel childModel = (ShapeModel)childEditPart.getModel();
				ShapePart shapePart = (ShapePart)childModel.getSapphirePart();
				LayoutConstraint layoutConstraint = shapePart.getLayoutConstraint();
				if (layoutConstraint instanceof SequenceLayoutConstraint)
				{
					SequenceLayoutConstraint sequenceLayoutConstraint = (SequenceLayoutConstraint)layoutConstraint;
					GridData gd = new GridData();
					if (childFigure instanceof Label)
					{
						setChildLabel((Label)childFigure);
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
					layoutManager.setConstraint(childFigure, gd);
				}
			}
		}
		
	}
			
}
