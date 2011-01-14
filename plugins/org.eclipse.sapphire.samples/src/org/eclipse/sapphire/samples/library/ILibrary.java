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
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl
@XmlRootBinding( elementName = "library")

public interface ILibrary extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( ILibrary.class );
	
    // *** Books ***

    @Type( base = IBook.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "book", type = IBook.class ) )
    
    ListProperty PROP_BOOKS = new ListProperty( TYPE, "Books" );
    
    ModelElementList<IBook> getBooks();
	

}
