/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ToggleDiagramGridActionHandler extends SapphireDiagramActionHandler 
{
    private SapphireDiagramEditor diagramEditor;
    
    @Override
    public boolean canExecute(Object obj) 
    {        
        return true;
    }

    public void setDiagramEditor(SapphireDiagramEditor diagramEditor)
    {
        this.diagramEditor = diagramEditor;
    }
    
    @Override
    public boolean isChecked()
    {
        if (this.diagramEditor != null)
        {
            return this.diagramEditor.isGridVisible();
        }
        return false;
    }
    
    @Override
    protected Object run(SapphireRenderingContext context) 
    {
        if (this.diagramEditor != null)
        {
            GraphicalViewer diagramViewer = this.diagramEditor.getGraphicalViewer();
            boolean visible = !diagramEditor.isGridVisible();
            diagramViewer.setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, new Boolean(visible));
            diagramViewer.setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, new Boolean(visible));
            
            diagramEditor.setGridVisible(visible);
        }
        return null;
    }

}
