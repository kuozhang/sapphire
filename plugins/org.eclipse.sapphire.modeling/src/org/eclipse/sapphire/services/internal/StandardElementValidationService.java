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

package org.eclipse.sapphire.services.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyEnablementEvent;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.PropertyValidationEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardElementValidationService extends ValidationService
{
    @Override
    protected void init()
    {
        final Element element = context( Element.class );
        
        element.attach
        (
            new FilteredListener<PropertyEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyEvent event )
                {
                    if( event instanceof PropertyValidationEvent || event instanceof PropertyEnablementEvent )
                    {
                        broadcast();
                    }
                }
            }
        );
    }

    @Override
    public Status validate()
    {
        final Element element = context( Element.class );
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();
        
        for( Property property : element.properties() )
        {
            if( property.enabled() )
            {
                factory.merge( property.validation() );
            }
        }
        
        return factory.create();
    }

}