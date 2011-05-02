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

package org.eclipse.sapphire.tests.modeling.xml.xsd.t0001;

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

public interface ITestXmlXsd0001Element2

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ITestXmlXsd0001Element2.class );
    
    // *** circle ***
    
    @XmlBinding( path = "circle" )
    
    ValueProperty PROP_CIRCLE = new ValueProperty( TYPE, "Circle" );
    
    Value<String> getCircle();
    void setCircle( String value );
    
    // *** square ***
    
    @XmlBinding( path = "square" )
    
    ValueProperty PROP_SQUARE = new ValueProperty( TYPE, "Square" );
    
    Value<String> getSquare();
    void setSquare( String value );

    // *** Aaa2 ***
    
    @XmlBinding( path = "aaa2" )
    
    ValueProperty PROP_AAA2 = new ValueProperty( TYPE, "Aaa2" );
    
    Value<String> getAaa2();
    void setAaa2( String value );

    // *** Bbb2 ***
    
    @XmlBinding( path = "bbb2" )
    
    ValueProperty PROP_BBB2 = new ValueProperty( TYPE, "Bbb2" );
    
    Value<String> getBbb2();
    void setBbb2( String value );
    
    // *** CCC2 ***
    
    @XmlBinding( path = "ccc2" )
    
    ValueProperty PROP_CCC2 = new ValueProperty( TYPE, "Ccc2" );
    
    Value<String> getCcc2();
    void setCcc2( String value );
    
}
