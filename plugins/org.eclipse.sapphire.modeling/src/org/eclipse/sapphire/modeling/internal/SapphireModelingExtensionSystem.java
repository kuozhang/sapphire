/******************************************************************************
 * Copyright (c) 2013 Oracle
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.sapphire.Context;
import org.eclipse.sapphire.modeling.ExtensionsLocator;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.sapphire.util.SetFactory;
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
    private static final String EL_CONDITION = "condition";
    private static final String EL_CONTEXT = "context";
    private static final String EL_FUNCTION = "function";
    private static final String EL_ID = "id";
    private static final String EL_IMPL = "impl";
    private static final String EL_IMPLEMENTATION = "implementation";
    private static final String EL_NAME = "name";
    private static final String EL_OPERAND_COUNT = "operand-count";
    private static final String EL_OVERRIDES = "overrides";
    private static final String EL_SERVICE = "service";
    
    private static boolean initialized = false;
    private static List<ServiceExtension> serviceExtensions;
    private static Map<String,List<FunctionFactory>> functionFactories;

    public static List<ServiceExtension> services()
    {
        initialize();
        return serviceExtensions;
    }

    public static Function createFunction( final String name,
                                           final Function... operands )
    {
        initialize();

        final List<FunctionFactory> factories = functionFactories.get( name.toLowerCase() );

        if( factories != null )
        {
            for( FunctionFactory factory : factories )
            {
                final Function function = factory.create( operands );
                
                if( function != null )
                {
                    return function;
                }
            }
            
            if( operands.length == 1 )
            {
                throw new FunctionException( NLS.bind( Resources.undefinedFunctionMessageExt1, name ) );
            }
            else
            {
                throw new FunctionException( NLS.bind( Resources.undefinedFunctionMessageExt, name, operands.length ) );
            }
        }
        
        throw new FunctionException( NLS.bind( Resources.undefinedFunctionMessage, name ) );
    }
    
    public static Function createFunctionNoEx( final String name,
                                               final Function... operands )
    {
        initialize();

        final List<FunctionFactory> factories = functionFactories.get( name.toLowerCase() );

        if( factories != null )
        {
            for( FunctionFactory factory : factories )
            {
                final Function function = factory.create( operands );
                
                if( function != null )
                {
                    return function;
                }
            }
        }
        
        return null;
    }
    
    private static synchronized void initialize()
    {
        if( ! initialized )
        {
            initialized = true;
            functionFactories = new HashMap<String,List<FunctionFactory>>();
            
            final ListFactory<ServiceExtension> serviceExtensionsFactory = ListFactory.start();

            for( final ExtensionsLocator.Handle handle : ExtensionsLocator.instance().find() )
            {
                final Element root = parse( handle.extension() );

                if( root != null )
                {
                    final NodeList nodes = root.getChildNodes();
                    final Context context = handle.context();

                    for( int i = 0, n = nodes.getLength(); i < n; i++ )
                    {
                        final Node node = nodes.item( i );

                        if( node instanceof Element )
                        {
                            final Element el = (Element) node;
                            final String elname = el.getLocalName();

                            try
                            {
                                if( elname.equals( EL_SERVICE ) )
                                {
                                    final String id = value( el, EL_ID ).required();
                                    final Set<String> contexts = values( el, EL_CONTEXT );
                                    final Set<String> overrides = values( el, EL_OVERRIDES );
                                    
                                    final Class<? extends Service> implementation = context.findClass( value( el, EL_IMPLEMENTATION ).required() );
                                    
                                    final String conditionClassName = value( el, EL_CONDITION ).optional();
                                    Class<? extends ServiceCondition> condition = null;
                                    
                                    if( conditionClassName != null )
                                    {
                                        condition = context.findClass( conditionClassName );
                                    }
                                    
                                    if( implementation != null )
                                    {
                                        serviceExtensionsFactory.add( new ServiceExtension( id, implementation, condition, contexts, overrides ) );
                                    }
                                }
                                else if( elname.equals( EL_FUNCTION ) )
                                {
                                    final String name = value( el, EL_NAME ).required().toLowerCase();
                                    final Class<? extends Function> impl = context.findClass( value( el, EL_IMPL ).required() );
                                    final Set<Integer> compatibleOperandCounts = integers( el, EL_OPERAND_COUNT );
                                    
                                    final ListFactory<FunctionFactory> factories = ListFactory.start();
                                    
                                    factories.add( functionFactories.get( name ) );
                                    factories.add( new FunctionFactory( impl, compatibleOperandCounts ) );
                                    
                                    functionFactories.put( name, factories.result() );
                                }
                            }
                            catch( InvalidExtensionException e ) {}
                        }
                    }
                }
            }
            
            serviceExtensions = serviceExtensionsFactory.result();
        }
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

    private static String value( final Element element )
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
    
    private static final class ValueLookupResult
    {
        private final String value;
        
        public ValueLookupResult( final String value )
        {
            this.value = value;
        }
        
        public String required()
        {
            if( this.value == null )
            {
                throw new InvalidExtensionException();
            }
            
            return this.value;
        }
        
        public String optional()
        {
            return this.value;
        }
    }

    private static ValueLookupResult value( final Element element, final String valueElementName )
    {
        final NodeList nodes = element.getChildNodes();

        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );

            if( node instanceof Element )
            {
                final Element el = (Element) node;

                if( valueElementName.equals( el.getLocalName() ) )
                {
                    return new ValueLookupResult( value( el ) );
                }
            }
        }
        
        return new ValueLookupResult( null );
    }

    private static Set<String> values( final Element root,
                                       final String entryElementName )
    {
        final SetFactory<String> factory = SetFactory.start();
        final NodeList nodes = root.getChildNodes();
    
        for( int i = 0, n = nodes.getLength(); i < n; i++ )
        {
            final Node node = nodes.item( i );
    
            if( node instanceof Element && node.getLocalName().equals( entryElementName ) )
            {
                final String text = value( (Element) node );
                
                if( text.length() > 0 )
                {
                    factory.add( text );
                }
            }
        }
        
        return factory.result();
    }
    
    private static Set<Integer> integers( final Element root,
                                          final String entryElementName )
    {
        final SetFactory<Integer> factory = SetFactory.start();
        
        for( String string : values( root, entryElementName ) )
        {
            final int integer;
            
            try
            {
                integer = Integer.parseInt( string );
            }
            catch( NumberFormatException e )
            {
                throw new InvalidExtensionException();
            }
            
            factory.add( integer );
        }
        
        return factory.result();
    }

    public static final class InvalidExtensionException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }
    
    public static final class ServiceExtension
    {
        private final String id;
        private final Class<? extends Service> implementation;
        private final Class<? extends ServiceCondition> condition;
        private final Set<String> contexts;
        private final Set<String> overrides;
        
        public ServiceExtension( final String id,
                                 final Class<? extends Service> implementation,
                                 final Class<? extends ServiceCondition> condition,
                                 final Set<String> contexts,
                                 final Set<String> overrides )
        {
            this.id = id;
            this.implementation = implementation;
            this.condition = condition;
            this.contexts = contexts;
            this.overrides = overrides;
        }
        
        public String id()
        {
            return this.id;
        }
        
        public Class<? extends Service> implementation()
        {
            return this.implementation;
        }
        
        public Class<? extends ServiceCondition> condition()
        {
            return this.condition;
        }
        
        public Set<String> contexts()
        {
            return this.contexts;
        }
        
        public Set<String> overrides()
        {
            return this.overrides;
        }
    }

    private static final class FunctionFactory
    {
        private final Class<? extends Function> functionClass;
        private boolean functionInstantiationFailed;
        private final Set<Integer> compatibleOperandCounts;

        public FunctionFactory( final Class<? extends Function> functionClass,
                                final Set<Integer> compatibleOperandCounts )
        {
            this.functionClass = functionClass;
            this.compatibleOperandCounts = compatibleOperandCounts;
        }

        public Function create( final Function... operands )
        {
            Function function = null;

            if( ! this.functionInstantiationFailed )
            {
                if( this.compatibleOperandCounts.isEmpty() || this.compatibleOperandCounts.contains( operands.length ) )
                {
                    try
                    {
                        function = this.functionClass.newInstance();
                        function.init( operands );
                    }
                    catch( Exception e )
                    {
                        LoggingService.log( e );
                        function = null;
                        this.functionInstantiationFailed = true;
                    }
                }
            }

            return function;
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String undefinedFunctionMessage;
        public static String undefinedFunctionMessageExt;
        public static String undefinedFunctionMessageExt1;
        
        static
        {
            initializeMessages( SapphireModelingExtensionSystem.class.getName(), Resources.class );
        }
    }

}