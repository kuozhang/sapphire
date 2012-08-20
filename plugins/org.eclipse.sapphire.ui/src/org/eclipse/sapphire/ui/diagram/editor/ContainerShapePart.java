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
import org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarkerDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContainerShapePart extends ShapePart 
{
	private ContainerShapeDef containerShapeDef;
	private IModelElement modelElement;	
	private List<ShapePart> children;
	private int validationMarkerIndex = -1;
	private ValidationMarkerPart validationMarkerPart;

	@Override
    protected void init()
    {
        super.init();
        this.containerShapeDef = (ContainerShapeDef)super.definition;
        this.modelElement = getModelElement();
        
        // create children parts
        this.children = new ArrayList<ShapePart>();
        int index = 0;
        for (ShapeDef shape : this.containerShapeDef.getContent())
        {
        	ShapePart childPart = null;
        	if (shape instanceof TextDef)
        	{
    	        childPart = new TextPart();
        	}
        	else if (shape instanceof ImageDef)
        	{
        		childPart = new ImagePart();
        	}
        	else if (shape instanceof ValidationMarkerDef)
        	{
        		this.validationMarkerPart = new ValidationMarkerPart();
        		this.validationMarkerIndex = index;
        		childPart = this.validationMarkerPart;
        	}
        	else if (shape instanceof RectangleDef)
        	{
        		childPart = new RectanglePart();
        	}
        	if (childPart != null)
        	{
        		childPart.init(this, this.modelElement, shape, Collections.<String,String>emptyMap());
        		this.children.add(childPart);
        	}
        	index++;
        }
    }

	public ShapeLayoutDef getLayout()
	{
		return this.containerShapeDef.getLayout().element();
	}
	
	public List<ShapePart> getChildren()
	{
		return this.children;
	}
	
	public boolean containsValidationMarker()
	{
		return this.validationMarkerIndex != -1;
	}
	
	public int getValidationMarkerIndex()
	{
		return this.validationMarkerIndex;
	}
	
	public ValidationMarkerPart getValidationMarkerPart()
	{
		return this.validationMarkerPart;
	}
}
