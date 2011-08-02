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

package org.eclipse.sapphire.samples.jee.environment;

import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "EJB remote reference" )
@GenerateImpl

public interface EjbRemoteRef extends EjbRef
{
    ModelElementType TYPE = new ModelElementType( EjbRemoteRef.class );
    
    // *** HomeInterface ***
    
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "javax.ejb.EJBHome" )
    @XmlBinding( path = "home" )
    
    ValueProperty PROP_HOME_INTERFACE = new ValueProperty( TYPE, EjbRef.PROP_HOME_INTERFACE );
    
    // *** BeanInterface ***
    
    @JavaTypeConstraint( kind = JavaTypeKind.INTERFACE, type = "javax.ejb.EJBObject" )
    @XmlBinding( path = "remote" )
    
    ValueProperty PROP_BEAN_INTERFACE = new ValueProperty( TYPE, EjbRef.PROP_BEAN_INTERFACE );
    
}
