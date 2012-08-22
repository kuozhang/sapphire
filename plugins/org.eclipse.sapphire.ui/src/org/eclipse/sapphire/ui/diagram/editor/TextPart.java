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
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextPart extends ShapePart 
{
	private TextDef textDef;
	private IModelElement modelElement;
	private FunctionResult textFunction;
	private ValueProperty labelProperty;
	private FunctionResult colorFunction;
	
	@Override
    protected void init()
    {
        super.init();
        this.textDef = (TextDef)super.definition;
        this.modelElement = getModelElement();
        
        this.textFunction = initExpression
        ( 
            this.modelElement,
            this.textDef.getContent().getContent(),
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshLabel();
                }
            }
        );
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, this.textFunction);
        
        this.colorFunction = initExpression
        ( 
            this.modelElement,
            this.textDef.getColor().getContent(),
            Color.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshLabel();
                }
            }
        );
    }
	
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.textFunction != null)
        {
            this.textFunction.dispose();
        }
        if (this.colorFunction != null)
        {
            this.colorFunction.dispose();
        }
    }
	
    public String getText()
    {
    	String path = null;
    	if (this.textFunction != null)
    	{
    		path = (String)this.textFunction.value();
    	}
    	return path;
    }
    
    public void setText(String text)
    {
        if (this.labelProperty != null)
        {
            this.modelElement.write(this.labelProperty, text);
        }
    	
    }
    
    public Color getTextColor()
    {
    	return (Color)this.colorFunction.value();
    }
    
	private void refreshLabel()
	{
		
	}
	
}
