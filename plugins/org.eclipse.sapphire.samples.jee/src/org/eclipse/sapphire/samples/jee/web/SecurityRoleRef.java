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
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "security role reference" )
@GenerateImpl

public interface SecurityRoleRef extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( SecurityRoleRef.class );

    // *** Role ***
    
    @Label( standard = "role" )
    @Required
    @XmlBinding( path = "role-name" )
    
    @Documentation
    (
        content = "Used as the parameter to the EJBContext.isCallerInRole() method or HttpServletRequest.isUserInRole() method."
    )
    
    ValueProperty PROP_ROLE = new ValueProperty( TYPE, "Role" );
    
    Value<String> getRole();
    void setRole( String value );
    
    // *** RoleLink ***
    
    @Label( standard = "role link" )
    @PossibleValues( property = "/SecurityRoles/Name" )
    @XmlBinding( path = "role-link" )
    
    @Documentation
    (
        content = "References a defined security role."
    )
    
    ValueProperty PROP_ROLE_LINK = new ValueProperty( TYPE, "RoleLink" );
    
    Value<String> getRoleLink();
    void setRoleLink( String value );
    
    // *** Description ***
    
    @Label( standard = "description" )
    @LongString
    @XmlBinding( path = "description" )
    
    ValueProperty PROP_DESCRIPTION = new ValueProperty( TYPE, "Description" );
    
    Value<String> getDescription();
    void setDescription( String value );

}
