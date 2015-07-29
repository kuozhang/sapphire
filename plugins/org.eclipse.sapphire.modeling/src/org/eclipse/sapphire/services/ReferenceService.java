/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementReferenceService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValuePropertyContentEvent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ReferenceService<T> extends DataService<T>
{
    @Override
    protected final void initDataService()
    {
        context( Property.class ).attach
        (
            new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyContentEvent event )
                {
                    // add by tds: record before reference element for compute
                    if (event instanceof ValuePropertyContentEvent
                            && ReferenceService.this instanceof ElementReferenceService) {
                        Element before = null;
                        String propName = event.property().name();
                        Element ele = event.property().element();
                        Property prop = ele.property(propName);
                        if (prop != null && prop instanceof ReferenceValue) {
                            before = (Element) ((ReferenceValue<?, ?>) prop).target();
                        }
                        ((ElementReferenceService) ReferenceService.this).setBefore(before);
                    }
                    //
                    refresh();
                }
            }
        );
        
        initReferenceService();
    }
    
    protected void initReferenceService()
    {
    }
    
    public final T target()
    {
        return data();
    }

}
