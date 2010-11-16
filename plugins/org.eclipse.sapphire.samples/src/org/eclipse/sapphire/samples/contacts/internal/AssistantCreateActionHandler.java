/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.contacts.internal;

import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.samples.contacts.IAssistant;
import org.eclipse.sapphire.samples.contacts.IContact;
import org.eclipse.sapphire.samples.contacts.IContactsDatabase;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class AssistantCreateActionHandler

    extends SapphireActionHandler
    
{
    private ModelPropertyListener listener;
    
    @Override
    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
        super.init( action, def );
        
        this.listener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                refreshEnablementState();
            }
        };
        
        final IAssistant assistant = (IAssistant) getModelElement();
        
        assistant.nearest( IContactsDatabase.class ).addListener( this.listener, "Contacts/Name" );
        assistant.addListener( this.listener, IAssistant.PROP_NAME.getName() );
        
        refreshEnablementState();
    }
    
    private void refreshEnablementState()
    {
        final IAssistant assistant = (IAssistant) getModelElement();
        final String name = assistant.getName().getText();
        final IContactsDatabase cdb = assistant.nearest( IContactsDatabase.class );
        
        boolean enabled;
        
        if( name == null )
        {
            enabled = false;
        }
        else
        {
            enabled = true;
            
            for( IContact contact : cdb.getContacts() )
            {
                if( name.equals( contact.getName().getText() ) )
                {
                    enabled = false;
                    break;
                }
            }
        }
        
        setEnabled( enabled );
    }

    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final IAssistant assistant = (IAssistant) getModelElement();
        final String name = assistant.getName().getText();
        final IContactsDatabase cdb = assistant.nearest( IContactsDatabase.class );
        
        final IContact newAssistantContact = cdb.getContacts().addNewElement();
        newAssistantContact.setName( name );
        
        return null;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        final IAssistant assistant = (IAssistant) getModelElement();
        
        assistant.nearest( IContactsDatabase.class ).removeListener( this.listener, "Contacts/Name" );
        assistant.removeListener( this.listener, IAssistant.PROP_NAME.getName() );
    }
    
}
