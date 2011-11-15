/******************************************************************************
 * Copyright (c) 2011 Red Hat and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Cernich - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 ******************************************************************************/
package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a> 
 */

@Label( standard = "section" )
@GenerateImpl
public interface FormEditorSectionDef extends ISapphireSectionDef {

    ModelElementType TYPE = new ModelElementType( FormEditorSectionDef.class );
    
    // *** ColumnSpan ***
    
    @Type(base = Integer.class)
    @Label( standard = "column span" )
    @DefaultValue( text = "1" )
    @Localizable
    @XmlBinding( path = "column-span" )
    ValueProperty PROP_COLUMN_SPAN = new ValueProperty( TYPE, "ColumnSpan" );
    
    Value<Integer> getColumnSpan();
    void setColumnSpan( Integer value );
    void setColumnSpan( String value );
    
    // *** RowSpan ***

    @Type(base = Integer.class)
    @Label( standard = "row span" )
    @DefaultValue( text = "1" )
    @Localizable
    @XmlBinding( path = "row-span" )
    ValueProperty PROP_ROW_SPAN = new ValueProperty( TYPE, "RowSpan" );
    
    Value<Integer> getRowSpan();
    void setRowSpan( Integer value );
    void setRowSpan( String value );
}
