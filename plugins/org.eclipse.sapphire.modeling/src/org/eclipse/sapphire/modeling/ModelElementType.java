/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.internal.MemoryResource;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ModelElementType 
    
    extends ModelMetadataItem
    
{
    private final Class<?> modelElementClass;
    private Class<?> implClass = null;
    private Constructor<?> implClassConstructor = null;
    private boolean implClassLoaded = false;
    private final List<ModelProperty> properties;
    private Map<String,String> resources;
    
    public ModelElementType( final Class<?> modelElementClass )
    {
        this.modelElementClass = modelElementClass;
        this.properties = new ArrayList<ModelProperty>();
        this.resources = null;
    }
    
    public static ModelElementType getModelElementType( final Class<?> modelElementClass )
    {
        return getModelElementType( modelElementClass, true );
    }
    
    public static ModelElementType getModelElementType( final Class<?> modelElementClass,
                                                        final boolean throwExceptionIfNotFound )
    {
        for( Field field : modelElementClass.getFields() )
        {
            if( field.getName().equals( "TYPE" ) ) //$NON-NLS-1$
            {
                try
                {
                    final Object fieldValue = field.get( null );
                    
                    if( fieldValue instanceof ModelElementType )
                    {
                        return (ModelElementType) fieldValue;
                    }
                    else
                    {
                        break;
                    }
                }
                catch( IllegalAccessException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
        
        if( throwExceptionIfNotFound )
        {
            throw new IllegalArgumentException( "Did not find TYPE field on " + modelElementClass.getName() ); //$NON-NLS-1$
        }
        else
        {
            return null;
        }
    }
    
    public Class<?> getModelElementClass()
    {
        return this.modelElementClass;
    }
    
    public String getSimpleName()
    {
        return this.modelElementClass.getSimpleName();
    }
    
    public String getQualifiedName()
    {
        return this.modelElementClass.getName();
    }
    
    public Class<?> getImplClass()
    {
        synchronized( this )
        {
            if( ! this.implClassLoaded )
            {
                this.implClassLoaded = true;
                
                final GenerateImpl generateImplAnnotation = getAnnotation( GenerateImpl.class );
                
                if( generateImplAnnotation != null )
                {
                    String implPackage = generateImplAnnotation.packageName();
                    
                    if( implPackage.length() == 0 )
                    {
                        implPackage = this.modelElementClass.getPackage().getName() + ".internal";
                    }
                    
                    final String implClassName = this.modelElementClass.getSimpleName().substring( 1 );
                    
                    final String implClassQualifiedName = implPackage + "." + implClassName;
                    
                    try
                    {
                        this.implClass = this.modelElementClass.getClassLoader().loadClass( implClassQualifiedName );
                    }
                    catch( ClassNotFoundException e )
                    {
                        // No need to report this. The null return value signifies that the impl class was not found.
                    }
                    
                    if( this.implClass != null )
                    {
                        try
                        {
                            this.implClassConstructor = this.implClass.getConstructor( IModelParticle.class, ModelProperty.class, Resource.class );
                        }
                        catch( NoSuchMethodException e )
                        {
                            // todo: log a better message here
                            
                            SapphireModelingFrameworkPlugin.log( e );
                            
                            this.implClass = null;
                        }
                    }
                }
            }
            
            return this.implClass;
        }
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate( final IModelParticle parent,
                                                    final ModelProperty parentProperty,
                                                    final Resource resource )
    {
        getImplClass();
        
        if( this.implClassConstructor != null )
        {
            try
            {
                return (T) this.implClassConstructor.newInstance( parent, parentProperty, resource );
            }
            catch( Exception e )
            {
                // Is this right or should it propagate? 
                
                SapphireModelingFrameworkPlugin.log( e );
            }
        }
        
        return null;
    }

    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate( final Resource resource )
    {
        return (T) instantiate( null, null, resource );
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate()
    {
        return (T) instantiate( null, null, new MemoryResource( this ) );
    }

    public List<ModelProperty> getProperties()
    {
        final TreeMap<String,ModelProperty> properties = new TreeMap<String,ModelProperty>();
        
        for( Class<?> cl : this.modelElementClass.getInterfaces() )
        {
            final ModelElementType t = getModelElementType( cl, false );
            
            if( t != null )
            {
                for( ModelProperty property : t.getProperties() )
                {
                    properties.put( property.getName(), property );
                }
            }
        }
        
        for( ModelProperty property : this.properties )
        {
            properties.put( property.getName(), property );
        }
        
        return new ArrayList<ModelProperty>( properties.values() );
    }
    
    public ModelProperty getProperty( final String propertyName )
    {
        for( ModelProperty property : getProperties() )
        {
            if( property.getName().equalsIgnoreCase( propertyName ) )
            {
                return property;
            }
        }
        
        return null;
    }
    
    void addProperty( final ModelProperty property )
    {
        this.properties.add( property );
    }
    
    @Override
    public <A extends Annotation> A getAnnotation( final Class<A> type,
                                                   final boolean localOnly )
    {
        return this.modelElementClass.getAnnotation( type );
    }

    @Override
    public synchronized String getResource( final String key )
    {
        if( this.resources == null )
        {
            this.resources = new HashMap<String,String>();

            try
            {
                final ResourceBundle resourceBundle 
                    = ResourceBundle.getBundle( this.modelElementClass.getName(), Locale.getDefault(), 
                                                this.modelElementClass.getClassLoader() );
                
                for( Enumeration<String> keys = resourceBundle.getKeys(); keys.hasMoreElements(); )
                {
                    final String k = keys.nextElement();
                    this.resources.put( k, resourceBundle.getString( k ) );
                }
            }
            catch( MissingResourceException e )
            {
                // Intentionally ignoring. It is acceptable for types to not have any resources.
            }
        }
        
        return this.resources.get( key );
    }
    
    @Override
    protected String getLabelResourceKeyBase()
    {
        return "$type$";
    }
    
    @Override
    protected String getDefaultLabel()
    {
        String className = this.modelElementClass.getName();
        int start = className.lastIndexOf( '.' ) + 1;
        final int startPlusOne = start + 1;
        
        if( className.charAt( start ) == 'I' && startPlusOne < className.length() && Character.isUpperCase( className.charAt( startPlusOne ) ) )
        {
            start = startPlusOne;
        }
        
        if( start > 0 )
        {
            className = className.substring( start );
        }
        
        return transformCamelCaseToLabel( className );
    }

    protected static abstract class ModelPropertyInitListener
    {
        public abstract void propertyInitialized( final ModelProperty property );
    }

}
