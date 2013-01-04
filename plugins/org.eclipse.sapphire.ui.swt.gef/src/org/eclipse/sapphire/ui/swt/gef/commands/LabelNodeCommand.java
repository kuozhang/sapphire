/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Refreshing connections attached to the node
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.FunctionUtil;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramModel;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramNodeModel;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class LabelNodeCommand extends Command {
	
	private DiagramNodeModel node;
	private TextPart textPart;
	private String labelText;

	public LabelNodeCommand(DiagramNodeModel node, TextPart textPart, String labelText) {
		this.node = node;
		this.textPart = textPart;
		this.labelText = labelText;
	}

	@Override
	public void execute() 
	{
		DiagramModel diagramModel = node.getDiagramModel();
		// Make a copy of all the connections that are attached to this node before setting the 
		// node label.
		List<DiagramConnectionModel> connModels = diagramModel.getConnections();
		List<DiagramConnectionPart> connParts1 = new ArrayList<DiagramConnectionPart>();
		List<DiagramConnectionPart> connParts2 = new ArrayList<DiagramConnectionPart>();
		for (DiagramConnectionModel connModel : connModels)
		{
			// Need to disable connection part listeners so the attached connections don't get deleted.
			// Will re-enable them once the end points are refreshed. 
			if (connModel.getSourceNode().equals(node) || (connModel.getTargetNode().equals(node)))
			{
				DiagramConnectionPart connPart = connModel.getModelPart();
	            connPart.removeModelListener();
	            connPart.getDiagramConnectionTemplate().removeModelListener();
	
				if (connModel.getSourceNode().equals(node))
				{
					connParts1.add(connModel.getModelPart());
				}
				else if (connModel.getTargetNode().equals(node))
				{
					connParts2.add(connModel.getModelPart());
				}
			}
		}		
		
		ValueProperty prop = FunctionUtil.getFunctionProperty(this.textPart.getLocalModelElement(), 
				this.textPart.getContentFunction());
		this.textPart.getLocalModelElement().write(prop, this.labelText);
				
		// Refreshing endpoints of attached connections and re-enable listeners on them.
		for (DiagramConnectionPart connPart : connParts1)
		{
			if (!connPart.disposed())
			{
				connPart.resetEndpoint1();
	            connPart.addModelListener();
	            connPart.getDiagramConnectionTemplate().addModelListener();
			}
		}
		for (DiagramConnectionPart connPart : connParts2)
		{
			if (!connPart.disposed())
			{
				connPart.resetEndpoint2();
	            connPart.addModelListener();
	            connPart.getDiagramConnectionTemplate().addModelListener();
			}
		}		
	}
	
}
