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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;
import org.eclipse.sapphire.ui.def.CompositeDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class CompositeMarginWidthHeightDefaultValueProvider extends DefaultValueService
{
    @Override
    public DefaultValueServiceData data()
    {
        refresh();
        return super.data();
    }

    @Override
    protected DefaultValueServiceData compute()
    {
        final CompositeDef def = context( CompositeDef.class );
        
        if( def.getScrollHorizontally().content() == true || def.getScrollVertically().content() == true )
        {
            return new DefaultValueServiceData( "10" );
        }
        else
        {
            return new DefaultValueServiceData( "0" );
        }
    }
    
}
