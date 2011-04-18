/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.SapphireMultiStatus;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributionDef;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributionPageDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertiesViewContributionPart

    extends SapphirePart
    
{
    private List<PropertiesViewContributionPagePart> pages;
    private List<PropertiesViewContributionPagePart> pagesReadOnly;
    private int selectedPageIndex;
    
    @Override
    protected void init()
    {
        super.init();

        final IModelElement element = getModelElement();
        
        this.pages = new ArrayList<PropertiesViewContributionPagePart>();
        this.pagesReadOnly = Collections.unmodifiableList( this.pages );
        
        for( IPropertiesViewContributionPageDef pageDef : ( (IPropertiesViewContributionDef) this.definition ).getPages() )
        {
            final PropertiesViewContributionPagePart pagePart = new PropertiesViewContributionPagePart();
            pagePart.init( this, element, pageDef, this.params );
            
            this.pages.add( pagePart );

            final SapphirePartListener pagePartListener = new SapphirePartListener()
            {
                @Override
                public void handleValidateStateChange( final IStatus oldValidateState,
                                                       final IStatus newValidationState )
                {
                    updateValidationState();
                }
            };
            
            pagePart.addListener( pagePartListener );
        }
        
        this.selectedPageIndex = 0;
    }
    
    public List<PropertiesViewContributionPagePart> getPages()
    {
        return this.pagesReadOnly;
    }
    
    public int getSelectedPageIndex()
    {
        return this.selectedPageIndex;
    }
    
    public void setSelectedPageIndex( final int selectedPageIndex )
    {
        this.selectedPageIndex = selectedPageIndex;
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        throw new IllegalStateException();
    }
    
    @Override
    protected IStatus computeValidationState()
    {
        final SapphireMultiStatus st = new SapphireMultiStatus();

        for( PropertiesViewContributionPagePart pagePart : this.pages )
        {
            st.add( pagePart.getValidationState() );
        }
        
        return st;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        for( PropertiesViewContributionPagePart pagePart : this.pages )
        {
            pagePart.dispose();
        }
    }
    
}
