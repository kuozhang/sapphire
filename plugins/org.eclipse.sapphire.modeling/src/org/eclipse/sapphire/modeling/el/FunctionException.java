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

package org.eclipse.sapphire.modeling.el;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * Thrown if a function evaluation fails for any reason.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class FunctionException

    extends RuntimeException
    
{
    private static final long serialVersionUID = 1L;
    
    private final IStatus status;
    
    public FunctionException( final IStatus status )
    {
        super( status.getMessage(), status.getException() );
        
        this.status = status;
    }
    
    public FunctionException( final String message )
    {
        this( new Status( IStatus.ERROR, SapphireModelingFrameworkPlugin.PLUGIN_ID, message ) );
    }
    
    public FunctionException( final Throwable throwable )
    {
        this( createErrorStatus( throwable ) );
    }
    
    public IStatus status()
    {
        return this.status;
    }
    
    public static IStatus createErrorStatus( final Throwable throwable )
    {
        return new Status( IStatus.ERROR, SapphireModelingFrameworkPlugin.PLUGIN_ID, Resources.unexpectedException, throwable );
    }
    
    private static final class Resources extends NLS
    {
        public static String unexpectedException;
        
        static
        {
            initializeMessages( FunctionException.class.getName(), Resources.class );
        }
    }
    
}
