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

package org.eclipse.sapphire.ui.forms.swt.presentation.internal;

import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.glayout;

import org.eclipse.sapphire.ui.def.Orientation;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.SplitFormPart;
import org.eclipse.sapphire.ui.forms.SplitFormSectionPart;
import org.eclipse.sapphire.ui.forms.swt.presentation.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SplitFormSectionPresentation extends CompositePresentation
{
    public SplitFormSectionPresentation( final FormComponentPart part, final SwtPresentation parent, final Composite composite )
    {
        super( part, parent, composite );
    }

    @Override
    public SplitFormSectionPart part()
    {
        return (SplitFormSectionPart) super.part();
    }
    
    @Override
    public void render()
    {
        final SplitFormSectionPart part = part();
        final SplitFormPart parent = part.parent();
        final Orientation orientation = parent.orientation();
        final int sectionCount = parent.children().all().size();
        final int sectionIndex = parent.children().all().indexOf( part );
        
        final Composite outerComposite = new Composite( composite(), SWT.NONE );

        register( outerComposite );
        
        outerComposite.setBackground( resources().color( part.getBackgroundColor() ) );
        outerComposite.setBackgroundMode( SWT.INHERIT_DEFAULT );
        
        outerComposite.setLayout
        (
            glayout
            (
                1,
                0,
                ( sectionIndex < sectionCount - 1 && orientation == Orientation.HORIZONTAL ? 4 : 0 ),
                ( sectionIndex > 0 && orientation == Orientation.VERTICAL ? 1 : 0 ),
                ( sectionIndex < sectionCount - 1 && orientation == Orientation.VERTICAL ? 1 : 0 )
            )
        );
        
        outerComposite.addControlListener
        (
            new ControlAdapter()
            {
                @Override
                public void controlResized( final ControlEvent event )
                {
                    part.weight( orientation == Orientation.HORIZONTAL ? outerComposite.getSize().x : outerComposite.getSize().y );
                }
            }
        );
        
        final Composite innerComposite = new Composite( outerComposite, SWT.NONE );
        innerComposite.setLayoutData( gdfill() );
        
        render( innerComposite );
    }

}
