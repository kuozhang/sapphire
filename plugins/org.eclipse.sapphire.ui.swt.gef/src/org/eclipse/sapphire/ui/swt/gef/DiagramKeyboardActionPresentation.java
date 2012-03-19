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

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.SapphireKeySequence;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentation;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentationManager;
import org.eclipse.sapphire.ui.swt.renderer.SapphireHotSpotsActionPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramKeyboardActionPresentation extends SapphireHotSpotsActionPresentation 
{
	private KeyHandler diagramKeyHandler;
	private GraphicalEditPart editPart;
	private SapphireDiagramEditor diagramEditor;
	
    public DiagramKeyboardActionPresentation(final SapphireActionPresentationManager manager, 
    		final GraphicalEditPart editPart,
    		final SapphireDiagramEditor diagramEditor)
    {
        super(manager);
        this.editPart = editPart;   
        this.diagramEditor = diagramEditor;
    }

	@Override
	public void render()
	{
		for (final SapphireAction action : getActions())
		{
			registerHotSpot(action, new EditPartHotSpot( this.editPart, this.diagramEditor ));
		}

	}
	
	private void createKeyHandler()
	{
		this.diagramKeyHandler = new SapphireDiagramKeyHandler(getManager());
	}
	
	public KeyHandler getKeyHandler()
	{
		if (this.diagramKeyHandler == null)
		{
			createKeyHandler();
		}
		return this.diagramKeyHandler;
	}

	private static class SapphireDiagramKeyHandler extends KeyHandler
	{
		private SapphireActionPresentationManager manager;
		private KeyHandler parent;
		
		public SapphireDiagramKeyHandler(final SapphireActionPresentationManager manager)
		{
			this.manager = manager;
		}
		
		@Override
		public boolean keyPressed(KeyEvent event) 
		{
	        for( SapphireAction action : this.manager.getActions() )
	        {
	            if( action.hasActiveHandlers() )
	            {
	                final SapphireKeySequence keySequence = action.getKeyBinding();
	                
	                if( keySequence != null )
	                {
	                    int expectedStateMask = 0;
	                    
	                    for( SapphireKeySequence.Modifier modifier : keySequence.getModifiers() )
	                    {
	                        if( modifier == SapphireKeySequence.Modifier.SHIFT )
	                        {
	                            expectedStateMask = expectedStateMask | SWT.SHIFT;
	                        }
	                        else if( modifier == SapphireKeySequence.Modifier.ALT )
	                        {
	                            expectedStateMask = expectedStateMask | SWT.ALT;
	                        }
	                        else if( modifier == SapphireKeySequence.Modifier.CONTROL )
	                        {
	                            expectedStateMask = expectedStateMask | SWT.CONTROL;
	                        }
	                    }
	                    
	                    if( event.stateMask == expectedStateMask && event.keyCode == keySequence.getKeyCode() )
	                    {
	                        final List<SapphireActionHandler> handlers = action.getActiveHandlers();
	                        
	                        if( handlers.size() == 1 )
	                        {
	                            final SapphireActionHandler handler = handlers.get( 0 );
	                            
	                            final Runnable runnable = new Runnable()
	                            {
	                                public void run()
	                                {
	                                	if (handler.isEnabled())
	                                	{
	                                		handler.execute( manager.getContext() );
	                                	}
	                                }
	                            };
	                            
	                            manager.getContext().getDisplay().asyncExec( runnable );
	                            return true;
	                        }
	                        else
	                        {
	                            for( SapphireActionPresentation presentation : manager.getPresentations() )
	                            {
	                                if( presentation.displayActionHandlerChoice( action ) )
	                                {
	                                    return true;
	                                }
	                            }
	                        }
	                        
	                    }
	                }
	            }
	        }
			
			return parent != null && parent.keyPressed(event);
		}
		
		public KeyHandler setParent(KeyHandler parent) 
		{
			this.parent = parent;
			return this;
		}
		
	}
	
    private static final class EditPartHotSpot extends HotSpot
	{
    	private GraphicalEditPart editPart;
    	private SapphireDiagramEditor diagramEditor;
    	
	    public EditPartHotSpot( final GraphicalEditPart editPart, final SapphireDiagramEditor diagramEditor )
	    {
	        this.editPart = editPart;
	        this.diagramEditor = diagramEditor;
	    }
	
	    @Override
	    public Rectangle getBounds()
	    {
	    	IFigure partFigure = this.editPart.getFigure();
	        final org.eclipse.draw2d.geometry.Rectangle bounds = partFigure.getBounds().getCopy();
	        partFigure.translateToAbsolute(bounds);
	        Point viewerOrigin = this.diagramEditor.getGraphicalViewer().getControl().getLocation();
	        viewerOrigin = this.diagramEditor.getGraphicalViewer().getControl().toDisplay(viewerOrigin);
	        bounds.x += viewerOrigin.x;
	        bounds.y += viewerOrigin.y;
	        return new Rectangle( bounds.x, bounds.y, bounds.width, 20 );
	    }
	}

	
}
