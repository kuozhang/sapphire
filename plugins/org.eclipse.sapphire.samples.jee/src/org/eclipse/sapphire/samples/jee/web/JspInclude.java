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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.jee.web.internal.WebContentRelativePathService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "JSP include" )
@GenerateImpl

public interface JspInclude extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( JspInclude.class );
    
    // *** Location ***
    
    @Type( base = Path.class )
    @Label( standard = "location" )
    @Required
    @Service( impl = WebContentRelativePathService.class )
    @XmlBinding( path = "" )
    
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );
    
    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );
    
}
