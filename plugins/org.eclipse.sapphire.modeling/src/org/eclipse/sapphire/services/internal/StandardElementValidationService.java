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

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.TransientProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardElementValidationService extends ValidationService
{
    @Override
    public Status validate()
    {
        final IModelElement element = context( IModelElement.class );
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();
        
        for( ModelProperty property : element.properties() )
        {
            if( element.enabled( property ) )
            {
                final Status x;
                
                if( property instanceof ValueProperty )
                {
                    x = element.read( (ValueProperty) property ).validation();
                }
                else if( property instanceof ListProperty )
                {
                    x = element.read( (ListProperty) property ).validation();
                }
                else if( property instanceof ImpliedElementProperty )
                {
                    x = element.read( (ImpliedElementProperty) property ).validation();
                }
                else if( property instanceof ElementProperty )
                {
                    x = element.read( (ElementProperty) property ).validation();
                }
                else if( property instanceof TransientProperty )
                {
                    x = element.read( (TransientProperty) property ).validation();
                }
                else
                {
                    throw new IllegalStateException();
                }
                
                factory.add( x );
            }
        }
        
        return factory.create();
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            return true;
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            return new StandardElementValidationService();
        }
    }

}