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
	
	private List<TextPart> textParts;
	private List<ShapeFactoryPart> shapeFactoryParts;

	@Override
    protected void init()
    {
        super.init();
        this.containerShapeDef = (ContainerShapeDef)super.definition;
        this.modelElement = getModelElement();
        
        // create children parts
        this.children = new ArrayList<ShapePart>();
        this.textParts = new ArrayList<TextPart>();
        this.shapeFactoryParts = new ArrayList<ShapeFactoryPart>();
        
        int index = 0;        
        
        for (ShapeDef shape : this.containerShapeDef.getContent())
        {
        	ShapePart childPart = null;
        	if (shape instanceof TextDef)
        	{
    	        childPart = new TextPart();
    	        this.textParts.add((TextPart)childPart);
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
        		childPart.setActive(true);
        		this.shapeFactoryParts.add((ShapeFactoryPart)childPart);
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
	
	public List<TextPart> getTextParts()
	{
		List<TextPart> textParts = new ArrayList<TextPart>();
		textParts.addAll(this.textParts);
		for (ShapePart shapePart : getChildren())
		{
			if (shapePart instanceof ContainerShapePart)
			{
				textParts.addAll(((ContainerShapePart)shapePart).getTextParts());
			}
		}
		return textParts;
	}
	
	@Override
	public boolean isEditable()
	{
		List<TextPart> textParts = getTextParts();
		for (TextPart textPart : textParts)
		{
			if (textPart.isEditable())
			{
				return true;
			}
		}
		return false;
	}
	
	public List<ShapeFactoryPart> getShapeFactoryParts()
	{
		List<ShapeFactoryPart> shapeFactories = new ArrayList<ShapeFactoryPart>();
		shapeFactories.addAll(this.shapeFactoryParts);
		for (ShapePart shapePart : getChildren())
		{
			if (shapePart instanceof ContainerShapePart)
			{
				shapeFactories.addAll(((ContainerShapePart)shapePart).getShapeFactoryParts());
			}
		}
		return shapeFactories;		
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
			activeChildren.addAll(child.getActiveChildren());
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
