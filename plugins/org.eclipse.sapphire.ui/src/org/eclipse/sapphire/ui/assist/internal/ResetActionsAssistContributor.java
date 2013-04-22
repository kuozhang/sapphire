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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistSection;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ResetActionsAssistContributor extends PropertyEditorAssistContributor
{
    public ResetActionsAssistContributor()
    {
        setId( ID_RESET_ACTIONS_CONTRIBUTOR );
        setPriority( PRIORITY_RESET_ACTIONS_CONTRIBUTOR );
    }
    
    @Override
    public void contribute( final PropertyEditorAssistContext context )
    {
        final Property property = context.property();
        
        if( context.isPropertyEditorReadOnly() || property.empty() )
        {
            return;
        }
        
        if( property instanceof Value<?> )
        {
            final DefaultValueService defaultValueService = property.service( DefaultValueService.class );
            final boolean hasDefaultValue = ( defaultValueService == null ? false : defaultValueService.value() != null );
            final boolean isBooleanType = property.definition().getTypeClass().equals( Boolean.class );
            
            final String actionText
                = ( hasDefaultValue || isBooleanType ? Resources.restoreDefaultValue : Resources.clear );
            
            final PropertyEditorAssistContribution.Factory contribution = PropertyEditorAssistContribution.factory();
            
            contribution.text( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml( actionText ) + "</a></p>" );
            
            contribution.link
            (
                "action",
                new Runnable()
                {
                    public void run()
                    {
                        property.clear();
                    }
                }
            );
            
            final PropertyEditorAssistSection section = context.getSection( SECTION_ID_ACTIONS );
            section.addContribution( contribution.create() );
        }
        else if( property.definition() instanceof ListProperty )
        {
            final PropertyEditorAssistContribution.Factory contribution = PropertyEditorAssistContribution.factory();
            
            contribution.text( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml( Resources.clear ) + "</a></p>" );
            
            contribution.link
            (
                "action",
                new Runnable()
                {
                    public void run()
                    {
                        property.clear();
                    }
                }
            );
            
            final PropertyEditorAssistSection section = context.getSection( SECTION_ID_ACTIONS );
            section.addContribution( contribution.create() );
        }
        else if( property instanceof ElementHandle<?> )
        {
            final PropertyEditorAssistContribution.Factory contribution = PropertyEditorAssistContribution.factory();
            
            contribution.text( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml( Resources.clear ) + "</a></p>" );
            
            contribution.link
            (
                "action",
                new Runnable()
                {
                    public void run()
                    {
                        property.clear();
                    }
                }
            );
            
            final PropertyEditorAssistSection section = context.getSection( SECTION_ID_ACTIONS );
            section.addContribution( contribution.create() );
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String restoreDefaultValue;
        public static String clear;
        
        static
        {
            initializeMessages( ResetActionsAssistContributor.class.getName(), Resources.class );
        }
    }
    
}
