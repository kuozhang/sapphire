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

package org.eclipse.sapphire.samples.address.internal;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.sapphire.samples.address.Address;
import org.eclipse.sapphire.samples.zipcodes.ZipCodeRepository;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CityNamePossibleValuesService extends PossibleValuesService
{
    @Override
    protected void fillPossibleValues( final Set<String> values )
    {
        final Address address = context( Address.class );
        
        final String zipCode = address.getZipCode().text();
        final String stateCode = address.getState().text();
        
        values.addAll( ZipCodeRepository.getCities( zipCode, stateCode ) );
    }

    @Override
    public String getInvalidValueMessage( final String invalidValue )
    {
        return MessageFormat.format( "\"{0}\" is not a valid city for the specified state and ZIP code.", invalidValue );
    }

    @Override
    public boolean isCaseSensitive()
    {
        return false;
    }

}
