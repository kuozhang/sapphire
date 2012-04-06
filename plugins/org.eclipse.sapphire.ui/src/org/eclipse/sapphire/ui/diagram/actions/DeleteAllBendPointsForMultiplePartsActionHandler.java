/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [376266] Diagram delete all connection bend points action should be available in multi-select mode
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.actions;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DeleteAllBendPointsForMultiplePartsActionHandler extends SapphireDiagramActionHandler 
{
    @Override
    public boolean canExecute(Object obj) 
    {
        return isEnabled();
    }
    
    @Override
    public boolean isEnabled()
    {
        final SapphireDiagramEditorPagePart page = (SapphireDiagramEditorPagePart) getPart();
        
        for( ISapphirePart selectedPart : page.getSelections() )
        {
            if( selectedPart instanceof DiagramConnectionPart )
            {
                if( ! ( (DiagramConnectionPart) selectedPart ).getConnectionBendpoints().isEmpty() )
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    protected Object run( final SapphireRenderingContext context) 
    {
        final SapphireDiagramEditorPagePart page = (SapphireDiagramEditorPagePart) getPart();
        
        for( ISapphirePart selectedPart : page.getSelections() )
        {
            if( selectedPart instanceof DiagramConnectionPart )
            {
                ( (DiagramConnectionPart) selectedPart ).removeAllBendpoints();
            }
        }
        
        return null;
    }

}
