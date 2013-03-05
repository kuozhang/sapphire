/******************************************************************************
 * Copyright (c) 2013 Oracle and Accenture
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Kamesh Sampath - [354276] Support initial values for properties
 ******************************************************************************/

package org.eclipse.sapphire.modeling;

import static org.eclipse.sapphire.modeling.localization.LocalizationUtil.transformCamelCaseToLabel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.MasterConversionService;
import org.eclipse.sapphire.internal.ElementClassLoaders;
import org.eclipse.sapphire.modeling.ModelPath.PropertySegment;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.internal.MemoryResource;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.modeling.localization.LocalizationSystem;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.internal.ElementMetaModelServiceContext;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.sapphire.util.MapFactory;
import org.eclipse.sapphire.util.SortedSetFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class ModelElementType extends ModelMetadataItem
{
    private final Class<?> typeClass;
    private Class<?> implClass = null;
    private Constructor<?> implClassConstructor = null;
    private boolean implClassLoaded = false;
    private final List<ModelElementType> baseTypes;
    private SortedSet<ModelProperty> properties;
    private Map<String,ModelProperty> propertiesByName;
    private final LocalizationService localizationService;
    private ImageData image;
    private boolean imageInitialized;
    private List<Listener> listeners;
    private ServiceContext serviceContext;
    
    public ModelElementType( final Class<?> typeClass )
    {
        this.typeClass = typeClass;
        this.localizationService = LocalizationSystem.service( this.typeClass );
        
        final ListFactory<ModelElementType> baseTypesFactory = ListFactory.start();
        
        for( Class<?> baseInterface : this.typeClass.getInterfaces() )
        {
            final ModelElementType baseType = read( baseInterface, false );
            
            if( baseType != null )
            {
                baseTypesFactory.add( baseType );
            }
        }
        
        this.baseTypes = baseTypesFactory.result();
    }
    
    public static ModelElementType read( final ClassLoader classLoader,
                                         final String qualifiedTypeName )
    {
        try
        {
            return read( classLoader.loadClass( qualifiedTypeName ) );
        }
        catch( ClassNotFoundException e )
        {
            throw new IllegalArgumentException( e );
        }
    }
    
    public static ModelElementType read( final Class<?> modelElementClass )
    {
        return read( modelElementClass, true );
    }
    
    public static ModelElementType read( final Class<?> modelElementClass,
                                         final boolean throwExceptionIfNotFound )
    {
        for( Field field : modelElementClass.getFields() )
        {
            if( field.getName().equals( "TYPE" ) )
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
            throw new IllegalArgumentException( "Did not find TYPE field on " + modelElementClass.getName() );
        }
        else
        {
            return null;
        }
    }
    
    public Class<?> getModelElementClass()
    {
        return this.typeClass;
    }
    
    public String getSimpleName()
    {
        return this.typeClass.getSimpleName();
    }
    
    public String getQualifiedName()
    {
        return this.typeClass.getName();
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate( final IModelParticle parent,
                                                    final ModelProperty parentProperty,
                                                    final Resource resource )
    {
        if( ! this.implClassLoaded )
        {
            this.implClassLoaded = true;
            
            this.implClass = ElementClassLoaders.loadImplementationClass( this );
            
            try
            {
                this.implClassConstructor = this.implClass.getConstructor( IModelParticle.class, ModelProperty.class, Resource.class );
            }
            catch( NoSuchMethodException e )
            {
                // todo: log a better message here
                
                LoggingService.log( e );
                
                this.implClass = null;
            }
        }
        
        if( this.implClassConstructor != null )
        {
            T element;
            
            try
            {
                element = (T) this.implClassConstructor.newInstance( parent, parentProperty, resource );
            }
            catch( Exception e )
            {
                final String msg = NLS.bind( Resources.cannotInstantiate, getSimpleName() );
                throw new RuntimeException( msg, e );
            }
            
            return element;                
        }
        
        final String msg = NLS.bind( Resources.cannotInstantiate, getSimpleName() );
        throw new RuntimeException( msg );
    }

    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate( final Resource resource )
    {
        if( resource == null )
        {
            throw new IllegalArgumentException();
        }
        
        return (T) instantiate( null, null, resource );
    }
    
    public <T extends IModelElement> T instantiate( final Object input )
    {
        return instantiate( service( MasterConversionService.class ).convert( input, Resource.class ) );
    }
    
    @SuppressWarnings( "unchecked" )
    
    public <T extends IModelElement> T instantiate()
    {
        final T element = (T) instantiate( new MemoryResource( this ) );
        element.initialize();
        return element;
    }
    
    /**
     * Returns all properties of this type.
     * 
     * @return all properties of this type
     */
    
    public synchronized SortedSet<ModelProperty> properties()
    {
        if( this.properties == null )
        {
            final SortedSetFactory<ModelProperty> propertiesSetFactory = SortedSetFactory.start
            (
                new Comparator<ModelProperty>()
                {
                    public int compare( final ModelProperty x, final ModelProperty y )
                    {
                        return x.getName().compareToIgnoreCase( y.getName() );
                    }
                }
            );
            
            for( Field field : this.typeClass.getDeclaredFields() )
            {
                if( field.getName().startsWith( "PROP_" ) )
                {
                    Object value = null;
                    
                    try
                    {
                        value = field.get( null );
                    }
                    catch( IllegalAccessException e )
                    {
                        LoggingService.log( e );
                    }
                    
                    if( value instanceof ModelProperty )
                    {
                        propertiesSetFactory.add( (ModelProperty) value );
                    }
                }
            }
            
            for( ModelElementType t : this.baseTypes )
            {
                propertiesSetFactory.add( t.properties() );
            }
            
            this.properties = propertiesSetFactory.result();
            
            final MapFactory<String,ModelProperty> propertiesByNameMapFactory = MapFactory.start();
            
            for( ModelProperty property : this.properties )
            {
                propertiesByNameMapFactory.add( property.getName().toLowerCase(), property );
            }
            
            this.propertiesByName = propertiesByNameMapFactory.result();
        }
        
        return this.properties;
    }
    
    /**
     * Returns the property specified by the given path. Only property name path segments are supported.
     * Using other segments, such as a parent navigation or a type filter, will result in an exception.
     * 
     * @param path the path specifying the property
     * @return the property or null if not found
     * @throws IllegalArgumentException if path is null or if path uses unsupported path segments
     */
    
    public <T extends ModelProperty> T property( final String path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        return property( new ModelPath( path ) );
    }
    
    /**
     * Returns the property specified by the given path. Only property name path segments are supported.
     * Using other segments, such as a parent navigation or a type filter, will result in an exception.
     * 
     * @param path the path specifying the property
     * @return the property or null if not found
     * @throws IllegalArgumentException if path is null or if path uses unsupported path segments
     */
    
    @SuppressWarnings( "unchecked" )
    
    public <T extends ModelProperty> T property( final ModelPath path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException();
        }
        
        properties(); // TODO: Remove when properties are initialized in the constructor.
        
        final ModelPath.Segment head = path.head();
        
        if( head instanceof PropertySegment )
        {
            final String name = ( (PropertySegment) head ).getPropertyName();
            final T property = (T) this.propertiesByName.get( name.toLowerCase() );
            
            if( property != null )
            {
                if( path.length() == 1 )
                {
                    return property;
                }
                else
                {
                    if( property instanceof ElementProperty || property instanceof ListProperty )
                    {
                        return property.getType().property( path.tail() );
                    }
                }
            }
        }
        else
        {
            throw new IllegalArgumentException( path.toString() );
        }

        return null;
    }
    
    @Override
    protected void initAnnotations( final ListFactory<Annotation> annotations )
    {
        annotations.add( this.typeClass.getDeclaredAnnotations() );
    }

    @Override
    public <A extends Annotation> List<A> getAnnotations( final Class<A> type )
    {
        final ListFactory<A> annotationsListFactory = ListFactory.start();
        
        annotationsListFactory.add( super.getAnnotations( type ) );
        
        for( ModelElementType baseType : this.baseTypes )
        {
            annotationsListFactory.add( baseType.getAnnotations( type ) );
        }
        
        return annotationsListFactory.result();
    }
    
    @Override
    public <A extends Annotation> A getAnnotation( final Class<A> type )
    {
        A annotation = super.getAnnotation( type );
        
        if( annotation == null )
        {
            for( ModelElementType baseType : this.baseTypes )
            {
                annotation = baseType.getAnnotation( type );
                
                if( annotation != null )
                {
                    break;
                }
            }
        }
        
        return annotation;
    }
    
    public Class<?> findAnnotationHostClass( final Annotation annotation )
    {
        if( this.typeClass.getAnnotation( annotation.annotationType() ) == annotation )
        {
            return this.typeClass;
        }
        
        for( ModelElementType baseType : this.baseTypes )
        {
            final Class<?> cl = baseType.findAnnotationHostClass( annotation );
            
            if( cl != null )
            {
                return cl;
            }
        }
        
        return null;
    }

    @Override
    protected String getDefaultLabel()
    {
        String label = this.typeClass.getName();
        int start = label.lastIndexOf( '.' ) + 1;
        final int startPlusOne = start + 1;
        
        if( label.charAt( start ) == 'I' && startPlusOne < label.length() && Character.isUpperCase( label.charAt( startPlusOne ) ) )
        {
            start = startPlusOne;
        }
        
        if( start > 0 )
        {
            label = label.substring( start );
        }
        
        final int lastDollarSign = label.lastIndexOf( '$' );
        
        if( lastDollarSign != -1 )
        {
            label = label.substring( lastDollarSign + 1 );
        }
        
        return transformCamelCaseToLabel( label );
    }
    
    @Override
    public LocalizationService getLocalizationService()
    {
        return this.localizationService;
    }
    
    public ImageData image()
    {
        if( ! this.imageInitialized )
        {
            final Image imageAnnotation = getAnnotation( Image.class );
            
            if( imageAnnotation != null )
            {
                try
                {
                    this.image = ImageData.createFromClassLoader( findAnnotationHostClass( imageAnnotation ), imageAnnotation.path() );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
            }
            
            this.imageInitialized = true;
        }
        
        return this.image;
    }
    
    synchronized List<Listener> listeners()
    {
        if( this.listeners == null )
        {
            final ListFactory<Listener> listenersListFactory = ListFactory.start();
            final Listeners listenersAnnotation = getAnnotation( Listeners.class );
            
            if( listenersAnnotation != null )
            {
                for( Class<? extends Listener> cl : listenersAnnotation.value() )
                {
                    try
                    {
                        listenersListFactory.add( cl.newInstance() );
                    }
                    catch( Exception e )
                    {
                        LoggingService.log( e );
                    }
                }
            }
            
            this.listeners = listenersListFactory.result();
        }
        
        return this.listeners;
    }

    /**
     * Returns the service of the specified type from the element metamodel service context.
     * 
     * <p>Service Context: <b>Sapphire.Element.MetaModel</b></p>
     * 
     * @param <S> the type of the service
     * @param type the type of the service
     * @return the service or <code>null</code> if not available
     */
    
    public <S extends Service> S service( final Class<S> type )
    {
        return services().service( type );
    }

    /**
     * Returns services of the specified type from the element metamodel service context.
     * 
     * <p>Service Context: <b>Sapphire.Element.MetaModel</b></p>
     * 
     * @param <S> the type of the service
     * @param type the type of the service
     * @return the list of services or an empty list if none are available
     */
    
    public <S extends Service> List<S> services( final Class<S> type )
    {
        return services().services( type );
    }

    /**
     * Returns the element metamodel service context.
     * 
     * <p>Service Context: <b>Sapphire.Element.MetaModel</b></p>
     * 
     * @return the element metamodel service context
     */
    
    public synchronized ServiceContext services()
    {
        if( this.serviceContext == null )
        {
            this.serviceContext = new ElementMetaModelServiceContext( this );
        }
        
        return this.serviceContext;
    }

    protected static abstract class ModelPropertyInitListener
    {
        public abstract void propertyInitialized( final ModelProperty property );
    }

    private static final class Resources extends NLS
    {
        public static String cannotInstantiate; 
        
        static
        {
            initializeMessages( ModelElementType.class.getName(), Resources.class );
        }
    }
    
}
