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
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.library.internal.BookReferenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IBookReference extends IModelElement 
{
	ModelElementType TYPE = new ModelElementType( IBookReference.class );
	
	// *** ReferencedBook ***
	
	@Reference( target = IBook.class, service = BookReferenceService.class )
	@XmlBinding( path = "referenced-book")
	@NonNullValue
	@Label(standard = "referenced book")

	ValueProperty PROP_REFERENCED_BOOK = new ValueProperty( TYPE, "ReferencedBook" );

    ReferenceValue<IBook> getReferencedBook();
    void setReferencedBook( String name );
	
}
