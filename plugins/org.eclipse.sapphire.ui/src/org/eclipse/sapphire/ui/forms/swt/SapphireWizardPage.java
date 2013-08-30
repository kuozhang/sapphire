/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui.forms.swt;

import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.forms.swt.presentation.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.forms.swt.presentation.SwtRendererUtil.toImageDescriptor;

import org.eclipse.help.IContext;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardContainer2;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ISapphireDocumentation;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationRef;
import org.eclipse.sapphire.ui.forms.WizardPagePart;
import org.eclipse.sapphire.ui.forms.swt.presentation.internal.CompositePresentation;
import org.eclipse.sapphire.ui.forms.swt.presentation.internal.HelpSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class SapphireWizardPage extends WizardPage
{
    private final WizardPagePart part;
    private final Listener listener;
    
    public SapphireWizardPage( final WizardPagePart part )
    {
        super( part.definition().getId().content() );
        
        this.part = part;
        
        setTitle( this.part.getLabel() );
        setDescription( this.part.getDescription() );
        
        this.listener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof SapphirePart.ImageChangedEvent )
                {
                    refreshImage();
                }
            }
        };
        
        this.part.attach( this.listener );
        
        refreshImage();
    }
    
    public void createControl( final Composite parent )
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( gdfill() );
        composite.setLayout( glayout( 1, 0, 0 ) );
        
        final Composite innerComposite = new Composite( composite, SWT.NONE );
        innerComposite.setLayout( glayout( 2, 0, 0 ) );
        innerComposite.setLayoutData( gdfill() );
        
        final Presentation presentation = new CompositePresentation( this.part, null, innerComposite )
        {
            @Override
            public void layout()
            {
                super.layout();
                
                final IWizardContainer container = getContainer();
                
                if( container instanceof IWizardContainer2 )
                {
                    ( (IWizardContainer2) container ).updateSize();
                }
            }
        };
        
        presentation.render();
        
        final Runnable messageUpdateOperation = new Runnable()
        {
            public void run()
            {
                final Status st = SapphireWizardPage.this.part.validation();
                
                if( st.severity() == Status.Severity.ERROR )
                {
                    setMessage( st.message(), ERROR );
                    setPageComplete( false );
                }
                else if( st.severity() == Status.Severity.WARNING )
                {
                    setMessage( st.message(), WARNING );
                    setPageComplete( true );
                }
                else
                {
                    setMessage( null );
                    setPageComplete( true );
                }
            }
        };
        
        messageUpdateOperation.run();
        
        this.part.attach
        (
            new FilteredListener<PartValidationEvent>()
            {
                @Override
                protected void handleTypedEvent( PartValidationEvent event )
                {
                    messageUpdateOperation.run();
                }
            }
        );
        
        final ISapphireDocumentation doc = this.part.definition().getDocumentation().content();
        
        if( doc != null )
        {
            ISapphireDocumentationDef docdef = null;
            
            if( doc instanceof ISapphireDocumentationDef )
            {
                docdef = (ISapphireDocumentationDef) doc;
            }
            else
            {
                docdef = ( (ISapphireDocumentationRef) doc ).resolve();
            }
            
            if( docdef != null )
            {
                HelpSystem.setHelp( composite, docdef );
            }
        }

        setControl( composite );
    }
    
    public final void performHelp() 
    {
        final IContext documentationContext = this.part.getDocumentationContext();
        
        if ( documentationContext != null  )
        {
            PlatformUI.getWorkbench().getHelpSystem().displayHelp( documentationContext );
        }
    }

    @Override
    public void setVisible( final boolean visible )
    {
        super.setVisible( visible );
        
        this.part.setVisible( visible );
        
        if( visible )
        {
            final String initialFocusProperty = this.part.definition().getInitialFocus().content();
            
            if( initialFocusProperty != null )
            {
                this.part.setFocus( initialFocusProperty );
            }
        }
    }
    
    private final void refreshImage()
    {
        setImageDescriptor( toImageDescriptor( this.part.getImage() ) );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        this.part.detach( this.listener );
    }
    
}
