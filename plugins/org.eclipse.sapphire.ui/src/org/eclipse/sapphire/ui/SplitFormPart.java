/******************************************************************************
 * Copyright (c) 2011 Oracle
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.def.SplitFormDef;
import org.eclipse.sapphire.ui.def.SplitFormSectionDef;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SplitFormPart extends SapphirePartContainer
{
    @Override
    protected List<SapphirePart> initChildParts()
    {
        final IModelElement element = getLocalModelElement();
        final List<SapphirePart> childParts = new ArrayList<SapphirePart>();
        
        for( SplitFormSectionDef splitFormSectionDef : getDefinition().getSections() )
        {
            final SapphirePart childPart = create( this, element, splitFormSectionDef, this.params );
            childParts.add( childPart );
        }
        
        return childParts;
    }

    @Override
    public SplitFormDef getDefinition()
    {
        return (SplitFormDef) super.getDefinition();
    }
    
    public Orientation getOrientation()
    {
        return getDefinition().getOrientation().getContent();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    
    public List<SplitFormSectionPart> getChildParts()
    {
        return (List<SplitFormSectionPart>) super.getChildParts();
    }

    @Override
    public final void render( final SapphireRenderingContext context )
    {
        final SashForm form = new SashForm( context.getComposite(), ( getOrientation() == Orientation.HORIZONTAL ? SWT.HORIZONTAL : SWT.VERTICAL ) | SWT.SMOOTH );
        form.setLayoutData( gdhspan( ( getScaleVertically() ? gdfill() : gdhfill() ), 2 ) );
        
        final List<SplitFormSectionPart> sectionParts = getChildParts();
        final int sectionPartsCount = sectionParts.size();
        final int[] weights = new int[ sectionPartsCount ];
        
        for( int i = 0; i < sectionPartsCount; i++ )
        {
            final SplitFormSectionPart section = sectionParts.get( i );
            final Composite sectionComposite = new Composite( form, SWT.NONE );
            final SapphireRenderingContext sectionContext = new SapphireRenderingContext( this, context, sectionComposite );
            
            final int rightMargin = ( i < sectionPartsCount - 1 && getOrientation() == Orientation.HORIZONTAL ? 4 : 0 );
            final int bottomMargin = ( i < sectionPartsCount - 1 && getOrientation() == Orientation.VERTICAL ? 1 : 0 );
            final int topMargin = ( i > 0 && getOrientation() == Orientation.VERTICAL ? 1 : 0 );
            sectionComposite.setLayout( glayout( 2, 0, rightMargin, topMargin, bottomMargin ) );
            
            section.render( sectionContext );
            
            weights[ i ] = section.getWeight();
        }
        
        form.setWeights( weights );
    }
    
}
