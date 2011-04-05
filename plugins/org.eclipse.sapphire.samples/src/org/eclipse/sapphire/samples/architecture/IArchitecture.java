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

package org.eclipse.sapphire.samples.architecture;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "architecture" )

public interface IArchitecture

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( IArchitecture.class );
    
    // *** Components ***
    
    @Type( base = IComponent.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "component", type = IComponent.class ) )
    
    ListProperty PROP_COMPONENTS = new ListProperty( TYPE, "Components" );
    
    ModelElementList<IComponent> getComponents();
    
}
