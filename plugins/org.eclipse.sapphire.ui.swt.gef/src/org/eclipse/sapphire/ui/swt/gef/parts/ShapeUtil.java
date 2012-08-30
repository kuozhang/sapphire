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

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.figures.DecoratorImageFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.FigureUtil;
import org.eclipse.sapphire.ui.swt.gef.figures.RectangleFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayoutConstraint;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.SWT;

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
			figure = new TextFigure(resourceCache, textPart.getText(), textPart.getTextColor());
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
			figure = FigureUtil.createValidationMarkerFigure(markerPart.getSize(), nodePart.getLocalModelElement(), nodePart.getImageCache());
		}
		else if (shapePart instanceof RectanglePart)
		{
			RectanglePart rectPart = (RectanglePart)shapePart;
			figure = new RectangleFigure(rectPart, resourceCache);
		}
		return figure;
	}
	
	public static boolean updateFigureForShape(ShapePart shapePart, ShapePart updateShapePart, HashMap<ShapePart, IFigure> partToFigure, DiagramResourceCache resourceCache)
	{
		if (shapePart.equals(updateShapePart)) {
			IFigure updateFigure = partToFigure.get(updateShapePart);
			if (updateShapePart.visible()) {
				// find the parent figure
				ISapphirePart pp = updateShapePart.getParentPart();
				if (pp instanceof ContainerShapePart) {
					ContainerShapePart containerShapePart = (ContainerShapePart)pp;
					IFigure containerFigure = partToFigure.get(containerShapePart);
					if (containerFigure != null) {
						
						int index = -1;
						if (updateFigure != null) {
							// first delete it
							index = findIndex(containerFigure, updateFigure);
							containerFigure.remove(updateFigure);
						}
						// add it
						updateFigure = createFigure(shapePart, resourceCache);
						partToFigure.put(updateShapePart, updateFigure);

						Object layoutConstraint = getLayoutConstraint(shapePart, containerShapePart.getLayout());
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
			} else if (!updateShapePart.visible() && updateFigure != null) {
				// remove it
				partToFigure.remove(updateShapePart);
				ISapphirePart pp = updateShapePart.getParentPart();
				if (pp instanceof ContainerShapePart) {
					ContainerShapePart containerShapePart = (ContainerShapePart)pp;
					IFigure containerFigure = partToFigure.get(containerShapePart);
					containerFigure.remove(updateFigure);
					containerFigure.revalidate();
				}
			}
			return true;
		}
		
		if (shapePart instanceof ContainerShapePart)
		{
			ContainerShapePart containerPart = (ContainerShapePart)shapePart;
			for (ShapePart childShapePart : containerPart.getChildren())
			{
				if (!childShapePart.isActive())
				{
					boolean updated = updateFigureForShape(childShapePart, updateShapePart, partToFigure, resourceCache);
					if (updated) {
						return true;
					}
				}
			}
		}
		return false;
	}

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
	
	private static Object getLayoutConstraint(ShapePart childShapePart, ShapeLayoutDef layoutDef)
	{
		Object layoutConstraint = null;
		if (layoutDef instanceof SequenceLayoutDef)
		{
			SequenceLayoutConstraintDef sequenceLayoutConstraint = (SequenceLayoutConstraintDef)childShapePart.getLayoutConstraint();
			GridData gd = new GridData();
			if (sequenceLayoutConstraint != null)
			{
				gd.horizontalAlignment = getSwtHorizontalAlignment(sequenceLayoutConstraint.getHorizontalAlignment().getContent());
				gd.verticalAlignment = getSwtVerticalAlignment(sequenceLayoutConstraint.getVerticalAlignment().getContent());
				gd.grabExcessHorizontalSpace = sequenceLayoutConstraint.isExpandCellHorizontally().getContent();
				gd.grabExcessVerticalSpace = sequenceLayoutConstraint.isExpandCellVertically().getContent();
				gd.horizontalIndent = sequenceLayoutConstraint.getHorizontalMargin().getContent();
			}							
			layoutConstraint = gd;
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
										stackLayoutConstraint.getHorizontalMargin().getContent(),
										stackLayoutConstraint.getVerticalMargin().getContent());
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
	

	private static int getSwtHorizontalAlignment(HorizontalAlignment horizontalAlign)
	{
		int swtAlign = SWT.CENTER;
		if (horizontalAlign == HorizontalAlignment.LEFT)
		{
			swtAlign = SWT.LEFT;
		}
		else if (horizontalAlign == HorizontalAlignment.RIGHT)
		{
			swtAlign = SWT.RIGHT;
		}
		return swtAlign;
	}

	private static int getSwtVerticalAlignment(VerticalAlignment verticalAlign)
	{
		int swtAlign = SWT.CENTER;
		if (verticalAlign == VerticalAlignment.TOP)
		{
			swtAlign = SWT.TOP;
		}
		else if (verticalAlign == VerticalAlignment.BOTTOM)
		{
			swtAlign = SWT.BOTTOM;
		}
		return swtAlign;
	}
}
