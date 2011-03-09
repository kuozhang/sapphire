/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.def.Alignment;
import org.eclipse.sapphire.ui.diagram.def.DecoratorPlacement;
import org.eclipse.sapphire.ui.diagram.def.IDiagramDecoratorDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDoubleClickNodeFeature;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider 
{
	private static final int ERROR_DECORATOR_WIDTH = 7;
	private static final int ERROR_DECORATOR_HEIGHT = 8;
	
	public SapphireDiagramToolBehaviorProvider(IDiagramTypeProvider dtp) 
	{
		super(dtp);
	}

	/**
	 * Override super class impl to not include "Remove"
	 */
	@Override
	protected void setGenericContextButtons(IContextButtonPadData data, PictogramElement pe, int identifiers) 
	{
		identifiers -= CONTEXT_BUTTON_REMOVE;
		super.setGenericContextButtons(data, pe, identifiers);
	}
	
	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) 
	{
		PictogramElement[] pes = context.getPictogramElements();
		for (PictogramElement pe : pes)
		{
			if (pe instanceof ContainerShape)
			{
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
				if (bo instanceof DiagramNodePart)
				{
					DiagramNodePart nodePart = (DiagramNodePart)bo;
					if (nodePart.getDefaultActionPart() != null)
					{
						SapphireDoubleClickNodeFeature dblClikFeature = 
							new SapphireDoubleClickNodeFeature(getFeatureProvider(), nodePart.getDefaultActionPart());
						return dblClikFeature;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public IDecorator[] getDecorators(PictogramElement pe) 
	{
		IFeatureProvider featureProvider = getFeatureProvider();
		Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof DiagramNodePart)
		{
			DiagramNodePart nodePart = (DiagramNodePart)bo;
			if (nodePart.showErrorIndicator())
			{
				IModelElement model = nodePart.getModelElement();
				IStatus status = model.validate();
				ImageDecorator imageRenderingDecorator = null;
				if (status.getSeverity() != IStatus.OK)
				{
					if (status.getSeverity() == IStatus.WARNING)
					{
						imageRenderingDecorator = new ImageDecorator(ErrorIndicatorImageProvider.IMG_WARNING_DECORATOR);
					}
					else if (status.getSeverity() == IStatus.ERROR)
					{
						imageRenderingDecorator = new ImageDecorator(ErrorIndicatorImageProvider.IMG_ERROR_DECORATOR);
					}
				}
				if (imageRenderingDecorator != null)
				{
					Point pt = getErrorIndicatorPosition(nodePart, pe);
					imageRenderingDecorator.setX(pt.getX());
					imageRenderingDecorator.setY(pt.getY());
					imageRenderingDecorator.setMessage(status.getMessage());
					return new IDecorator[] { imageRenderingDecorator };
				}
			}
		}
		return super.getDecorators(pe);
	}
	
	private Point getErrorIndicatorPosition(DiagramNodePart nodePart, PictogramElement pe)
	{
		IDiagramDecoratorDef decoratorDef = nodePart.getErrorIndicatorDef();
		if (decoratorDef != null)
		{
			DecoratorPlacement decoratorPlace = decoratorDef.getDecoratorPlacement().getContent();
			if (decoratorPlace == DecoratorPlacement.IMAGE)
			{
				ContainerShape containerShape = (ContainerShape)pe;
				EList<Shape> children = containerShape.getChildren();
				for (Shape child : children)
				{
					GraphicsAlgorithm ga = child.getGraphicsAlgorithm();
					if (ga instanceof Image)
					{
						Image image = (Image)ga;
						Alignment horizontalAlign = decoratorDef.getHorizontalAlign().getContent();						
						int offsetX = 0;
						if (horizontalAlign == Alignment.RIGHT)
						{
							offsetX = image.getWidth() - ERROR_DECORATOR_WIDTH;
							if (decoratorDef.getRightMargin().getContent() != null)
							{
								offsetX -= decoratorDef.getRightMargin().getContent();
							}
						}
						else if (horizontalAlign == Alignment.LEFT)
						{
							if (decoratorDef.getLeftMargin().getContent() != null)
							{
								offsetX += decoratorDef.getLeftMargin().getContent();
							}
						}
						else if (horizontalAlign == Alignment.CENTER)
						{
							offsetX = (image.getWidth() - ERROR_DECORATOR_WIDTH) >> 1;
						}
						
						Alignment verticalAlign = decoratorDef.getVerticalAlign().getContent();
						int offsetY = 0;
						if (verticalAlign == Alignment.BOTTOM)
						{
							offsetY = image.getHeight() - ERROR_DECORATOR_HEIGHT;
							if (decoratorDef.getBottomMargin().getContent() != null)
							{
								offsetY -= decoratorDef.getBottomMargin().getContent();
							}
						}
						else if (verticalAlign == Alignment.TOP)
						{
							if (decoratorDef.getTopMargin().getContent() != null)
							{
								offsetY += decoratorDef.getTopMargin().getContent();
							}							
						}
						else if (verticalAlign == Alignment.CENTER)
						{
							offsetY = (image.getHeight() - ERROR_DECORATOR_HEIGHT) / 2;
						}
						
						return new Point(offsetX, offsetY);
					}
				}
			}
		}
		return new Point(0, 0);
	}
}
