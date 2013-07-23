/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutOrientation;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.OrthogonalLineFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.SapphireImageFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.SmoothImageFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.SpacerFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireSequenceLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.ContainerShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ImagePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.LineShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.RectanglePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapeFactoryPresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.SpacerPresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.TextPresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ValidationMarkerPresentation;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class ShapeUtil {  
	
	public static IFigure createFigureForShape(ShapePresentation shapePresentation, DiagramResourceCache resourceCache,
			DiagramConfigurationManager configManager)
	{
		IFigure figure = createFigure(shapePresentation, resourceCache, configManager);
		
		if (shapePresentation instanceof ContainerShapePresentation)
		{
			ContainerShapePresentation containerPresentation = (ContainerShapePresentation)shapePresentation;
			ShapeLayoutDef layoutDef = containerPresentation.getLayout();
			for (ShapePresentation childShapePresentation : containerPresentation.getChildren())
			{
				if (!(childShapePresentation instanceof ContainerShapePresentation) && 
					!childShapePresentation.getPart().isActive()) {
					IFigure childFigure = createFigureForShape(childShapePresentation, resourceCache, configManager);
					if (childFigure != null)
					{
						Object layoutConstraint = getLayoutConstraint(childShapePresentation, layoutDef);
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
				if (childShapePresentation instanceof ShapeFactoryPresentation) {
					ShapeFactoryPresentation shapeFactoryPresentation = (ShapeFactoryPresentation)childShapePresentation;
					shapeFactoryPresentation.setIndex(figure.getChildren().size());
				}
			}
		}
		return figure;
	}
	
	private static IFigure createFigure(ShapePresentation shapePresentation, DiagramResourceCache resourceCache, DiagramConfigurationManager configManager)
	{
		IFigure figure = null;
		if (shapePresentation instanceof TextPresentation)
		{
			TextPresentation textPresentation = (TextPresentation)shapePresentation;
			if (textPresentation.visible())
			{
				figure = new TextFigure(resourceCache, textPresentation);
			}
		}
		else if (shapePresentation instanceof ImagePresentation)
		{
			ImagePresentation imagePresentation = (ImagePresentation)shapePresentation;
			if (imagePresentation.visible()) 
			{
				ImagePart imagePart = imagePresentation.getImagePart();
				DiagramNodePart nodePart = imagePart.nearest(DiagramNodePart.class);
				final ImageData data = imagePresentation.getImage();
				if (data != null) 
				{
					figure = new SapphireImageFigure(imagePresentation, nodePart.getSwtResourceCache().image(data));
				}
				else 
				{
					figure = new SmoothImageFigure();
				}
			}
		}
		else if (shapePresentation instanceof ValidationMarkerPresentation)
		{
			ValidationMarkerPresentation markerPresentation = (ValidationMarkerPresentation)shapePresentation;
			if (markerPresentation.visible())
			{
				figure = markerPresentation.getValidationMarkerFigure();
			}
		}
		else if (shapePresentation instanceof LineShapePresentation)
		{
			LineShapePresentation linePresentation = (LineShapePresentation)shapePresentation;
			if (linePresentation.visible())
			{
				figure = new OrthogonalLineFigure(linePresentation, resourceCache);
			}
		}
		else if (shapePresentation instanceof RectanglePresentation)
		{
			RectanglePresentation rectPresentation = (RectanglePresentation)shapePresentation;
			figure = new RectangleFigure(rectPresentation, resourceCache, configManager);
		}
		else if (shapePresentation instanceof SpacerPresentation)
		{
			SpacerPresentation spacerPresentation = (SpacerPresentation)shapePresentation;
			if (spacerPresentation.visible())
			{
				figure = new SpacerFigure(spacerPresentation);
			}
		}
		
		shapePresentation.setFigure(figure);
		return figure;
	}
	
	public static boolean updateFigureForShape(ShapePresentation updateShape, DiagramResourceCache resourceCache, DiagramConfigurationManager configManager)
	{
		if (updateShape == null || updateShape instanceof ContainerShapePresentation) {
			return false;
		}
		
		IFigure updateFigure = updateShape.getFigure();
		IFigure containerFigure = updateShape.getParentFigure();
		if (updateShape.visible()) 
		{			
			ContainerShapePresentation containerPresentation = (ContainerShapePresentation)updateShape.getParent();
			// find the parent figure
			if (containerFigure != null) 
			{
				int index = containerPresentation.getChildFigureIndex(updateShape);
				if (updateFigure != null) 
				{
					// first delete it
					containerFigure.remove(updateFigure);
				}
				// add it
				updateFigure = createFigure(updateShape, resourceCache, configManager);

				Object layoutConstraint = getLayoutConstraint(updateShape, containerPresentation.getLayout());
				if (layoutConstraint != null)
				{
					containerFigure.add(updateFigure, layoutConstraint, index);
				}
				else
				{
					containerFigure.add(updateFigure, index);
				}
				containerFigure.revalidate();
			}
		} 
		else if (!updateShape.visible() && updateFigure != null) 
		{
			// remove it
			updateShape.removeFigure();
			containerFigure.remove(updateFigure);
			containerFigure.revalidate();
		}
		return true;		
	}
	
	public static Object getLayoutConstraint(ShapePresentation childShapePresentation, ShapeLayoutDef layoutDef)
	{
		Object layoutConstraint = null;
		if (layoutDef instanceof SequenceLayoutDef)
		{
			if (((SequenceLayoutDef)layoutDef).getOrientation().content() != SequenceLayoutOrientation.STACKED)
			{
				SequenceLayoutConstraintDef def = (SequenceLayoutConstraintDef)childShapePresentation.getLayoutConstraint();
				layoutConstraint = new SapphireSequenceLayoutConstraint(def);
			}
			else
			{
				SapphireStackLayoutConstraint constraint = null;
				if (childShapePresentation.getLayoutConstraint() != null)
				{
					SequenceLayoutConstraintDef constraintDef = 
							(SequenceLayoutConstraintDef)childShapePresentation.getLayoutConstraint();
					if (constraintDef != null)
					{
						constraint = new SapphireStackLayoutConstraint(
								constraintDef.getHorizontalAlignment().content(),
								constraintDef.getVerticalAlignment().content(),
								constraintDef.getTopMargin().content(),
								constraintDef.getBottomMargin().content(),
								constraintDef.getLeftMargin().content(),
								constraintDef.getRightMargin().content());
					}
				}	
				layoutConstraint = constraint != null ? constraint : new SapphireStackLayoutConstraint();
			}
		}
		return layoutConstraint;
	}
	
	public static int getTextAlignment(LayoutConstraintDef constraint)
	{
		int alignment = PositionConstants.CENTER;
		HorizontalAlignment sapphireAlign = constraint.getHorizontalAlignment().content();
		switch (sapphireAlign) 
		{
			case LEFT:
				alignment = PositionConstants.LEFT;
				break;
			case RIGHT:
				alignment = PositionConstants.RIGHT;
				break;
			default:			
				break;
		}
		return alignment;
	}
	
	public static int getPresentationCount(ContainerShapePresentation parentPresentation, ShapePresentation shapePresentation) {
		int count = 0;
		for (ShapePresentation sp : parentPresentation.getChildren()) {
			if (shapePresentation.equals(sp)) {
				return count;
			}
			if (!(sp instanceof ContainerShapePresentation)) {
				count++;
			}
		}
		return count;
	}

}
