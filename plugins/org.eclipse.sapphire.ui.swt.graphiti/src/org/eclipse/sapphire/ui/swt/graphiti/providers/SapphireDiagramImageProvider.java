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

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramImageProvider extends AbstractImageProvider 
{
    public SapphireDiagramImageProvider()
    {
        super();
    }
    
    @Override
    protected void addAvailableImages() 
    {

    }
    
    public void registerImage(String imageId, String imageFilePath)
    {
        this.addImageFilePath(imageId, imageFilePath);
    }

}
