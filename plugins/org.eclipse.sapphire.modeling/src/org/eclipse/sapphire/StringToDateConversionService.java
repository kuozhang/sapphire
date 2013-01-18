/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcelo Paternostro - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 ******************************************************************************/

package org.eclipse.sapphire;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * ConversionService implementation for String to Date conversions. Can be subclassed to support
 * custom date formats.
 * 
 * @author <a href="marcelo.paternostro@oracle.com">Marcelo Paternostro</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class StringToDateConversionService extends ConversionService<String,Date> 
{    
    private final static List<DateFormat> STANDARD_FORMATS;
    
    static 
    {
        final List<DateFormat> formats = new ArrayList<DateFormat>();
        formats.add( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'.'SSSZ" ) );
        formats.add( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'.'SSS" ) );
        formats.add( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ) );
        formats.add( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm" ) );
        formats.add( new SimpleDateFormat( "yyyy-MM-dd" ) );
        formats.add( new SimpleDateFormat( "HH:mm" ) );
        formats.add( new SimpleDateFormat( "h:mm a" ) );

        DateFormat format = DateFormat.getDateInstance();
        
        if( ! formats.contains( format ) )
        {
            formats.add( format );
        }
        
        format = DateFormat.getTimeInstance();
        
        if( ! formats.contains( format ) ) 
        {
            formats.add( format );
        }
        
        format = DateFormat.getDateTimeInstance();
        
        if( ! formats.contains( format ) ) 
        {
            formats.add(format);
        }
        
        STANDARD_FORMATS = Collections.unmodifiableList(formats);
    };
    
    private List<DateFormat> formats;

    public StringToDateConversionService()
    {
        super( String.class, Date.class );
    }

    @Override
    protected void init()
    {
        final List<String> formatParamNames = new ArrayList<String>();
        
        for( String paramName : params().keySet() )
        {
            if( paramName.startsWith( "format" ) )
            {
                formatParamNames.add( paramName );
            }
        }
        
        if( formatParamNames.isEmpty() )
        {
            this.formats = STANDARD_FORMATS;
        }
        else
        {
            Collections.sort( formatParamNames );
            
            this.formats = new ArrayList<DateFormat>( formatParamNames.size() );
            
            for( String formatParamName : formatParamNames )
            {
                this.formats.add( new SimpleDateFormat( param( formatParamName ) ) );
            }
            
            this.formats = Collections.unmodifiableList( this.formats );
        }
    }

    /**
     * Returns the list of date formats supported by this conversion service. The formats
     * will be tried (in order) when decoding from string.
     * 
     * <p>Subclasses may override to support different formats.</p>
     * 
     * @return the list of date formats supported by this conversion service
     */
    
    protected List<? extends DateFormat> formats()
    {
        return this.formats;
    }
    
    @Override
    public final Date convert( final String string ) 
    {
        for( DateFormat format : formats() )
        {
            try
            {
                return format.parse( string );
            }
            catch( ParseException e ) 
            {
                // Intentionally ignored.
            }
        }
        
        return null;
    }
    
}
