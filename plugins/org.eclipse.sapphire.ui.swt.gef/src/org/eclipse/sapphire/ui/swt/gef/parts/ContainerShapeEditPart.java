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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.ContainerShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModelUtil;
import org.eclipse.sapphire.ui.swt.gef.policies.ContainerShapeLabelDirectEditPolicy;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeEditPart extends ShapeEditPart 
{	
	public ContainerShapeEditPart(DiagramConfigurationManager configManager) 
	{
    	super(configManager);    	
    }
	
	@Override
	protected void createEditPolicies() 
	{
		ContainerShapeModel model = getCastedModel();
		ContainerShapePart containerPart = (ContainerShapePart)model.getSapphirePart();
		// Create direct edit policy if it contains non-active sapphire text part. Active 
		// Sapphire text part has its own GEF editpart.
		if (containerPart.isEditable())
		{
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ContainerShapeLabelDirectEditPolicy());
		}
	}
	
	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		ContainerShapeModel containerModel = getCastedModel();
		returnedModelChildren.addAll(ShapeModelUtil.collectActiveChildrenRecursively(containerModel));
		return returnedModelChildren;
	}
	
	public ContainerShapeModel getCastedModel() 
	{
		return (ContainerShapeModel)getModel();
	}
	
	@Override
	public void performRequest(Request request) 
	{
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
		{
			if (!(request instanceof DirectEditRequest))
			{
				// Direct edit invoked using key command
				performDirectEdit();
			}
		}
		else if (request.getType().equals(REQ_OPEN))
		{
			SelectionRequest selRequest = (SelectionRequest)request;
			Point pt = selRequest.getLocation();
			TextPart textPart = getTextPart(pt);
			if (textPart != null)
			{
				performDirectEdit(textPart);
			}
		}
		else
		{
			super.performRequest(request);
		}
	}
		
	private void performDirectEdit() 
	{
		List<TextPart> textParts = getTextParts();
		if (!textParts.isEmpty())
		{
			performDirectEdit(textParts.get(0));
		}
	}
	
	private void performDirectEdit(TextPart textPart)
	{
		if (textPart.isEditable())
		{
			TextFigure textFigure = (TextFigure)getPartFigure(textPart);
			if (textFigure != null)
			{
				NodeDirectEditManager manager = 
						new NodeDirectEditManager(this, textPart, new NodeCellEditorLocator(getConfigurationManager(), textFigure), textFigure);
				manager.show();
			}
		}
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		String prop = evt.getPropertyName();
		if (prop.equals(ShapeModel.SHAPE_START_EDITING))
		{
			performDirectEdit();
		}
	}

	private List<TextPart> getTextParts()
	{
		List<TextPart> textParts = new ArrayList<TextPart>();
		ContainerShapePart containerPart = (ContainerShapePart)getCastedModel().getSapphirePart();
		textParts.addAll(containerPart.getTextParts());
		return textParts;		
	}
	
	private TextPart getTextPart(Point mouseLocation)
	{
		Point realLocation = this.getConfigurationManager().getDiagramEditor().calculateRealMouseLocation(mouseLocation);
		List<TextPart> textParts = getTextParts();
		for (TextPart textPart : textParts)
		{
			TextFigure textFigure = (TextFigure)getPartFigure(textPart);
			if (textFigure != null && textFigure.getBounds().contains(realLocation))
			{
				return textPart;
			}
		}
		return null;
	}
	
}
