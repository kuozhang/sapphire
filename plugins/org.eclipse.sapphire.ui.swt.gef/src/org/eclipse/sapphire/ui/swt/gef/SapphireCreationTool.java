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

package org.eclipse.sapphire.ui.swt.gef;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireCreationTool extends CreationTool 
{
	public SapphireCreationTool() 
	{
		super();
	}

	/**
	 * Constructs a new CreationTool with the given factory.
	 * 
	 * @param aFactory
	 *            the creation factory
	 */
	public SapphireCreationTool(CreationFactory aFactory)
	{
		super(aFactory);
	}

	/**
	 * Executes the current command and selects the newly created object. The
	 * button that was released to cause this creation is passed in, but since
	 * {@link #handleButtonDown(int)} goes into the invalid state if the button
	 * pressed is not button 1, this will always be button 1.
	 * 
	 * @param button
	 *            the button that was pressed
	 */
	protected void performCreation(int button) 
	{
		if (getCurrentCommand() != null)
		{
			executeCurrentCommand();
			selectAddedObject();
		}
	}

	/*
	 * Add the newly created object to the viewer's selected objects.
	 */
	private void selectAddedObject()
	{		
		Object model = getCreateRequest().getNewObject();
		if (model instanceof DiagramNodeTemplate)
		{
			DiagramNodeTemplate nodeTemplate = (DiagramNodeTemplate)model;
			List<DiagramNodePart> nodeParts = nodeTemplate.getDiagramNodes();
			if (nodeParts.size() > 0)
			{
				DiagramNodePart nodePart = nodeParts.get(nodeParts.size() - 1);
				EditPart parentPart = getTargetEditPart();
				DiagramModel diagramModel = (DiagramModel)parentPart.getModel();
				DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(nodePart);
				
				EditPartViewer viewer = getCurrentViewer();
				viewer.getControl().forceFocus();
				Object editpart = viewer.getEditPartRegistry().get(nodeModel);
				if (editpart instanceof EditPart) 
				{
					// Force a layout first.
					viewer.flush();
					viewer.select((EditPart) editpart);
				}
				
				diagramModel.handleDirectEditing(nodePart);
			}
		}		
	}
	
}
