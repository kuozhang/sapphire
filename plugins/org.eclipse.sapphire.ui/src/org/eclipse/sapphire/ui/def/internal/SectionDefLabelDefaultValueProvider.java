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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;
import org.eclipse.sapphire.ui.def.SectionDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SectionDefLabelDefaultValueProvider extends DefaultValueService
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
        String defaultValue = null;
        
        final SectionDef section = context( SectionDef.class );
        
        if( section.parent() != null )
        {
            final Element parent = section.parent().element();
            
            if( parent instanceof MasterDetailsContentNodeDef )
            {
                defaultValue = ( (MasterDetailsContentNodeDef) parent ).getLabel().text();
            }
        }
        
        return new DefaultValueServiceData( defaultValue );
    }
    
}
