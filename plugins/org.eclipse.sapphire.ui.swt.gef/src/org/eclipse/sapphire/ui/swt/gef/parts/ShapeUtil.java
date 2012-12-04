/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.parts;

import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.figures.DecoratorImageFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.FigureUtil;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireSequenceLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class ShapeUtil {  
	
	public static IFigure createFigureForShape(ShapePart shapePart, HashMap<ShapePart, IFigure> partToFigure, DiagramResourceCache resourceCache)
	{
		IFigure figure = createFigure(shapePart, resourceCache);
		partToFigure.put(shapePart, figure);
		
		if (shapePart instanceof ContainerShapePart)
		{
			ContainerShapePart containerPart = (ContainerShapePart)shapePart;
			ShapeLayoutDef layoutDef = containerPart.getLayout();
			for (ShapePart childShapePart : containerPart.getChildren())
			{
				if (!childShapePart.isActive())
				{
					IFigure childFigure = createFigureForShape(childShapePart, partToFigure, resourceCache);
					if (childFigure != null)
					{
						Object layoutConstraint = getLayoutConstraint(childShapePart, layoutDef);
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
	
	private static IFigure createFigure(ShapePart shapePart, DiagramResourceCache resourceCache)
	{
		IFigure figure = null;
		if (shapePart instanceof TextPart)
		{
			TextPart textPart = (TextPart)shapePart;
			int textALignment = getTextAlignment(textPart.getLayoutConstraint());
			figure = new TextFigure(resourceCache, textPart.getContent(), 
					textPart.getTextColor(), textPart.getFontDef(), textALignment);
		}
		else if (shapePart instanceof ImagePart)
		{
			ImagePart imagePart = (ImagePart)shapePart;
			if (imagePart.visible()) {
				DiagramNodePart nodePart = imagePart.nearest(DiagramNodePart.class);
				final ImageData data = imagePart.getImage();
				if (data != null) {
					figure = new DecoratorImageFigure(nodePart.getImageCache().getImage(data));
				}
			}
		}
		else if (shapePart instanceof ValidationMarkerPart)
		{
			ValidationMarkerPart markerPart = (ValidationMarkerPart)shapePart;
			DiagramNodePart nodePart = markerPart.nearest(DiagramNodePart.class);
			figure = FigureUtil.createValidationMarkerFigure(markerPart.getSize(), shapePart.getLocalModelElement(), nodePart.getImageCache());
		}
		else if (shapePart instanceof RectanglePart)
		{
			RectanglePart rectPart = (RectanglePart)shapePart;
			figure = new RectangleFigure(rectPart, resourceCache);
		}
		return figure;
	}
	
	public static boolean updateFigureForShape(ShapePart updateShapePart, HashMap<ShapePart, IFigure> partToFigure, DiagramResourceCache resourceCache)
	{
		IFigure updateFigure = partToFigure.get(updateShapePart);
		ContainerShapePart containerShapePart = null;
		ISapphirePart pp = updateShapePart.getParentPart();
		if (pp instanceof ContainerShapePart)
		{
			containerShapePart = (ContainerShapePart)pp;
		}
		else if (pp instanceof ShapeFactoryPart)
		{
			ISapphirePart ppp = pp.getParentPart();
			assert (ppp instanceof ContainerShapePart);
			containerShapePart = (ContainerShapePart)ppp;
		}
		if (updateShapePart.visible()) 
		{
			IFigure containerFigure = partToFigure.get(containerShapePart);
			// find the parent figure
			if (containerFigure != null) 
			{
				int index = -1;
				if (updateFigure != null) 
				{
					// first delete it
					index = findIndex(containerFigure, updateFigure);
					containerFigure.remove(updateFigure);
				}
				// add it
				updateFigure = createFigure(updateShapePart, resourceCache);
				partToFigure.put(updateShapePart, updateFigure);

				Object layoutConstraint = getLayoutConstraint(updateShapePart, containerShapePart.getLayout());
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
		else if (!updateShapePart.visible() && updateFigure != null) 
		{
			// remove it
			partToFigure.remove(updateShapePart);
			IFigure containerFigure = partToFigure.get(containerShapePart);
			containerFigure.remove(updateFigure);
			containerFigure.revalidate();
		}
		return true;		
	}

	@SuppressWarnings("rawtypes")
	private static int findIndex(IFigure parentFigure, IFigure figure) {
		List list = parentFigure.getChildren();
		for (int i = 0; i < list.size(); i++) {
			IFigure childFigure = (IFigure)list.get(i);
			if (childFigure.equals(figure)) {
				return i;
			}
		}
		return -1;
	}
	
	public static Object getLayoutConstraint(ShapePart childShapePart, ShapeLayoutDef layoutDef)
	{
		Object layoutConstraint = null;
		if (layoutDef instanceof SequenceLayoutDef)
		{
			SequenceLayoutConstraintDef def = (SequenceLayoutConstraintDef)childShapePart.getLayoutConstraint();
			layoutConstraint = new SapphireSequenceLayoutConstraint(def);
		}
		else if (layoutDef instanceof StackLayoutDef)
		{
			if (childShapePart.getLayoutConstraint() != null)
			{
				StackLayoutConstraintDef stackLayoutConstraint = 
						(StackLayoutConstraintDef)childShapePart.getLayoutConstraint();
				SapphireStackLayoutConstraint constraint = null;
				if (stackLayoutConstraint != null)
				{
					constraint = new SapphireStackLayoutConstraint(
										stackLayoutConstraint.getHorizontalAlignment().getContent(),
										stackLayoutConstraint.getVerticalAlignment().getContent(),
										stackLayoutConstraint.getTopMargin().getContent(),
										stackLayoutConstraint.getBottomMargin().getContent(),
										stackLayoutConstraint.getLeftMargin().getContent(),
										stackLayoutConstraint.getRightMargin().getContent());
				}
				else
				{
					constraint = new SapphireStackLayoutConstraint();
				}
				layoutConstraint = constraint;
			}
			
		}
		return layoutConstraint;
	}
	
	public static int getTextAlignment(LayoutConstraintDef constraint)
	{
		int alignment = PositionConstants.CENTER;
		HorizontalAlignment sapphireAlign = constraint.getHorizontalAlignment().getContent();
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
	
}
