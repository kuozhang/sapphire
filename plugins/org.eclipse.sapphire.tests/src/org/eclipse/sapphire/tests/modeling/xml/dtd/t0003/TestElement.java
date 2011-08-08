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

package org.eclipse.sapphire.tests.modeling.xml.dtd.t0003;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@XmlRootBinding( elementName = "root", schemaLocation = "TestXmlDtd0003.dtd" )
@GenerateImpl

public interface TestElement extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( TestElement.class );
    
    // *** AAA ***
    
    @Type( base = TestElementChild.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "aaa", type = TestElementChild.class ) )
    
    ListProperty PROP_AAA = new ListProperty( TYPE, "Aaa" );
    
    ModelElementList<TestElementChild> getAaa();
    
    // *** BBB ***
    
    @Type( base = TestElementChild.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "bbb", type = TestElementChild.class ) )
    
    ListProperty PROP_BBB = new ListProperty( TYPE, "Bbb" );
    
    ModelElementList<TestElementChild> getBbb();
    
    // *** CCC ***
    
    @Type( base = TestElementChild.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "ccc", type = TestElementChild.class ) )
    
    ListProperty PROP_CCC = new ListProperty( TYPE, "Ccc" );
    
    ModelElementList<TestElementChild> getCcc();
    
}
