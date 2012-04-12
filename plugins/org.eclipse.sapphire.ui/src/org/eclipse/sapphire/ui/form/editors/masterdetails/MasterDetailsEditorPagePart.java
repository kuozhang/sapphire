/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails;

import static org.eclipse.sapphire.ui.SapphireActionSystem.CONTEXT_EDITOR_PAGE_OUTLINE_HEADER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphireEditorPagePart;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsEditorPageDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.state.IMasterDetailsEditorPageState;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class MasterDetailsEditorPagePart extends SapphireEditorPagePart
{
    private IMasterDetailsEditorPageState state;
    private MasterDetailsContentOutline contentOutline;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.contentOutline = new MasterDetailsContentOutline( this );
        
        this.contentOutline.attach
        (
            new Listener()
            {
               @Override
                public void handle( final Event event )
                {
                    if( event instanceof MasterDetailsContentOutline.SelectionChangedEvent )
                    {
                        final MasterDetailsContentOutline.SelectionChangedEvent evt = (MasterDetailsContentOutline.SelectionChangedEvent) event;
                        final List<MasterDetailsContentNode> selection = evt.selection();

                        PropertiesViewContributionPart propertiesViewContribution = null;
                        
                        if( selection.size() == 1 )
                        {
                            propertiesViewContribution = selection.get( 0 ).getPropertiesViewContribution();
                        }
                        
                        setPropertiesViewContribution( propertiesViewContribution );
                    }
                }
            }
        );
    }

    @Override
    public IMasterDetailsEditorPageDef definition()
    {
        return (IMasterDetailsEditorPageDef) super.definition();
    }
    
    @Override
    public Set<String> getActionContexts()
    {
        final Set<String> contexts = new HashSet<String>();
        contexts.addAll( super.getActionContexts() );
        contexts.add( CONTEXT_EDITOR_PAGE_OUTLINE_HEADER );
        return contexts;
    }
    
    @Override
    public String getMainActionContext()
    {
        return SapphireActionSystem.CONTEXT_EDITOR_PAGE;
    }
    
    public final MasterDetailsContentOutline getContentOutline()
    {
        return this.contentOutline;
    }
    
    public final void expandAllNodes()
    {
        for( MasterDetailsContentNode node : this.contentOutline.getRoot().getChildNodes() )
        {
            node.setExpanded( true, true );
        }
    }

    public final void collapseAllNodes()
    {
        for( MasterDetailsContentNode node : this.contentOutline.getRoot().getChildNodes() )
        {
            node.setExpanded( false, true );
        }
    }
    
    public final IMasterDetailsEditorPageState getState()
    {
        return this.state;
    }
    
    public final void setState( final IMasterDetailsEditorPageState state )
    {
        this.state = state;
    }
    
    public final void setFocusOnDetails()
    {
        broadcast( new DetailsFocusRequested( this ) );
    }
    
    public static final class DetailsFocusRequested extends PartEvent
    {
        public DetailsFocusRequested( final MasterDetailsEditorPagePart part )
        {
            super( part );
        }
    }
    
}
