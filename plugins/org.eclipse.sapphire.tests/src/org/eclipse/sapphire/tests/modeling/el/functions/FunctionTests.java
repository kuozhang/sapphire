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

package org.eclipse.sapphire.tests.modeling.el.functions;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.sapphire.tests.modeling.el.functions.content.ContentFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.enabled.EnabledFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.endswith.EndsWithFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.fragment.FragmentFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.head.HeadFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.index.IndexFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.matches.MatchesFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.message.MessageFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.overloaded.OverloadedFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.parent.part.PartParentFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.part.PartFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.severity.SeverityFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.size.SizeFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.startswith.StartsWithFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.tail.TailFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.text.TextFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.validation.part.PartValidationFunctionTests;
import org.eclipse.sapphire.tests.modeling.el.functions.validation.property.PropertyValidationFunctionTests;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FunctionTests extends TestCase
{
    private FunctionTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "FunctionTests" );
        
        suite.addTest( ContentFunctionTests.suite() );
        suite.addTest( EnabledFunctionTests.suite() );
        suite.addTest( EndsWithFunctionTests.suite() );
        suite.addTest( FragmentFunctionTests.suite() );
        suite.addTest( HeadFunctionTests.suite() );
        suite.addTest( IndexFunctionTests.suite() );
        suite.addTest( MatchesFunctionTests.suite() );
        suite.addTest( MessageFunctionTests.suite() );
        suite.addTest( OverloadedFunctionTests.suite() );
        suite.addTest( PartParentFunctionTests.suite() );
        suite.addTest( PartFunctionTests.suite() );
        suite.addTest( SeverityFunctionTests.suite() );
        suite.addTest( SizeFunctionTests.suite() );
        suite.addTest( StartsWithFunctionTests.suite() );
        suite.addTest( TailFunctionTests.suite() );
        suite.addTest( TextFunctionTests.suite() );
        suite.addTest( PartValidationFunctionTests.suite() );
        suite.addTest( PropertyValidationFunctionTests.suite() );
        
        return suite;
    }

}

