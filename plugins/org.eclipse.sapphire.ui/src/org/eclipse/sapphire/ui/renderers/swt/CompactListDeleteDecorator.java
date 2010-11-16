/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.SapphireImageCache;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public final class CompactListDeleteDecorator
{

	private final CompactListPropertyEditorRenderer compactListPropertyEditorRenderer;
	private final SapphirePropertyEditor propertyEditor;
    private final SapphireRenderingContext context;
    private final CompactListBinding binding;
    private final Label control;
    private final ModelProperty property;
    private boolean mouseOverEditorControl;
    private EditorControlMouseTrackListener mouseTrackListener;
    
    public CompactListDeleteDecorator( final CompactListPropertyEditorRenderer compactListPropertyEditorRenderer,
                                       final CompactListBinding binding,
                                       final Composite parent )
    {
    	this.compactListPropertyEditorRenderer = compactListPropertyEditorRenderer;
        this.propertyEditor = compactListPropertyEditorRenderer.getPart();
        this.context = compactListPropertyEditorRenderer.getUiContext();
        this.binding = binding;
        this.property = this.propertyEditor.getProperty();
        this.mouseOverEditorControl = false;
        this.mouseTrackListener = new EditorControlMouseTrackListener();
        
        this.control = new Label( parent, SWT.NONE );
        this.context.adapt( this.control );
        
        this.control.addMouseListener
        (
            new MouseAdapter()
            {
                @Override
                public void mouseUp( final MouseEvent event )
                {
                	IModelElement elem = CompactListDeleteDecorator.this.binding.getModelElement();
                	if (elem != null) {
                		CompactListDeleteDecorator.this.compactListPropertyEditorRenderer.getList().remove(elem);
                	}
                }
            }
        );
        
        this.control.addMouseTrackListener
        (
            new EditorControlMouseTrackListener()
            {
                @Override
                public void mouseEnter( MouseEvent event )
                {
                    super.mouseEnter( event );
                    refreshImageAndCursor();
                }

                @Override
                public void mouseHover( final MouseEvent event )
                {
                    // Suppress default behavior.
                }
            }
        );
        
        addEditorControl(binding.getText());
        
        refresh();
    }
    
    public Label getControl()
    {
        return this.control;
    }
    
    public SapphireRenderingContext getUiContext()
    {
        return this.context;
    }
    
    public Shell getShell()
    {
        return this.context.getShell();
    }
    
    public void addEditorControl( final Control control )
    {
        if( control instanceof Composite )
        {
            for( Control child : ( (Composite) control ).getChildren() )
            {
                addEditorControl( child );
            }
        }
        
        control.addMouseTrackListener( this.mouseTrackListener );
    }
    
    public void refresh()
    {
        final IModelElement element = this.propertyEditor.getModelElement();
        
        final boolean enabled 
            = ( element == null ? false : element.isPropertyEnabled( this.property ) );
        
        // TODO also check for readonly?
        
        if( enabled )
        {
        }            

        refreshImageAndCursor();
    }
    
    private void refreshImageAndCursor()
    {
        if( this.control.isDisposed() ) 
        {
            return;
        }
        
        final SapphireImageCache imageCache = this.propertyEditor.getImageCache();
        
        if( this.mouseOverEditorControl )
        {
            this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_ASSIST ) );
        }
        else
        {
            this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_ASSIST_FAINT ) );
        }
        this.control.setVisible( true );
        this.control.setCursor( Display.getCurrent().getSystemCursor( SWT.CURSOR_HAND ) );

//        this.control.setVisible( false );
//		this.control.setImage( imageCache.getImage( SapphireImageCache.DECORATOR_BLANK ) );
//		this.control.setCursor( null );
    }
    
    private class EditorControlMouseTrackListener
    
        extends MouseTrackAdapter
        
    {
        @Override
        public void mouseEnter( final MouseEvent event )
        {
            CompactListDeleteDecorator.this.mouseOverEditorControl = true;
        }
        
        @Override
        public void mouseHover( final MouseEvent event )
        {
            refreshImageAndCursor();
        }

        @Override
        public void mouseExit( final MouseEvent event )
        {
            CompactListDeleteDecorator.this.mouseOverEditorControl = false;
            performedDelayedImageRefresh();
        }
        
        private void performedDelayedImageRefresh()
        {
            final Runnable op = new Runnable()
            {
                public void run()
                {
                    refreshImageAndCursor();
                }
            };
            
            final Thread thread = new Thread()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 250 );
                    }
                    catch( InterruptedException e ) {}
                    
                    Display.getDefault().asyncExec( op );
                }
            };
            
            thread.start();
        }
    };

}
