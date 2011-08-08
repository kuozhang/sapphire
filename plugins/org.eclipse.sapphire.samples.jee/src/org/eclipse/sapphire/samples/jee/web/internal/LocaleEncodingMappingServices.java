/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.jee.web.internal;

import java.util.Locale;
import java.util.SortedSet;

import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class LocaleEncodingMappingServices
{
    public static final class LocalePossibleValuesService extends PossibleValuesService
    {
        @Override
        protected void fillPossibleValues( final SortedSet<String> values )
        {
            for( Locale locale : Locale.getAvailableLocales() )
            {
                final String localeString = locale.toString();
                int separators = 0;
                
                for( int i = 0, n = localeString.length(); i < n; i++ )
                {
                    if( localeString.charAt( i ) == '_' )
                    {
                        separators++;
                    }
                }
                
                if( separators < 2 )
                {
                    values.add( localeString );
                }
            }
        }
    }
    
}
