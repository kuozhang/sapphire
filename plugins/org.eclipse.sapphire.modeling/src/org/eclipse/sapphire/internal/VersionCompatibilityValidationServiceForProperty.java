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

package org.eclipse.sapphire.internal;

import org.eclipse.sapphire.Property;

/**
 * An implementation of ValidationService that produces a validation error when a property 
 * is not compatible with the version compatibility target yet contains data. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class VersionCompatibilityValidationServiceForProperty extends VersionCompatibilityValidationService
{
    @Override
    protected Property property()
    {
        return context( Property.class );
    }
    
    @Override
    protected boolean problem()
    {
        return ( super.problem() && ! property().empty() );
    }

}
