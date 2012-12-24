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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.diagram.shape.def.ImageDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ImagePart extends ShapePart 
{
	private ImageDef imageDef;
	private IModelElement modelElement;
	private FunctionResult imagePathFunction;
	private FunctionResult imageDataFunctionResult;

	@Override
    protected void init()
    {
        super.init();
        this.imageDef = (ImageDef)super.definition;
        this.modelElement = getModelElement();
        
        this.imagePathFunction = initExpression
        ( 
            this.modelElement,
            this.imageDef.getPath().getContent(),
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    notifyShapeUpdate(ImagePart.this);
                }
            }
        );
        
        this.imageDataFunctionResult = initExpression
        ( 
            this.modelElement,
            this.imageDef.getPath().getContent(),
            ImageData.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    notifyShapeUpdate(ImagePart.this);
                }
            }
        );
        
    }
	
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.imagePathFunction != null)
        {
            this.imagePathFunction.dispose();
        }
        if (this.imageDataFunctionResult != null)
        {
            this.imageDataFunctionResult.dispose();
        }
    }
	
    public ImageData getImage()
    {
        if( this.imageDataFunctionResult != null )
        {
        	return (ImageData) this.imageDataFunctionResult.value();
        }
        return null;        
    }
 
    public String getImagePath()
    {
    	String path = null;
    	if (this.imagePathFunction != null)
    	{
    		path = (String)this.imagePathFunction.value();
    	}
    	return path;
    }
    
}
