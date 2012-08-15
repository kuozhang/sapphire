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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.diagram.shape.def.ContainerShape;
import org.eclipse.sapphire.ui.diagram.shape.def.Image;
import org.eclipse.sapphire.ui.diagram.shape.def.Rectangle;
import org.eclipse.sapphire.ui.diagram.shape.def.Shape;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayout;
import org.eclipse.sapphire.ui.diagram.shape.def.Text;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarker;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapePart extends ShapePart 
{
	private ContainerShape containerShapeDef;
	private IModelElement modelElement;	
	private List<ShapePart> children;

	@Override
    protected void init()
    {
        super.init();
        this.containerShapeDef = (ContainerShape)super.definition;
        this.modelElement = getModelElement();
        
        // create children parts
        this.children = new ArrayList<ShapePart>();
        for (Shape shape : this.containerShapeDef.getContent())
        {
        	ShapePart childPart = null;
        	if (shape instanceof Text)
        	{
    	        childPart = new TextPart();
        	}
        	else if (shape instanceof Image)
        	{
        		childPart = new ImagePart();
        	}
        	else if (shape instanceof ValidationMarker)
        	{
        		childPart = new ValidationMarkerPart();
        	}
        	else if (shape instanceof Rectangle)
        	{
        		childPart = new RectanglePart();
        	}
        	if (childPart != null)
        	{
        		childPart.init(this, this.modelElement, shape, Collections.<String,String>emptyMap());
        		this.children.add(childPart);
        	}        	
        }
    }

	public ShapeLayout getLayout()
	{
		return this.containerShapeDef.getLayout().element();
	}
	
	public List<ShapePart> getChildren()
	{
		return this.children;
	}
}
