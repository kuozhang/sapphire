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

import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionModel extends DiagramModelBase {
	
	public final static String CONNECTION_UPDATES = "CONNECTION_UPDATES";
	public final static String CONNECTION_BEND_POINTS = "CONNECTION_BEND_POINTS";

	private DiagramConnectionPart part;
	
	private DiagramNodeModel sourceNode;
	private DiagramNodeModel targetNode;

	public DiagramConnectionModel(DiagramConnectionPart part) {
		this.part = part;
	}
	
	public SapphirePart getSapphirePart() {
		return getModelPart();
	}

	public DiagramConnectionPart getModelPart() {
		return part;
	}
	
	public DiagramNodeModel getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(DiagramNodeModel sourceNode) {
		this.sourceNode = sourceNode;
	}

	public DiagramNodeModel getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(DiagramNodeModel targetNode) {
		this.targetNode = targetNode;
	}

	public void handleUpdateConnection() {
		firePropertyChange(CONNECTION_UPDATES, null, getModelPart().getLabel());
	}
	
	public void handleUpdateBendPoints() {
		firePropertyChange(CONNECTION_BEND_POINTS, null, getModelPart());
	}
}
