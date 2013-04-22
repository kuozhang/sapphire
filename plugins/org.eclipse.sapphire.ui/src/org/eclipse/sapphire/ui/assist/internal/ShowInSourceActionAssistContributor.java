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

package org.eclipse.sapphire.ui.assist.internal;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.SourceEditorService;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistSection;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ShowInSourceActionAssistContributor extends PropertyEditorAssistContributor
{
    public ShowInSourceActionAssistContributor()
    {
        setId( ID_SHOW_IN_SOURCE_ACTION_CONTRIBUTOR );
        setPriority( PRIORITY_SHOW_IN_SOURCE_ACTION_CONTRIBUTOR );
    }
    
    @Override
    public void contribute( final PropertyEditorAssistContext context )
    {
        final Element element = context.element();
        final Property property = context.property();
        final SourceEditorService sourceEditorService = element.adapt( SourceEditorService.class );
        
        if( sourceEditorService == null )
        {
            return;
        }
        
        boolean contribute = false;
        
        if( property == null )
        {
        	contribute = true;
        }
        else if( ! property.definition().isDerived() )
        {
            contribute = ! property.empty();
        }
        
        if( ! contribute )
        {
            return;
        }

        final PropertyEditorAssistContribution.Factory contribution = PropertyEditorAssistContribution.factory();
        
        contribution.text( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml( Resources.action ) + "</a></p>" );
        
        contribution.link
        (
            "action",
            new Runnable()
            {
                public void run()
                {
                    sourceEditorService.show( element, property.definition() );
                }
            }
        );
        
        final PropertyEditorAssistSection section = context.getSection( SECTION_ID_ACTIONS );
        section.addContribution( contribution.create() );
    }
    
    private static final class Resources extends NLS
    {
        public static String action;
        
        static
        {
            initializeMessages( ShowInSourceActionAssistContributor.class.getName(), Resources.class );
        }
    }
    
}
