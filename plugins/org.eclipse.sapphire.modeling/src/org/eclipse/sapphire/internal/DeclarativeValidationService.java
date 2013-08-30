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

package org.eclipse.sapphire.internal;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.Validations;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.CompositeStatusFactory;
import org.eclipse.sapphire.modeling.el.FailSafeFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.el.parser.ExpressionLanguageParser;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.util.ListFactory;

/**
 * {@link ValidationService} implementation that derives its behavior from @{@link Validation} annotation.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class DeclarativeValidationService extends ValidationService
{
    private List<Rule> rules;
    
    @Override
    protected void initValidationService()
    {
        final Element element = context( Element.class );
        final PropertyDef property = context( PropertyDef.class );
        
        final ListFactory<Validation> annotations = ListFactory.start();
        
        if( property == null )
        {
            final ElementType type = element.type();
            
            annotations.add( type.getAnnotations( Validation.class ) );
            
            for( Validations v : type.getAnnotations( Validations.class ) )
            {
                annotations.add( v.value() );
            }
        }
        else
        {
            annotations.add( property.getAnnotations( Validation.class ) );
            
            for( Validations v : property.getAnnotations( Validations.class ) )
            {
                annotations.add( v.value() );
            }
        }
        
        final ModelElementFunctionContext context = new ModelElementFunctionContext( element );
        
        final Listener listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                refresh();
            }
        };
        
        final ListFactory<Rule> rulesListFactory = ListFactory.start();
        
        for( Validation annotation : annotations.result() )
        {
            if( annotation.severity() != Status.Severity.OK )
            {
                Function function = null;
                
                try
                {
                    function = ExpressionLanguageParser.parse( annotation.rule() );
                    function = FailSafeFunction.create( function, Boolean.class, false );
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                    function = null;
                }
                
                if( function != null )
                {
                    final FunctionResult conditionFunctionResult = function.evaluate( context );
                    conditionFunctionResult.attach( listener );
                    
                    rulesListFactory.add( new Rule( conditionFunctionResult, annotation.message(), annotation.severity() ) );
                }
            }
        }
        
        this.rules = rulesListFactory.result();
    }

    @Override
    protected Status compute()
    {
        final CompositeStatusFactory factory = Status.factoryForComposite();
        
        for( Rule rule : this.rules )
        {
            factory.merge( rule.validate() );
        }
        
        return factory.create();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        for( Rule rule : this.rules )
        {
            rule.dispose();
        }
        
        this.rules = null;
    }
    
    private static final class Rule
    {
        private final FunctionResult conditionFunctionResult;
        private final String message;
        private final Status.Severity severity;
        
        public Rule( final FunctionResult conditionFunctionResult,
                     final String message,
                     final Status.Severity severity )
        {
            this.conditionFunctionResult = conditionFunctionResult;
            this.message = message;
            this.severity = severity;
        }
        
        public Status validate()
        {
            if( ( (Boolean) this.conditionFunctionResult.value() ) == false )
            {
                return Status.createStatus( this.severity, this.message );
            }
            else
            {
                return Status.createOkStatus();
            }
        }
        
        public void dispose()
        {
            this.conditionFunctionResult.dispose();
        }
    }

    public static final class Condition extends ServiceCondition
    {
        @Override
        public boolean applicable( final ServiceContext context )
        {
            final PropertyDef property = context.find( PropertyDef.class );
            
            if( property == null )
            {
                final ElementType type = context.find( Element.class ).type();
                return ( type.hasAnnotation( Validation.class ) || type.hasAnnotation( Validations.class ) );
            }
            else
            {
                return ( property.hasAnnotation( Validation.class ) || property.hasAnnotation( Validations.class ) );
            }
        }
    }

}
