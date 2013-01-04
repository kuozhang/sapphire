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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarkerSize;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.DecoratorImageFigure;
import org.eclipse.sapphire.ui.swt.gef.model.ValidationMarkerModel;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ValidationMarkerEditPart extends ShapeEditPart 
{
    private static final ImageDescriptor IMG_ERROR_SMALL
			= SwtRendererUtil.createImageDescriptor( ValidationMarkerEditPart.class, "error_small.png" );
    private static final ImageDescriptor IMG_ERROR
			= SwtRendererUtil.createImageDescriptor( ValidationMarkerEditPart.class, "error.gif" );
    private static final ImageDescriptor IMG_WARNING_SMALL
			= SwtRendererUtil.createImageDescriptor( ValidationMarkerEditPart.class, "warning_small.png" );
    private static final ImageDescriptor IMG_WARNING
			= SwtRendererUtil.createImageDescriptor( ValidationMarkerEditPart.class, "warning.gif" );
	
	public ValidationMarkerEditPart(DiagramConfigurationManager configManager)
	{
		super(configManager);
	}

	@Override
	protected IFigure createFigure() 
	{
		ValidationMarkerModel markerModel = (ValidationMarkerModel)this.getModel();
		ValidationMarkerPart markerPart = (ValidationMarkerPart)markerModel.getSapphirePart();
		DiagramNodePart nodePart = markerPart.nearest(DiagramNodePart.class);
		ValidationMarkerSize size = markerPart.getSize();
		Image image = null;
				
		IModelElement model = nodePart.getModelElement();		
		Status status = model.validation();
		if (status.severity() != Status.Severity.OK) 
		{
			if (status.severity() == Status.Severity.WARNING) 
			{
				if (size == ValidationMarkerSize.SMALL) 
				{
					image = nodePart.getImageCache().getImage(IMG_WARNING_SMALL);
				} 
				else 
				{
					image = nodePart.getImageCache().getImage(IMG_WARNING);					
				}
			} 
			else if (status.severity() == Status.Severity.ERROR) 
			{
				if (size == ValidationMarkerSize.SMALL) 
				{
					image = nodePart.getImageCache().getImage(IMG_ERROR_SMALL);
				} 
				else 
				{
					image = nodePart.getImageCache().getImage(IMG_ERROR);
				}
			}
		}
		if (image != null)
		{
			DecoratorImageFigure markerFigure = new DecoratorImageFigure(image);
			markerFigure.setToolTip(new Label(status.message()));
			return markerFigure;
		}
		return null;
	}

}
