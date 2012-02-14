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

package org.eclipse.sapphire.ui.gef.diagram.editor.model;

import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionLabelModel {
	
	private DiagramConnectionModel connectionModel;

	public DiagramConnectionLabelModel(DiagramConnectionModel connectionModel) {
		this.connectionModel = connectionModel;
	}
	
	public DiagramConnectionModel getConnectionModel() {
		return connectionModel;
	}

	public DiagramConnectionPart getModelPart() {
		return connectionModel.getModelPart();
	}
	
}
