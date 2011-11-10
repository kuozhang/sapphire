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

package org.eclipse.sapphire.ui.gef.diagram.editor.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DeleteNodeCommand extends Command {
	
    private static final String DELETE_ACTION_ID = "Sapphire.Delete";

    private DiagramNodePart nodePart;

	public DeleteNodeCommand(DiagramNodePart part) {
		this.nodePart = part;
	}

	@Override
	public void execute() {
        IModelElement nodeModel = nodePart.getLocalModelElement();
        // Need to remove connection parts that are associated with this node
        deleteNodeConnections(nodePart);
        
        // Check top level connections to see whether we need to remove the connection parent element
        SapphireDiagramEditorPagePart editorPart = nodePart.getDiagramNodeTemplate().getDiagramEditorPart();
        List<DiagramConnectionTemplate> connTemplates = editorPart.getConnectionTemplates();
        for (DiagramConnectionTemplate connTemplate : connTemplates)
        {
            if (connTemplate.getConnectionType() == DiagramConnectionTemplate.ConnectionType.OneToMany)
            {
                IModelElement connParentElement = connTemplate.getConnectionParentElement(nodeModel);
                if (connParentElement != null)
                {
                    ModelElementList<?> connParentList = (ModelElementList<?>)connParentElement.parent();
                    connParentList.remove(connParentElement);
                }
            }
        }
        
        ModelElementList<?> list = (ModelElementList<?>) nodeModel.parent();
        list.remove(nodeModel);
	}
	
    private void deleteNodeConnections(DiagramNodePart nodePart)
    {
        IModelElement nodeModel = nodePart.getLocalModelElement();
        
        // Look for embedded connections
        DiagramEmbeddedConnectionTemplate embeddedConn = nodePart.getDiagramNodeTemplate().getEmbeddedConnectionTemplate();
        if (embeddedConn != null)
        {
            for (DiagramConnectionPart connPart : embeddedConn.getDiagramConnections(null))
            {
                if ((connPart.getEndpoint1() != null && connPart.getEndpoint1().equals(nodeModel)) || 
                        (connPart.getEndpoint2() != null && connPart.getEndpoint2().equals(nodeModel)))
                {
                    deleteConnection(connPart);
                }
            }
        }
        // Look for top level connections
        SapphireDiagramEditorPagePart diagramPart = nodePart.getDiagramNodeTemplate().getDiagramEditorPart();
        for (DiagramConnectionTemplate connTemplate : diagramPart.getConnectionTemplates())
        {
            for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
            {
                if ((connPart.getEndpoint1() != null && connPart.getEndpoint1().equals(nodeModel)) || 
                        (connPart.getEndpoint2() != null && connPart.getEndpoint2().equals(nodeModel)))
                {
                    if (!(connPart instanceof DiagramImplicitConnectionPart))
                    {
                        deleteConnection(connPart);
                    }
                }
            }
        }
    }
    
    private void deleteConnection(DiagramConnectionPart connPart)
    {
        SapphireActionHandler deleteActionHandler = connPart.getAction(DELETE_ACTION_ID).getFirstActiveHandler();
        SapphireRenderingContext renderingCtx = new SapphireRenderingContext(connPart, null);
        deleteActionHandler.execute(renderingCtx);
    }
}
