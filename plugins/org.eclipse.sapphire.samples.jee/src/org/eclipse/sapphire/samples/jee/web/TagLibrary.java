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
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.jee.web.internal.WebContentRelativePathService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "tag library" )
@GenerateImpl

public interface TagLibrary extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( TagLibrary.class );

    // *** Uri ***
    
    @Label( standard = "URI" )
    @Required
    @NoDuplicates
    @XmlBinding( path = "taglib-uri" )
    
    @Documentation
    (
        content = "The tag library URI identifies the tag library in the web application. Either absolute " +
                  "or relative URI can be used. No two tag libraries in a given web application should " +
                  "share the same URI."
    )
    
    ValueProperty PROP_URI = new ValueProperty( TYPE, "Uri" );
    
    Value<String> getUri();
    void setUri( String value );
    
    // *** Location ***
    
    @Type( base = Path.class )
    @Label( standard = "location" )
    @Required
    @Service( impl = WebContentRelativePathService.class )
    @XmlBinding( path = "taglib-location" )
    
    @Documentation
    (
        content = "The tag library location is a path (relative to the root of the web application) " +
                  "where to find the tag library description file."
    )
    
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );
    
    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );
    
}
