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

package org.eclipse.sapphire.tests.services.t0002;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.services.FileExtensionsService;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests FileExtensionsService and @FileExtensions annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestServices0002

    extends SapphireTestCase
    
{
    private TestServices0002( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestServices0002" );

        suite.addTest( new TestServices0002( "testNoFileExtensionsService" ) );
        suite.addTest( new TestServices0002( "testSingleFileExtension" ) );
        suite.addTest( new TestServices0002( "testMultipleFileExtensions" ) );
        suite.addTest( new TestServices0002( "testFileExtensionsExpr" ) );
        suite.addTest( new TestServices0002( "testCustomFileExtensionsService" ) );
        
        return suite;
    }
    
    public void testNoFileExtensionsService() throws Exception
    {
        final ITestModel model = ITestModel.TYPE.instantiate();
        final FileExtensionsService service = model.service( ITestModel.PROP_FILE_PATH_1, FileExtensionsService.class );
        
        assertNull( service );
    }

    public void testSingleFileExtension() throws Exception
    {
        final ITestModel model = ITestModel.TYPE.instantiate();
        final FileExtensionsService service = model.service( ITestModel.PROP_FILE_PATH_2, FileExtensionsService.class );
        
        assertNotNull( service );
        assertEquals( service.extensions(), list( "png" ) );
    }

    public void testMultipleFileExtensions() throws Exception
    {
        final ITestModel model = ITestModel.TYPE.instantiate();
        final FileExtensionsService service = model.service( ITestModel.PROP_FILE_PATH_3, FileExtensionsService.class );
        
        assertNotNull( service );
        assertEquals( service.extensions(), list( "png", "gif", "jpeg" ) );
    }
    
    public void testFileExtensionsExpr() throws Exception
    {
        final ITestModel model = ITestModel.TYPE.instantiate();
        final FileExtensionsService service = model.service( ITestModel.PROP_FILE_PATH_4, FileExtensionsService.class );
        
        assertNotNull( service );
        
        model.setLossyCompression( true );
        assertEquals( service.extensions(), list( "jpeg" ) );

        model.setLossyCompression( false );
        assertEquals( service.extensions(), list( "png", "gif" ) );
    }
    
    public void testCustomFileExtensionsService() throws Exception
    {
        final ITestModel model = ITestModel.TYPE.instantiate();
        final FileExtensionsService service = model.service( ITestModel.PROP_FILE_PATH_5, FileExtensionsService.class );
        
        assertNotNull( service );
        assertEquals( service.extensions(), list( "avi", "mpeg" ) );
    }
    
}
