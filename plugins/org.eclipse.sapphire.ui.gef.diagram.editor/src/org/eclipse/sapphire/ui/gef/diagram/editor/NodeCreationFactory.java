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

package org.eclipse.sapphire.ui.gef.diagram.editor;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class NodeCreationFactory implements CreationFactory {
	private DiagramNodeTemplate nodeTemplate;
	
	public NodeCreationFactory(DiagramNodeTemplate nodeTemplate) {
		this.nodeTemplate = nodeTemplate;
	}

	public Object getNewObject() {
		DiagramNodePart newPart = this.nodeTemplate.createNewDiagramNode();
		return newPart;
	}

	public Object getObjectType() {
		return DiagramNodeTemplate.class;
	}

}
