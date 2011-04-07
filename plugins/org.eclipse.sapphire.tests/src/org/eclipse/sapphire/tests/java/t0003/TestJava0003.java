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

package org.eclipse.sapphire.tests.java.t0003;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests resolution of Java type references in the model via ClassLoaderJavaTypeReferenceService.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestJava0003

    extends SapphireTestCase
    
{
    private static final String PACKAGE_NAME = "org.eclipse.sapphire.tests.java.t0003";
    
    private TestJava0003( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Java0003" );

        suite.addTest( new TestJava0003( "test" ) );
        
        return suite;
    }
    
    public void test()
    {
        final ITestElement element = ITestElement.TYPE.instantiate();
        element.setSomeClass( PACKAGE_NAME + ".TestClass" );
        
        final JavaType type = element.getSomeClass().resolve();

        assertNotNull( type );
    }

}
