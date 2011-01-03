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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface ISapphireSectionDef

    extends ISapphireCompositeDef
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireSectionDef.class );
    
    // *** Label ***
    
    @Label( standard = "label" )
    @Localizable
    @XmlBinding( path = "label" )
    
    ValueProperty PROP_LABEL = new ValueProperty( TYPE, "Label" );
    
    Value<String> getLabel();
    void setLabel( String label );
    
    // *** Description ***
    
    @Label( standard = "description" )
    @Localizable
    @XmlValueBinding( path = "description", collapseWhitespace = true )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String description );
    
    // *** VisibleWhenConditionClass ***
    
    @Reference( target = Class.class )
    @Label( standard = "visible when condition class" )
    @XmlBinding( path = "visible-when/condition/class" )
    
    ValueProperty PROP_VISIBLE_WHEN_CONDITION_CLASS = new ValueProperty( TYPE, "VisibleWhenConditionClass" );
    
    ReferenceValue<Class<?>> getVisibleWhenConditionClass();
    void setVisibleWhenConditionClass( String visibleWhenConditionClass );
    
    // *** VisibleWhenConditionParameter ***
    
    @Label( standard = "visible when condition parameter" )
    @XmlBinding( path = "visible-when/condition/parameter" )
    
    ValueProperty PROP_VISIBLE_WHEN_CONDITION_PARAMETER = new ValueProperty( TYPE, "VisibleWhenConditionParameter" );
    
    Value<String> getVisibleWhenConditionParameter();
    void setVisibleWhenConditionParameter( String visibleWhenConditionParameter );
    
}
