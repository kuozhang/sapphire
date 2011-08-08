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

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DefaultValueServiceFactory

    extends ServiceFactory
    
{
    @Override
    public boolean applicable( final ServiceContext context,
                               final Class<? extends Service> service )
    {
        return ( context.find( ValueProperty.class ) != null );
    }

    @Override
    public Service create( final ServiceContext context,
                           final Class<? extends Service> service )
    {
        DefaultValueService svc = null;
        final DefaultValue defaultValueAnnotation = context.find( ValueProperty.class ).getAnnotation( DefaultValue.class );
        
        if( defaultValueAnnotation != null )
        {
            svc = new StaticDefaultValueService( defaultValueAnnotation.text() );
        }
        
        if( svc == null )
        {
            svc = new StaticDefaultValueService( null );
        }
        
        return svc;
    }
    
    private static final class StaticDefaultValueService extends DefaultValueService
    {
        private final String value;
        
        public StaticDefaultValueService( final String value )
        {
            this.value = value;
        }

        @Override
        public String getDefaultValue()
        {
            return this.value;
        }
    }
    
}
