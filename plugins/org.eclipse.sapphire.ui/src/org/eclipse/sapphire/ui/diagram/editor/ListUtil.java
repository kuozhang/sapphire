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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Element;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ListUtil 
{
    public static List<Element> ListDiff(List<? extends Element> list1, 
            List<? extends Element> list2)
    {
        List<Element> retList = new ArrayList<Element>();
        for (Element element : list1)
        {
            if (!list2.contains(element))
            {
                retList.add(element);
            }
        }
        
        return retList;
    }
    
    public static boolean ListDiffers(List<? extends Element> list1, 
    		List<? extends Element> list2)
    {
    	if (list1.size() != list2.size())
    	{
    		return true;
    	}
    	for (int i = 0; i < list1.size(); i++)
    	{
    		Element element1 = list1.get(i);
    		Element element2 = list2.get(i);
    		if ((element1 != null && element2 != null && element1 != element2) ||
    				(element1 != null && element2 == null) ||
    				(element1 == null && element2 != null))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    	
}
