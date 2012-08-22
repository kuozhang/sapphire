/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeModel extends DiagramModelBase {
	
    public static final int DEFAULT_NODE_WIDTH = 100;
    public static final int DEFAULT_NODE_HEIGHT = 30;
    private static final int DEFAULT_TEXT_HEIGHT = 16;
    
    public final static String SOURCE_CONNECTIONS = "SOURCE_CONNECTIONS";
	public final static String TARGET_CONNECTIONS = "TARGET_CONNECTIONS";
	public final static String NODE_BOUNDS = "NODE_BOUNDS";
	public final static String NODE_UPDATES = "NODE_UPDATES";
	public final static String NODE_START_EDITING = "NODE_START_EDITING";
	
	private DiagramModel parent;
    private DiagramNodePart part;
	private List<DiagramConnectionModel> sourceConnections = new ArrayList<DiagramConnectionModel>();
	private List<DiagramConnectionModel> targetConnections = new ArrayList<DiagramConnectionModel>();
	private ShapeModel shapeModel;
	
	public DiagramNodeModel(DiagramModel parent, DiagramNodePart part) 
	{
		this.parent = parent;
		this.part = part;
		ShapePart shapePart = this.part.getShapePart();
		if (shapePart instanceof TextPart)
		{
			this.shapeModel = new TextModel(this, null, (TextPart)shapePart);
		}
		else if (shapePart instanceof ImagePart)
		{
			this.shapeModel = new ImageModel(this, null, (ImagePart)shapePart);
		}
		else if (shapePart instanceof ValidationMarkerPart)
		{
			this.shapeModel = new ValidationMarkerModel(this, null, (ValidationMarkerPart)shapePart);
		}
		else if (shapePart instanceof RectanglePart)
		{
			this.shapeModel = new RectangleModel(this, null, (RectanglePart)shapePart);
		}
	}
	
	public DiagramModel getDiagramModel() {
		return parent;
	}

	public SapphirePart getSapphirePart() {
		return getModelPart();
	}

	public DiagramNodePart getModelPart() {
		return part;
	}
	
	public ShapeModel getShapeModel()
	{
		return this.shapeModel;
	}
	
	public String getLabel() 
	{
		ShapePart shapePart = getModelPart().getShapePart();
		if (shapePart instanceof ContainerShapePart)
		{
			ContainerShapePart containerShapePart = (ContainerShapePart)shapePart;
			TextPart textPart = containerShapePart.getTextPart();
			if (textPart != null)
			{
				return textPart.getText();
			}
		}
		return null;
	}
		
	public void setLabel(String newValue)
	{
		ShapePart shapePart = getModelPart().getShapePart();
		if (shapePart instanceof ContainerShapePart)
		{
			ContainerShapePart containerShapePart = (ContainerShapePart)shapePart;
			TextPart textPart = containerShapePart.getTextPart();
			if (textPart != null)
			{
				textPart.setText(newValue);
			}
		}		
	}
	
	public Bounds getNodeBounds() {
		Bounds bounds = getModelPart().getNodeBounds();
		return bounds;
	}
	

	public void handleMoveNode() {
		firePropertyChange(NODE_BOUNDS, null, getModelPart().getNodeBounds());
	}
	
	public void handleUpdateNode() {
		firePropertyChange(NODE_UPDATES, null, getModelPart().getNodeBounds());
	}

	public List<DiagramConnectionModel> getSourceConnections() {
		return sourceConnections;
	}

	public List<DiagramConnectionModel> getTargetConnections() {
		return targetConnections;
	}

	public void addSourceConnection(DiagramConnectionModel connection) {
		sourceConnections.add(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void addTargetConnection(DiagramConnectionModel connection) {
		targetConnections.add(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

	public void removeSourceConnection(DiagramConnectionModel connection) {
		sourceConnections.remove(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void removeTargetConnection(DiagramConnectionModel connection) {
		targetConnections.remove(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

	public void handleStartEditing() {
		firePropertyChange(NODE_START_EDITING, null, null);
	}
	
	@Override
	public String toString() {
		return getLabel();
	}

}
