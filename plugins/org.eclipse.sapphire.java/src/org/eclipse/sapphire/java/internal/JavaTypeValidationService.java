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

package org.eclipse.sapphire.java.internal;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ModelPropertyValidationService;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaTypeValidationService

    extends ModelPropertyValidationService<ReferenceValue<JavaTypeName,JavaType>>

{
    private ValueProperty property;
    private boolean isClassOk;
    private boolean isAbstractClassOk;
    private boolean isInterfaceOk;
    private boolean isAnnotationOk;
    private boolean isEnumOk;
    private String[] requiredBaseTypes;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );

        this.property = (ValueProperty) property;
        
        final JavaTypeConstraint javaTypeConstraintAnnotation = this.property.getAnnotation( JavaTypeConstraint.class );
        
        if( javaTypeConstraintAnnotation == null )
        {
            throw new IllegalStateException();
        }
        
        this.requiredBaseTypes = javaTypeConstraintAnnotation.type();
        
        boolean c = false, d = false, i = false, a = false, e = false;
        
        for( JavaTypeKind kind : javaTypeConstraintAnnotation.kind() )
        {
            switch( kind )
            {
                case CLASS:           c = true; break;
                case ABSTRACT_CLASS:  d = true; break;
                case INTERFACE:       i = true; break;
                case ANNOTATION:      a = true; break;
                case ENUM:            e = true; break;
                default:              throw new IllegalStateException();
            }
        }
        
        this.isClassOk = c;
        this.isAbstractClassOk = d;
        this.isInterfaceOk = i;
        this.isAnnotationOk = a;
        this.isEnumOk = e;
    }
    
    @Override
    public Status validate()
    {
        final ReferenceValue<JavaTypeName,JavaType> value = target();
        final String val = value.getText( false );
        
        if( val != null )
        {
            final JavaType type = value.resolve();
            
            if( type == null )
            {
                return Status.createOkStatus();
            }
            
            final JavaTypeKind kind = type.kind();
            
            switch( kind )
            {
                case CLASS:
                {
                    if( ! this.isClassOk )
                    {
                        final String label = this.property.getLabel( true, CapitalizationType.NO_CAPS, false );
                        final String msg = Resources.bind( Resources.classNotAllowed, val, label );
                        return Status.createErrorStatus( msg );
                    }
                    
                    break;
                }
                case ABSTRACT_CLASS:
                {
                    if( ! this.isAbstractClassOk )
                    {
                        final String label = this.property.getLabel( true, CapitalizationType.NO_CAPS, false );
                        final String msg = Resources.bind( Resources.abstractClassNotAllowed, val, label );
                        return Status.createErrorStatus( msg );
                    }
                    
                    break;
                }
                case INTERFACE:
                {
                    if( ! this.isInterfaceOk )
                    {
                        final String label = this.property.getLabel( true, CapitalizationType.NO_CAPS, false );
                        final String msg = Resources.bind( Resources.interfaceNotAllowed, val, label );
                        return Status.createErrorStatus( msg );
                    }
                    
                    break;
                }
                case ANNOTATION:
                {
                    if( ! this.isAnnotationOk )
                    {
                        final String label = this.property.getLabel( true, CapitalizationType.NO_CAPS, false );
                        final String msg = Resources.bind( Resources.annotationNotAllowed, val, label );
                        return Status.createErrorStatus( msg );
                    }
                    
                    break;
                }
                case ENUM:
                {
                    if( ! this.isEnumOk )
                    {
                        final String label = this.property.getLabel( true, CapitalizationType.NO_CAPS, false );
                        final String msg = Resources.bind( Resources.enumNotAllowed, val, label );
                        return Status.createErrorStatus( msg );
                    }
                    
                    break;
                }
                default:
                {
                    throw new IllegalStateException();
                }
            }
            
            if( kind != JavaTypeKind.ENUM && kind != JavaTypeKind.ANNOTATION )
            {
                for( String baseType : this.requiredBaseTypes )
                {
                    if( ! type.isOfType( baseType ) )
                    {
                        final String template = ( type.kind() == JavaTypeKind.INTERFACE ? Resources.interfaceDoesNotExtend : Resources.classDoesNotImplementOrExtend );
                        final String msg = Resources.bind( template, val, baseType );
                        return Status.createErrorStatus( msg );
                    }
                }
            }
        }
        
        return Status.createOkStatus();
    }
    
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            if( property instanceof ValueProperty && property.getTypeClass() == JavaTypeName.class )
            {
                final Reference referenceAnnotation = property.getAnnotation( Reference.class );
                
                if( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class )
                {
                    return property.hasAnnotation( JavaTypeConstraint.class );
                }
            }
            
            return false;
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new JavaTypeValidationService();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String classDoesNotImplementOrExtend;
        public static String interfaceDoesNotExtend;
        public static String abstractClassNotAllowed;
        public static String classNotAllowed;
        public static String interfaceNotAllowed;
        public static String annotationNotAllowed;
        public static String enumNotAllowed;
        
        static
        {
            initializeMessages( JavaTypeValidationService.class.getName(), Resources.class );
        }
    }
    
}
