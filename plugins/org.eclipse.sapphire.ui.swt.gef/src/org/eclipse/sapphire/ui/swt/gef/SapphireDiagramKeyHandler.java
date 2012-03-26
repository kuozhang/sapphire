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

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.SapphireKeySequence;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentation;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentationManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramKeyHandler extends KeyHandler
{		
	private KeyHandler parent;
	private SapphireDiagramEditor diagramEditor;
	private ISapphirePart sapphirePart;
	private String actionContext;
	private GraphicalEditPart editPart;
	private SapphireActionGroup tempActions;
	private SapphireActionPresentationManager actionPresentationManager;
	private DiagramKeyboardActionPresentation actionPresentation;
		
	public SapphireDiagramKeyHandler(final SapphireDiagramEditor diagramEditor,
					final ISapphirePart sapphirePart,
					final String actionContext)
	{
		this.diagramEditor = diagramEditor;
		this.sapphirePart = sapphirePart;
		this.actionContext = actionContext;
		this.editPart = diagramEditor.getSelectedEditParts().get(0);
	}
	
	@Override
	public boolean keyPressed(KeyEvent event) 
	{
		final SapphireActionPresentationManager manager = getManager();
        for( SapphireAction action : manager.getActions() )
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
	
	public void dispose()
	{
		if (this.tempActions != null)
		{
			this.tempActions.dispose();
			this.tempActions = null;
		}
		if (this.actionPresentationManager != null)
		{
			this.actionPresentationManager.dispose();
			this.actionPresentationManager = null;			
		}
	}
	
	private SapphireActionPresentationManager getManager()
	{
		if (this.actionPresentationManager == null)
		{
			this.tempActions = this.sapphirePart.getActions(this.actionContext);
			this.actionPresentationManager = new SapphireActionPresentationManager(
					new DiagramRenderingContext(this.sapphirePart, this.diagramEditor),
					this.tempActions);
			this.actionPresentation = new DiagramKeyboardActionPresentation(
					this.actionPresentationManager, this.diagramEditor, this.editPart);
			this.actionPresentation.render();
		}
		return this.actionPresentationManager;
	}
	
}
