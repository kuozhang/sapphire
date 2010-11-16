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

package org.eclipse.sapphire.modeling.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.EnablementService;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.scripting.Script;
import org.eclipse.sapphire.modeling.scripting.ScriptsManager;
import org.eclipse.sapphire.modeling.scripting.VariableResolver;
import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EnablementServiceFactory

    extends ModelPropertyServiceFactory
    
{
    private static EnablementService DEFAULT_ENABLEMENT_SERVICE = new EnablementService()
    {
        @Override
        public boolean isEnabled()
        {
            return true;
        }
    };
    
    @Override
    public boolean applicable( final IModelElement element,
                               final ModelProperty property,
                               final Class<? extends ModelPropertyService> service )
    {
        return true;
    }

    @Override
    public ModelPropertyService create( final IModelElement element,
                                        final ModelProperty property,
                                        final Class<? extends ModelPropertyService> service )
    {
        List<EnablementService> services = new ArrayList<EnablementService>();
        
        for( Enablement annotation : property.getAnnotations( Enablement.class ) )
        {
            EnablementService svc = null;
            
            if( ! annotation.service().equals( EnablementService.class ) )
            {
                try
                {
                    svc = annotation.service().newInstance();
                    svc.init( element, property, annotation.params() );
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                    svc = null;
                }
            }
            
            if( svc == null && annotation.property().length() > 0 )
            {
                svc = new PropertyEnablementService();
                svc.init( element, property, new String[] { annotation.property(), annotation.propertyValue() } );
            }
            
            if( svc == null && annotation.script().length() > 0 )
            {
                svc = new ScriptBasedEnablementService();
                svc.init( element, property, new String[] { annotation.script() } );
            }

            if( svc != null )
            {
                services.add( svc );
            }
        }
        
        final int count = services.size();
        final EnablementService result;
        
        if( count == 0 )
        {
            result = DEFAULT_ENABLEMENT_SERVICE;
        }
        else if( count == 1 )
        {
            result = services.get( 0 );
        }
        else
        {
            result = new UnionEnabler( services );
        }
        
        return result;
    }
    
    private static final class UnionEnabler extends EnablementService
    {
        private final List<EnablementService> enablers;
        
        public UnionEnabler( final List<EnablementService> enablers )
        {
            this.enablers = enablers;
        }

        @Override
        public boolean isEnabled()
        {
            for( EnablementService enabler : this.enablers )
            {
                try
                {
                    if( ! enabler.isEnabled() )
                    {
                        return false;
                    }
                }
                catch( Exception e )
                {
                    SapphireModelingFrameworkPlugin.log( e );
                }
            }

            return true;
        }
    }
    
    private static final class ScriptBasedEnablementService extends EnablementService
    {
        private Script script;
        private VariableResolver variableResolver;
        
        @Override
        public void init( final IModelElement element,
                          final ModelProperty property,
                          final String[] params )
        {
            super.init( element, property, params );
            
            if( params.length != 1 )
            {
                throw new IllegalArgumentException();
            }
            
            this.script = ScriptsManager.loadScript( params[ 0 ] );
            
            this.variableResolver = new VariableResolver()
            {
                @Override
                public Object resolve( final String name )
                {
                    final ModelProperty property = element.getModelElementType().getProperty( name );
                    
                    if( property != null && property instanceof ValueProperty )
                    {
                        final ValueProperty prop = (ValueProperty) property;
                        final String val = element.read( prop ).getText();
                        return ( val != null ? val : "" );
                    }
                    
                    return name;
                }
            };
        }
    
        @Override
        public boolean isEnabled()
        {
            final Object result = this.script.execute( this.variableResolver );
            
            if( result instanceof Boolean )
            {
                return (Boolean) result;
            }
            
            return false;
        }
    }
    
    private static final class PropertyEnablementService extends EnablementService
    {
        private ValueProperty property;
        private ValueSerializationService serializer;    
        private Object[] values;
        
        @Override
        public void init( final IModelElement element,
                          final ModelProperty property,
                          final String[] params )
        {
            super.init( element, property, params );
            
            if( params.length != 2 )
            {
                throw new IllegalArgumentException();
            }
            
            final ModelProperty prop = element.getModelElementType().getProperty( params[ 0 ] );
            
            if( prop == null )
            {
                throw new IllegalArgumentException();
            }
    
            this.property = (ValueProperty) prop;
            this.serializer = element.service( this.property, ValueSerializationService.class );
            
            final String valuesString = params[ 1 ];
            
            if( valuesString.length() == 0 && this.property.isOfType( Boolean.class ) )
            {
                this.values = new Object[] { Boolean.TRUE };
            }
            else
            {
                final List<Object> valuesList = new ArrayList<Object>();
                
                for( String segment : valuesString.split( "," ) )
                {
                    final Object value = this.serializer.decode( segment );
                    
                    if( value != null )
                    {
                        valuesList.add( value );
                    }
                    else
                    {
                        final String message
                            = NLS.bind( Resources.couldNotDecode, 
                                        new Object[] { element.getModelElementType().getModelElementClass().getName(),
                                        property.getName(), segment } );
                        
                        SapphireModelingFrameworkPlugin.logError( message, null );
                    }
                }
                
                this.values = valuesList.toArray( new Enum<?>[ valuesList.size() ] );
            }
        }
    
        @Override
        public boolean isEnabled()
        {
            final IModelElement element = element();
            
            if( element.isPropertyEnabled( this.property ) )
            {
                final Value<Enum<?>> result;
                
                try
                {
                    result = element.read( this.property );
                }
                catch( Exception e )
                {
                    throw new RuntimeException( e );
                }
                
                final String res = result.getText( true );
                
                if( res != null )
                {
                    final Object val = this.serializer.decode( res );
                    
                    if( val != null )
                    {
                        for( Object value : this.values )
                        {
                            if( value.equals( val ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
    
            return false;
        }
    }

    private static final class Resources extends NLS
    {
        public static String couldNotDecode;
        
        static
        {
            initializeMessages( EnablementServiceFactory.class.getName(), Resources.class );
        }
    }

}
