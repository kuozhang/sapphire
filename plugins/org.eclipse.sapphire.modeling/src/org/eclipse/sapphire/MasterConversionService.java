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

package org.eclipse.sapphire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.services.Service;

/**
 * Converts an object to the specified type by delegating to available ConversionService and UniversalConversionService 
 * implementations. If object is null or is already of desired type, the object is returned unchanged.
 * 
 * <p>An implementation of this service is provided with Sapphire. This service is not intended to
 * be implemented by adopters.</p>
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MasterConversionService extends Service
{
    private Map<Conversion,List<ConversionService<?,?>>> conversions;

    /**
     * Converts an object to the specified type.
     * 
     * @param object the object to convert
     * @param type the desired type of a converted object
     * @return the converted object or null if could not be converted
     * @throws IllegalArgumentException if type is null
     */
    
    public <T> T convert( final Object object, final Class<T> type )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( object == null )
        {
            return null;
        }
        
        if( type.isInstance( object ) )
        {
            return type.cast( object );
        }
        
        if( this.conversions == null )
        {
            this.conversions = new HashMap<Conversion,List<ConversionService<?,?>>>();
            
            for( ConversionService<?,?> service : services( ConversionService.class ) )
            {
                final Class<?> source = service.source();
                
                for( Class<?> target : types( service.target() ) )
                {
                    final Conversion conversion = new Conversion( source, target );
                    
                    List<ConversionService<?,?>> services = this.conversions.get( conversion );
                    
                    if( services == null )
                    {
                        services = Collections.<ConversionService<?,?>>singletonList( service );
                        this.conversions.put( conversion, services );
                    }
                    else
                    {
                        if( services.size() == 1 )
                        {
                            services = new ArrayList<ConversionService<?,?>>( services );
                            this.conversions.put( conversion, services );
                        }
                        
                        services.add( service );
                    }
                }
            }
        }
        
        T result = convert( object, object.getClass(), type );
        
        if( result == null )
        {
            for( Class<?> source : types( object.getClass() ) )
            {
                result = convert( object, source, type );
                
                if( result != null )
                {
                    break;
                }
            }
        }
        
        if( result == null )
        {
            for( UniversalConversionService service : services( UniversalConversionService.class ) )
            {
                try
                {
                    result = type.cast( service.convert( object, type ) );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
                
                if( result != null )
                {
                    break;
                }
            }
        }
        
        return result;
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    
    private <S,T> T convert( final Object object, final Class<S> source, final Class<T> target )
    {
        final List<ConversionService<?,?>> services = this.conversions.get( new Conversion( source, target ) );
        
        if( services != null )
        {
            for( ConversionService service : services )
            {
                T result = null;
                
                try
                {
                    result = target.cast( service.convert( object ) );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
                
                if( result != null )
                {
                    return result;
                }
            }
        }
        
        return null;
    }
    
    private static Set<Class<?>> types( final Class<?> cl )
    {
        final Queue<Class<?>> queue = new LinkedList<Class<?>>();
        final Set<Class<?>> types = new LinkedHashSet<Class<?>>();
        
        queue.add( cl );
        
        while( ! queue.isEmpty() )
        {
            final Class<?> type = queue.remove();
            
            types.add( type );
            
            final Class<?> superclass = type.getSuperclass();
            
            if( superclass != null )
            {
                queue.add( superclass );
            }
            
            final Class<?>[] interfaces = type.getInterfaces();
            
            for( Class<?> interfc : interfaces )
            {
                if( ! types.contains( interfc ) )
                {
                    queue.add( interfc );
                }
            }
        }
        
        return types;
    }

    private static class Conversion
    {
        private final Class<?> source;
        private final Class<?> target;
        
        public Conversion( final Class<?> source, final Class<?> target )
        {
            if( source == null )
            {
                throw new IllegalArgumentException();
            }
            
            if( target == null )
            {
                throw new IllegalArgumentException();
            }
            
            this.source = source;
            this.target = target;
        }
    
        @Override
        public boolean equals( final Object obj )
        {
            if( obj instanceof Conversion )
            {
                final Conversion conversion = (Conversion) obj;
                return ( this.source == conversion.source && this.target == conversion.target );
            }
            
            return false;
        }
    
        @Override
        public int hashCode()
        {
            return this.source.hashCode() ^ this.target.hashCode();
        }
    
        @Override
        public String toString()
        {
            return this.source.getName() + " -> " + this.target.getName();
        }
    }
    
}
