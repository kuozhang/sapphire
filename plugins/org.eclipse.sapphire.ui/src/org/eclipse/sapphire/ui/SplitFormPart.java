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

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.def.SplitFormBlockDef;
import org.eclipse.sapphire.ui.def.SplitFormDef;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SplitFormPart extends FormPart
{
    private int[] weights;
    
    @Override
    protected void init()
    {
        super.init();
        
        final List<SplitFormBlockDef> blocks = definition().getBlocks();
        final int count = blocks.size();
        
        this.weights = new int[ count ];
        
        for( int i = 0; i < count; i++ )
        {
            this.weights[ i ] = blocks.get( i ).getWeight().content();
        }
    }

    @Override
    protected List<SapphirePart> initChildParts()
    {
        final Element element = getLocalModelElement();
        final ListFactory<SapphirePart> partsListFactory = ListFactory.start();
        
        for( SplitFormBlockDef splitFormBlockDef : definition().getBlocks() )
        {
            partsListFactory.add( create( this, element, splitFormBlockDef, this.params ) );
        }
        
        return partsListFactory.result();
    }

    @Override
    public SplitFormDef definition()
    {
        return (SplitFormDef) super.definition();
    }
    
    public Orientation getOrientation()
    {
        return definition().getOrientation().content();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    
    public List<SplitFormBlockPart> getChildParts()
    {
        return (List<SplitFormBlockPart>) super.getChildParts();
    }

    @Override
    public final void render( final SapphireRenderingContext context )
    {
        final int formMarginLeft = definition().getMarginLeft().content();
        final int formMarginRight = definition().getMarginRight().content();
        final int formMarginTop = definition().getMarginTop().content();
        final int formMarginBottom = definition().getMarginBottom().content();
        
        final Composite formMarginsComposite = new Composite( context.getComposite(), SWT.NONE );
        formMarginsComposite.setLayout( glayout( 1, formMarginLeft, formMarginRight, formMarginTop, formMarginBottom ) );
        formMarginsComposite.setLayoutData( gdhspan( ( getScaleVertically() ? gdfill() : gdhfill() ), 2 ) );
        
        formMarginsComposite.setBackground( getSwtResourceCache().color( getBackgroundColor() ) );
        formMarginsComposite.setBackgroundMode( SWT.INHERIT_DEFAULT );
        
        final SashForm form = new SashForm( formMarginsComposite, ( getOrientation() == Orientation.HORIZONTAL ? SWT.HORIZONTAL : SWT.VERTICAL ) | SWT.SMOOTH );
        form.setLayoutData( gdfill() );
        form.setBackground( getSwtResourceCache().color( getBackgroundColor() ) );
        form.setBackgroundMode( SWT.INHERIT_DEFAULT );
        
        final List<SplitFormBlockPart> blockParts = getChildParts();
        final int blockPartsCount = blockParts.size();
        
        for( int i = 0; i < blockPartsCount; i++ )
        {
            final SplitFormBlockPart block = blockParts.get( i );
            final Composite blockComposite = new Composite( form, SWT.NONE );
            blockComposite.setBackground( getSwtResourceCache().color( block.getBackgroundColor() ) );
            blockComposite.setBackgroundMode( SWT.INHERIT_DEFAULT );
            final SapphireRenderingContext blockContext = new SapphireRenderingContext( this, context, blockComposite );
            
            final int blockMarginLeft = block.definition().getMarginLeft().content();
            final int blockMarginRight = block.definition().getMarginRight().content() + ( i < blockPartsCount - 1 && getOrientation() == Orientation.HORIZONTAL ? 4 : 0 );
            final int blockMarginTop = block.definition().getMarginTop().content() + ( i > 0 && getOrientation() == Orientation.VERTICAL ? 1 : 0 );
            final int blockMarginBottom = block.definition().getMarginBottom().content() + ( i < blockPartsCount - 1 && getOrientation() == Orientation.VERTICAL ? 1 : 0 );
            blockComposite.setLayout( glayout( 2, blockMarginLeft, blockMarginRight, blockMarginTop, blockMarginBottom ) );
            
            block.render( blockContext );
            
            final Listener blockChildPartsListener = new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof PartVisibilityEvent || event instanceof PartChildrenEvent )
                    {
                        final SapphirePart part = ( (PartEvent) event ).part();
                        
                        if( event instanceof PartChildrenEvent && ! ( part instanceof CompositePart || part instanceof SplitFormBlockPart ) )
                        {
                            attachChildPartsListener( part, this );
                        }
                        
                        for( Control control : blockComposite.getChildren() )
                        {
                            control.dispose();
                        }
                        
                        block.render( blockContext );
                        context.layout();
                    }
                }
            };
            
            for( SapphirePart child : block.getChildParts() )
            {
                attachChildPartsListener( child, blockChildPartsListener );
            }
            
            final int blockIndex = i;
            
            blockComposite.addControlListener
            (
                new ControlAdapter()
                {
                    @Override
                    public void controlResized( final ControlEvent event )
                    {
                        SplitFormPart.this.weights[ blockIndex ] = ( getOrientation() == Orientation.HORIZONTAL ? blockComposite.getSize().x : blockComposite.getSize().y );
                    }
                }
            );
            
            blockComposite.addDisposeListener
            (
                new DisposeListener()
                {
                    public void widgetDisposed( final DisposeEvent event )
                    {
                        detachChildPartsListener( block, blockChildPartsListener );
                    }
                }
            );
        }
        
        form.setWeights( this.weights );
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
        
        if( part instanceof FormPart && ! ( part instanceof CompositePart || part instanceof SplitFormBlockPart ) )
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
    
}
