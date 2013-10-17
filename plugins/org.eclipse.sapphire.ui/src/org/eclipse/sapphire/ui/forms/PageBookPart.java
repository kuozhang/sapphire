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

package org.eclipse.sapphire.ui.forms;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.AndFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.PartVisibilityEvent;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.sapphire.ui.forms.swt.internal.PageBookPresentation;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PageBookPart extends FormComponentPart
{
    private static CompositeDef systemDefaultPageDef;
    
    private Map<Object,CompositeDef> pageDefs;
    private CompositeDef defaultPageDef;
    private CompositePart currentPage;
    private Map<PageCacheKey,CompositePart> pages = Collections.synchronizedMap( new HashMap<PageCacheKey,CompositePart>() );
    private boolean exposePageValidationState = false;
    private Listener childPartValidationListener = null;
    
    @Override
    protected void init()
    {
        super.init();
        
        final PageBookDef def = (PageBookDef) this.definition;
        
        this.pageDefs = new LinkedHashMap<Object,CompositeDef>();
        
        for( PageBookKeyMapping page : def.getPages() )
        {
            final Object key = parsePageKey( page.getKey().text() );
            this.pageDefs.put( key, page );
        }
        
        this.defaultPageDef = def.getDefaultPage();
        
        if( this.defaultPageDef.getContent().isEmpty() )
        {
            if( systemDefaultPageDef == null )
            {
                final ISapphireUiDef root = ISapphireUiDef.TYPE.instantiate();
                systemDefaultPageDef = (CompositeDef) root.getPartDefs().insert( CompositeDef.TYPE );
            }
            
            this.defaultPageDef = systemDefaultPageDef;
        }
    }
    
    @Override
    protected Function initVisibleWhenFunction()
    {
        final Function function = new Function()
        {
            @Override
            public String name()
            {
                return "VisibleIfChildrenVisible";
            }

            @Override
            public FunctionResult evaluate( final FunctionContext context )
            {
                return new FunctionResult( this, context )
                {
                    @Override
                    protected void init()
                    {
                        final Listener pageVisibilityListener = new FilteredListener<PartVisibilityEvent>()
                        {
                            @Override
                            protected void handleTypedEvent( final PartVisibilityEvent event )
                            {
                                refresh();
                            }
                        };
                        
                        PageBookPart.this.attach
                        (
                            new FilteredListener<PageChangedEvent>()
                            {
                                @Override
                                protected void handleTypedEvent( final PageChangedEvent event )
                                {
                                    final CompositePart page = getCurrentPage();
                                    
                                    if( page != null )
                                    {
                                        page.attach( pageVisibilityListener );
                                    }
                                    
                                    refresh();
                                }
                            }
                        );
                        
                        final CompositePart page = getCurrentPage();
                        
                        if( page != null )
                        {
                            page.attach( pageVisibilityListener );
                        }
                    }

                    @Override
                    protected Object evaluate()
                    {
                        boolean visible = false;
                        
                        final CompositePart page = getCurrentPage();
                        
                        if( page != null )
                        {
                            visible = page.visible();
                        }
                        
                        return visible;
                    }
                };
            }
        };
        
        function.init();
        
        final Function base = super.initVisibleWhenFunction();
        
        if( base == null )
        {
            return function;
        }
        else
        {
            return AndFunction.create( base, function );
        }
    }

    public final CompositePart getCurrentPage()
    {
        return this.currentPage;
    }

    protected final void changePage( final Element modelElementForPage,
                                     final Object pageKey )
    {
        CompositeDef pageDef = this.defaultPageDef;
        
        if( pageKey != null )
        {
            for( Map.Entry<Object,CompositeDef> entry : this.pageDefs.entrySet() )
            {
                if( entry.getKey().equals( pageKey ) )
                {
                    pageDef = entry.getValue();
                    break;
                }
            }
        }
        
        changePage( modelElementForPage, pageDef );
    }

    private void changePage( final Element modelElementForPage,
                             final CompositeDef pageDef )
    {
        if( modelElementForPage == null )
        {
            throw new IllegalArgumentException();
        }
        
        final CompositePart oldPage = this.currentPage;
        
        if( oldPage != null && this.childPartValidationListener != null )
        {
            oldPage.detach( this.childPartValidationListener );
        }

        this.currentPage = null;
        
        if( pageDef != null )
        {
            final PageCacheKey key = new PageCacheKey( pageDef, modelElementForPage );
            
            this.currentPage = this.pages.get( key );
            
            if( this.currentPage == null )
            {
                this.currentPage = createPagePart( modelElementForPage, pageDef );
                this.pages.put( key, this.currentPage );
                
                final Listener elementDisposeListener = new FilteredListener<ElementDisposeEvent>()
                {
                    @Override
                    protected void handleTypedEvent( final ElementDisposeEvent event )
                    {
                        final CompositePart page = PageBookPart.this.pages.remove( key );
                        
                        if( page != null )
                        {
                            page.dispose();
                        }
                    }
                };
                
                modelElementForPage.attach( elementDisposeListener );
                
                final Listener pageDisposeListener = new FilteredListener<org.eclipse.sapphire.DisposeEvent>()
                {
                    @Override
                    protected void handleTypedEvent( final org.eclipse.sapphire.DisposeEvent event )
                    {
                        modelElementForPage.detach( elementDisposeListener );
                    }
                };
                
                this.currentPage.attach( pageDisposeListener );
            }
            
            if( this.childPartValidationListener != null )
            {
                this.currentPage.attach( this.childPartValidationListener );
            }
        }
        
        refreshValidation();
        
        broadcast( new PageChangedEvent( this ) );
    }
    
    protected abstract Object parsePageKey( final String pageKeyString );
    
    protected CompositePart createPagePart( final Element modelElementForPage, final CompositeDef pageDef )
    {
        return (CompositePart) create( this, modelElementForPage, pageDef, this.params );
    }

    @Override
    protected Status computeValidation()
    {
        if( this.exposePageValidationState == true )
        {
            final CompositePart currentPage = getCurrentPage();
            
            if( currentPage != null )
            {
                return currentPage.validation();
            }
        }
        
        return Status.createOkStatus();
    }
    
    protected final void setExposePageValidationState( final boolean exposePageValidationState )
    {
        if( this.exposePageValidationState != exposePageValidationState )
        {
            this.exposePageValidationState = exposePageValidationState;
            
            if( this.exposePageValidationState == true )
            {
                this.childPartValidationListener = new FilteredListener<PartValidationEvent>()
                {
                    @Override
                    protected void handleTypedEvent( PartValidationEvent event )
                    {
                        refreshValidation();
                    }
                };
                
                if( this.currentPage != null )
                {
                    this.currentPage.attach( this.childPartValidationListener );
                }
            }
            else
            {
                this.childPartValidationListener = null;
            }
            
            refreshValidation();
        }
    }
    
    @Override
    public FormComponentPresentation createPresentation( final SwtPresentation parent, final Composite composite )
    {
        return new PageBookPresentation( this, parent, composite );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        for( final CompositePart page : this.pages.values() )
        {
            page.dispose();
        }
        
        this.pageDefs = null;
        this.defaultPageDef = null;
        this.currentPage = null;
        this.pages = null;
        this.childPartValidationListener = null;
    }
    
    public static final class PageChangedEvent extends PartEvent
    {
        public PageChangedEvent( final SapphirePart part )
        {
            super( part );
        }
    }
    
    protected static final class ClassBasedKey
    {
        private final Class<?> cl;
        
        private ClassBasedKey( final Class<?> cl )
        {
            this.cl = cl;
        }
        
        public static ClassBasedKey create( final Class<?> cl )
        {
            return ( cl == null ? null : new ClassBasedKey( cl ) );
        }
        
        public static ClassBasedKey create( final Object obj )
        {
            return ( obj == null ? null : new ClassBasedKey( obj.getClass() ) );
        }
        
        public int hashCode()
        {
            return this.cl == null ? -1 : this.cl.hashCode();
        }
        
        public boolean equals( final Object obj )
        {
            if( ! ( obj instanceof ClassBasedKey ) )
            {
                return false;
            }
            else
            {
                final Class<?> cl2 = ( (ClassBasedKey) obj ).cl;
                return this.cl.isAssignableFrom( cl2 ) || cl2.isAssignableFrom( this.cl );
            }
        }
    }
    
    private static final class PageCacheKey
    {
        private final CompositeDef def;
        private final Element element;
        
        public PageCacheKey( final CompositeDef def, final Element element )
        {
            this.def = def;
            this.element = element;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode( this.def ) ^ System.identityHashCode( this.element );
        }

        @Override
        public boolean equals( final Object obj )
        {
            if( obj instanceof PageCacheKey )
            {
                final PageCacheKey key = (PageCacheKey) obj;
                return ( this.def == key.def && this.element == key.element );
            }
            
            return false;
        }
    }

}
