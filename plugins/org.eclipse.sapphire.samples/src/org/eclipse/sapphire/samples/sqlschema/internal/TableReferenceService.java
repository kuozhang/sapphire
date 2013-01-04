/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.sqlschema.internal;

import org.eclipse.sapphire.samples.sqlschema.Schema;
import org.eclipse.sapphire.samples.sqlschema.Table;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TableReferenceService extends ReferenceService 
{

	@Override
    public Object resolve( final String reference ) 
    {
        if( reference != null )
        {
            final Schema schema = context( Schema.class );
            
            for( Table table : schema.getTables() )
            {
                if( reference.equals( table.getName().getText() ) )
                {
                    return table;
                }
            }
        }
        
        return null;
    }

}
