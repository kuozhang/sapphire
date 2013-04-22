/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributionPageDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertiesViewContributionPagePart extends FormPart
{
    private FunctionResult labelFunctionResult;
    private ImageManager imageManager;

    @Override
    protected void init()
    {
        super.init();

        final IPropertiesViewContributionPageDef def = definition();
        
        this.labelFunctionResult = initExpression
        (
            def.getLabel().content(), 
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    broadcast( new LabelChangedEvent( PropertiesViewContributionPagePart.this ) );
                }
            }
        );
        
        this.imageManager = new ImageManager( def.getImage().content() );
    }

    @Override
    public IPropertiesViewContributionPageDef definition()
    {
        return (IPropertiesViewContributionPageDef) super.definition();
    }
    
    public String getLabel()
    {
        return (String) this.labelFunctionResult.value();
    }
    
    public ImageDescriptor getImage()
    {
        return this.imageManager.getImage();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.labelFunctionResult != null )
        {
            this.labelFunctionResult.dispose();
        }
        
        if( this.imageManager != null )
        {
            this.imageManager.dispose();
        }
    }
    
}
