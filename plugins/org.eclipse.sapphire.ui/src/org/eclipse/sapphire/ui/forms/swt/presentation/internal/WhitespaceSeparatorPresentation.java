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

import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.gdhhint;
import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.glayout;

import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.WhitespaceSeparatorPart;
import org.eclipse.sapphire.ui.forms.swt.presentation.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.presentation.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class WhitespaceSeparatorPresentation extends FormComponentPresentation
{
    public WhitespaceSeparatorPresentation( final FormComponentPart part, final SwtPresentation parent, final Composite composite )
    {
        super( part, parent, composite );
    }

    @Override
    public WhitespaceSeparatorPart part()
    {
        return (WhitespaceSeparatorPart) super.part();
    }
    
    @Override
    public void render()
    {
        final Composite composite = new Composite( composite(), SWT.NONE );
        composite.setLayoutData( gdhhint( gdhspan( gdhfill(), 2 ), part().definition().getSize().content() ) );
        composite.setLayout( glayout( 1, 0, 0, 0, 0 ) );
        
        register( composite );
    }

}