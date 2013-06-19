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

import java.util.SortedSet;

import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.FactsService;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;

/**
 * Creates fact statements about property's read-only state by using semantical information 
 * specified by @ReadOnly annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ReadOnlyPropertyFactsService extends FactsService
{
    @Override
    protected void facts( final SortedSet<String> facts )
    {
        facts.add( Resources.statement );
    }
    
    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            return context.find( PropertyDef.class ).hasAnnotation( ReadOnly.class );
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String statement;
        
        static
        {
            initializeMessages( ReadOnlyPropertyFactsService.class.getName(), Resources.class );
        }
    }
    
}
