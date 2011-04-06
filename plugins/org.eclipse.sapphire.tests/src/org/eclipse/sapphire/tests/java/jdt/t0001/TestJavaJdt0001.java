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

package org.eclipse.sapphire.tests.java.jdt.t0001;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.jdt.internal.JdtJavaTypeService;
import org.eclipse.sapphire.tests.java.jdt.JavaJdtTestCase;

/**
 * Tests correctness of Java type kind determination of JdtJavaTypeService.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestJavaJdt0001

    extends JavaJdtTestCase
    
{
    private TestJavaJdt0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "JavaJdt0001" );

        suite.addTest( new TestJavaJdt0001( "testIsClass" ) );
        suite.addTest( new TestJavaJdt0001( "testIsAbstractClass" ) );
        suite.addTest( new TestJavaJdt0001( "testIsInterface" ) );
        suite.addTest( new TestJavaJdt0001( "testIsAnnotation" ) );
        suite.addTest( new TestJavaJdt0001( "testIsEnum" ) );
        
        return suite;
    }
    
    public void testIsClass() throws Exception
    {
        final IJavaProject project = getJavaProject();
        
        writeJavaSourceFile( "foo.bar", "TestClass", "public class TestClass {}" );
        
        final JdtJavaTypeService service = new JdtJavaTypeService( project );
        final JavaType type = service.find( "foo.bar.TestClass" );
        
        assertNotNull( type );
        assertEquals( JavaTypeKind.CLASS, type.getKind() );
    }

    public void testIsAbstractClass() throws Exception
    {
        final IJavaProject project = getJavaProject();
        
        writeJavaSourceFile( "foo.bar", "TestAbstractClass", "public abstract class TestAbstractClass {}" );
        
        final JdtJavaTypeService service = new JdtJavaTypeService( project );
        final JavaType type = service.find( "foo.bar.TestAbstractClass" );
        
        assertNotNull( type );
        assertEquals( JavaTypeKind.ABSTRACT_CLASS, type.getKind() );
    }

    public void testIsInterface() throws Exception
    {
        final IJavaProject project = getJavaProject();
        
        writeJavaSourceFile( "foo.bar", "TestInterface", "public interface TestInterface {}" );
        
        final JdtJavaTypeService service = new JdtJavaTypeService( project );
        final JavaType type = service.find( "foo.bar.TestInterface" );
        
        assertNotNull( type );
        assertEquals( JavaTypeKind.INTERFACE, type.getKind() );
    }
    
    public void testIsAnnotation() throws Exception
    {
        final IJavaProject project = getJavaProject();
        
        writeJavaSourceFile( "foo.bar", "TestAnnotation", "public @interface TestAnnotation {}" );
        
        final JdtJavaTypeService service = new JdtJavaTypeService( project );
        final JavaType type = service.find( "foo.bar.TestAnnotation" );
        
        assertNotNull( type );
        assertEquals( JavaTypeKind.ANNOTATION, type.getKind() );
    }
    
    public void testIsEnum() throws Exception
    {
        final IJavaProject project = getJavaProject();
        
        writeJavaSourceFile( "foo.bar", "TestEnum", "public enum TestEnum { A, B, C }" );
        
        final JdtJavaTypeService service = new JdtJavaTypeService( project );
        final JavaType type = service.find( "foo.bar.TestEnum" );
        
        assertNotNull( type );
        assertEquals( JavaTypeKind.ENUM, type.getKind() );
    }

}
