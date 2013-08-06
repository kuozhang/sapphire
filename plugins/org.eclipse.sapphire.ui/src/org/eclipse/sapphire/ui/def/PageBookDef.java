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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "page book" )

public interface PageBookDef extends FormComponentDef
{
    ElementType TYPE = new ElementType( PageBookDef.class );
    
    // *** Pages ***
    
    @Label( standard = "pages" )
    @Type( base = PageBookKeyMapping.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "panel", type = PageBookKeyMapping.class ) )
    
    ListProperty PROP_PAGES = new ListProperty( TYPE, "Pages" );
    
    ElementList<PageBookKeyMapping> getPages();

    // *** DefaultPage ***
    
    @Type( base = FormDef.class )
    @Label( standard = "default page" )
    @XmlBinding( path = "default-panel" )
    
    ImpliedElementProperty PROP_DEFAULT_PAGE = new ImpliedElementProperty( TYPE, "DefaultPage" );
    
    FormDef getDefaultPage();

}
