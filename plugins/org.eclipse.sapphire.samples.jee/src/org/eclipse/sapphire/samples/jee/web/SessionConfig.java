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

package org.eclipse.sapphire.samples.jee.web;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface SessionConfig extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( SessionConfig.class );
    
    // *** Timeout ***
    
    @Type( base = Integer.class )
    @Label( standard = "timeout (minutes)" )
    @XmlBinding( path = "session-timeout" )
    
    @Documentation
    (
        content = "Defines the default session timeout interval for all sessions created in this " +
                  "web application. The specified timeout must be expressed in a whole number of " +
                  "minutes. If the timeout is 0 or less, the container ensures the default behavior " +
                  "of sessions is never to time out. If not specified, the container must set its " +
                  "default timeout period."
    )
    
    
    ValueProperty PROP_TIMEOUT = new ValueProperty( TYPE, "Timeout" );
    
    Value<Integer> getTimeout();
    void setTimeout( String value );
    void setTimeout( Integer value );
    
}
