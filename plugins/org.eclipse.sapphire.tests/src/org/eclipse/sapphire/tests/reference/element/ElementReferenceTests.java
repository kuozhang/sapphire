/******************************************************************************
 * Copyright (c) 2014 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.reference.element;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.tests.SapphireTestCase;
import org.junit.Test;

/**
 * Tests {@literal @}ElementReference, ElementReferenceService and the attendant PossibleValuesService implementation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ElementReferenceTests extends SapphireTestCase
{
    @Test
    
    public void testDeclarativeElementReference()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final TestElement.Item a = element.getItemList1().insert();
            a.setName( "a" );
            
            final TestElement.Item b = element.getItemList1().insert();
            b.setName( "b" );
            
            final TestElement.Item c = element.getItemList1().insert();
            c.setName( "c" );
            
            assertValidationOk( element.getDeclarativeReference() );
            
            element.setDeclarativeReference( "a" );
            assertSame( a, element.getDeclarativeReference().target() );
            assertValidationOk( element.getDeclarativeReference() );
            
            element.setDeclarativeReference( "c" );
            assertSame( c, element.getDeclarativeReference().target() );
            assertValidationOk( element.getDeclarativeReference() );
            
            element.setDeclarativeReference( "d" );
            assertNull( element.getDeclarativeReference().target() );
            assertValidationError( element.getDeclarativeReference(), "Could not resolve declarative reference \"d\"" );
            
            final PossibleValuesService possibleValuesService = element.getDeclarativeReference().service( PossibleValuesService.class );
            
            assertNotNull( possibleValuesService );
            assertEquals( set( "a", "b", "c" ), possibleValuesService.values() );
            
            final TestElement.Item d = element.getItemList1().insert();
            d.setName( "d" );
            
            assertEquals( set( "a", "b", "c", "d" ), possibleValuesService.values() );
            assertValidationOk( element.getDeclarativeReference() );
        }
        finally
        {
            element.dispose();
        }
    }

    @Test
    
    public void testCustomElementReference()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final TestElement.Item a = element.getItemList1().insert();
            a.setName( "a" );
            a.setValue( "1" );
            
            final TestElement.Item b = element.getItemList1().insert();
            b.setName( "b" );
            b.setValue( "2" );
            
            final TestElement.Item c = element.getItemList1().insert();
            c.setName( "c" );
            c.setValue( "3" );
            
            final TestElement.Item x = element.getItemList2().insert();
            x.setName( "x" );
            x.setValue( "4" );
            
            final TestElement.Item y = element.getItemList2().insert();
            y.setName( "y" );
            y.setValue( "5" );
            
            final TestElement.Item z = element.getItemList2().insert();
            z.setName( "z" );
            z.setValue( "6" );
            
            assertValidationOk( element.getCustomReference() );
            
            element.setCustomReference( "a" );
            
            assertSame( a, element.getCustomReference().target() );
            assertValidationOk( element.getCustomReference() );
            
            element.setCustomReference( "c" );
            
            assertSame( c, element.getCustomReference().target() );
            assertValidationOk( element.getCustomReference() );
            
            element.setCustomReference( "d" );
            
            assertNull( element.getCustomReference().target() );
            assertValidationError( element.getCustomReference(), "Could not resolve custom reference \"d\"" );
            
            final PossibleValuesService possibleValuesService = element.getCustomReference().service( PossibleValuesService.class );
            
            assertNotNull( possibleValuesService );
            assertEquals( set( "a", "b", "c" ), possibleValuesService.values() );
            
            final TestElement.Item d = element.getItemList1().insert();
            d.setName( "d" );
            d.setValue( "7" );
            
            assertEquals( set( "a", "b", "c", "d" ), possibleValuesService.values() );
            assertValidationOk( element.getCustomReference() );
            
            element.setUseItemList2( true );
            
            assertValidationError( element.getCustomReference(), "Could not resolve custom reference \"d\"" );
            assertEquals( set( "x", "y", "z" ), possibleValuesService.values() );
            
            element.setCustomReference( "y" );
            
            assertSame( y, element.getCustomReference().target() );
            assertValidationOk( element.getCustomReference() );
            
            element.setUseValueAsKey( true );
            
            assertValidationError( element.getCustomReference(), "Could not resolve custom reference \"y\"" );
            assertEquals( set( "4", "5", "6" ), possibleValuesService.values() );
            
            element.setCustomReference( "6" );

            assertSame( z, element.getCustomReference().target() );
            assertValidationOk( element.getCustomReference() );
            
            z.setValue( "z6" );
            
            assertValidationError( element.getCustomReference(), "Could not resolve custom reference \"6\"" );
            assertEquals( set( "4", "5", "z6" ), possibleValuesService.values() );
            
            element.setCustomReference( "z6" );
            
            assertValidationOk( element.getCustomReference() );
        }
        finally
        {
            element.dispose();
        }
    }

    @Test
    
    public void testExternalElementReference()
    {
        final TestElement element = TestElement.TYPE.instantiate();
        
        try
        {
            final TestElement external = TestElement.TYPE.instantiate();
            
            try
            {
                final TestElement.Item a = external.getItemList1().insert();
                a.setName( "a" );
                
                final TestElement.Item b = external.getItemList1().insert();
                b.setName( "b" );
                
                final TestElement.Item c = external.getItemList1().insert();
                c.setName( "c" );
                
                element.getExternalReference().service( ExternalElementReferenceService.class ).list( external.getItemList1() );
                
                assertValidationOk( element.getExternalReference() );
                
                element.setExternalReference( "a" );
                assertSame( a, element.getExternalReference().target() );
                assertValidationOk( element.getExternalReference() );
                
                element.setExternalReference( "c" );
                assertSame( c, element.getExternalReference().target() );
                assertValidationOk( element.getExternalReference() );
                
                element.setExternalReference( "d" );
                assertNull( element.getExternalReference().target() );
                assertValidationError( element.getExternalReference(), "Could not resolve external reference \"d\"" );
                
                final PossibleValuesService possibleValuesService = element.getExternalReference().service( PossibleValuesService.class );
                
                assertNotNull( possibleValuesService );
                assertEquals( set( "a", "b", "c" ), possibleValuesService.values() );
                
                final TestElement.Item d = external.getItemList1().insert();
                d.setName( "d" );
                
                assertEquals( set( "a", "b", "c", "d" ), possibleValuesService.values() );
                assertValidationOk( element.getExternalReference() );
            }
            finally
            {
                external.dispose();
            }
        }
        finally
        {
            element.dispose();
        }
    }

}
