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

package org.eclipse.sapphire.tests.modeling.el;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.tests.modeling.el.functions.FunctionTests;
import org.eclipse.sapphire.tests.modeling.el.operators.OperatorTests;
import org.eclipse.sapphire.tests.modeling.el.properties.PropertyTests;
import org.eclipse.sapphire.tests.modeling.el.t0001.TestExpr0001;
import org.eclipse.sapphire.tests.modeling.el.t0002.TestExpr0002;
import org.eclipse.sapphire.tests.modeling.el.t0003.TestExpr0003;
import org.eclipse.sapphire.tests.modeling.el.t0005.TestExpr0005;
import org.eclipse.sapphire.tests.modeling.el.t0007.TestExpr0007;
import org.eclipse.sapphire.tests.modeling.el.t0008.TestExpr0008;
import org.eclipse.sapphire.tests.modeling.el.t0009.TestExpr0009;
import org.eclipse.sapphire.tests.modeling.el.t0010.TestExpr0010;
import org.eclipse.sapphire.tests.modeling.el.t0011.TestExpr0011;
import org.eclipse.sapphire.tests.modeling.el.t0012.TestExpr0012;
import org.eclipse.sapphire.tests.modeling.el.t0013.TestExpr0013;
import org.eclipse.sapphire.tests.modeling.el.t0014.TestExpr0014;
import org.eclipse.sapphire.tests.modeling.el.t0016.TestExpr0016;
import org.eclipse.sapphire.tests.modeling.el.t0017.TestExpr0017;
import org.eclipse.sapphire.tests.modeling.el.t0018.TestExpr0018;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ExpressionLanguageTests

    extends TestCase
    
