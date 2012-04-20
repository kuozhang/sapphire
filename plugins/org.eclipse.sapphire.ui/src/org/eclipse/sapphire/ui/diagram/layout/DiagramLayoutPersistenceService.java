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

package org.eclipse.sapphire.ui.diagram.layout;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.IdUtil;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DiagramLayoutPersistenceService extends Service
{
	private SapphireDiagramEditorPagePart diagramPart;
	private Set<DiagramLayoutPersistenceServiceListener> listeners;
	
	@Override
    protected void init()
    {
		super.init();
		this.diagramPart = (SapphireDiagramEditorPagePart)context().find(ISapphirePart.class);
    }	
	
	public SapphireDiagramEditorPagePart getDiagramEditorPagePart()
	{
		return this.diagramPart;
	}
	
    public final void addListener( final DiagramLayoutPersistenceServiceListener listener )
    {
        if( this.listeners== null )
        {
            this.listeners= Collections.singleton( listener );
        }
        else
        {
            this.listeners= new HashSet<DiagramLayoutPersistenceServiceListener>( this.listeners);
            this.listeners.add( listener );
        }
    }
    
    public final void removeListener( final DiagramLayoutPersistenceServiceListener listener )
    {
        if( this.listeners!= null )
        {
            if( this.listeners.contains( listener ) )
            {
                if( this.listeners.size() == 1 )
                {
                    this.listeners= null;
                }
                else
                {
                    this.listeners = new HashSet<DiagramLayoutPersistenceServiceListener>( this.listeners);
                    this.listeners.remove( listener );
                }
            }
        }
    }
    
    public final Set<DiagramLayoutPersistenceServiceListener> getListeners()
    {
        if( this.listeners == null)
        {
            return Collections.emptySet();
        }
        else
        {
            return this.listeners;
        }
    }
			
}
