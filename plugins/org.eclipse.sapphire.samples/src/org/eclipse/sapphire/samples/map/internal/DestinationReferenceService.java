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

package org.eclipse.sapphire.samples.map.internal;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.samples.map.IDestination;
import org.eclipse.sapphire.samples.map.IMap;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DestinationReferenceService extends ReferenceService 
{
    @Override
    public Object resolve(String reference) 
    {
        if (reference != null)
        {
            IMap map = context( IMap.class );
            ModelElementList<IDestination> dests = map.getDestinations();
            for (IDestination dest : dests)
            {
                if (reference.equals(dest.getName().getContent()))
                {
                    return dest;
                }
            }
        }
        return null;
    }

}
