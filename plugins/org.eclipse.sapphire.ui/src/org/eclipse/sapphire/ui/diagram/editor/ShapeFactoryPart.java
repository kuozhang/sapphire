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

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;
import org.eclipse.sapphire.ui.diagram.shape.def.RectangleDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeFactoryCaseDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeFactoryDef;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapeFactoryPart extends ShapePart 
{
	private ShapeFactoryDef shapeFactoryDef;
	private IModelElement modelElement;	
	private ListProperty modelProperty;
	private List<ShapePart> children;
	
	@Override
    protected void init()
    {
        super.init();
        this.modelElement = getModelElement();
        this.shapeFactoryDef = (ShapeFactoryDef)super.definition;
        this.children = new ArrayList<ShapePart>();
        
        String propertyName = this.shapeFactoryDef.getProperty().getContent();
        this.modelProperty = (ListProperty)resolve(this.modelElement, propertyName);
        ModelElementList<?> list = this.modelElement.read(this.modelProperty);
        for( IModelElement listEntryModelElement : list )
        {
        	ShapeFactoryCaseDef shapeFactoryCase = getShapeFactoryCase(listEntryModelElement);
        	ShapeDef shapeDef = shapeFactoryCase.getShape().element();
        	ShapePart childShapePart = createShapePart(shapeDef, listEntryModelElement);
        	if (childShapePart != null)
        	{
        		childShapePart.setActive(true);
        		this.children.add(childShapePart);
        	}
        }
    }
	
	public List<ShapePart> getChildren()
	{
		return this.children;
	}
	
	private ShapeFactoryCaseDef getShapeFactoryCase(IModelElement listEntryModelElement)
	{
        for (ShapeFactoryCaseDef shapeFactoryCase : this.shapeFactoryDef.getShapeFactoryCases())
        {
        	JavaType javaType = shapeFactoryCase.getType().resolve();
        	Class<?> cl = javaType.artifact();
        	if (cl.isAssignableFrom(listEntryModelElement.getClass()))
        	{
        		return shapeFactoryCase;
        	}
        			
        }
		return null;
	}
	
    private ShapePart createShapePart(ShapeDef shapeDef, IModelElement modelElement)
    {
    	ShapePart shapePart = null;
    	if (shapeDef instanceof TextDef)
    	{
	        shapePart = new TextPart();
    	}
    	else if (shapeDef instanceof ImageDef)
    	{
    		shapePart = new ImagePart();
    	}
    	else if (shapeDef instanceof RectangleDef)
    	{
    		shapePart = new RectanglePart();
    	}
    	else if (shapeDef instanceof ShapeFactoryDef)
    	{
    		shapePart = new ShapeFactoryPart();
    	}
    	if (shapePart != null)
    	{
    		shapePart.init(this, modelElement, shapeDef, Collections.<String,String>emptyMap());    		
    	}
    	return shapePart;
    }
	
}
