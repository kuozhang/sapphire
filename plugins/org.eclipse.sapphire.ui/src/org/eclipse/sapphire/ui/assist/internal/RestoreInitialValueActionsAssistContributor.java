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

package org.eclipse.sapphire.ui.assist.internal;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.services.InitialValueService;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistSection;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class RestoreInitialValueActionsAssistContributor extends PropertyEditorAssistContributor
{
    public RestoreInitialValueActionsAssistContributor()
    {
        setId( ID_RESTORE_INITIAL_VALUE_ACTIONS_CONTRIBUTOR );
        setPriority( PRIORITY_RESTORE_INITIAL_VALUE_ACTIONS_CONTRIBUTOR );
    }
    
    @Override
    public void contribute( final PropertyEditorAssistContext context )
    {
        final IModelElement element = context.getModelElement();
        final ModelProperty property = context.getProperty();
        
        if( property.isReadOnly() )
        {
            return;
        }
        
        if( property instanceof ValueProperty )
        {
            final ValueProperty prop = (ValueProperty) property; 
            final InitialValueService initialValueService = element.service( prop, InitialValueService.class );
            
            if( initialValueService != null )
            {
                final String initialValue = initialValueService.text();
                final String currentValue = element.read( prop ).getText( false );
                
                if( initialValue != null && ! initialValue.equals( currentValue ) )
                {
                    final PropertyEditorAssistContribution contribution = new PropertyEditorAssistContribution();
                    contribution.setText( "<p><a href=\"action\" nowrap=\"true\">" + escapeForXml( Resources.restore ) + "</a></p>" );
                    
                    contribution.setHyperlinkListener
                    (
                        new HyperlinkAdapter()
                        {
                            @Override
                            public void linkActivated( final HyperlinkEvent event )
                            {
                                element.write( prop, initialValue );
                            }
                        }
                    );
                    
                    final PropertyEditorAssistSection section = context.getSection( SECTION_ID_ACTIONS );
                    section.addContribution( contribution );
                }
            }
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String restore;
        
        static
        {
            initializeMessages( RestoreInitialValueActionsAssistContributor.class.getName(), Resources.class );
        }
    }
    
}
