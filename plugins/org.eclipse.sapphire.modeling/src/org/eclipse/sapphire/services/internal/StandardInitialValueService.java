/*******************************************************************************
 * Copyright (c) 2011 Accenture Services Pvt Ltd. and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 *******************************************************************************/

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.services.InitialValueService;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;

/**
 * Implementation of {@link InitialValueService} that draws the initial value from @{@link InitialValue} annotation.
 * 
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardInitialValueService extends InitialValueService 
{
    private String text;

    @Override
    protected void init()
    {
        super.init();
        
        final ValueProperty property = context( ValueProperty.class );
        final InitialValue initialValueAnnotation = property.getAnnotation( InitialValue.class );
        this.text = initialValueAnnotation.text();
    }

    @Override
    public String text() 
    {
        return this.text;
    }

    public static final class Factory extends ServiceFactory 
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service ) 
        {

            final ValueProperty property = context.find( ValueProperty.class );

            if( property != null && property.getAnnotation( InitialValue.class ) != null ) 
            {
                return true;
            }
            
            return false;
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service ) 
        {
            return new StandardInitialValueService();
        }
    }

}
