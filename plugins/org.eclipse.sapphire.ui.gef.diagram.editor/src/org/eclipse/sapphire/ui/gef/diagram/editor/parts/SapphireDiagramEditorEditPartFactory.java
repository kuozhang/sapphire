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

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.sapphire.ui.gef.diagram.editor.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionLabelModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorEditPartFactory implements EditPartFactory {

	private DiagramConfigurationManager configManager;
	
	public SapphireDiagramEditorEditPartFactory(DiagramConfigurationManager configManager) {
		this.configManager = configManager;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object modelElement) {
		// get EditPart for model element
		EditPart part = getPartForElement(modelElement);
		// store model element in EditPart
		part.setModel(modelElement);
		return part;
	}

	/**
	 * Maps an object to an EditPart.
	 * 
	 * @throws RuntimeException
	 *             if no match was found (programming error)
	 */
	private EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof DiagramModel) {
			return new SapphireDiagramEditorPageEditPart(this.configManager);
		}
		if (modelElement instanceof DiagramNodeModel) {
			return new DiagramNodeEditPart(this.configManager);
		}
		if (modelElement instanceof DiagramConnectionModel) {
			return new DiagramConnectionEditPart(this.configManager);
		}
		if (modelElement instanceof DiagramConnectionLabelModel) {
			return new DiagramConnectionLabelEditPart(this.configManager);
		}
		throw new RuntimeException("Can't create part for model element: "
				+ ((modelElement != null) ? modelElement.getClass().getName()
						: "null"));
	}

}