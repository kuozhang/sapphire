/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [344015] Insertion order lost if xsd includes another xsd
 *               [337232] Certain schema causes elements to be out of order in corresponding xml files
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.xml.xsd.t0001;

import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.modeling.xml.schema.XmlContentModel;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchema;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchemasCache;
import org.eclipse.sapphire.modeling.xml.schema.XmlElementDefinition;
import org.eclipse.sapphire.modeling.xml.schema.XmlSequenceGroup;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests handling of XML Schema redefine directive.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestXmlXsd0001

    extends SapphireTestCase
    
{
	private final static String SCHEMA_LOCATION = "http://www.eclipse.org/sapphire/tests/xml/xsd/0001";
	
    private TestXmlXsd0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "XmlXsd0001" );

        suite.addTest( new TestXmlXsd0001( "testSchemaParsing" ) );
        suite.addTest( new TestXmlXsd0001( "testChildSchemaParsing" ) );
        suite.addTest( new TestXmlXsd0001( "testInsertOrder" ) );
        
        return suite;
    }
    
    public void testSchemaParsing() throws Exception
    {
        final XmlDocumentSchema schema = XmlDocumentSchemasCache.getSchema( SCHEMA_LOCATION, null );
        
        final XmlElementDefinition rootElementDef = schema.getElement( "root" );
        final XmlSequenceGroup rootContentModel = (XmlSequenceGroup) rootElementDef.getContentModel();
        final List<XmlContentModel> nestedContent = rootContentModel.getNestedContent();
        
        assertEquals( 6, nestedContent.size() );
        assertEquals( "aaa", ( (XmlElementDefinition) nestedContent.get( 0 ) ).getName().getLocalPart() );
        assertEquals( "bbb", ( (XmlElementDefinition) nestedContent.get( 1 ) ).getName().getLocalPart() );
        assertEquals( "ccc", ( (XmlElementDefinition) nestedContent.get( 2 ) ).getName().getLocalPart() );
        assertEquals( "ddd", ( (XmlElementDefinition) nestedContent.get( 3 ) ).getName().getLocalPart() );
    }
    
    public void testChildSchemaParsing() throws Exception
    {
        final XmlDocumentSchema schema = XmlDocumentSchemasCache.getSchema( SCHEMA_LOCATION, null );
        
        final XmlElementDefinition rootElementDef = schema.getElement( "root" );
        final XmlSequenceGroup rootContentModel = (XmlSequenceGroup) rootElementDef.getContentModel();
        final XmlSequenceGroup childContentModel = (XmlSequenceGroup)rootContentModel.findChildElementContentModel(new QName( SCHEMA_LOCATION, "element2" ));
        final List<XmlContentModel> childNestedContent = childContentModel.getNestedContent();
        assertEquals( 4, childNestedContent.size() );
        assertEquals( "shape", ( (XmlElementDefinition) childNestedContent.get( 0 ) ).getName().getLocalPart() );
        assertEquals( "aaa2", ( (XmlElementDefinition) childNestedContent.get( 1 ) ).getName().getLocalPart() );
        assertEquals( "bbb2", ( (XmlElementDefinition) childNestedContent.get( 2 ) ).getName().getLocalPart() );
        assertEquals( "ccc2", ( (XmlElementDefinition) childNestedContent.get( 3 ) ).getName().getLocalPart() );
        
        final XmlContentModel circleContentModel = childContentModel.findChildElementContentModel(new QName( SCHEMA_LOCATION, "circle" ));
        assertNotNull(circleContentModel);
        final XmlContentModel squareContentModel = childContentModel.findChildElementContentModel(new QName( SCHEMA_LOCATION, "square" ));
        assertNotNull(squareContentModel);
    }

    public void testInsertOrder() throws Exception
    {
        final ByteArrayResourceStore resourceStore = new ByteArrayResourceStore();
        final ITestXmlXsd0001ModelRoot model = ITestXmlXsd0001ModelRoot.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( resourceStore ) ) );
        
        model.setDdd( "ddd" );
        model.setCcc( "ccc" );
        model.setBbb( "bbb" );
        model.setAaa( "aaa" );
        final ITestXmlXsd0001Element2 element2 = model.getElement2();
        element2.setCcc2("ccc2");
        element2.setBbb2( "bbb2" );
        element2.setCircle( "circle" );
        element2.setAaa2( "aaa2" );
        element2.setSquare( "square" );
        
        model.resource().save();
        
        final String result = new String( resourceStore.getContents(), "UTF-8" );
        
        assertEqualsIgnoreNewLineDiffs( loadResource( "0001.txt" ), result );
    }
    
}
