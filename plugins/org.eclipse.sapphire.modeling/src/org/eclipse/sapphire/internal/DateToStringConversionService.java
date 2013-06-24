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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.sapphire.ConversionService;

/**
 * ConversionService implementation for Date to String conversions.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DateToStringConversionService extends ConversionService<Date,String> 
{    
    private final static DateFormat FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ" );
    
    public DateToStringConversionService()
    {
        super( Date.class, String.class );
    }

    @Override
    public final String convert( final Date date )
    {
        return FORMAT.format( date );
    }
    
}
