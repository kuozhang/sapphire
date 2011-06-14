/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeAddActionHandlerFactory

    extends SapphireActionHandlerFactory 
    
{

    @Override
    public List<SapphireActionHandler> create() 
    {
        final List<SapphireActionHandler> handlers = new ArrayList<SapphireActionHandler>();
        
        final SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart) getPart();
        for (final DiagramNodeTemplate nodeTemplate : diagramPart.getNodeTemplates())
        {
            final DiagramNodeAddActionHandler addNodeHandler = new DiagramNodeAddActionHandler(nodeTemplate);
            handlers.add(addNodeHandler);
        }
        return handlers;
    }

}
