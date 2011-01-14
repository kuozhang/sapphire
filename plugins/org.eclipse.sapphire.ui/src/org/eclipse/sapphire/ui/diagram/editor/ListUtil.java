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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ListUtil 
{
	public static List<IModelElement> ListDiff(List<? extends IModelElement> list1, 
			List<? extends IModelElement> list2)
	{
		List<IModelElement> retList = new ArrayList<IModelElement>();
		if (list1.size() > list2.size())
		{
			for (IModelElement element : list1)
			{
				if (!list2.contains(element))
				{
					retList.add(element);
				}
			}
		}
		else if (list2.size() > list1.size())
		{			
			for (IModelElement element : list2)
			{
				if (!list1.contains(element))
				{
					retList.add(element);
				}
			}
		}
		return retList;
	}
	
}
