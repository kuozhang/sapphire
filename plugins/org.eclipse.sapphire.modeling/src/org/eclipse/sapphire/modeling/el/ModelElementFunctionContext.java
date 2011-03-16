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

package org.eclipse.sapphire.modeling.el;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.modeling.localization.SourceLanguageLocalizationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class ModelElementFunctionContext

    extends FunctionContext
    
{
    private final IModelElement element;
    private final LocalizationService localizationService;
    
    public ModelElementFunctionContext( final IModelElement element )
    {
        this( element, SourceLanguageLocalizationService.INSTANCE );
    }
    
    public ModelElementFunctionContext( final IModelElement element,
                                        final LocalizationService localizationService )
    {
        this.element = element;
        this.localizationService = localizationService;
    }
    
    public final IModelElement element()
    {
        return this.element;
    }
    
    @Override
    public FunctionResult property( final Object element,
                                    final String name )
    {
        if( element == this || element instanceof IModelElement )
        {
            final IModelElement el = ( element == this ? element() : (IModelElement) element );
            final ModelElementType type = el.getModelElementType();
            final ModelProperty property = type.getProperty( name );
            
            if( property != null )
            {
                final Function f = new Function()
                {
                    @Override
                    public FunctionResult evaluate( final FunctionContext context )
                    {
                        return new FunctionResult( this, context )
                        {
                            private ModelPropertyListener listener;
                            
                            @Override
                            protected void init()
                            {
                                super.init();
                                
                                this.listener = new ModelPropertyListener()
                                {
                                    @Override
                                    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                                    {
                                        refresh();
                                    }
                                };
                                
                                el.addListener( this.listener, property.getName() );
                            }
            
                            @Override
                            protected Object evaluate()
                            {
                                Object res = el.read( property );
                                
                                if( res instanceof ModelElementHandle<?> )
                                {
                                    res = ( (ModelElementHandle<?>) res ).element();
                                }
                                
                                return res;
                            }
                            
                            @Override
                            public void dispose()
                            {
                                super.dispose();
                                el.removeListener( this.listener, property.getName() );
                            }
                        };
                    }
                };
                
                f.init();
                
                return f.evaluate( this );
            }
        }
        
        return super.property( element, name );
    }

    @Override
    public LocalizationService getLocalizationService()
    {
        return this.localizationService;
    }
    
}
