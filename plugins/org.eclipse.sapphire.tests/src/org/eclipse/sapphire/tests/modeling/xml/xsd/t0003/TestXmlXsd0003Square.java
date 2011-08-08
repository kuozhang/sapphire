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

package org.eclipse.sapphire.tests.modeling.xml.xsd.t0003;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@GenerateImpl

public interface TestXmlXsd0003Square

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( TestXmlXsd0003Square.class );
    
    // *** square1 ***
    
    @XmlBinding( path = "square1" )
    
    ValueProperty PROP_SQUARE1 = new ValueProperty( TYPE, "Square1" );
    
    Value<String> getSquare1();
    void setSquare1( String value );
    
    // *** square2 ***
    
    @XmlBinding( path = "square2" )
    
    ValueProperty PROP_SQUARE2 = new ValueProperty( TYPE, "Square2" );
    
    Value<String> getSquare2();
    void setSquare2( String value );

    // *** square3 ***
    
    @XmlBinding( path = "square3" )
    
    ValueProperty PROP_SQUARE3 = new ValueProperty( TYPE, "Square3" );
    
    Value<String> getSquare3();
    void setSquare3( String value );
}
