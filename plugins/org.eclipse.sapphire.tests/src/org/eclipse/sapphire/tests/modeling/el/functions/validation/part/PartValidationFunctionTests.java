/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.el.functions.validation.part;

import java.util.Collections;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.TestExpr;
import org.eclipse.sapphire.ui.PartFunctionContext;
import org.eclipse.sapphire.ui.SapphireDialogPart;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DialogDef;

/**
 * Tests Validation function for parts.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PartValidationFunctionTests extends TestExpr
{
    private PartValidationFunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "PartValidationFunctionTests" );

        suite.addTest( new PartValidationFunctionTests( "testPartValidationFunction" ) );
        
        return suite;
    }
    
    public void testPartValidationFunction()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final DefinitionLoader.Reference<DialogDef> definition = DefinitionLoader.sdef( PartValidationFunctionTests.class ).dialog();
            final SapphirePart part = new SapphireDialogPart();
            
            try
            {
                part.init( null, element, definition.resolve(), Collections.<String,String>emptyMap() );
                
                final FunctionResult fr = ExpressionLanguageParser.parse( "${ Part.Validation }" ).evaluate( new PartFunctionContext( part, element ) );
                
                try
                {
                    assertInstanceOf( fr.value(), Status.class );
                    assertEquals( Status.Severity.ERROR, ( (Status) fr.value() ).severity() );
                    
                    element.setValue( "abc" );
                    
                    assertInstanceOf( fr.value(), Status.class );
                    assertEquals( Status.Severity.OK, ( (Status) fr.value() ).severity() );
                }
                finally
                {
                    fr.dispose();
                }
                
            }
            finally
            {
                part.dispose();
            }
        }
        finally
        {
            element.dispose();
        }
    }

}
