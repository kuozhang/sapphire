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

package org.eclipse.sapphire.modeling.validation;

import static org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PathValidation
{
    public static final IStatus validateExtensions( final String fileName,
                                                    final String[] validFileExtensions )
    {
        if( validFileExtensions != null && validFileExtensions.length > 0 )
        {
            final String trimmedFileName = fileName.trim();
            final int lastdot = trimmedFileName.lastIndexOf( '.' );
            final String extension;
            
            if( lastdot == -1 )
            {
                extension = "";
            }
            else
            {
                extension = trimmedFileName.substring( lastdot + 1 );
            }
            
            boolean match = false;
            
            if( extension != null && extension.length() != 0 )
            {
                for( String ext : validFileExtensions )
                {
                    if( extension.equalsIgnoreCase( ext ) )
                    {
                        match = true;
                        break;
                    }
                }
            }
            
            if( ! match )
            {
                final String message;
                
                if( validFileExtensions.length == 1 )
                {
                    message = NLS.bind( Resources.invalidFileExtensionOne, trimmedFileName, validFileExtensions[ 0 ] );
                }
                else if( validFileExtensions.length == 2 )
                {
                    message = NLS.bind( Resources.invalidFileExtensionOne, new String[] { trimmedFileName, validFileExtensions[ 0 ], validFileExtensions[ 1 ] } );
                }
                else
                {
                    final StringBuilder buf = new StringBuilder();
                    
                    for( String ext : validFileExtensions )
                    {
                        if( buf.length() != 0 )
                        {
                            buf.append( ", " );
                        }
                        
                        buf.append( ext );
                    }
                    
                    message = NLS.bind( Resources.invalidFileExtensionMultiple, trimmedFileName, buf.toString() ); 
                }
                
                return new Status( Status.ERROR, PLUGIN_ID, message );
            }
        }
        
        return Status.OK_STATUS;
    }
    
    protected static final class Resources extends NLS
    {
        public static String invalidFileExtensionOne;
        public static String invalidFileExtensionTwo;
        public static String invalidFileExtensionMultiple;
        
        static
        {
            initializeMessages( PathValidation.class.getName(), Resources.class );
        }
    }
    
}
