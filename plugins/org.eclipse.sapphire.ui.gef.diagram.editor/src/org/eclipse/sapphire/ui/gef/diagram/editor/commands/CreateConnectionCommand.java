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

import org.eclipse.draw2d.Connection;
import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class CreateConnectionCommand extends Command {
    
	private SapphireDiagramEditorPagePart diagramPart;
    private IDiagramConnectionDef connDef;

    /** The connection instance. */
	//private DiagramConnectionPart connection;

	/** Start endpoint for the connection. */
	private final DiagramNodePart source;
	/** Target endpoint for the connection. */
	private DiagramNodePart target;

	/**
	 * Instantiate a command that can create a connection between two shapes.
	 * 
	 * @param source
	 *            the source endpoint (a non-null Shape instance)
	 * @param lineStyle
	 *            the desired line style. See Connection#setLineStyle(int) for
	 *            details
	 * @throws IllegalArgumentException
	 *             if source is null
	 * @see Connection#setLineStyle(int)
	 */
	public CreateConnectionCommand(DiagramNodePart source, IDiagramConnectionDef connDef) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("connection creation");
		this.source = source;
		this.connDef = connDef;
		this.diagramPart = source.nearest(SapphireDiagramEditorPagePart.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		// disallow source -> source connections
		if (target == null || source.equals(target)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		DiagramConnectionTemplate connectionTemplate = getConnectionTemplate(this.source);
		DiagramConnectionPart connection = connectionTemplate.createNewDiagramConnection(this.source, this.target);
		// TODO activate direct editing after object creation
	}

	/**
	 * Set the target endpoint for the connection.
	 * 
	 * @param target
	 *            that target endpoint (a non-null DiagramNodePart instance)
	 * @throws IllegalArgumentException
	 *             if target is null
	 */
	public void setTarget(DiagramNodePart target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

    private DiagramConnectionTemplate getConnectionTemplate(DiagramNodePart srcNode)
    {
        DiagramEmbeddedConnectionTemplate embeddedConn = srcNode.getDiagramNodeTemplate().getEmbeddedConnectionTemplate();
        if (embeddedConn != null && 
                embeddedConn.getConnectionId().equalsIgnoreCase(this.connDef.getId().getContent()))
        {
            return embeddedConn;
        }
        
        // check top level connections
        List<DiagramConnectionTemplate> connTemplates = this.diagramPart.getConnectionTemplates();
        for (DiagramConnectionTemplate connTemplate : connTemplates)
        {
            if (connTemplate.getConnectionId().equalsIgnoreCase(this.connDef.getId().getContent()))
            {
                return connTemplate;
            }
        }
        return null;
    }
}
