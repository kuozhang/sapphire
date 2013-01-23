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

package org.eclipse.sapphire;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ConversionService implementation for Date to String conversions. Can be subclassed to support
 * a custom date format.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DateToStringConversionService extends ConversionService<Date,String> 
{    
    private final static DateFormat STANDARD_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ" );
    
    private DateFormat format;

    public DateToStringConversionService()
    {
        super( Date.class, String.class );
    }

    @Override
    protected void init()
    {
        final String formatParamString = param( "format" );
        
        if( formatParamString == null )
        {
            this.format = STANDARD_FORMAT;
        }
        else
        {
            this.format = new SimpleDateFormat( formatParamString );
        }
    }

    /**
     * Returns the date format supported by this conversion service.
     * 
     * <p>Subclasses may override to support a different format.</p>
     * 
     * @return the date format supported by this conversion service
     */
    
    public DateFormat format()
    {
        return this.format;
    }
    
    @Override
    public final String convert( final Date date )
    {
        return format().format( date );
    }
    
}
