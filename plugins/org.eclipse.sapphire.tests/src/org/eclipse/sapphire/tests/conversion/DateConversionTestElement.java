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

package org.eclipse.sapphire.tests.conversion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.sapphire.DateToStringConversionService;
import org.eclipse.sapphire.StringToDateConversionService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface DateConversionTestElement extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( DateConversionTestElement.class );
    
    // *** Date1 ***
    
    @Type( base = Date.class )
    
    ValueProperty PROP_DATE_1 = new ValueProperty( TYPE, "Date1" );
    
    Value<Date> getDate1();
    void setDate1( String value );
    void setDate1( Date value );
    
    // *** Date2 ***
    
    @Type( base = Date.class )
    
    @Services
    (
        {
            @Service
            (
                impl = StringToDateConversionService.class,
                params =
                { 
                    @Service.Param( name = "format-1", value = "yyyy.MM.dd" ), 
                    @Service.Param( name = "format-2", value = "MM/dd/yyyy" )
                },
                overrides = "Sapphire.ConversionService.StringToDate"
            ),
            @Service
            (
                impl = DateToStringConversionService.class,
                params = @Service.Param( name = "format", value = "yyyy.MM.dd" ),
                overrides = "Sapphire.ConversionService.DateToString"
            )
        }
    )

    ValueProperty PROP_DATE_2 = new ValueProperty( TYPE, "Date2" );
    
    Value<Date> getDate2();
    void setDate2( String value );
    void setDate2( Date value );
    
    // *** Date3 ***
    
    final class TestStringToDateConversionService extends StringToDateConversionService
    {
        private final static List<DateFormat> TEST_FORMATS;
        
        static 
        {
            final List<DateFormat> formats = new ArrayList<DateFormat>();
            formats.add( new SimpleDateFormat( "dd.MM.yyyy" ) );
            formats.add( new SimpleDateFormat( "yyyy/MM/dd" ) );
            
            TEST_FORMATS = Collections.unmodifiableList(formats);
        };

        @Override
        protected List<? extends DateFormat> formats()
        {
            return TEST_FORMATS;
        }
        
    }
    
    final class TestDateToStringConversionService extends DateToStringConversionService
    {
        private final static DateFormat TEST_FORMAT = new SimpleDateFormat( "dd.MM.yyyy" );

        @Override
        protected DateFormat format()
        {
            return TEST_FORMAT;
        }
    }
    
    @Type( base = Date.class )
    
    @Services
    (
        {
            @Service
            (
                impl = TestStringToDateConversionService.class,
                overrides = "Sapphire.ConversionService.StringToDate"
            ),
            @Service
            (
                impl = TestDateToStringConversionService.class,
                overrides = "Sapphire.ConversionService.DateToString"
            )
        }
    )

    ValueProperty PROP_DATE_3 = new ValueProperty( TYPE, "Date3" );
    
    Value<Date> getDate3();
    void setDate3( String value );
    void setDate3( Date value );
    
}