{
    private ExpressionLanguageTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "ExpressionLanguageTests" );
        
        suite.addTest( new ExpressionLanguageTests( "literalNull" ) );

        for( int i = 1; i <= 2; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "literalBoolean" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 2; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "literalInteger" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 14; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "literalDecimal" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 6; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "literalString" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 2; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "precedence" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 6; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "composite" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 3; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "functions" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 2; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "concat" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 4; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "firstSegment" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 4; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "lastSegment" + String.valueOf( i ) ) );
        }
        
        for( int i = 1; i <= 2; i++ )
        {
            suite.addTest( new ExpressionLanguageTests( "properties" + String.valueOf( i ) ) );
        }
        
        suite.addTest( OperatorTests.suite() );
        suite.addTest( FunctionTests.suite() );
        suite.addTest( PropertyTests.suite() );
        suite.addTest( TestExpr0001.suite() );
        suite.addTest( TestExpr0002.suite() );
        suite.addTest( TestExpr0003.suite() );
        suite.addTest( TestExpr0005.suite() );
        suite.addTest( TestExpr0007.suite() );
        suite.addTest( TestExpr0008.suite() );
        suite.addTest( TestExpr0009.suite() );
        suite.addTest( TestExpr0010.suite() );
        suite.addTest( TestExpr0011.suite() );
        suite.addTest( TestExpr0012.suite() );
        suite.addTest( TestExpr0013.suite() );
        suite.addTest( TestExpr0014.suite() );
        suite.addTest( TestExpr0016.suite() );
        suite.addTest( TestExpr0017.suite() );
        suite.addTest( TestExpr0018.suite() );
        
        return suite;
    }
    
    private void test( final String expr,
                       final Object expected )
    {
        test( expr, expected, new FunctionContext() );
    }
    
    private void test( final String expr,
                       final Object expected,
                       final FunctionContext context )
    {
        final Object actual = ExpressionLanguageParser.parse( expr ).evaluate( context ).value();
        assertEquals( expected, actual );
    }

    public void literalNull()
    {
        test( "${null}", null );
    }
    
    private void literalBoolean( final String literal )
    {
        test( "${" + literal + "}", Boolean.valueOf( literal ) );
    }
    
    public void literalBoolean1()
    {
        literalBoolean( "true" );
    }
    
    public void literalBoolean2()
    {
        literalBoolean( "false" );
    }
    
    private void literalInteger( final String literal )
    {
        test( "${" + literal + "}", new BigInteger( literal ) );
    }
    
    public void literalInteger1()
    {
        literalInteger( "1234567" );
    }
    
    public void literalInteger2()
    {
        literalInteger( "1234567890123456789012345678901234567890" );
    }

    private void literalDecimal( final String literal )
    {
        test( "${" + literal + "}", new BigDecimal( literal ) );
    }

    public void literalDecimal1()
    {
        literalDecimal( "12.34567" );
    }
    
    public void literalDecimal2()
    {
        literalDecimal( ".34567" );
    }
    
    public void literalDecimal3()
    {
        literalDecimal( "12e23" );
    }
    
    public void literalDecimal4()
    {
        literalDecimal( "12e+23" );
    }

    public void literalDecimal5()
    {
        literalDecimal( "12e-23" );
    }
    
    public void literalDecimal6()
    {
        literalDecimal( "12.345e23" );
    }
    
    public void literalDecimal7()
    {
        literalDecimal( "12.345e+23" );
    }

    public void literalDecimal8()
    {
        literalDecimal( "12.345e-23" );
    }
    
    public void literalDecimal9()
    {
        literalDecimal( "12E23" );
    }
    
    public void literalDecimal10()
    {
        literalDecimal( "12E+23" );
    }

    public void literalDecimal11()
    {
        literalDecimal( "12E-23" );
    }
    
    public void literalDecimal12()
    {
        literalDecimal( "12.345E23" );
    }
    
    public void literalDecimal13()
    {
        literalDecimal( "12.345E+23" );
    }

    public void literalDecimal14()
    {
        literalDecimal( "12.345E-23" );
    }

    public void literalString1()
    {
        test( "${\"abcdefg 12345 xyz%%\"}", "abcdefg 12345 xyz%%" );
    }

    public void literalString2()
    {
        test( "${'abcdefg 12345 xyz%%'}", "abcdefg 12345 xyz%%" );
    }

    public void literalString3()
    {
        test( "${\"abcdefg \\\"12345\\\" xyz%%\"}", "abcdefg \"12345\" xyz%%" );
    }

    public void literalString4()
    {
        test( "${\"abcdefg '12345' xyz%%\"}", "abcdefg '12345' xyz%%" );
    }

    public void literalString5()
    {
        test( "${'abcdefg \"12345\" xyz%%'}", "abcdefg \"12345\" xyz%%" );
    }

    public void literalString6()
    {
        test( "${'abcdefg \\'12345\\' xyz%%'}", "abcdefg '12345' xyz%%" );
    }
    
    public void precedence1()
    {
        test( "${3+4*5}", new BigInteger( "23" ) );
    }

    public void precedence2()
    {
        test( "${(3+4)*5}", new BigInteger( "35" ) );
    }
    
    public void composite1()
    {
        test( "abc", "abc" );
    }
    
    public void composite2()
    {
        test( "abc${'def'}", "abcdef" );
    }
    
    public void composite3()
    {
        test( "abc${'def'}ghi", "abcdefghi" );
    }
    
    public void composite4()
    {
        test( "abc${'def'}ghi${'jkl'}", "abcdefghijkl" );
    }
    
    public void composite5()
    {
        test( "abc${5}ghi${6}", "abc5ghi6" );
    }

    public void composite6()
    {
        test( "abc${3*5}ghi${3>5?6.2:7.3}", "abc15ghi7.3" );
    }

    public void functions1()
    {
        final FunctionContext context = new FunctionContext()
        {
            @Override
            public Function function( final String name,
                                      final List<Function> operands )
            {
                Function function = null;
                
                if( name.equals( "eclipse:add" ) )
                {
                    function = new Function()
                    {
                        @Override
                        public String name()
                        {
                            return "eclipse:add";
                        }

                        @Override
                        public FunctionResult evaluate( final FunctionContext context )
                        {
                            return new FunctionResult( this, context )
                            {
                                @Override
                                protected Object evaluate()
                                {
                                    return cast( operand( 0 ), BigInteger.class ).add( cast( operand( 1 ), BigInteger.class ) );
                                }
                            };
                        }
                    };
                }
                else if( name.equals( "subtract" ) )
                {
                    function = new Function()
                    {
                        @Override
                        public String name()
                        {
                            return "subtract";
                        }

                        @Override
                        public FunctionResult evaluate( final FunctionContext context )
                        {
                            return new FunctionResult( this, context )
                            {
                                @Override
                                protected Object evaluate()
                                {
                                    return cast( operand( 0 ), BigInteger.class ).subtract( cast( operand( 1 ), BigInteger.class ) );
                                }
                            };
                        }
                    };
                }
                
                if( function != null )
                {
                    function.init( operands.toArray( new Function[ operands.size() ] ) );
                    return function;
                }
                
                return super.function( name, operands );
            }
        };
        
        test( "${ 3 + eclipse:add( 4, 5 ) + subtract( 6, 7 ) + subtract( eclipse:add( 8 + 9, 10 + 11 ), 12 + 13 ) }", new BigInteger( "24" ), context ); 
    }
    
    public void functions2()
    {
        test( "${ test:factorial( 7 ) }", new BigInteger( "5040" ) );
    }
    
    public void functions3()
    {
        test( "${ test:factorial( 15 + 5 ) }", new BigInteger( "2432902008176640000" ) );
    }

    public void concat1()
    {
        test( "${concat('a','b')}", "ab" );
    }

    public void concat2()
    {
        test( "${concat(2010,'-',12,'-',2)}", "2010-12-2" );
    }

    public void firstSegment1()
    {
        test( "${FirstSegment('abc.def.ghi','.')}", "abc" );
    }

    public void firstSegment2()
    {
        test( "${FirstSegment('abc/def\\\\ghi','\\\\/')}", "abc" );
    }

    public void firstSegment3()
    {
        test( "${FirstSegment('abc','.')}", "abc" );
    }
    
    public void firstSegment4()
    {
        test( "${FirstSegment(null,'.')}", "" );
    }
    
    public void lastSegment1()
    {
        test( "${LastSegment('abc.def.ghi','.')}", "ghi" );
    }

    public void lastSegment2()
    {
        test( "${LastSegment('abc/def\\\\ghi','\\\\/')}", "ghi" );
    }

    public void lastSegment3()
    {
        test( "${LastSegment('abc','.')}", "abc" );
    }
    
    public void lastSegment4()
    {
        test( "${LastSegment(null,'.')}", "" );
    }

    private void properties( final String expr,
                             final Object expected )
    {
        final TestElement root = TestElement.TYPE.instantiate();
        final TestElement child = root.getFooBar().content( true );
        final TestElement grandchild = child.getFooBar().content( true );
        
        root.setIntegerProp( 1 );
        root.setStringProp( "ABC" );
        child.setIntegerProp( 2 );
        child.setStringProp( "DEF" );
        grandchild.setIntegerProp( 3 );
        grandchild.setStringProp( "GHI" );
        
        final FunctionContext context = new ModelElementFunctionContext( root );
        
        test( expr, expected, context );
    }
    
    public void properties1()
    {
        properties( "${ IntegerProp + FooBar.IntegerProp + FooBar.FooBar[ 'IntegerProp' ] }", 6L );
    }
    
    public void properties2()
    {
        properties( "${ StringProp }${ FooBar.StringProp }${ FooBar.FooBar[ 'StringProp' ] }", "ABCDEFGHI" );
    }

}

