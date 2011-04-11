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

import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.PossibleValuesService;
import org.eclipse.sapphire.ui.def.ISapphireHint;
import org.eclipse.sapphire.ui.def.ISapphirePropertyEditorDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireHintValuePossibleValuesService

    extends PossibleValuesService
    
{
    @Override
    protected void fillPossibleValues( final SortedSet<String> values )
    {
        final String hint = ( (ISapphireHint) element() ).getName().getText();
        
        if( hint != null )
        {
            if( hint.equals( ISapphirePropertyEditorDef.HINT_CHECKBOX_LAYOUT ) )
            {
                values.add( ISapphirePropertyEditorDef.HINT_VALUE_CHECKBOX_LAYOUT_LEADING_LABEL );
                values.add( ISapphirePropertyEditorDef.HINT_VALUE_CHECKBOX_LAYOUT_TRAILING_LABEL );
                values.add( ISapphirePropertyEditorDef.HINT_VALUE_CHECKBOX_LAYOUT_TRAILING_LABEL_INDENTED );
            }
        }
    }
    
    @Override
    public int getInvalidValueSeverity( final String invalidValue )
    {
        return IStatus.OK;
    }
    
}
