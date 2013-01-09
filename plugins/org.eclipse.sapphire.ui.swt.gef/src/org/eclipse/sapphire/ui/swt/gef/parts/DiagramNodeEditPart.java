/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - double click handling
 *    Shenxue Zhou - [Bug 348640] - Disable click-wait-click editing activation
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.commands.DoubleClickNodeCommand;
import org.eclipse.sapphire.ui.swt.gef.contextbuttons.ContextButtonManager;
import org.eclipse.sapphire.ui.swt.gef.figures.ContainerShapeFigure;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.ContainerShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModel;
import org.eclipse.sapphire.ui.swt.gef.model.ShapeModelUtil;
import org.eclipse.sapphire.ui.swt.gef.policies.DiagramNodeEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeLabelDirectEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeLayoutEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.presentation.ContainerShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.ShapePresentation;
import org.eclipse.sapphire.ui.swt.gef.presentation.TextPresentation;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeEditPart extends ShapeEditPart 
		implements NodeEditPart {

    public static final String DIRECT_EDIT_REQUEST_PARAM = "TEXTPART";
    private ConnectionAnchor sourceAnchor;
    private ConnectionAnchor targetAnchor;
    
    public DiagramNodeEditPart(DiagramConfigurationManager configManager) {
    	super(configManager);
    }
    
    @Override
	protected IFigure createFigure() {
    	ShapePresentation shapePresentation = getCastedModel().getShapePresentation();
    	return ShapeUtil.createFigureForShape(shapePresentation, getCastedModel().getDiagramModel().getResourceCache());
    	
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DiagramNodeEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NodeLabelDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeLayoutEditPolicy((DiagramNodeModel)getModel()));
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getCastedModel().addPropertyChangeListener(this);
			ContextButtonManager contextButtonManager = getConfigurationManager().getDiagramEditor().getContextButtonManager();
			contextButtonManager.register(this);			
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			ContextButtonManager contextButtonManager = getConfigurationManager().getDiagramEditor().getContextButtonManager();
			contextButtonManager.deRegister(this);
			getCastedModel().removePropertyChangeListener(this);			
			super.deactivate();
		}
	}
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) 
	{
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		if (child == null)
			return;
		
		ShapeModel shapeModel = (ShapeModel)childEditPart.getModel();
		ShapePresentation shapePresentation = shapeModel.getShapePresentation();
		ContainerShapePresentation parentPresentation = getParentContainer(shapePresentation);
		IFigure parentFigure = parentPresentation.getFigure();
		Object layoutConstraint = ShapeUtil.getLayoutConstraint(shapePresentation, 
				parentPresentation.getLayout());
		parentFigure.add(child, layoutConstraint, index);
	}
	
	@Override
	protected void removeChildVisual(EditPart childEditPart) 
	{
		IFigure child = ((GraphicalEditPart) childEditPart).getFigure();
		if (child == null)
			return;
		
		ShapeModel shapeModel = (ShapeModel)childEditPart.getModel();
		ContainerShapePresentation parentPresentation = getParentContainer(shapeModel.getShapePresentation());
		IFigure parentFigure = parentPresentation.getFigure();
		parentFigure.remove(child);		
	}
	
	@Override
	protected List<ShapeModel> getModelChildren() 
	{
		List<ShapeModel> returnedModelChildren = new ArrayList<ShapeModel>();
		DiagramNodeModel nodeModel = (DiagramNodeModel)getModel();
		ShapeModel shapeModel = nodeModel.getShapeModel();
		if (shapeModel instanceof ContainerShapeModel)
		{
			ContainerShapeModel containerModel = (ContainerShapeModel)shapeModel;
			returnedModelChildren.addAll(ShapeModelUtil.collectActiveChildrenRecursively(containerModel));
		}
		
		return returnedModelChildren;
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
			else
			{
				Command cmd = new DoubleClickNodeCommand(this, getCastedModel().getModelPart());
				// If executing the command from edit domain's command stack, we'd get an 
				// invalid cursor before the double click cmd is executed.
				// Bypassing the command stack
				//this.getViewer().getEditDomain().getCommandStack().execute(cmd);
				if (cmd.canExecute())
				{
					cmd.execute();
				}
			}
		}
		else
		{
			super.performRequest(request);
		}
	}

	@Override
	protected List<DiagramConnectionModel> getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	@Override
	protected List<DiagramConnectionModel> getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}
	
	public DiagramNodeModel getCastedModel() {
		return (DiagramNodeModel)getModel();
	}
	

	@Override
	protected void refreshVisuals() 
	{
		refreshNodeBounds();
	}
	
	private void refreshNodeBounds()
	{
		Bounds nb = getCastedModel().getNodeBounds();
		
		Dimension minSize = getFigure().getMinimumSize();
		int width = nb.getWidth() != -1 ? Math.max(minSize.width, nb.getWidth()) : -1;
		int height = nb.getHeight() != -1 ? Math.max(minSize.height, nb.getHeight()) : -1;
		Rectangle bounds = new Rectangle(nb.getX(), nb.getY(), width, height);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,	getFigure(), bounds);		
	}
	
	// Called when node is updated which could validation change
	private void refreshNodeValidation(Object obj)
	{
		IFigure figure = null;
		if (obj == null)
		{
			figure = getFigure();
		}
		else if (obj instanceof ShapePart)
		{
			figure = getPartFigure((ShapePart)obj);
		}
		if (figure instanceof ContainerShapeFigure)
		{
			ContainerShapeFigure containerShapeFigure = (ContainerShapeFigure)figure;
			containerShapeFigure.refreshValidationStatus();
		}
	}
	
	private void updateShape(ShapePart shapePart) 
	{
		ShapePresentation nodePresentation = getCastedModel().getShapePresentation();
		ShapePresentation shapePresentation = ShapeModelUtil.getChildShapePresentation(nodePresentation, shapePart);
		if (shapePresentation instanceof TextPresentation)
		{
			TextFigure textFigure = (TextFigure)shapePresentation.getFigure();
			textFigure.setText(((TextPresentation)shapePresentation).getContent());
		}
		else
		{
			ShapeUtil.updateFigureForShape(shapePresentation, getCastedModel().getDiagramModel().getResourceCache());
		}
	}
	
	private void updateShapeVisibility(ShapePart shapePart) 
	{
		ShapePresentation nodePresentation = getCastedModel().getShapePresentation();
		ShapePresentation shapePresentation = ShapeModelUtil.getChildShapePresentation(nodePresentation, shapePart);		
		boolean updated = ShapeUtil.updateFigureForShape(shapePresentation, getCastedModel().getDiagramModel().getResourceCache());
		if (updated && (shapePart instanceof TextPart))
		{
			// The label figure has been recreated; we need to throw away the direct edit cache.
			//this.manager = null;
		}
		
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		if (targetAnchor == null) {
			targetAnchor = new ChopboxAnchor(getFigure());
		}
		return targetAnchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// when moving or creating connections, the line should always end
		// directly at the mouse-pointer.
		return null;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (DiagramNodeModel.SOURCE_CONNECTIONS.equals(prop)) {
			refreshSourceConnections();
		} else if (DiagramNodeModel.TARGET_CONNECTIONS.equals(prop)) {
			refreshTargetConnections();
		} else if (DiagramNodeModel.NODE_BOUNDS.equals(prop)) {
			refreshNodeBounds();
		} else if (DiagramNodeModel.NODE_VALIDATION.equals(prop)) {
			refreshNodeValidation(evt.getNewValue());
		} else if (DiagramNodeModel.SHAPE_UPDATES.equals(prop)) {
			Object obj = evt.getNewValue();
			if (obj instanceof ShapePart) {
				updateShape((ShapePart)obj);
			}
		} else if (DiagramNodeModel.SHAPE_VISIBILITY_UPDATES.equals(prop)) {
			Object obj = evt.getNewValue();
			if (obj instanceof ShapePart) {
				updateShapeVisibility((ShapePart)obj);
			}		
		} else if (DiagramNodeModel.NODE_START_EDITING.equals(prop)) {
			performDirectEdit();
		}
	}
	
	public TextPart getTextPart(Point mouseLocation)
	{
		Point realLocation = this.getConfigurationManager().getDiagramEditor().calculateRealMouseLocation(mouseLocation);
		DiagramNodePart nodePart = getCastedModel().getModelPart();
		ShapePart shapePart = nodePart.getShapePart();
		if (shapePart instanceof TextPart)
		{
			TextFigure textFigure = (TextFigure)getPartFigure(shapePart);
			if (textFigure != null && textFigure.getTextBounds().contains(realLocation))
			{
				return (TextPart)shapePart;
			}
		}
		else if (shapePart instanceof ContainerShapePart)
		{
			List<TextPart> textParts = ((ContainerShapePart)shapePart).getTextParts();
			for (TextPart textPart : textParts)
			{
				TextFigure textFigure = (TextFigure)getPartFigure(textPart);
				if (textFigure != null && textFigure.getTextBounds().contains(realLocation))
				{
					return textPart;
				}
			}
		}
		return null;
	}
	
	private List<TextPart> getTextParts()
	{
		DiagramNodePart nodePart = getCastedModel().getModelPart();
		ShapePart shapePart = nodePart.getShapePart();
		List<TextPart> textParts = new ArrayList<TextPart>();
		if (shapePart instanceof TextPart)
		{
			textParts.add((TextPart)shapePart);
		}
		else if (shapePart instanceof ContainerShapePart)
		{
			textParts.addAll(((ContainerShapePart)shapePart).getTextParts());
		}
		return textParts;
	}
		
}
