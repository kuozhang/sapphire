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

package org.eclipse.sapphire.ui.swt.gef.model;

import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeModel extends DiagramModelBase 
{
	private DiagramNodeModel nodeModel;
	private ShapeModel parent;
    private SapphirePart sapphirePart;
    public final static String SHAPE_START_EDITING = "SHAPE_START_EDITING";

    public ShapeModel(DiagramNodeModel nodeModel, ShapeModel parent, SapphirePart part)
    {
    	this.nodeModel = nodeModel;
    	this.parent = parent;
    	this.sapphirePart = part;
    }
    
	@Override
	public SapphirePart getSapphirePart() 
	{		
		return this.sapphirePart;
	}

	public ShapeModel getParent()
	{
		return this.parent;
	}
	
	public DiagramNodeModel getNodeModel()
	{
		return this.nodeModel;
	}
	
	public void handleDirectEditing()
	{
		firePropertyChange(SHAPE_START_EDITING, null, null);
	}
	
    @SuppressWarnings( "unchecked" )
    public final <T> T nearest( final Class<T> partType )
    {
        if( partType.isAssignableFrom( getClass() ) )
        {
            return (T) this;
        }
        else
        {
            if( this.parent != null )
            {
                return this.parent.nearest( partType );
            }
            else
            {
                return null;
            }
        }
    }
	
}
