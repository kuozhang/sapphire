/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [329102] excess scroll space in editor sections
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhindent;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.swt.renderer.SwtUtil.reflowOnResize;

import org.eclipse.sapphire.ui.def.ISapphireLabelDef;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireLabel

    extends SapphirePart
    
{
    @Override
    public void render( final SapphireRenderingContext context )
    {
        final ISapphireLabelDef def = (ISapphireLabelDef) this.definition;
        
        final Label l = new Label( context.getComposite(), SWT.WRAP );
        l.setLayoutData( gdhindent( gdwhint( gdhspan( gdhfill(), 2 ), 100 ), 9 ) );
        l.setText( def.getText().getLocalizedText() );
        context.adapt( l );
        
        reflowOnResize( l );
    }
    
}
