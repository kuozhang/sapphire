/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.ui.SapphirePartListener;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class SapphireDiagramPartListener extends SapphirePartListener 
{
    public void handleShapeVisibilityEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleShapeUpdateEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleShapeValidationEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleShapeAddEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleShapeReorderEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleShapeDeleteEvent(final DiagramShapeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleNodeValidationEvent(final DiagramNodeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleNodeAddEvent(final DiagramNodeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleNodeDeleteEvent(final DiagramNodeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleNodeMoveEvent(final DiagramNodeEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
    {
        // The default implementation doesn't do anything.
    }
    
    public void handleConnectionAddEvent(final DiagramConnectionEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleConnectionDeleteEvent(final DiagramConnectionEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleConnectionAddBendpointEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleConnectionResetBendpointsEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleDirectEditEvent(final DiagramPartEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleGridStateChangeEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleGuideStateChangeEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleDiagramUpdateEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleDiagramSaveEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleSelectAllEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }
            
    public void handleSelectAllNodesEvent(final DiagramPageEvent event)
    {
    	// The default implementation doesn't do anything.
    }
}
