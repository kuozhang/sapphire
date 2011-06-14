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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireActionCustomFeature extends AbstractCustomFeature 
{
    private SapphireActionHandler sapphireActionHandler;
    
    public SapphireActionCustomFeature(IFeatureProvider fp, SapphireActionHandler sapphireActionHandler)
    {
        super(fp);
        this.sapphireActionHandler = sapphireActionHandler;
    }

    @Override
    public String getName() 
    {
        return this.sapphireActionHandler.getLabel();
    }

    @Override
    public boolean canExecute(ICustomContext context) 
    {
        return true;
    }
    
    public void execute(ICustomContext context) 
    {
        SapphireRenderingContext sapphireContext = 
                ((SapphireDiagramFeatureProvider)this.getFeatureProvider()).getRenderingContext(this.sapphireActionHandler.getPart());
        this.sapphireActionHandler.execute(sapphireContext);
    }

    @Override
    public boolean hasDoneChanges() 
    {
        return ((SapphireDiagramActionHandler)this.sapphireActionHandler).hasDoneModelChanges();
    }    
}
