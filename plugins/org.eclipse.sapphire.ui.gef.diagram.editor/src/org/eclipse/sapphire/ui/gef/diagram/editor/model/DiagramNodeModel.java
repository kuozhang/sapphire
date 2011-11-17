/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeModel extends DiagramModelBase {
	
    public static final int DEFAULT_NODE_WIDTH = 100;
    public static final int DEFAULT_NODE_HEIGHT = 30;
    
    public final static String SOURCE_CONNECTIONS = "SOURCE_CONNECTIONS";
	public final static String TARGET_CONNECTIONS = "TARGET_CONNECTIONS";
	public final static String NODE_BOUNDS = "NODE_BOUNDS";
	public final static String NODE_UPDATES = "NODE_UPDATES";
	
    private DiagramNodePart part;
	private List<DiagramConnectionModel> sourceConnection = new ArrayList<DiagramConnectionModel>();
	private List<DiagramConnectionModel> targetConnection = new ArrayList<DiagramConnectionModel>();

	public DiagramNodeModel(DiagramNodePart part) {
		this.part = part;
	}

	public SapphirePart getSapphirePart() {
		return getModelPart();
	}

	public DiagramNodePart getModelPart() {
		return part;
	}
	
	public String getLabel() {
		return getModelPart().getLabel();
	}
	
	public Bounds getNodeBounds() {
		Bounds bounds = getModelPart().getNodeBounds();
		if (bounds.getWidth() < 0) {
			bounds.setWidth(DEFAULT_NODE_WIDTH);
		}
		if (bounds.getHeight() < 0) {
			bounds.setHeight(DEFAULT_NODE_HEIGHT);
		}
		return bounds;
	}
	
	public void handleMoveNode() {
		firePropertyChange(NODE_BOUNDS, null, getModelPart().getNodeBounds());
	}
	
	public void handleUpdateNode() {
		firePropertyChange(NODE_UPDATES, null, getModelPart().getNodeBounds());
	}

	public List<DiagramConnectionModel> getSourceConnections() {
		return sourceConnection;
	}

	public List<DiagramConnectionModel> getTargetConnections() {
		return targetConnection;
	}

	public void addSourceConnection(DiagramConnectionModel connection) {
		sourceConnection.add(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void addTargetConnection(DiagramConnectionModel connection) {
		targetConnection.add(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

	public void removeSourceConnection(DiagramConnectionModel connection) {
		sourceConnection.remove(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void removeTargetConnection(DiagramConnectionModel connection) {
		targetConnection.remove(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

}
