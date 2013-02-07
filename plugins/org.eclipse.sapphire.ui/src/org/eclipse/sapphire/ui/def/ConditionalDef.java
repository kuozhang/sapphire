/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlValueBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "if" )
@XmlBinding( path = "if" )

public interface ConditionalDef extends FormComponentDef
{
    ModelElementType TYPE = new ModelElementType( ConditionalDef.class );
    
    // *** Condition ***
    
    @Type( base = Function.class )
    @Label( standard = "condition" )
    @Required
    @XmlValueBinding( path = "condition" )
    
    ValueProperty PROP_CONDITION = new ValueProperty( TYPE, "Condition" );
    
    Value<Function> getCondition();
    void setCondition( String value );
    void setCondition( Function value );
    
    // *** ThenContent ***
    
    @Type
    ( 
        base = PartDef.class,
        possible = 
        { 
            PropertyEditorDef.class, 
            LineSeparatorDef.class,
            WhitespaceSeparatorDef.class,
            ISapphireLabelDef.class,
            ISapphireGroupDef.class,
            ISapphireWithDirectiveDef.class,
            ConditionalDef.class,
            CompositeDef.class,
            ActuatorDef.class,
            ISapphireCustomPartDef.class,
            ISapphireStaticTextFieldDef.class,
            PageBookExtDef.class,
            TabGroupDef.class,
            HtmlPanelDef.class,
            IFormPartInclude.class,
            FormDef.class
        }
    )
                      
    @XmlListBinding( path = "then" )
                             
    ListProperty PROP_THEN_CONTENT = new ListProperty( TYPE, "ThenContent" );
    
    ModelElementList<PartDef> getThenContent();
    
    // *** ElseContent ***
    
    @Type
    ( 
        base = PartDef.class,
        possible = 
        { 
            PropertyEditorDef.class, 
            LineSeparatorDef.class,
            WhitespaceSeparatorDef.class,
            ISapphireLabelDef.class,
            ISapphireGroupDef.class,
            ISapphireWithDirectiveDef.class,
            ConditionalDef.class,
            CompositeDef.class,
            ActuatorDef.class,
            ISapphireCustomPartDef.class,
            ISapphireStaticTextFieldDef.class,
            PageBookExtDef.class,
            TabGroupDef.class,
            HtmlPanelDef.class,
            IFormPartInclude.class,
            FormDef.class
        }
    )
                      
    @XmlListBinding( path = "else" )
                             
    ListProperty PROP_ELSE_CONTENT = new ListProperty( TYPE, "ElseContent" );
    
    ModelElementList<PartDef> getElseContent();
    
}
