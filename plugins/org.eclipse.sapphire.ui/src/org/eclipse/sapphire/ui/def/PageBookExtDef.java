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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.internal.PageBookPartDefControlMethodBinding;
import org.eclipse.sapphire.ui.def.internal.PageBookPartDefControlPropertyBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "page book" )
@XmlBinding( path = "switching-panel" )

public interface PageBookExtDef extends PageBookDef
{
    ElementType TYPE = new ElementType( PageBookExtDef.class );
    
    // *** ControlMethod ***
    
    @Type( base = PageBookPartControlMethod.class )
    @Label( standard = "control method" )
    @Required
    @CustomXmlValueBinding( impl = PageBookPartDefControlMethodBinding.class )
    
    ValueProperty PROP_CONTROL_METHOD = new ValueProperty( TYPE, "ControlMethod" );
    
    Value<PageBookPartControlMethod> getControlMethod();
    void setControlMethod( String controlMethod );
    void setControlMethod( PageBookPartControlMethod controlMethod );
    
    // *** ControlProperty ***
    
    @Label( standard = "control property" )
    @Required
    @CustomXmlValueBinding( impl = PageBookPartDefControlPropertyBinding.class )
    
    ValueProperty PROP_CONTROL_PROPERTY = new ValueProperty( TYPE, "ControlProperty" );
    
    Value<String> getControlProperty();
    void setControlProperty( String controlProperty );
    
}
