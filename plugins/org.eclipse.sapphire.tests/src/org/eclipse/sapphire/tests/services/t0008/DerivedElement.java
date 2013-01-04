/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.tests.services.t0008;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface DerivedElement extends BaseElement
{
    ModelElementType TYPE = new ModelElementType( DerivedElement.class );
    
    // *** TestProperty ***
    
    @Service( impl = DerivedValidationService.class )
    
    ValueProperty PROP_TEST_PROPERTY = new ValueProperty( TYPE, BaseElement.PROP_TEST_PROPERTY );
    
}
