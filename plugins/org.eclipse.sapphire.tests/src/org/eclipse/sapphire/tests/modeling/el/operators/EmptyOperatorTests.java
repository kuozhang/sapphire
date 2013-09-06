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

package org.eclipse.sapphire.tests.modeling.el.operators;

import org.junit.Test;

/**
 * Tests for the empty operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EmptyOperatorTests extends AbstractOperatorTests
{
    @Test
    
    public void testEmptyOperator1()
    {
        test( "${ empty null }", true );
    }
    
    @Test

    public void testEmptyOperator2()
    {
        test( "${ empty 5 }", false );
    }
    
    @Test
    
    public void testEmptyOperator3()
    {
        test( "${ empty 'abc' }", false );
    }
    
    @Test
    
    public void testEmptyOperator4()
    {
        test( "${ empty '' }", true );
    }

}

