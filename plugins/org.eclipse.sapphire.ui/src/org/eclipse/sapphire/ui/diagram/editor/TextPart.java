/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Extend Sapphire Diagram Framework to support SQL Schema diagram like editors
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.shape.def.FontDef;
import org.eclipse.sapphire.ui.diagram.shape.def.TextDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextPart extends ShapePart 
{
	private TextDef textDef;
	private IModelElement modelElement;
	private Function textFunction;
	private FunctionResult functionResult;
	
	@Override
    protected void init()
    {
        super.init();
        this.textDef = (TextDef)super.definition;
        this.modelElement = getModelElement();
        
        this.textFunction = this.textDef.getContent().getContent();
        this.functionResult = initExpression
        ( 
            this.modelElement,
            this.textFunction,
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    notifyShapeUpdate(TextPart.this);
                }
            }
        );
        this.setEditable(!(this.textFunction instanceof Literal));
    }
	
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.functionResult != null)
        {
            this.functionResult.dispose();
        }
    }
	
    public String getContent()
    {
    	String value = null;
    	if (this.functionResult != null)
    	{
    		value = (String)this.functionResult.value();
    	}
    	return value;
    }
    
    public FunctionResult getContentFunction()
    {
    	return this.functionResult;
    }
        
    public Color getTextColor()
    {
    	return this.textDef.getColor().getContent();
    }

    public FontDef getFontDef() 
    {
    	return this.textDef.getFont();
    }
    
}
