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
import org.eclipse.sapphire.ui.diagram.editor.ImagePart;
import org.eclipse.sapphire.ui.diagram.editor.RectanglePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;

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
    
    public static final class ShapeModelFactory
    {
    	public static ShapeModel createShapeModel(DiagramNodeModel nodeModel, ShapeModel parent, ShapePart shapePart)
    	{
    		ShapeModel childModel = null;
        	if (shapePart instanceof TextPart)
        	{
    	        childModel = new TextModel(nodeModel, parent, (TextPart)shapePart);
        	}
        	else if (shapePart instanceof ImagePart)
        	{
        		childModel = new ImageModel(nodeModel, parent, (ImagePart)shapePart);
        	}
        	else if (shapePart instanceof ValidationMarkerPart)
        	{
        		childModel = new ValidationMarkerModel(nodeModel, parent, (ValidationMarkerPart)shapePart);
        	}
        	else if (shapePart instanceof RectanglePart)
        	{
        		childModel = new RectangleModel(nodeModel, parent, (RectanglePart)shapePart);
        	}
        	else if (shapePart instanceof ShapeFactoryPart)
        	{
            	childModel = new ShapeFactoryModel(nodeModel, parent, (ShapeFactoryPart)shapePart);        		
        	}
    		return childModel;
    	}
    	
   	
    }
}
