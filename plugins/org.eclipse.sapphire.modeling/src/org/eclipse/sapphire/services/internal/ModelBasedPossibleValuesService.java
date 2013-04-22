/******************************************************************************
 * Copyright (c) 2013 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Gregory Amerson - [384683] PossibleValuesServiceFromModel does not load values initially
 ******************************************************************************/

package org.eclipse.sapphire.services.internal;

import java.util.Collections;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyVisitor;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.util.SetFactory;

/**
 * Implementation of PossibleValuesService based on @PossibleValues annotation's property attribute..
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public final class ModelBasedPossibleValuesService extends PossibleValuesService
{
    private final ModelPath path;
    private Set<String> values;
    private boolean initialized;
    private boolean readPriorToInit;
    
    public ModelBasedPossibleValuesService( final ModelPath path,
                                            final String invalidValueMessageTemplate,
                                            final Status.Severity invalidValueSeverity,
                                            final boolean caseSensitive,
                                            final boolean ordered )
    {
        super( invalidValueMessageTemplate, invalidValueSeverity, caseSensitive, ordered );
        
        this.path = path;
        this.values = Collections.emptySet();
    }
    
    @Override
    protected void init()
    {
        super.init();
        
        final Element element = context( Element.class );
        
        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };
        
        element.attach( listener, this.path );
        
        refresh();
        
        element.attach
        (
            new FilteredListener<ElementDisposeEvent>()
            {
                @Override
                protected void handleTypedEvent( final ElementDisposeEvent event )
                {
                    element.detach( listener, ModelBasedPossibleValuesService.this.path );
                }
            }
        );
        
        this.initialized = true;
    }

    @Override
    protected void fillPossibleValues( final Set<String> values )
    {
        if( ! this.initialized )
        {
            this.readPriorToInit = true;
        }
        
        values.addAll( this.values );
    }
    
    private void refresh()
    {
        final Element element = context( Element.class );
        
        if( ! element.disposed() )
        {
            final SetFactory<String> newValuesFactory = SetFactory.start();
            
            context( Element.class ).visit
            (
                this.path,
                new PropertyVisitor()
                {
                    @Override
                    public boolean visit( final Value<?> property )
                    {
                        newValuesFactory.add( property.text() );
                        return true;
                    }
                }
            );
            
            final Set<String> newValues = newValuesFactory.result();
            
            if( ! this.values.equals( newValues ) )
            {
                this.values = newValues;
                
                if( this.initialized || this.readPriorToInit )
                {
                    broadcast();
                }
            }
        }
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            final ValueProperty property = context.find( ValueProperty.class );
            return ( property != null && property.hasAnnotation( PossibleValues.class ) && property.getAnnotation( PossibleValues.class ).property().length() > 0 );
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            final PossibleValues a = context.find( ValueProperty.class ).getAnnotation( PossibleValues.class );
            return new ModelBasedPossibleValuesService( new ModelPath( a.property() ), a.invalidValueMessage(), a.invalidValueSeverity(), a.caseSensitive(), a.ordered() );
        }
    }
    
    
}
