/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.gallery;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "gallery" )

public interface IGallery

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( IGallery.class );
    
    // *** IntegerValueGallery ***
    
    @Type( base = IIntegerValueGallery.class )
    @XmlBinding( path = "integer" )

    ElementProperty PROP_INTEGER_VALUE_GALLERY = new ElementProperty( TYPE, "IntegerValueGallery" );
    
    ModelElementHandle<IIntegerValueGallery> getIntegerValueGallery();
    
    // *** LongIntegerValueGallery ***
    
    @Type( base = ILongIntegerValueGallery.class )
    @XmlBinding( path = "long-integer" )

    ElementProperty PROP_LONG_INTEGER_VALUE_GALLERY = new ElementProperty( TYPE, "LongIntegerValueGallery" );
    
    ModelElementHandle<ILongIntegerValueGallery> getLongIntegerValueGallery();
    
    // *** BigIntegerValueGallery ***

    @Type( base = IBigIntegerValueGallery.class )
    @XmlBinding( path = "big-integer" )
    
    ElementProperty PROP_BIG_INTEGER_VALUE_GALLERY = new ElementProperty( TYPE, "BigIntegerValueGallery" );
    
    ModelElementHandle<IBigIntegerValueGallery> getBigIntegerValueGallery();
    
    // *** FloatValueGallery ***

    @Type( base = IFloatValueGallery.class )
    @XmlBinding( path = "float" )
    
    ElementProperty PROP_FLOAT_VALUE_GALLERY = new ElementProperty( TYPE, "FloatValueGallery" );
    
    ModelElementHandle<IFloatValueGallery> getFloatValueGallery();
    
    // *** DoubleValueGallery ***

    @Type( base = IDoubleValueGallery.class )
    @XmlBinding( path = "double" )
    
    ElementProperty PROP_DOUBLE_VALUE_GALLERY = new ElementProperty( TYPE, "DoubleValueGallery" );
    
    ModelElementHandle<IDoubleValueGallery> getDoubleValueGallery();
    
    // *** BigDecimalValueGallery ***

    @Type( base = IBigDecimalValueGallery.class )
    @XmlBinding( path = "big-decimal" )
    
    ElementProperty PROP_BIG_DECIMAL_VALUE_GALLERY = new ElementProperty( TYPE, "BigDecimalValueGallery" );
    
    ModelElementHandle<IBigDecimalValueGallery> getBigDecimalValueGallery();
    
    // *** EnumValueGallery ***

    @Type( base = IEnumValueGallery.class )
    @XmlBinding( path = "enum" )
    
    ElementProperty PROP_ENUM_VALUE_GALLERY = new ElementProperty( TYPE, "EnumValueGallery" );
    
    ModelElementHandle<IEnumValueGallery> getEnumValueGallery();
    
    // *** JavaTypeNameValueGallery ***

    @Type( base = IJavaTypeNameValueGallery.class )
    @XmlBinding( path = "java-type-name" )
    
    ElementProperty PROP_JAVA_TYPE_NAME_VALUE_GALLERY = new ElementProperty( TYPE, "JavaTypeNameValueGallery" );
    
    ModelElementHandle<IJavaTypeNameValueGallery> getJavaTypeNameValueGallery();
    
    // *** BrowseSupportGallery ***
    
    @Type( base = IBrowseSupportGallery.class )
    @Label( standard = "browse support gallery" )
    @XmlBinding( path = "browse-support" )
    
    ElementProperty PROP_BROWSE_SUPPORT_GALLERY = new ElementProperty( TYPE, "BrowseSupportGallery" );
    
    ModelElementHandle<IBrowseSupportGallery> getBrowseSupportGallery();

    // *** MultiSelectListGallery ***
    
    @Type( base = IMultiSelectListGallery.class )
    @Label( standard = "multi select list gallery" )
    @XmlBinding( path = "multi-select-list" )
    
    ElementProperty PROP_MULTI_SELECT_LIST_GALLERY = new ElementProperty( TYPE, "MultiSelectListGallery" );
    
    ModelElementHandle<IMultiSelectListGallery> getMultiSelectListGallery();
    
    // *** ValuePropertyActionsGallery ***
    
    @Type( base = IValuePropertyActionsGallery.class )
    @Label( standard = "value property actions gallery" )
    @XmlBinding( path = "value-property-actions-gallery" )
    
    ElementProperty PROP_VALUE_PROPERTY_ACTIONS_GALLERY = new ElementProperty( TYPE, "ValuePropertyActionsGallery" );
    
    ModelElementHandle<IValuePropertyActionsGallery> getValuePropertyActionsGallery();

    // *** IHelpGallery ***
    
    @Type( base = IHelpGallery.class )
    @XmlBinding( path = "help" )

    ElementProperty PROP_HELP_GALLERY = new ElementProperty( TYPE, "HelpGallery" );
    
    ModelElementHandle<IHelpGallery> getHelpGallery();

    // *** IExtendedHelpGallery ***
    
    @Type( base = IExtendedHelpGallery.class )
    @XmlBinding( path = "extended-help" )

    ElementProperty PROP_EXTENDED_HELP_GALLERY = new ElementProperty( TYPE, "ExtendedHelpGallery" );
    
    ModelElementHandle<IExtendedHelpGallery> getExtendedHelpGallery();

    // *** IEnablementGallery ***
    
    @Type( base = IEnablementGallery.class )
    @XmlBinding( path = "enablement" )

    ElementProperty PROP_ENABLEMENT_GALLERY = new ElementProperty( TYPE, "EnablementGallery" );
    
    ModelElementHandle<IEnablementGallery> getEnablementGallery();
    
}
