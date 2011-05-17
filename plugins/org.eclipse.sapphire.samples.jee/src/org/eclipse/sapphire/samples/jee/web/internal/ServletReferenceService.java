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

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import org.eclipse.sapphire.modeling.ReferenceService;
import org.eclipse.sapphire.samples.jee.web.IServlet;
import org.eclipse.sapphire.samples.jee.web.IWebAppConfig;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ServletReferenceService extends ReferenceService
{
    @Override
    public Object resolve( final String reference )
    {
        final IWebAppConfig config = nearest( IWebAppConfig.class );
        
        if( config != null )
        {
            for( IServlet servlet : config.getServlets() )
            {
                if( equal( servlet.getName().getContent(), reference ) )
                {
                    return servlet;
                }
            }
        }
        
        return null;
    }
    
}
