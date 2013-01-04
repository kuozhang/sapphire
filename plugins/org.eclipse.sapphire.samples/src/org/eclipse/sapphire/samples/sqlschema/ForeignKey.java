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

package org.eclipse.sapphire.samples.sqlschema;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.sqlschema.internal.TableReferenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public interface ForeignKey extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( ForeignKey.class );
	
    // *** ReferencedTable ***
    
    @Reference( target = Table.class )
    @Service( impl = TableReferenceService.class )
    @Required
    @PossibleValues( property = "/Tables/Name" )
    @XmlBinding( path = "referenced-table" )

    ValueProperty PROP_REFERENCED_TABLE = new ValueProperty( TYPE, "ReferencedTable" );

    ReferenceValue<String, Table> getReferencedTable();
    void setReferencedTable( String value );

}
