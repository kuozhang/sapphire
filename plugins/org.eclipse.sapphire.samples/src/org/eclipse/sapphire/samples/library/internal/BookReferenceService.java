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

package org.eclipse.sapphire.samples.library.internal;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ReferenceService;
import org.eclipse.sapphire.samples.library.IBook;
import org.eclipse.sapphire.samples.library.ILibrary;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class BookReferenceService extends ReferenceService 
{
	@Override
	public Object resolve(String reference) 
	{
		if (reference != null)
		{
			ILibrary library = element().nearest(ILibrary.class);
			ModelElementList<IBook> books = library.getBooks();
			for (IBook book : books)
			{
				if (reference.equals(book.getTitle().getContent()))
				{
					return book;
				}
			}
		}
		return null;
	}

}
