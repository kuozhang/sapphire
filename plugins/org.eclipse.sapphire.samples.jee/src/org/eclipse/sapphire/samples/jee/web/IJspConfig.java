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
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "JSP configuration" )
@GenerateImpl

public interface IJspConfig extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( IJspConfig.class );
    
    // *** TagLibraries ***
    
    @Type( base = ITagLibrary.class )
    @Label( standard = "tag libraries" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "taglib", type = ITagLibrary.class ) )
    
    @Documentation
    (
        content = "Tag library definitions specify the tag libraries available to the web application. This can " +
                  "be done to override implicit map entries from TLD files and from the container."
    )
    
    ListProperty PROP_TAG_LIBRARIES = new ListProperty( TYPE, "TagLibraries" );
    
    ModelElementList<ITagLibrary> getTagLibraries();
    
    // *** PropertyGroups ***
    
    @Type( base = IJspPropertyGroup.class )
    @Label( standard = "property groups" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "jsp-property-group", type = IJspPropertyGroup.class ) )
    
    @Documentation
    (
        content = "Property groups provide global configuration that should be applied to the specified sets of " +
                  "web application resources. All resources so described are deemed to be JSP files."
    )
    
    ListProperty PROP_PROPERTY_GROUPS = new ListProperty( TYPE, "PropertyGroups" );
    
    ModelElementList<IJspPropertyGroup> getPropertyGroups();
    
}
