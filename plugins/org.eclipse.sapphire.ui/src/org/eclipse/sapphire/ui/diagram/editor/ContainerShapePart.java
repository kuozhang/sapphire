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

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.modeling.ElementValidationEvent;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeFactoryDef;
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
	private FilteredListener<ElementValidationEvent> elementValidationListener;
	
	// TODO support multiple text part
	private TextPart textPart;

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
        		this.textPart = new TextPart();
    	        childPart = this.textPart;
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
        	else if (shape instanceof ShapeFactoryDef)
        	{
        		childPart = new ShapeFactoryPart();
        	}
        	if (childPart != null)
        	{
        		childPart.init(this, this.modelElement, shape, Collections.<String,String>emptyMap());
        		this.children.add(childPart);
        	}
        	index++;
        }
        if (this.validationMarkerIndex != -1)
        {
	        this.elementValidationListener = new FilteredListener<ElementValidationEvent>()
	        {
                @Override
                protected void handleTypedEvent( final ElementValidationEvent event )
                {
                    refreshShapeValidation();
                }
	        };
            this.modelElement.attach(this.elementValidationListener); 
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
	
	// TODO support multiple text part
	public TextPart getTextPart()
	{
		if (this.textPart != null)
		{
			return this.textPart;
		}
		for (ShapePart shapePart : getChildren())
		{
			if (shapePart instanceof ContainerShapePart)
			{
				TextPart textPart = ((ContainerShapePart)shapePart).getTextPart();
				if (textPart != null)
				{
					return textPart;
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean isEditable()
	{
		return this.textPart != null;
	}
	
	// TODO support multiple shape factory
	public ShapeFactoryPart getShapeFactoryPart()
	{
		for (ShapePart shapePart : getChildren())
		{
			if (shapePart instanceof ShapeFactoryPart)
			{
				return (ShapeFactoryPart)shapePart;
			}
			else if (shapePart instanceof ContainerShapePart)
			{
				ShapeFactoryPart shapeFactoryPart = ((ContainerShapePart)shapePart).getShapeFactoryPart();
				if (shapeFactoryPart != null)
				{
					return shapeFactoryPart;
				}
			}
		}
		return null;
	}
	
	@Override
    public List<ShapePart> getActiveChildren()
    {
		List<ShapePart> activeChildren = new ArrayList<ShapePart>();
		for (ShapePart child : getChildren())
		{
			if (child.isActive())
			{
				activeChildren.add(child);
			}
			else if (child instanceof ShapeFactoryPart)
			{
				activeChildren.addAll(((ShapeFactoryPart)child).getActiveChildren());
			}
			else if (child instanceof ContainerShapePart)
			{
				activeChildren.addAll(((ContainerShapePart)child).getActiveChildren());
			}
		}
    	return activeChildren;
    }	
	
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.elementValidationListener != null)
        {
        	this.modelElement.detach(this.elementValidationListener);
        }
    }

    private void refreshShapeValidation()
	{
    	DiagramNodePart nodePart = getNodePart();
    	nodePart.refreshShapeValidation(this);		
	}
	
}
