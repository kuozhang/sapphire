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

package org.eclipse.sapphire.sdk.extensibility;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;
import org.eclipse.sapphire.ui.def.ISapphireUiExtensionDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "extension", namespace = "http://www.eclipse.org/sapphire/xmlns/extension" )

public interface SapphireExtensionDef

    extends ISapphireUiExtensionDef
    
{
    ModelElementType TYPE = new ModelElementType( SapphireExtensionDef.class );

    // *** Services ***
    
    @Type( base = ServiceDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "service", type = ServiceDef.class ) )
    @Label( standard = "service" )
    
    ListProperty PROP_SERVICES = new ListProperty( TYPE, "Services" );
    
    ModelElementList<ServiceDef> getServices();
    
    // *** Functions ***
    
    @Type( base = FunctionDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "function", type = FunctionDef.class ) )
    @Label( standard = "functions" )
    
    ListProperty PROP_FUNCTIONS = new ListProperty( TYPE, "Functions" );
    
    ModelElementList<FunctionDef> getFunctions();

    // *** TypeCasts ***
    
    @Type( base = TypeCastDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "type-cast", type = TypeCastDef.class ) )
    @Label( standard = "type casts" )
    
    ListProperty PROP_TYPE_CASTS = new ListProperty( TYPE, "TypeCasts" );
    
    ModelElementList<TypeCastDef> getTypeCasts();

}