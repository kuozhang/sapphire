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
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.LinePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SelectionPresentation;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapePresentation 
{
	private ShapePresentation parent;
	private ShapePart shapePart;
	private DiagramConfigurationManager configManager;
	private IFigure figure;
	
	public ShapePresentation(ShapePresentation parent, ShapePart shapePart, DiagramConfigurationManager configManager)
	{
		this.parent = parent;
		this.shapePart = shapePart;
		this.configManager = configManager;
	}
	
	public ShapePresentation getParent()
	{
		return this.parent;
	}
	
	public ShapePart getPart()
	{
		return this.shapePart;
	}
	
	public DiagramConfigurationManager getConfigurationManager()
	{
		return this.configManager;
	}
	
	public void setFigure(IFigure fig)
	{
		this.figure = fig;
	}
	
	public IFigure getFigure()
	{
		return this.figure;
	}
	
	public void removeFigure()
	{
		this.figure = null;
	}
	
	public IFigure getParentFigure()
	{
		IFigure parentFigure = null;
		ShapePresentation parentPresentation = getParent();
		while (parentPresentation != null)
		{
			parentFigure = parentPresentation.getFigure();
			if (parentFigure != null)
			{
				break;
			}
			parentPresentation = parentPresentation.getParent();
		}
		return parentFigure;
	}
	
	public IFigure getNodeFigure()
	{
		IFigure parentFigure = null;
		ShapePresentation parentPresentation = getParent();
		while (parentPresentation != null)
		{
			parentFigure = parentPresentation.getFigure();
			parentPresentation = parentPresentation.getParent();
		}
		return parentFigure;		
	}
	
	public LayoutConstraintDef getLayoutConstraint()
	{
		return this.shapePart.getLayoutConstraint();
	}
	
	public boolean visible()
	{
		return this.shapePart.visible();
	}
	
	public SelectionPresentation getSelectionPresentation()
	{
		return this.shapePart.getSelectionPresentation();
	}	
	
	public void dispose()
	{		
	}
	
    public static final class ShapePresentationFactory
    {
    	public static ShapePresentation createShapePresentation(ShapePresentation parent, ShapePart shapePart, DiagramConfigurationManager configManager)
    	{
    		ShapePresentation shapePresentation = null;
        	if (shapePart instanceof TextPart)
        	{
        		shapePresentation = new TextPresentation(parent, (TextPart)shapePart, configManager);
        	}
        	else if (shapePart instanceof ImagePart)
        	{
        		shapePresentation = new ImagePresentation(parent, (ImagePart)shapePart, configManager);
        	}
        	else if (shapePart instanceof ValidationMarkerPart)
        	{
        		shapePresentation = new ValidationMarkerPresentation(parent, (ValidationMarkerPart)shapePart, configManager);
        	}
        	else if (shapePart instanceof LinePart)
        	{
        		shapePresentation = new LineShapePresentation(parent, (LinePart)shapePart, configManager);
        	}
        	else if (shapePart instanceof RectanglePart)
        	{
        		shapePresentation = new RectanglePresentation(parent, (RectanglePart)shapePart, configManager);        		
        	}
        	else if (shapePart instanceof ShapeFactoryPart)
        	{
        		shapePresentation = new ShapeFactoryPresentation(parent, (ShapeFactoryPart)shapePart, configManager);        		
        	}
    		return shapePresentation;
    	}
    }
}
