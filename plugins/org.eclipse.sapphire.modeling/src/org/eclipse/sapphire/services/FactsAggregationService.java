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

package org.eclipse.sapphire.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.ModelPropertyService;

/**
 * Aggregates the data from all applicable facts services in order to produce a single list of facts. A fact
 * is a short statement describing semantics.
 * 
 * <p>An implementation of this service is provided with Sapphire. This service is not intended to
 * be implemented by adopters.</p>
 * 
 * @since 0.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class FactsAggregationService extends ModelPropertyService
{
    public final List<String> facts()
    {
        final List<String> facts = new ArrayList<String>();
        
        for( FactsService fs : element().services( property(), FactsService.class ) )
        {
            facts.addAll( fs.facts() );
        }
        
        return Collections.unmodifiableList( facts );
    }

}
