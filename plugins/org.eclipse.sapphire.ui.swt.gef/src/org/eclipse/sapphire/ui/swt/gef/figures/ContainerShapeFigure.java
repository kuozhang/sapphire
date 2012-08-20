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

package org.eclipse.sapphire.ui.swt.gef.figures;

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Shape;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeFigure extends Shape 
{
	private ContainerShapePart containerShapePart;
	private DiagramResourceCache resourceCache;
	private IModelElement model;
	private int validationMarkerIndex;
	private Status validationStatus;
	private ShapeLayoutDef layout;
	
	public ContainerShapeFigure(ContainerShapePart containerShapePart, DiagramResourceCache resourceCache)
	{
		this.containerShapePart = containerShapePart;
		this.resourceCache = resourceCache;
		this.validationMarkerIndex = this.containerShapePart.getValidationMarkerIndex();
		this.model = this.containerShapePart.getLocalModelElement();
		this.validationStatus = this.model.validation();
		this.layout = this.containerShapePart.getLayout();
	}
	
	@Override
	protected void fillShape(Graphics graphics) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void outlineShape(Graphics graphics) 
	{
		// TODO Auto-generated method stub

	}

	public boolean showValidationMarker()
	{
		boolean show = false;
		if (this.validationMarkerIndex != -1)
		{
			Status status = this.model.validation();		
			show =  status.severity() != Status.Severity.OK;
		}
		return show;
	}
	
	public boolean isHorizontalSequenceLayout()
	{		
		if (this.layout instanceof SequenceLayoutDef)
		{
			SequenceLayoutDef sequenceLayout = (SequenceLayoutDef)layout;				
			if (sequenceLayout.getOrientation().getContent() == Orientation.HORIZONTAL)	
			{
				return true;
			}
		}
		return false;
		
	}
	
	public void refreshValidationStatus()
	{
		if (this.validationMarkerIndex == -1)
		{
			return;
		}
		Status newStatus = this.model.validation();
		
		GridLayout gridLayout = null;
		boolean isHorizontalSequenceLayout = isHorizontalSequenceLayout(); 
		if (isHorizontalSequenceLayout)
		{
			gridLayout = (GridLayout)this.getLayoutManager();
		}
		if (!newStatus.equals(this.validationStatus))
		{
			ValidationMarkerFigure markerFigure = getValidationMarkerFigure();
			if (this.validationStatus.severity() != Status.Severity.OK && markerFigure != null)
			{
				this.remove(markerFigure);
				if (isHorizontalSequenceLayout)
				{
					gridLayout.numColumns--;
				}
			}
			if (newStatus.severity() != Status.Severity.OK)
			{
				DiagramNodePart nodePart = this.containerShapePart.nearest(DiagramNodePart.class);
				ValidationMarkerPart markerPart = this.containerShapePart.getValidationMarkerPart();
				ValidationMarkerFigure newMarkerFigure = 
						FigureUtil.createValidationMarkerFigure(markerPart.getSize(), this.model, nodePart.getImageCache()) ;
				this.add(newMarkerFigure, this.validationMarkerIndex);
				if (isHorizontalSequenceLayout)
				{
					gridLayout.numColumns++;
				}
			}
			this.validationStatus = newStatus;
			this.layout();
		}
	}
	
	public ValidationMarkerFigure getValidationMarkerFigure()
	{
		List children = this.getChildren();
		ValidationMarkerFigure markerFigure = null;
		for (Object figureObj : children)
		{
			if (figureObj instanceof ValidationMarkerFigure)
			{
				markerFigure = (ValidationMarkerFigure)figureObj;
				break;
			}
		}
		return markerFigure;
	}
	
	public TextFigure getTextFigure()
	{
		List children = this.getChildren();
		TextFigure textFigure = null;
		for (Object figureObj : children)
		{
			if (figureObj instanceof TextFigure)
			{
				textFigure = (TextFigure)figureObj;
				break;
			}
		}
		return textFigure;
		
	}
	
}
