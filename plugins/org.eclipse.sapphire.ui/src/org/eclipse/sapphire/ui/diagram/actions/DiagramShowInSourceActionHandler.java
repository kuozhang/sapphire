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

package org.eclipse.sapphire.ui.diagram.actions;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.SourceEditorService;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramShowInSourceActionHandler extends
        SapphireDiagramActionHandler 
{
    
    @Override
    public boolean canExecute(Object obj) 
    {
        return true;
    }

    @Override
    protected Object run(SapphireRenderingContext context) 
    {
        ISapphirePart part = context.getPart();
        IModelElement element = part.getModelElement();
        element.adapt( SourceEditorService.class ).show( element, null );
        return null;
    }

}
