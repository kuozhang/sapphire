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

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeFactoryRef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MasterDetailsTreeNodeFactoryRefMethods
{
    public static IMasterDetailsContentNodeFactoryDef resolve( final IMasterDetailsContentNodeFactoryRef ref )
    {
        final ISapphireUiDef rootdef = ref.nearest( ISapphireUiDef.class );
        return (IMasterDetailsContentNodeFactoryDef) rootdef.getPartDef( ref.getPart().getText(), true, IMasterDetailsContentNodeFactoryDef.class );
    }
    
}
