/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.sqlschema;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public enum ColumnType 
{
    @Label( standard = "varchar" )
    @EnumSerialization( primary = "VARCHAR" )
    
    VARCHAR,
    
    @Label( standard = "integer" )
    @EnumSerialization( primary = "INTEGER" )
    
    INTEGER,
    
    @Label( standard = "date" )
    @EnumSerialization( primary = "DATE" )
    
    DATE

}
