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

package org.eclipse.sapphire.modeling.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementService;
import org.eclipse.sapphire.modeling.ModelElementServiceFactory;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;
import org.eclipse.sapphire.modeling.util.DependencySorter;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireModelingExtensionSystem
{
    private static final String EL_VALUE_SERIALIZATION_SERVICE = "value-serialization-service";
    private static final String EL_MODEL_ELEMENT_SERVICE = "model-element-service";
    private static final String EL_MODEL_PROPERTY_SERVICE = "model-property-service";
    private static final String EL_FUNCTION = "function";
    private static final String EL_NAME = "name";
    private static final String EL_TYPE = "type";
    private static final String EL_FACTORY = "factory";
    private static final String EL_IMPL = "impl";
    private static final String EL_ID = "id";
    private static final String EL_OVERRIDES = "overrides";

    private static boolean initialized = false;
    private static List<ExtensionHandle> extensionHandles;
    private static List<ModelElementServiceFactoryProxy> modelElementServiceFactories;
    private static List<ModelPropertyServiceFactoryProxy> modelPropertyServiceFactories;
    private static List<ValueSerializationServiceFactory> valueSerializerFactories;
    private static Map<String,FunctionFactory> functionFactories;

    public static List<ExtensionHandle> getExtensionHandles()
    {
        initialize();

        return extensionHandles;
    }

    public static ModelElementService createModelElementService( final IModelElement element,
                                                                 final Class<? extends ModelElementService> service )
    {
        initialize();
        
        final Collection<ModelElementServiceFactoryProxy> applicable = new ArrayList<ModelElementServiceFactoryProxy>();

        for( ModelElementServiceFactoryProxy factory : modelElementServiceFactories )
        {
            if( factory.applicable( element, service ) )
            {
                applicable.add( factory );
            }
        }
        
        final int count = applicable.size();
        
        if( count == 1 )
        {
            return applicable.iterator().next().create( element, service );
        }
        else if( count > 1 )
        {
            final DependencySorter<String,ModelElementServiceFactoryProxy> sorter = new DependencySorter<String,ModelElementServiceFactoryProxy>();
            
            for( ModelElementServiceFactoryProxy factory : applicable )
            {
                sorter.add( factory.id(), factory );
                
                for( String override : factory.overrides() )
                {
                    sorter.dependency( factory, override );
                }
            }
            
            final List<ModelElementServiceFactoryProxy> sorted = sorter.sort();
            
            for( int i = sorted.size() - 1; i >= 0; i-- )
            {
                final ModelElementService svc = sorted.get( i ).create( element, service );
                
                if( svc != null )
                {
                    return svc;
                }
            }
        }
        
        return null;
    }

    public static ModelPropertyService createModelPropertyService( final IModelElement element,
                                                                   final ModelProperty property,
                                                                   final Class<? extends ModelPropertyService> service )
    {
        initialize();

        final Collection<ModelPropertyServiceFactoryProxy> applicable = new ArrayList<ModelPropertyServiceFactoryProxy>();

        for( ModelPropertyServiceFactoryProxy factory : modelPropertyServiceFactories )
        {
            if( factory.applicable( element, property, service ) )
            {
                applicable.add( factory );
            }
        }
        
        final int count = applicable.size();
        
        if( count == 1 )
        {
            return applicable.iterator().next().create( element, property, service );
        }
        else if( count > 1 )
        {
            final DependencySorter<String,ModelPropertyServiceFactoryProxy> sorter = new DependencySorter<String,ModelPropertyServiceFactoryProxy>();
            
            for( ModelPropertyServiceFactoryProxy factory : applicable )
            {
                sorter.add( factory.id(), factory );
                
                for( String override : factory.overrides() )
                {
                    sorter.dependency( factory, override );
                }
            }
            
            final List<ModelPropertyServiceFactoryProxy> sorted = sorter.sort();
            
            for( int i = sorted.size() - 1; i >= 0; i-- )
            {
                final ModelPropertyService svc = sorted.get( i ).create( element, property, service );
                
                if( svc != null )
                {
                    return svc;
                }
            }
        }
        
        return null;
    }

    public static ValueSerializationService createValueSerializer( final IModelElement element,
                                                                   final ValueProperty property,
                                                                   final Class<?> type )
    {
        initialize();

        for( ValueSerializationServiceFactory factory : valueSerializerFactories )
        {
            if( factory.applicable( type ) )
            {
                final ValueSerializationService instance = factory.create( element, property );
                
                if( instance != null )
                {
                    return instance;
                }
            }
        }

        return null;
    }

    public static Function createFunction( final String name,
                                           final Function... operands )
    {
        initialize();

        final FunctionFactory factory = functionFactories.get( name.toLowerCase() );

        if( factory != null )
        {
            return factory.create( operands );
        }

        return null;
    }

    private static synchronized void initialize()
    {
        if( ! initialized )
        {
            initialized = true;
            modelElementServiceFactories = new ArrayList<ModelElementServiceFactoryProxy>();
            modelPropertyServiceFactories = new ArrayList<ModelPropertyServiceFactoryProxy>();
            valueSerializerFactories = new ArrayList<ValueSerializationServiceFactory>();
            functionFactories = new HashMap<String,FunctionFactory>();

            extensionHandles = new ArrayList<ExtensionHandle>();

            final IExtensionRegistry registry = Platform.getExtensionRegistry();
            final IExtensionPoint point = registry.getExtensionPoint( SapphireModelingFrameworkPlugin.PLUGIN_ID, "extension" );

            if( point == null )
            {
				extensionHandles.add( new ClassLoaderExtensionHandle( SapphireModelingExtensionSystem.class.getClassLoader() ) );
		    }
            else
            {
                for( IExtension extension : point.getExtensions() )
                {
                    extensionHandles.add( new BundleExtensionHandle( extension.getContributor().getName() ) );
                }
		    }

            for( ExtensionHandle handle : extensionHandles )
            {
                for( URL url : handle.findExtensionFiles() )
                {
                    final Element root = parse( url );

                    if( root != null )
                    {
                        final NodeList nodes = root.getChildNodes();

                        for( int i = 0, n = nodes.getLength(); i < n; i++ )
                        {
                            final Node node = nodes.item( i );

                            if( node instanceof Element )
                            {
                                final Element el = (Element) node;
                                final String elname = el.getLocalName();

                                try
                                {
                                    if( elname.equals( EL_VALUE_SERIALIZATION_SERVICE ) )
                                    {
                                        final Class<?> valueType = handle.loadClass( text( child( el, EL_TYPE ) ) );
                                        final Class<? extends ValueSerializationService> serializerClass = handle.loadClass( text( child( el, EL_IMPL ) ) );

                                        valueSerializerFactories.add( new ValueSerializationServiceFactory( valueType, serializerClass ) );
                                    }
                                    else if( elname.equals( EL_MODEL_ELEMENT_SERVICE ) )
                                    {
                                        final String id = text( child( el, EL_ID ) );
                                        final Class<? extends ModelElementService> serviceType = handle.loadClass( text( child( el, EL_TYPE ) ) );
                                        final Class<? extends ModelElementServiceFactory> serviceFactory = handle.loadClass( text( child( el, EL_FACTORY ) ) );
                                        final Set<String> overrides = parseOverrides( el );

                                        modelElementServiceFactories.add( new ModelElementServiceFactoryProxy( id, serviceType, serviceFactory, overrides ) );
                                    }
                                    else if( elname.equals( EL_MODEL_PROPERTY_SERVICE ) )
                                    {
                                        final String id = text( child( el, EL_ID ) );
                                        final Class<? extends ModelPropertyService> serviceType = handle.loadClass( text( child( el, EL_TYPE ) ) );
                                        final Class<? extends ModelPropertyServiceFactory> serviceFactory = handle.loadClass( text( child( el, EL_FACTORY ) ) );
                                        final Set<String> overrides = parseOverrides( el );

                                        modelPropertyServiceFactories.add( new ModelPropertyServiceFactoryProxy( id, serviceType, serviceFactory, overrides ) );
                                    }
                                    else if( elname.equals( EL_FUNCTION ) )
                                    {
                                        final String name = text( child( el, EL_NAME ) );
                                        final Class<? extends Function> impl = handle.loadClass( text( child( el, EL_IMPL ) ) );

                                        functionFactories.put( name.toLowerCase(), new FunctionFactory( impl ) );
                                    }
                                }
                                catch( InvalidExtensionException e ) {}
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static Set<String> parseOverrides( final Element root )
    {
        Set<String> overrides = null;
        final NodeList nodes = root.getChildNodes();

        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );

            if( node instanceof Element && node.getLocalName().equals( EL_OVERRIDES ) )
            {
                final String text = text( (Element) node );
                
                if( text.length() > 0 )
                {
                    if( overrides == null )
                    {
                        overrides = new HashSet<String>();
                    }
                    
                    overrides.add( text );
                }
            }
        }
        
        if( overrides == null )
        {
            overrides = Collections.emptySet();
        }
        
        return overrides;
    }

    private static Element parse( final URL url )
    {
        try
        {
            final InputStream in = url.openStream();

            try
            {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                factory.setValidating( false );
                factory.setNamespaceAware( true );
                factory.setIgnoringComments( false );

                final DocumentBuilder docbuilder = factory.newDocumentBuilder();

                docbuilder.setEntityResolver
                (
                    new EntityResolver()
                    {
                        public InputSource resolveEntity( final String publicID,
                                                          final String systemID )
                        {
                            return new InputSource( new StringReader( "" ) );
                        }
                    }
                );

                final Document document = docbuilder.parse( in );

                return document.getDocumentElement();
            }
            finally
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
        }
        catch( Exception e )
        {
            // TODO: Log the problem.
            return null;
        }
    }

    private static Element child( final Element element,
                                  final String name )
    {
        final NodeList nodes = element.getChildNodes();

        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );

            if( node instanceof Element )
            {
                final Element el = (Element) node;

                if( name.equals( el.getLocalName() ) )
                {
                    return el;
                }
            }
        }

        // TODO: Log the problem.
        throw new InvalidExtensionException();
    }

    private static String text( final Element element )
    {
        final StringBuilder buf = new StringBuilder();
        final NodeList nodes = element.getChildNodes();

        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );

            if( node instanceof Text )
            {
                buf.append( ( (Text) node ).getData() );
            }
        }

        return buf.toString().trim();
    }

    public static abstract class ExtensionHandle
    {
        public abstract List<URL> findExtensionFiles();
        public abstract <T> Class<T> loadClass( String className );
        public abstract URL resolveResource( String name );
    }

    public static final class ClassLoaderExtensionHandle extends ExtensionHandle
    {
        private final ClassLoader classLoader;

        public ClassLoaderExtensionHandle( final ClassLoader classLoader )
        {
            this.classLoader = classLoader;
        }

        @Override
        
        public List<URL> findExtensionFiles()
        {
            final List<URL> files = new ArrayList<URL>();

            try
            {
                final Enumeration<URL> urls = this.classLoader.getResources( "META-INF/sapphire-extension.xml" );

                while( urls.hasMoreElements() )
                {
                    final URL url = urls.nextElement();

                    if( url != null )
                    {
                        files.add( url );
                    }
                }
            }
            catch( IOException e )
            {
                SapphireModelingFrameworkPlugin.log( e );
            }

            return files;
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> Class<T> loadClass( final String className )
        {
            Class<?> cl = null;

            if( className != null )
            {
                try
                {
                    cl = this.classLoader.loadClass( className );
                }
                catch( ClassNotFoundException e )
                {
                    // TODO: Log the problem.
                    throw new InvalidExtensionException();
                }
            }

            return (Class<T>) cl;
        }

        @Override
        public URL resolveResource( final String name )
        {
            return this.classLoader.getResource( name );
        }
    }

    public static final class BundleExtensionHandle extends ExtensionHandle
    {
        private final Bundle bundle;

        public BundleExtensionHandle( final String bundleId )
        {
            this.bundle = Platform.getBundle( bundleId );
        }

        @Override
        public List<URL> findExtensionFiles()
        {
            final List<URL> files = new ArrayList<URL>();

            if( this.bundle != null )
            {
                try
                {
                    final Enumeration<URL> urls = this.bundle.getResources( "META-INF/sapphire-extension.xml" );

                    if( urls != null )
                    {
                        while( urls.hasMoreElements() )
                        {
                            final URL url = urls.nextElement();
    
                            if( url != null )
                            {
                                files.add( url );
                            }
                        }
                    }
                }
                catch( IOException e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                }
            }

            return files;
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public <T> Class<T> loadClass( final String className )
        {
            Class<?> cl = null;

            if( className != null )
            {
                try
                {
                    cl = this.bundle.loadClass( className );
                }
                catch( ClassNotFoundException e )
                {
                    // TODO: Log the problem.
                    throw new InvalidExtensionException();
                }
            }

            return (Class<T>) cl;
        }

        @Override
        public URL resolveResource( final String name )
        {
            return this.bundle.getResource( name );
        }
    }

    public static final class InvalidExtensionException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }

    private static final class ModelElementServiceFactoryProxy

        extends ModelElementServiceFactory

    {
        private final String id;
        private final Class<? extends ModelElementService> type;
        private final Class<? extends ModelElementServiceFactory> factoryClass;
        private final Set<String> overrides;
        private ModelElementServiceFactory factoryInstance;
        private boolean factoryInstantiationFailed;

        public ModelElementServiceFactoryProxy( final String id,
                                                final Class<? extends ModelElementService> type,
                                                final Class<? extends ModelElementServiceFactory> factoryClass,
                                                final Set<String> overrides )
        {
            this.id = id;
            this.type = type;
            this.factoryClass = factoryClass;
            this.overrides = overrides;
        }
        
        public String id()
        {
            return this.id;
        }

        @Override
        public boolean applicable( final IModelElement element,
                                   final Class<? extends ModelElementService> service )
        {
            boolean result = false;

            if( service.isAssignableFrom( this.type ) )
            {
                final ModelElementServiceFactory factory = factory();

                if( factory != null )
                {
                    result = factory.applicable( element, service );
                }
            }

            return result;
        }
        
        public Set<String> overrides()
        {
            return this.overrides;
        }

        @Override
        public ModelElementService create( final IModelElement element,
                                           final Class<? extends ModelElementService> service )
        {
            ModelElementService result = null;
            final ModelElementServiceFactory factory = factory();

            if( factory != null )
            {
                result = factory.create( element, service );
            }

            return result;
        }

        private synchronized ModelElementServiceFactory factory()
        {
            if( this.factoryInstance == null && ! this.factoryInstantiationFailed )
            {
                try
                {
                    this.factoryInstance = this.factoryClass.newInstance();
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    this.factoryInstantiationFailed = true;
                }
            }

            return this.factoryInstance;
        }
    }

    private static final class ModelPropertyServiceFactoryProxy

        extends ModelPropertyServiceFactory

    {
        private final String id;
        private final Class<? extends ModelPropertyService> type;
        private final Class<? extends ModelPropertyServiceFactory> factoryClass;
        private final Set<String> overrides;
        private ModelPropertyServiceFactory factoryInstance;
        private boolean factoryInstantiationFailed;

        public ModelPropertyServiceFactoryProxy( final String id,
                                                 final Class<? extends ModelPropertyService> type,
                                                 final Class<? extends ModelPropertyServiceFactory> factoryClass,
                                                 final Set<String> overrides )
        {
            this.id = id;
            this.type = type;
            this.factoryClass = factoryClass;
            this.overrides = overrides;
        }
        
        public String id()
        {
            return this.id;
        }

        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            boolean result = false;

            if( service.isAssignableFrom( this.type ) )
            {
                final ModelPropertyServiceFactory factory = factory();

                if( factory != null )
                {
                    result = factory.applicable( element, property, service );
                }
            }

            return result;
        }

        public Set<String> overrides()
        {
            return this.overrides;
        }

        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            ModelPropertyService result = null;
            final ModelPropertyServiceFactory factory = factory();

            if( factory != null )
            {
                result = factory.create( element, property, service );
            }

            return result;
        }

        private synchronized ModelPropertyServiceFactory factory()
        {
            if( this.factoryInstance == null && ! this.factoryInstantiationFailed )
            {
                try
                {
                    this.factoryInstance = this.factoryClass.newInstance();
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    this.factoryInstantiationFailed = true;
                }
            }

            return this.factoryInstance;
        }
    }

    private static final class ValueSerializationServiceFactory
    {
        private final Class<?> type;
        private final Class<? extends ValueSerializationService> serializerClass;
        private boolean serializerInstantiationFailed;

        public ValueSerializationServiceFactory( final Class<?> type,
                                                 final Class<? extends ValueSerializationService> serializerClass )
        {
            this.type = type;
            this.serializerClass = serializerClass;
        }

        public boolean applicable( final Class<?> type )
        {
            boolean result = false;

            if( this.type.isAssignableFrom( type ) )
            {
                result = true;
            }

            return result;
        }

        public ValueSerializationService create( final IModelElement element,
                                                 final ValueProperty property )
        {
            ValueSerializationService serializer = null;

            if( ! this.serializerInstantiationFailed )
            {
                try
                {
                    serializer = this.serializerClass.newInstance();
                    serializer.init( element, property, new String[ 0 ] );
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    serializer = null;
                    this.serializerInstantiationFailed = true;
                }
            }

            return serializer;
        }
    }

    private static final class FunctionFactory
    {
        private final Class<? extends Function> functionClass;
        private boolean functionInstantiationFailed;

        public FunctionFactory( final Class<? extends Function> functionClass )
        {
            this.functionClass = functionClass;
        }

        public Function create( final Function... operands )
        {
            Function function = null;

            if( ! this.functionInstantiationFailed )
            {
                try
                {
                    function = this.functionClass.newInstance();
                    function.init( operands );
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    function = null;
                    this.functionInstantiationFailed = true;
                }
            }

            return function;
        }
    }

}
