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

import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a> 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label(standard = "form editor page")
@GenerateImpl
public interface FormEditorPageDef extends IEditorPageDef {

    ModelElementType TYPE = new ModelElementType(FormEditorPageDef.class);

    // *** Sections ***
    
    @Type(base = FormEditorSectionDef.class)
    @XmlListBinding(path = "sections", mappings = {
            @XmlListBinding.Mapping(element = "section", type = FormEditorSectionDef.class)})
    ListProperty PROP_SECTIONS = new ListProperty(TYPE, "Sections");

    ModelElementList<FormEditorSectionDef> getSections();
    
    // *** NumColumns ***

    @Type(base = Integer.class)
    @Label( standard = "number of columns" )
    @DefaultValue( text = "2" )
    @XmlBinding( path = "num-columns" )
    ValueProperty PROP_NUM_COLUMNS = new ValueProperty( TYPE, "NumColumns" );
    
    Value<Integer> getNumColumns();
    void setNumColumns( Integer value );
    void setNumColumns( String value );

}
