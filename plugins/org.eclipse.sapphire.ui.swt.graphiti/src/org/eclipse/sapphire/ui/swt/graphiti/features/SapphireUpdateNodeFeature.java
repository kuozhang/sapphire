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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramPropertyKeys;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireUpdateNodeFeature extends AbstractUpdateFeature 
{
	private static final String LABEL_REASON = "Label is out of date";	//$NON-NLS-1$
	private static final String IMAGE_REASON = "Icon is out of date";	//$NON-NLS-1$
	
	public SapphireUpdateNodeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	public boolean canUpdate(IUpdateContext context) 
	{
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return bo instanceof DiagramNodePart;
	}

	public IReason updateNeeded(IUpdateContext context) 
	{
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		ContainerShape cs = null;
		if (pictogramElement instanceof ContainerShape) 
		{
			cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) 
			{
				if (shape.getGraphicsAlgorithm() instanceof Text) 
				{
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
					break;
				}
			}
		}
		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		DiagramNodePart nodePart = (DiagramNodePart) bo;
		businessName = nodePart.getLabel();

		// update needed, if names are different
		boolean updateNameNeeded = 
			((pictogramName == null && businessName != null) || 
					(pictogramName != null && !pictogramName.equals(businessName)));
		if (updateNameNeeded) 
		{
			return Reason.createTrueReason(LABEL_REASON);
		} 
		else 
		{
			String newImageId = nodePart.getImageId();
			String oldImageId = Graphiti.getPeService().getPropertyValue(cs, SapphireDiagramPropertyKeys.NODE_IMAGE_ID);
			if (newImageId != null && oldImageId != null && !newImageId.equals(oldImageId))
			{
				return Reason.createTrueReason(IMAGE_REASON);
			}
			return Reason.createFalseReason();
		}		
	}

	public boolean update(IUpdateContext context) 
	{
		// retrieve name from business model
		String businessName = null;
		String newImageId = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof DiagramNodePart) 
		{
			DiagramNodePart nodePart = (DiagramNodePart) bo;
			businessName = nodePart.getLabel();
			newImageId = nodePart.getImageId();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) 
		{
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) 
			{
				if (shape.getGraphicsAlgorithm() instanceof Text) 
				{
					Text text = (Text) shape.getGraphicsAlgorithm();
					text.setValue(businessName);
					return true;
				}
				else if (shape.getGraphicsAlgorithm() instanceof Image) 
				{
					Image oldImage = (Image)shape.getGraphicsAlgorithm();
					if (!oldImage.getId().equals(newImageId))
					{
						int x = oldImage.getX();
						int y = oldImage.getY();
						int width = oldImage.getWidth();
						int height = oldImage.getHeight();
						
						Image image = Graphiti.getGaService().createImage(shape, newImageId);
						Graphiti.getGaService().setLocationAndSize(image, x, y, width, height);
			    		Graphiti.getPeService().setPropertyValue(cs, 
			    				SapphireDiagramPropertyKeys.NODE_IMAGE_ID, newImageId);
	
						return true;
					}
				}
				
			}
		}

		return false;
	}

}
