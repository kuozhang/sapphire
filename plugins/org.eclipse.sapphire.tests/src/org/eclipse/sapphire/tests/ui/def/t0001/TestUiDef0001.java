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

package org.eclipse.sapphire.tests.ui.def.t0001;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.tests.SapphireTestCase;
import org.eclipse.sapphire.ui.def.IImportDirective;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsEditorPageDef;

/**
 * Tests class resolution via import statements in the Sapphire UI Definition model.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestUiDef0001

    extends SapphireTestCase
    
{
    private TestUiDef0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "UiDef0001" );

        suite.addTest( new TestUiDef0001( "test" ) );
        
        return suite;
    }
    
    public void test()
    {
        final ISapphireUiDef sdef = ISapphireUiDef.TYPE.instantiate();
        final IMasterDetailsEditorPageDef page = sdef.getPartDefs().addNewElement( IMasterDetailsEditorPageDef.class );
        final ISapphireActionHandlerDef handler = page.getActionHandlers().addNewElement( ISapphireActionHandlerDef.class );
        
        IImportDirective imp;
        JavaType type;
        Class<?> cl;
        
        imp = sdef.getImportDirectives().addNewElement();
        imp.setBundle( "org.eclipse.core.resources" );
        imp.getPackages().addNewElement().setName( "org.eclipse.core.resources" );
        
        handler.setImplClass( "IFile" );
        type = handler.getImplClass().resolve();
        assertNotNull( type );
        cl = type.artifact();
        assertNotNull( cl );
        assertEquals( IFile.class, cl );

        imp = sdef.getImportDirectives().addNewElement();
        imp.setBundle( "org.eclipse.sapphire.modeling.xml" );
        imp.getPackages().addNewElement().setName( "org.eclipse.sapphire.modeling.xml.annotations" );
        
        handler.setImplClass( "XmlBinding" );
        type = handler.getImplClass().resolve();
        assertNotNull( type );
        cl = type.artifact();
        assertNotNull( cl );
        assertEquals( XmlBinding.class, cl );

        imp = sdef.getImportDirectives().addNewElement();
        imp.setBundle( "org.eclipse.sapphire.tests" );
        imp.getPackages().addNewElement().setName( "org.eclipse.sapphire.tests.ui.def.t0001" );
        
        handler.setImplClass( "TestClass" );
        type = handler.getImplClass().resolve();
        assertNotNull( type );
        cl = type.artifact();
        assertNotNull( cl );
        assertEquals( TestClass.class, cl );
    }

}
