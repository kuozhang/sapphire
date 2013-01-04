/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface IDiagramGuidesDef extends IModelElement 
{
    ModelElementType TYPE = new ModelElementType( IDiagramGuidesDef.class);
    
    // *** Visible ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "visible" )
    @DefaultValue( text = "false" )
    @Label( standard = "show guides")
    
    ValueProperty PROP_VISIBLE = new ValueProperty(TYPE, "Visible");
    
    Value<Boolean> isVisible();
    void setVisible( String value );
    void setVisible( Boolean value );

}
