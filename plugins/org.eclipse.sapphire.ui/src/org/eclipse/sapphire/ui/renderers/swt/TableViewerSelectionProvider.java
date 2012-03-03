/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class TableViewerSelectionProvider implements ISelectionProvider
{
    public static final String DATA_SELECTION_PROVIDER = "selection.provider";
    
    private final TableViewer tableViewer;
    private final Set<ISelectionChangedListener> listeners;
    
    public TableViewerSelectionProvider( final TableViewer tableViewer )
    {
        this.tableViewer = tableViewer;
        this.listeners = new CopyOnWriteArraySet<ISelectionChangedListener>();
        
        this.tableViewer.addSelectionChangedListener
        (
            new ISelectionChangedListener()
            {
                public void selectionChanged( final SelectionChangedEvent event )
                {
                    notifySelectionChangedListeners();
                }
            }
        );
    }

    public ISelection getSelection()
    {
        return this.tableViewer.getSelection();
    }
    
    public final void setSelection( final ISelection selection )
    {
        throw new UnsupportedOperationException();
    }
    
    public final void addSelectionChangedListener( final ISelectionChangedListener listener )
    {
        this.listeners.add( listener );
    }

    public final void removeSelectionChangedListener( final ISelectionChangedListener listener )
    {
        this.listeners.remove( listener );
    }
    
    public final void notifySelectionChangedListeners()
    {
        final SelectionChangedEvent event = new SelectionChangedEvent( this, getSelection() );
        
        for( ISelectionChangedListener listener : this.listeners )
        {
            listener.selectionChanged( event );
        }
    }
}
