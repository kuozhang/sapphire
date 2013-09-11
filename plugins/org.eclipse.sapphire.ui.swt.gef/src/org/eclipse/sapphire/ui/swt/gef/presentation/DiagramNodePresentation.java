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

import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation.ShapePresentationFactory;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodePresentation extends DiagramPresentation 
{
	private DiagramResourceCache resourceCache;
	private ShapePresentation shapePresentation;
	
	public DiagramNodePresentation(final DiagramNodePart nodePart, final DiagramPresentation parent, 
			final Shell shell, final DiagramConfigurationManager configManager, final DiagramResourceCache resourceCache)
	{
		super(nodePart, parent, configManager, shell);
		this.resourceCache = resourceCache;
		this.shapePresentation = ShapePresentationFactory.createShapePresentation(this, 
				nodePart.getShapePart(), this.resourceCache);
	}
	
	public ShapePresentation getShapePresentation()
	{
		return this.shapePresentation;
	}
	
	@Override
	public DiagramNodePart part()
	{
		return (DiagramNodePart)super.part();
	}
	
	@Override
    public void render()
    {
		this.shapePresentation.render();
		setFigure(this.shapePresentation.getFigure());
    }	
}
