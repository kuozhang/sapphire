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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireRemoveFeature extends DefaultRemoveFeature 
{
    public SapphireRemoveFeature(IFeatureProvider fp)
    {
        super(fp);
    }

    @Override
    public void preRemove(IRemoveContext context) 
    {
        PictogramElement pe = context.getPictogramElement();
        SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getFeatureProvider();
        final Object bo = sfp.getBusinessObjectForPictogramElement(pe);        
        sfp.remove(bo);
        
    	if (pe instanceof Connection)
    	{
    		SapphireConnectionRouter.getInstance().removeConnectionFromCache((Connection)pe);
    	}
        
    }
    
}
