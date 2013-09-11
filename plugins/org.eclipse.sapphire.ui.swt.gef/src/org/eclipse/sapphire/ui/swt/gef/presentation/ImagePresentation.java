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

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.draw2d.IFigure;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.swt.gef.figures.SapphireImageFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.SmoothImageFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ImagePresentation extends ShapePresentation 
{
	public ImagePresentation(DiagramPresentation parent, ImagePart imagePart, DiagramResourceCache resourceCache)
	{
		super(parent, imagePart, resourceCache);
	}

	@Override
	public ImagePart part()
	{
		return (ImagePart) super.part();
	}
	
	public ImageData getImage()
	{
		return part().getImage();
	}
	
	@Override
    public void render()
    {
		IFigure figure = null;
		if (visible()) 
		{
			DiagramNodePart nodePart = part().nearest(DiagramNodePart.class);
			final ImageData data = getImage();
			if (data != null) 
			{
				figure = new SapphireImageFigure(this, nodePart.getSwtResourceCache().image(data));
			}
			else 
			{
				figure = new SmoothImageFigure();
			}
		}
		setFigure(figure);
    }
	
}
