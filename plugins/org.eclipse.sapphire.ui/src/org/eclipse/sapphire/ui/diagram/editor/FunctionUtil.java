/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [348751] Diagram connection labels should check for derived and readonly properties
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.PropertyAccessFunction;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class FunctionUtil 
{
    public static ValueProperty getFunctionProperty(IModelElement modelElement, FunctionResult functionResult)
    {
        if (functionResult.function() instanceof PropertyAccessFunction)
        {
            if (functionResult.operand(0).value() instanceof String)
            {
                String propName = (String)functionResult.operand(0).value();
                final ModelElementType type = modelElement.getModelElementType();
                final ModelProperty property = type.getProperty(propName);
                
                if( isWritableValueProperty( property ) )
                {
                    return (ValueProperty)property;
                }
            }
        }
        else 
        {
            List<FunctionResult> subFuncs = functionResult.operands();
            for (FunctionResult subFunc : subFuncs)
            {
                ValueProperty property = getFunctionProperty(modelElement, subFunc);
                
                if( isWritableValueProperty( property ) )
                {
                    return property;
                }
            }
        }
        return null;
    }
    
    private static boolean isWritableValueProperty( final ModelProperty property )
    {
        return ( property != null && property instanceof ValueProperty && ! property.isReadOnly() );
    }

}
