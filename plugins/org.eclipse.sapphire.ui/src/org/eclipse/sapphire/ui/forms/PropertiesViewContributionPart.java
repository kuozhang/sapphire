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

package org.eclipse.sapphire.ui.forms;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertiesViewContributionPart extends SapphirePart
{
    private List<PropertiesViewContributionPagePart> pages;
    private PropertiesViewContributionPagePart selectedPage;
    
    @Override
    protected void init()
    {
        super.init();

        final Element element = getModelElement();
        final ListFactory<PropertiesViewContributionPagePart> pagesListFactory = ListFactory.start();
        
        final Listener pagePartListener = new FilteredListener<PartValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( PartValidationEvent event )
            {
                refreshValidation();
            }
        };
        
        for( PropertiesViewContributionPageDef pageDef : ( (PropertiesViewContributionDef) this.definition ).getPages() )
        {
            final PropertiesViewContributionPagePart pagePart = new PropertiesViewContributionPagePart();
            pagePart.init( this, element, pageDef, this.params );
            pagePart.initialize();
            pagePart.attach( pagePartListener );
            pagesListFactory.add( pagePart );
        }
        
        this.pages = pagesListFactory.result();
        
        this.selectedPage = this.pages.get( 0 );
    }
    
    public List<PropertiesViewContributionPagePart> getPages()
    {
        return this.pages;
    }
    
    public PropertiesViewContributionPagePart getSelectedPage()
    {
        return this.selectedPage;
    }
    
    public void setSelectedPage( final PropertiesViewContributionPagePart selectedPage )
    {
        this.selectedPage = selectedPage;
    }
    
    @Override
    protected Status computeValidation()
    {
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();

        for( PropertiesViewContributionPagePart pagePart : this.pages )
        {
            factory.merge( pagePart.validation() );
        }
        
        return factory.create();
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
