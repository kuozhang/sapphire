/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.library;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IBook extends IModelElement 
{
    ModelElementType TYPE = new ModelElementType( IBook.class );

    // *** Title ***
    
    @XmlBinding( path = "title" )
    @Label( standard = "title" )
    @NonNullValue

    ValueProperty PROP_TITLE = new ValueProperty( TYPE, "Title" );

    Value<String> getTitle();
    void setTitle( String name );

    // *** Author ***
    
    @XmlBinding( path = "Author" )
    @Label( standard = "Author" )
    @NonNullValue

    ValueProperty PROP_AUTHOR = new ValueProperty( TYPE, "Author" );

    Value<String> getAuthor();
    void setAuthor( String author );
    
    // *** ReferencedBooks ***
    
    @Type( base = IBookReference.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "referenced-book", type = IBookReference.class ) )
    
    ListProperty PROP_REFERENCED_BOOKS = new ListProperty( TYPE, "ReferencedBooks" );
    
    ModelElementList<IBookReference> getReferencedBooks();
    
}
