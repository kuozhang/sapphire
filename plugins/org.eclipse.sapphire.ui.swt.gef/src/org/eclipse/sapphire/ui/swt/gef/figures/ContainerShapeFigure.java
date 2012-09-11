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
import org.eclipse.draw2d.Shape;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayoutConstraint;
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
		List children = getChildren();
		for (Object figureObj : children)
		{
			if (figureObj instanceof ContainerShapeFigure)
			{
				((ContainerShapeFigure)figureObj).refreshValidationStatus();
			}
		}
		
		if (this.validationMarkerIndex == -1)
		{
			return;
		}
		Status newStatus = this.model.validation();
		
		if (!newStatus.equals(this.validationStatus))
		{
			ValidationMarkerFigure markerFigure = getValidationMarkerFigure();
			if (this.validationStatus.severity() != Status.Severity.OK && markerFigure != null)
			{
				this.remove(markerFigure);
			}
			if (newStatus.severity() != Status.Severity.OK)
			{
				DiagramNodePart nodePart = this.containerShapePart.nearest(DiagramNodePart.class);
				ValidationMarkerPart markerPart = this.containerShapePart.getValidationMarkerPart();
				ValidationMarkerFigure newMarkerFigure = 
						FigureUtil.createValidationMarkerFigure(markerPart.getSize(), this.model, nodePart.getImageCache()) ;
				if (this.layout instanceof StackLayoutDef)
				{
					StackLayoutConstraintDef stackLayoutConstraint = (StackLayoutConstraintDef)markerPart.getLayoutConstraint();
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
					this.add(newMarkerFigure, constraint);
				}
				else
				{
					this.add(newMarkerFigure, this.validationMarkerIndex);
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
