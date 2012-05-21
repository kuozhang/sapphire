/******************************************************************************
 * Copyright (c) 2012 Oracle
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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.commands.DoubleClickNodeCommand;
import org.eclipse.sapphire.ui.swt.gef.contextbuttons.ContextButtonManager;
import org.eclipse.sapphire.ui.swt.gef.figures.NodeFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.swt.gef.policies.DiagramNodeEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeLabelDirectEditPolicy;
import org.eclipse.sapphire.ui.swt.gef.policies.NodeLayoutEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeEditPart extends AbstractGraphicalEditPart 
		implements NodeEditPart, PropertyChangeListener, IConfigurationManagerHolder {

	private DiagramConfigurationManager configManager;
    private NodeDirectEditManager manager;
    
    private ConnectionAnchor sourceAnchor;
    private ConnectionAnchor targetAnchor;
    
    private List<IFigure> decorators = new ArrayList<IFigure>();

    public DiagramNodeEditPart(DiagramConfigurationManager configManager) {
    	this.configManager = configManager;
    }
    
    public DiagramConfigurationManager getConfigurationManager() {
    	return this.configManager;
    }
    
    @Override
	protected IFigure createFigure() {
    	ImageData imageData = getCastedModel().getModelPart().getImage();
		return new NodeFigure(imageData != null, getCastedModel().getDiagramModel().getResourceCache());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DiagramNodeEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NodeLabelDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeLayoutEditPolicy());
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

	private void performDirectEdit() {
		if (manager == null) {
			Label label = getNodeFigure().getLabelFigure();
			manager = new NodeDirectEditManager(this, new NodeCellEditorLocator(label), label);
		}
		manager.show();
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

	private boolean mouseInLabelRegion(Point pt)
	{
		Point realLocation = this.configManager.getDiagramEditor().calculateRealMouseLocation(pt);
		NodeFigure nodeFig = getNodeFigure();
		Rectangle bounds = nodeFig.getLabelFigure().getBounds();
		if (bounds.contains(realLocation))
		{
			return true;
		}
		return false;
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
	
	protected NodeFigure getNodeFigure() {
		return (NodeFigure)getFigure();
	}
	
	private void addDecorators(Bounds labelBounds, Bounds imageBounds) {
		NodeFigure nodeFigure = getNodeFigure();
		
		// first remove all decorators
		for (IFigure decorator : decorators) {
			nodeFigure.remove(decorator);
		}
		decorators.clear();

		NodeDecorator util = new NodeDecorator(getCastedModel(), labelBounds, imageBounds);
		decorators.addAll(util.decorate(getNodeFigure()));
	}

	@Override
	protected void refreshVisuals() {
		getNodeFigure().setText(getCastedModel().getLabel());
		getNodeFigure().setImage(getCastedModel().getImage());
		
		Bounds nb = getCastedModel().getNodeBounds();
		Bounds labelBounds = getCastedModel().getLabelBounds(nb);
		Bounds imageBounds = getCastedModel().getImageBounds(nb);
		
		getNodeFigure().refreshConstraints(labelBounds, imageBounds);
		
		addDecorators(labelBounds, imageBounds);
		
		Rectangle bounds = new Rectangle(nb.getX(), nb.getY(), nb.getWidth(), nb.getHeight());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,	getFigure(), bounds);
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
			refreshVisuals();
		} else if (DiagramNodeModel.NODE_UPDATES.equals(prop)) {
			refreshVisuals();
		} else if (DiagramNodeModel.NODE_START_EDITING.equals(prop)) {
			performDirectEdit();
		}
	}
	
}
