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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.FoldingXmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.internal.MasterDetailsTreeNodeRefMethods;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "content outline node reference" )
@Image( small = "org.eclipse.sapphire.ui/images/objects/part.gif" )
@GenerateImpl

public interface IMasterDetailsTreeNodeRef

    extends IMasterDetailsTreeNodeListEntry
    
{
    ModelElementType TYPE = new ModelElementType( IMasterDetailsTreeNodeRef.class );
    
    // *** Part ***
    
    @Label( standard = "part" )
    @CustomXmlValueBinding( impl = FoldingXmlValueBindingImpl.class, params = "part" )
    
    ValueProperty PROP_PART = new ValueProperty( TYPE, "Part" );
    
    Value<String> getPart();
    void setPart( String value );

    // *** Params ***
    
    @Label( standard = "params" )
    @Type( base = ISapphireParam.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "param", type = ISapphireParam.class ) )
    
    ListProperty PROP_PARAMS = new ListProperty( TYPE, "Params" );
    
    ModelElementList<ISapphireParam> getParams();

    // *** Method : resolve ***
    
    @DelegateImplementation( MasterDetailsTreeNodeRefMethods.class )
    
    IMasterDetailsTreeNodeDef resolve();

}
