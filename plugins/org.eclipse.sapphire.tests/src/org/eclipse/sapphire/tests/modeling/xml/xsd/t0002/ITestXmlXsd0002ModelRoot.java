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

package org.eclipse.sapphire.tests.modeling.xml.xsd.t0002;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@GenerateImpl

@XmlRootBinding( namespace = "http://www.eclipse.org/sapphire/tests/xml/xsd/0002",
                 elementName = "element" )

public interface ITestXmlXsd0002ModelRoot

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ITestXmlXsd0002ModelRoot.class );
    
    // *** Aaa ***
    
    @XmlBinding( path = "aaa" )
    
    ValueProperty PROP_AAA = new ValueProperty( TYPE, "Aaa" );
    
    Value<String> getAaa();
    void setAaa( String value );
    
    // *** Bbb ***
    
    @XmlBinding( path = "bbb" )
    
    ValueProperty PROP_BBB = new ValueProperty( TYPE, "Bbb" );
    
    Value<String> getBbb();
    void setBbb( String value );
    
    // *** CCC ***
    
    @XmlBinding( path = "ccc" )
    
    ValueProperty PROP_CCC = new ValueProperty( TYPE, "Ccc" );
    
    Value<String> getCcc();
    void setCcc( String value );
    
    // *** ITestXmlXsd0002Element2b ***
    
    @Type( base = ITestXmlXsd0002Element2b.class )
    @XmlBinding( path = "element2" )
    
    ImpliedElementProperty PROP_ELEMENT2 = new ImpliedElementProperty( TYPE, "Element2" );
    
    ITestXmlXsd0002Element2b getElement2();
}
