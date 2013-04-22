/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhhint;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;

import java.util.Collections;
import java.util.Set;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.def.CompositeDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentation;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationRef;
import org.eclipse.sapphire.ui.util.SapphireHelpSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class CompositePart extends FormPart
{
    @Override
    public CompositeDef definition()
    {
        return (CompositeDef) super.definition();
    }
    
    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_FORM );
    }

    @Override
    public final void render( final SapphireRenderingContext context )
    {
        if( ! visible() )
        {
            return;
        }
        
        Composite parent = createOuterComposite( context );
        
        final CompositeDef def = (CompositeDef) this.definition;
        final boolean indent = def.getIndent().content();
        final boolean scrollVertically = def.getScrollVertically().content();
        final boolean scrollHorizontally = def.getScrollHorizontally().content();
        
        if( indent )
        {
            final Label label = new Label( parent, SWT.NONE );
            label.setLayoutData( gd() );
        }

        final ScrolledComposite scrolledComposite;
        
        if( scrollVertically || scrollHorizontally )
        {
            final int style
                = ( scrollVertically ? SWT.V_SCROLL : SWT.NONE ) | 
                  ( scrollHorizontally ? SWT.H_SCROLL : SWT.NONE );
            
            scrolledComposite = new ScrolledComposite( parent, style );
            scrolledComposite.setExpandHorizontal( true );
            scrolledComposite.setExpandVertical( true );
            
            // ScrolledComposite does not seem to inherit background color like other controls, so
            // we need to set it explicitly.
            
            scrolledComposite.setBackground( parent.getBackground() );
            scrolledComposite.setBackgroundMode( SWT.INHERIT_DEFAULT );
            
            parent = scrolledComposite;
        }
        else
        {
            scrolledComposite = null;
        }
        
        final boolean scaleVertically = def.getScaleVertically().content(); 
        
        final int width = getWidth( -1 );
        final int height = getHeight( -1 );
        
        final GridData gd = gdwhint( gdhhint( gdhspan( ( scaleVertically ? gdfill() : gdhfill() ), ( indent ? 1 : 2 ) ), height ), width );
        
        final int marginLeft = def.getMarginLeft().content();
        final int marginRight = def.getMarginRight().content();
        final int marginTop = def.getMarginTop().content();
        final int marginBottom = def.getMarginBottom().content();
        
        final Composite composite = new Composite( parent, SWT.NONE ) {
            public Point computeSize (int wHint, int hHint, boolean changed) {
                if (this.getChildren().length == 0) {
                    return new Point(0, 0);
                }
                return super.computeSize(wHint, hHint, changed);
            }
        };
        composite.setLayout( glayout( 2, marginLeft, marginRight, marginTop, marginBottom ) );
        
        composite.setBackground( getSwtResourceCache().color( getBackgroundColor() ) );
        composite.setBackgroundMode( SWT.INHERIT_DEFAULT );

        if( scrolledComposite != null )
        {
            scrolledComposite.setContent( composite );
            scrolledComposite.setLayoutData( gd );
        }
        else
        {
            composite.setLayoutData( gd );
        }
        
        final ISapphireDocumentation doc = this.definition.getDocumentation().content();
        
        if( doc != null )
        {
            ISapphireDocumentationDef docdef = null;
            
            if( doc instanceof ISapphireDocumentationDef )
            {
                docdef = (ISapphireDocumentationDef) doc;
            }
            else
            {
                docdef = ( (ISapphireDocumentationRef) doc ).resolve();
            }
            
            if( docdef != null )
            {
                SapphireHelpSystem.setHelp( composite, docdef );
            }
        }

        final SapphireRenderingContext innerContext = new SapphireRenderingContext( this, context, composite );
        
        if( scrolledComposite != null )
        {
            scrolledComposite.setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        }
        
        final Listener childPartsListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof PartVisibilityEvent || event instanceof PartChildrenEvent )
                {
                    if( composite.isDisposed() )
                    {
                        return;
                    }
                    
                    final SapphirePart part = ( (PartEvent) event ).part();
                    
                    if( event instanceof PartChildrenEvent && ! ( part instanceof CompositePart || part instanceof SplitFormBlockPart ) )
                    {
                        attachChildPartsListener( part, this );
                    }
                    
                    for( Control control : composite.getChildren() )
                    {
                        control.dispose();
                    }
                    
                    CompositePart.super.render( innerContext );
                    
                    if( scrolledComposite != null )
                    {
                        scrolledComposite.setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
                    }
                    
                    context.layout();
                }
            }
        };
        
        for( SapphirePart child : getChildParts() )
        {
            attachChildPartsListener( child, childPartsListener );
        }
        
        composite.addDisposeListener
        (
            new DisposeListener()
            {
                public void widgetDisposed( final DisposeEvent event )
                {
                    detachChildPartsListener( CompositePart.this, childPartsListener );
                }
            }
        );
        
        super.render( innerContext );
    }
    
    private static void attachChildPartsListener( final SapphirePart part,
                                                  final Listener listener )
    {
        part.attach( listener );
        
        if( part instanceof FormPart && ! ( part instanceof CompositePart || part instanceof SplitFormBlockPart ) )
        {
            for( SapphirePart child : ( (FormPart) part ).getChildParts() )
            {
                attachChildPartsListener( child, listener );
            }
        }
        else if( part instanceof ConditionalPart )
        {
            for( SapphirePart child : ( (ConditionalPart) part ).getCurrentBranchContent() )
            {
                attachChildPartsListener( child, listener );
            }
        }
    }
    
    private static void detachChildPartsListener( final SapphirePart part,
                                                  final Listener listener )
    {
        part.detach( listener );
        
        if( part instanceof FormPart && ! ( part instanceof CompositePart || part instanceof SplitFormBlockPart )  )
        {
            for( SapphirePart child : ( (FormPart) part ).getChildParts() )
            {
                detachChildPartsListener( child, listener );
            }
        }
        else if( part instanceof ConditionalPart )
        {
            for( SapphirePart child : ( (ConditionalPart) part ).getCurrentBranchContent() )
            {
                detachChildPartsListener( child, listener );
            }
        }
    }
    
    protected Composite createOuterComposite( final SapphireRenderingContext context )
    {
        return context.getComposite();
    }
    
    public int getWidth( final int defaultValue )
    {
        final Integer width = definition().getWidth().content();
        return ( width == null || width < 1 ? defaultValue : width );
    }
    
    public int getHeight( final int defaultValue )
    {
        final Integer height = definition().getHeight().content();
        return ( height == null || height < 1 ? defaultValue : height );
    }
    
}
