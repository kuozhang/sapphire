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

package org.eclipse.sapphire.ui.swt;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.FormComponentDef;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class SapphirePropertyPage extends PropertyPage
{
    private IModelElement element;
    private DefinitionLoader.Reference<FormComponentDef> definition;
    
    public SapphirePropertyPage( final IModelElement element,
                                 final DefinitionLoader.Reference<FormComponentDef> definition )
    {
        this.element = element;
        this.definition = definition;
    }

    @Override
    public void createControl( final Composite parent )
    {
        super.createControl( parent );
        getDefaultsButton().setVisible( false );
        parent.layout( true, true );
    }

    protected Control createContents( final Composite parent ) 
    {
        final SapphireForm control = new SapphireForm( parent, this.element, this.definition );
        
        final Runnable messageUpdateOperation = new Runnable()
        {
            public void run()
            {
                final Status st = control.getPart().getValidationState();
                
                if( st.severity() == Status.Severity.ERROR )
                {
                    setMessage( st.message(), ERROR );
                    setValid( false );
                }
                else if( st.severity() == Status.Severity.WARNING )
                {
                    setMessage( st.message(), WARNING );
                    setValid( true );
                }
                else
                {
                    setMessage( null );
                    setValid( true );
                }
            }
        };
        
        messageUpdateOperation.run();
        
        final Listener messageUpdateListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof SapphirePart.ValidationChangedEvent )
                {
                    messageUpdateOperation.run();
                }
            }
        };
        
        control.getPart().attach( messageUpdateListener );
        
        return control;
    }
    
    @Override
    public boolean performOk() 
    {
        try
        {
            this.element.resource().save();
            
            return true;
        }
        catch( ResourceStoreException e )
        {
            MessageDialog.openError( getShell(), Resources.errorDialogTitle, e.getMessage() );
            
            return false;
        }
    }
    
    @Override
    protected void performApply() 
    {
        performOk();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        this.element = null;
        
        this.definition.dispose();
        this.definition = null;
    }

    private static final class Resources extends NLS
    {
        public static String errorDialogTitle;
        
        static
        {
            initializeMessages( SapphirePropertyPage.class.getName(), Resources.class );
        }
    }
    
}
