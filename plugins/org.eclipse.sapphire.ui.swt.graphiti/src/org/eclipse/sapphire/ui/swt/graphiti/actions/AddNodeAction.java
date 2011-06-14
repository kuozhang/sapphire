/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class AddNodeAction extends SapphireActionHandlerDelegate
{
    
    public AddNodeAction(SapphireDiagramEditor diagramEditor, SapphireActionHandler sapphireActionHandler)
    {
        super(diagramEditor, sapphireActionHandler);
    }
        
    @Override
    protected void handlePostExecutionEvent(SapphireActionHandler.PostExecuteEvent event)
    {
        DiagramNodePart nodePart = (DiagramNodePart)event.getResult();
        ILocation loc = getSapphireDiagramEditor().getCurrentMouseLocation();
        final AddContext ctx = new AddContext();
        ctx.setNewObject(nodePart);
        Diagram diagram = getSapphireDiagramEditor().getDiagramTypeProvider().getDiagram();
        ctx.setTargetContainer(diagram);
        ctx.setX(loc.getX());
        ctx.setY(loc.getY());
        final IAddFeature ft = getSapphireDiagramEditor().getDiagramTypeProvider().getFeatureProvider().getAddFeature(ctx);
        TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
        ted.getCommandStack().execute(new RecordingCommand(ted) 
        {
            protected void doExecute() 
            {        
                ft.add(ctx);
                ft.getFeatureProvider().getDirectEditingInfo().setActive(true);
            }
        });
    }
        
}
