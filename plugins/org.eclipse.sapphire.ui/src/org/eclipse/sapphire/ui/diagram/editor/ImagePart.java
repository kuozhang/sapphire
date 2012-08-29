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

import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
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
	private FunctionResult imageVisibleFunctionResult;

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
                    refreshImage();
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
                    refreshImage();
                }
            }
        );
        
        this.imageVisibleFunctionResult = initExpression
        ( 
            this.modelElement,
            this.imageDef.getVisibleWhen().getContent(),
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshImage();
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
        if (this.imageVisibleFunctionResult != null)
        {
            this.imageVisibleFunctionResult.dispose();
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
    
	private void refreshImage()
	{
    	DiagramNodePart nodePart = getNodePart();
    	if (nodePart != null) {
        	Set<SapphirePartListener> listeners = nodePart.getListeners();
    		for(SapphirePartListener listener : listeners)
    		{
    			if (listener instanceof SapphireDiagramPartListener)
    			{
    				DiagramNodeEvent nue = new DiagramNodeEvent(nodePart, this);
    				((SapphireDiagramPartListener)listener).handleNodeUpdateEvent(nue);
    			}
    		}
    	}
	}
	
	private DiagramNodePart getNodePart() 
	{
		DiagramNodePart nodePart = null;
		ISapphirePart part = this;
		while (part != null) {
			if (part instanceof DiagramNodePart) {
				nodePart = (DiagramNodePart)part;
				break;
			}
			part = part.getParentPart();
		}
		return nodePart;
	}
   
}
