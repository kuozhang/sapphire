/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.util.internal;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.DefaultValueService;
import org.eclipse.sapphire.modeling.EnumValueType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LabelTransformer;
import org.eclipse.sapphire.modeling.ValueKeyword;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.NamedValues;
import org.eclipse.sapphire.modeling.annotations.NamedValues.NamedValue;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireCommonUtil {
    
    public final static String getDefaultValueLabel(final IModelElement modelElement, final ValueProperty property) 
    {
        String defaultValue = modelElement.service( property, DefaultValueService.class ).getDefaultValue();
        
        if( defaultValue != null )
        {
            if( property.isOfType( Enum.class ) )
            {
                final EnumValueType enumValueType = new EnumValueType( property.getTypeClass() );
                
                for( Enum<?> item : enumValueType.getItems() )
                {
                    if( item.toString().equals( defaultValue ) )
                    {
                        defaultValue = enumValueType.getLabel( item, true, CapitalizationType.NO_CAPS, false );
                        break;
                    }
                }
            }
            else
            {
                ValueKeyword keyword = property.getKeyword( defaultValue );
                
                if( keyword != null )
                {
                    defaultValue = keyword.toDisplayString();
                }
                else if ( property.hasAnnotation( NamedValues.class ) ) 
                {
                    final NamedValues namedValuesAnnotation = property.getAnnotation( NamedValues.class );
                    final NamedValue[] namedValueAnnotations = namedValuesAnnotation.namedValues();
                    final String propName = property.getName();

                    for( int i = 0, n = namedValueAnnotations.length; i < n; i++ )
                    {
                        final NamedValue x = namedValueAnnotations[ i ];
                        
                        if ( defaultValue.equals(x.value()) ) 
                        {
                            String namedValueLabel = property.getResource( propName + ".namedValue." + x.value() );
                            namedValueLabel = LabelTransformer.transform( namedValueLabel, CapitalizationType.NO_CAPS, true );
                            defaultValue = namedValueLabel + " (" + x.value() + ")";
                            break;
                        }
                    }
                }
            }
            
            if( ! ( property.isOfType( Integer.class ) ||
                    property.isOfType( Long.class ) ||
                    property.isOfType( Float.class ) ||
                    property.isOfType( Double.class ) ||
                    property.isOfType( BigInteger.class ) ||
                    property.isOfType( BigDecimal.class ) ||
                    property.isOfType( Boolean.class ) ) )
            {
                defaultValue = "\"" + defaultValue + "\"";
            }
        }
        return defaultValue;
    }

    public static final String normalizeForDisplay(final ValueProperty property, final String value) {
        String result = property.encodeKeywords(property.decodeKeywords(value));

        ValueKeyword keyword = property.getKeyword(result);

        if (keyword != null) {
            result = keyword.toDisplayString();
        }

        return result;
    }

}
