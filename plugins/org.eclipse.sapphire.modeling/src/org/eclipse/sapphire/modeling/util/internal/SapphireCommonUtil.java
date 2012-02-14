/******************************************************************************
 * Copyright (c) 2012 Oracle
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
import org.eclipse.sapphire.modeling.EnumValueType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ValueKeyword;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.NamedValues;
import org.eclipse.sapphire.modeling.annotations.NamedValues.NamedValue;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.services.DefaultValueService;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireCommonUtil {
    
    public final static String getDefaultValueLabel( final IModelElement element, 
                                                     final ValueProperty property ) 
    {
        final DefaultValueService defaultValueService = element.service( property, DefaultValueService.class );
        
        if( defaultValueService != null )
        {
            final String defaultValue = defaultValueService.value();
            
            if( defaultValue != null )
            {
                return getValueLabel( element, property, defaultValue );
            }
        }
        
        return null;
    }
    
    public final static String getValueLabel( final IModelElement element, 
                                              final ValueProperty property,
                                              final String value ) 
    {
        String valueLabel = value;
        
        if( property.isOfType( Enum.class ) )
        {
            final EnumValueType enumValueType = new EnumValueType( property.getTypeClass() );
            
            for( Enum<?> item : enumValueType.getItems() )
            {
                if( item.toString().equals( value ) )
                {
                    valueLabel = enumValueType.getLabel( item, true, CapitalizationType.NO_CAPS, false );
                    break;
                }
            }
        }
        else
        {
            final ValueKeyword keyword = property.getKeyword( value );
            
            if( keyword != null )
            {
                valueLabel = keyword.toDisplayString();
            }
            else if( property.hasAnnotation( NamedValues.class ) ) 
            {
                final LocalizationService localization = property.getLocalizationService();
                
                for( NamedValue x : property.getAnnotation( NamedValues.class ).namedValues() )
                {
                    if( value.equals( x.value() ) ) 
                    {
                        String namedValueLabel = localization.text( x.label(), CapitalizationType.NO_CAPS, false );
                        valueLabel = namedValueLabel + " (" + x.value() + ")";
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
            valueLabel = "\"" + value + "\"";
        }
        
        return valueLabel;
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
