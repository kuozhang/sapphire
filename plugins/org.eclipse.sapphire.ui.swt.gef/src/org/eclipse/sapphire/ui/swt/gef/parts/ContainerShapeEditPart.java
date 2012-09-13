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

package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
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
import org.eclipse.sapphire.ui.swt.gef.policies.ContainerShapeLabelDirectEditPolicy;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapeEditPart extends ShapeEditPart 
{
	private NodeDirectEditManager manager;
	
	public ContainerShapeEditPart(DiagramConfigurationManager configManager) 
	{
    	super(configManager);    	
    }
	
	@Override
	protected void createEditPolicies() 
	{
		ContainerShapeModel model = getCastedModel();
		ContainerShapePart containerPart = (ContainerShapePart)model.getSapphirePart();
		if (containerPart.getTextPart() != null)
		{
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ContainerShapeLabelDirectEditPolicy());
		}
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
			if (mouseInLabelRegion(pt))
			{
				performDirectEdit();
			}
		}
		else
		{
			super.performRequest(request);
		}
	}
	
	private void performDirectEdit() 
	{
		if (manager == null) 
		{
			TextFigure textFigure = getLabelFigure();
			if (textFigure != null)
			{
				manager = new NodeDirectEditManager(this, new NodeCellEditorLocator(getConfigurationManager(), textFigure), textFigure);
			}
		}
		if (manager != null)
			manager.show();
	}
	
	private TextFigure getLabelFigure()
	{
		ContainerShapePart containerPart = (ContainerShapePart)getCastedModel().getSapphirePart();
		TextPart textPart = containerPart.getTextPart();
		DiagramNodeEditPart nodeEditPart = this.getNodeEditPart();
		TextFigure textFigure = (TextFigure)nodeEditPart.getPartFigureMap().get(textPart);
		return textFigure;		
	}
	
	private boolean mouseInLabelRegion(Point pt)
	{
		Point realLocation = this.getConfigurationManager().getDiagramEditor().calculateRealMouseLocation(pt);
		TextFigure label = getLabelFigure();
		if (label != null)
		{
			Rectangle bounds = label.getTextBounds();
			if (bounds.contains(realLocation))
			{
				return true;
			}
		}
		return false;
	}
	
}
