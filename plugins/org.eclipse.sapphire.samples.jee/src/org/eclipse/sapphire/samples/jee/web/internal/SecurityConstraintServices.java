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

package org.eclipse.sapphire.samples.jee.web.internal;

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.modeling.xml.StandardXmlListBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.samples.jee.web.SecurityConstraint;
import org.eclipse.sapphire.samples.jee.web.SecurityRoleRef2;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SecurityConstraintServices
{
    private static final String ANY_ROLE_WILDCARD = "*";

    private static final String EL_AUTH_CONSTRAINT = "auth-constraint";
    private static final String EL_ROLE_NAME = "role-name";
    private static final String MC_METHOD = "method";
    private static final String MC_METHOD_LISTED = "listed";
    
    public static final class RolesSpecificationMethodBinding extends XmlValueBindingImpl
    {
        private ValueSerializationService valueSerializationService;
        
        @Override
        public void init( final IModelElement element,
                          final ModelProperty property,
                          final String[] params )
        {
            super.init( element, property, params );
            
            this.valueSerializationService = element.service( property, ValueSerializationService.class );
        }

        @Override
        public String read()
        {
            final XmlElement elSecurityConstraint = xml();
            final XmlElement elAuthConstraint = elSecurityConstraint.getChildElement( EL_AUTH_CONSTRAINT, false );
            
            if( elAuthConstraint != null )
            {
                final List<XmlElement> elRoleNameList = elAuthConstraint.getChildElements( EL_ROLE_NAME );
                final int elRoleNameCount = elRoleNameList.size();
                final boolean hasMetaComment = ( elAuthConstraint.getMetaComment( MC_METHOD, false ) != null );
                
                if( elRoleNameCount > 0 )
                {
                    if( elRoleNameCount == 1 && elRoleNameList.get( 0 ).getText().equals( ANY_ROLE_WILDCARD ) && ! hasMetaComment )
                    {
                        return this.valueSerializationService.encode( SecurityConstraint.RolesSpecificationMethod.ANY );
                    }
                    else
                    {
                        return this.valueSerializationService.encode( SecurityConstraint.RolesSpecificationMethod.LISTED );
                    }
                }
                else if( hasMetaComment )
                {
                    return this.valueSerializationService.encode( SecurityConstraint.RolesSpecificationMethod.LISTED );
                }
            }
            
            return null;
        }

        @Override
        public void write( final String value )
        {
            SecurityConstraint.RolesSpecificationMethod method 
                = (SecurityConstraint.RolesSpecificationMethod) this.valueSerializationService.decode( value );
            
            if( method == null )
            {
                method = SecurityConstraint.RolesSpecificationMethod.NONE;
            }
            
            final XmlElement elSecurityConstraint = xml();

            if( method == SecurityConstraint.RolesSpecificationMethod.NONE )
            {
                final XmlElement elAuthConstraint = elSecurityConstraint.getChildElement( EL_AUTH_CONSTRAINT, false );
                
                if( elAuthConstraint != null )
                {
                    elAuthConstraint.remove();
                }
            }
            else
            {
                final XmlElement elAuthConstraint = elSecurityConstraint.getChildElement( EL_AUTH_CONSTRAINT, true );
                
                if( method == SecurityConstraint.RolesSpecificationMethod.ANY )
                {
                    boolean wroteWildcard = false;
                    
                    for( XmlElement elRoleName : elAuthConstraint.getChildElements( EL_ROLE_NAME ) )
                    {
                        if( wroteWildcard )
                        {
                            elRoleName.remove();
                        }
                        else
                        {
                            elRoleName.setText( ANY_ROLE_WILDCARD );
                            wroteWildcard = true;
                        }
                    }
                    
                    if( ! wroteWildcard )
                    {
                        final XmlElement elRoleName = elAuthConstraint.addChildElement( EL_ROLE_NAME );
                        elRoleName.setText( ANY_ROLE_WILDCARD );
                    }
                    
                    elAuthConstraint.setMetaCommentText( MC_METHOD, null );
                }
                else
                {
                    final List<XmlElement> elRoleNameList = elAuthConstraint.getChildElements( EL_ROLE_NAME );
                    final int elRoleNameCount = elRoleNameList.size();
                    
                    if( elRoleNameCount == 1 )
                    {
                        final XmlElement elRoleName = elRoleNameList.get( 0 );
                        
                        if( elRoleName.getText().trim().equals( ANY_ROLE_WILDCARD ) )
                        {
                            elAuthConstraint.setMetaCommentText( MC_METHOD, MC_METHOD_LISTED );
                            elRoleName.remove();
                        }
                    }
                    else if( elRoleNameCount == 0 )
                    {
                        elAuthConstraint.setMetaCommentText( MC_METHOD, MC_METHOD_LISTED );
                    }
                    else
                    {
                        elAuthConstraint.setMetaCommentText( MC_METHOD, null );
                    }
                }
            }
        }
        
        @Override
        public XmlNode getXmlNode()
        {
            return xml().getChildElement( EL_AUTH_CONSTRAINT, false );
        }
    }
    
    public static final class RolesBinding extends StandardXmlListBindingImpl
    {
        @Override
        protected void initBindingMetadata( final IModelElement element,
                                            final ModelProperty property,
                                            final String[] params )
        {
            final XmlNamespaceResolver xmlNamespaceResolver = ( (XmlResource) element.resource() ).getXmlNamespaceResolver();
            
            this.path = new XmlPath( EL_AUTH_CONSTRAINT, xmlNamespaceResolver );
            this.modelElementTypes = new ModelElementType[] { SecurityRoleRef2.TYPE };
            this.xmlElementNames = new QName[] { createQualifiedName( EL_ROLE_NAME, xmlNamespaceResolver ) };
        }

        @Override
        protected List<?> readUnderlyingList()
        {
            // If there is only one "role-name" element and it specifies the wildcard,
            // return an empty list.
            
            List<?> list = super.readUnderlyingList();
            
            if( list.size() == 1 )
            {
                final XmlElement element = (XmlElement) list.get( 0 );
                
                if( element.getText().trim().equals( ANY_ROLE_WILDCARD ) )
                {
                    list = Collections.emptyList();
                }
            }
            
            return list;
        }

        @Override
        protected Object addUnderlyingObject( final ModelElementType type )
        {
            // After adding a "role-name" element, remove the method meta comment as "listed"
            // method is implied when at least one "role-name" element is present.

            final XmlElement element = (XmlElement) super.addUnderlyingObject( type );
            element.getParent().setMetaCommentText( MC_METHOD, null );
            
            return element;
        }

        @Override
        public void remove( final Resource resource )
        {
            // Prior to removing last "role-name" element, set the method meta comment to avoid
            // transition from "listed" to "none" specification method.
            
            final XmlResource xmlResource = (XmlResource) resource;
            final XmlElement xmlElement = xmlResource.getXmlElement();
            final XmlElement xmlElementParent = xmlElement.getParent();
            
            if( xmlElementParent.getChildElements( EL_ROLE_NAME ).size() == 1 )
            {
                xmlElementParent.setMetaCommentText( MC_METHOD, MC_METHOD_LISTED );
            }
            
            super.remove( resource );
        }
    }
    
}
