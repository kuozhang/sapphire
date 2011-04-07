/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.gallery;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
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

    ImpliedElementProperty PROP_INTEGER_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "IntegerValueGallery" );
    
    IIntegerValueGallery getIntegerValueGallery();
    
    // *** LongIntegerValueGallery ***
    
    @Type( base = ILongIntegerValueGallery.class )
    @XmlBinding( path = "long-integer" )

    ImpliedElementProperty PROP_LONG_INTEGER_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "LongIntegerValueGallery" );
    
    ILongIntegerValueGallery getLongIntegerValueGallery();
    
    // *** BigIntegerValueGallery ***

    @Type( base = IBigIntegerValueGallery.class )
    @XmlBinding( path = "big-integer" )
    
    ImpliedElementProperty PROP_BIG_INTEGER_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "BigIntegerValueGallery" );
    
    IBigIntegerValueGallery getBigIntegerValueGallery();
    
    // *** FloatValueGallery ***

    @Type( base = IFloatValueGallery.class )
    @XmlBinding( path = "float" )
    
    ImpliedElementProperty PROP_FLOAT_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "FloatValueGallery" );
    
    IFloatValueGallery getFloatValueGallery();
    
    // *** DoubleValueGallery ***

    @Type( base = IDoubleValueGallery.class )
    @XmlBinding( path = "double" )
    
    ImpliedElementProperty PROP_DOUBLE_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "DoubleValueGallery" );
    
    IDoubleValueGallery getDoubleValueGallery();
    
    // *** BigDecimalValueGallery ***

    @Type( base = IBigDecimalValueGallery.class )
    @XmlBinding( path = "big-decimal" )
    
    ImpliedElementProperty PROP_BIG_DECIMAL_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "BigDecimalValueGallery" );
    
    IBigDecimalValueGallery getBigDecimalValueGallery();
    
    // *** BooleanValueGallery ***

    @Type( base = IBooleanValueGallery.class )
    @XmlBinding( path = "boolean" )
    
    ImpliedElementProperty PROP_BOOLEAN_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "BooleanValueGallery" );
    
    IBooleanValueGallery getBooleanValueGallery();
    
    // *** EnumValueGallery ***

    @Type( base = IEnumValueGallery.class )
    @XmlBinding( path = "enum" )
    
    ImpliedElementProperty PROP_ENUM_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "EnumValueGallery" );
    
    IEnumValueGallery getEnumValueGallery();
    
    // *** JavaTypeNameValueGallery ***

    @Type( base = IJavaTypeNameValueGallery.class )
    @XmlBinding( path = "java-type-name" )
    
    ImpliedElementProperty PROP_JAVA_TYPE_NAME_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "JavaTypeNameValueGallery" );
    
    IJavaTypeNameValueGallery getJavaTypeNameValueGallery();
    
    // *** CustomValueGallery ***
    
    @Type( base = ICustomValueGallery.class )
    @XmlBinding( path = "custom-value" )
    
    ImpliedElementProperty PROP_CUSTOM_VALUE_GALLERY = new ImpliedElementProperty( TYPE, "CustomValueGallery" );
    
    ICustomValueGallery getCustomValueGallery();
    
    // *** BrowseSupportGallery ***
    
    @Type( base = IBrowseSupportGallery.class )
    @Label( standard = "browse support gallery" )
    @XmlBinding( path = "browse-support" )
    
    ImpliedElementProperty PROP_BROWSE_SUPPORT_GALLERY = new ImpliedElementProperty( TYPE, "BrowseSupportGallery" );
    
    IBrowseSupportGallery getBrowseSupportGallery();
    
    // *** HomogeneousList ***
    
    @Type( base = IListItem.class )
    @Label( standard = "homogeneous list")
    @XmlListBinding( path = "homogeneous-list", mappings = @XmlListBinding.Mapping( element = "item", type = IListItem.class ) )
    
    ListProperty PROP_HOMOGENEOUS_LIST = new ListProperty( TYPE, "HomogeneousList" );
    
    ModelElementList<IListItem> getHomogeneousList();
    
    // *** HomogeneousList of java types ***

    @Type( base = Boolean.class )
    @Label( standard = "show list of java types" )
    @DefaultValue( text = "true" )
    @XmlBinding( path = "show-another" )
    
    ValueProperty PROP_SHOW_ANOTHER = new ValueProperty( TYPE, "ShowAnother" );
    
    Value<Boolean> getShowAnother();
    void setShowAnother( String value );
    void setShowAnother( Boolean value );

    // *** Another HomogeneousList ***
    
    @Type( base = IListItemWithJavaType.class )
    @Label( standard = "homogeneous list of java types")
    @Enablement( expr = "${ ShowAnother }" )
    @XmlListBinding( path = "another-list", mappings = @XmlListBinding.Mapping( element = "another", type = IListItemWithJavaType.class ) )
    
    ListProperty PROP_ANOTHER_LIST = new ListProperty( TYPE, "AnotherList" );
    
    ModelElementList<IListItemWithJavaType> getAnotherList();

    // *** HeterogeneousList ***
    
    @Type( base = IListItem.class, possible = { IListItem.class, IListItemWithInteger.class, IListItemWithEnum.class } )
    @Label( standard = "heterogeneous list" )
    
    @XmlListBinding
    (
        path = "heterogeneous-list", 
        mappings = 
        {
            @XmlListBinding.Mapping( element = "item", type = IListItem.class ),
            @XmlListBinding.Mapping( element = "item-with-integer", type = IListItemWithInteger.class ),
            @XmlListBinding.Mapping( element = "item-with-enum", type = IListItemWithEnum.class )
        }
    )
    
    ListProperty PROP_HETEROGENEOUS_LIST = new ListProperty( TYPE, "HeterogeneousList" );
    
    ModelElementList<IListItem> getHeterogeneousList();

    // *** MultiSelectListGallery ***
    
    @Type( base = IMultiSelectListGallery.class )
    @Label( standard = "multi select list gallery" )
    @XmlBinding( path = "multi-select-list" )
    
    ImpliedElementProperty PROP_MULTI_SELECT_LIST_GALLERY = new ImpliedElementProperty( TYPE, "MultiSelectListGallery" );
    
    IMultiSelectListGallery getMultiSelectListGallery();
    
    // *** ValuePropertyActionsGallery ***
    
    @Type( base = IValuePropertyActionsGallery.class )
    @Label( standard = "value property actions gallery" )
    @XmlBinding( path = "value-property-actions-gallery" )
    
    ImpliedElementProperty PROP_VALUE_PROPERTY_ACTIONS_GALLERY = new ImpliedElementProperty( TYPE, "ValuePropertyActionsGallery" );
    
    IValuePropertyActionsGallery getValuePropertyActionsGallery();

    // *** HelpGallery ***
    
    @Type( base = IHelpGallery.class )
    @XmlBinding( path = "help" )

    ImpliedElementProperty PROP_HELP_GALLERY = new ImpliedElementProperty( TYPE, "HelpGallery" );
    
    IHelpGallery getHelpGallery();

    // *** ExtendedHelpGallery ***
    
    @Type( base = IExtendedHelpGallery.class )
    @XmlBinding( path = "extended-help" )

    ImpliedElementProperty PROP_EXTENDED_HELP_GALLERY = new ImpliedElementProperty( TYPE, "ExtendedHelpGallery" );
    
    IExtendedHelpGallery getExtendedHelpGallery();

    // *** EnablementGallery ***
    
    @Type( base = IEnablementGallery.class )
    @XmlBinding( path = "enablement" )

    ImpliedElementProperty PROP_ENABLEMENT_GALLERY = new ImpliedElementProperty( TYPE, "EnablementGallery" );
    
    IEnablementGallery getEnablementGallery();
    
    // *** RelatedContentGallery ***
    
    @Type( base = IRelatedContentGallery.class )
    @XmlBinding( path = "related-content" )

    ImpliedElementProperty PROP_RELATED_CONTENT_GALLERY = new ImpliedElementProperty( TYPE, "RelatedContentGallery" );
    
    IRelatedContentGallery getRelatedContentGallery();
    
    // *** HtmlContentGallery ***
    
    @Type( base = IHtmlContentGallery.class )
    @XmlBinding( path = "html-content" )

    ImpliedElementProperty PROP_HTML_CONTENT_GALLERY = new ImpliedElementProperty( TYPE, "HtmlContentGallery" );
    
    IHtmlContentGallery getHtmlContentGallery();
    
}
