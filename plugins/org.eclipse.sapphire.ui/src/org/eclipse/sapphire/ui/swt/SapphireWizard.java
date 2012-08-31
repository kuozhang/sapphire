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

import static org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil.toImageDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.DelayedTasksExecutor;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePart.ImageChangedEvent;
import org.eclipse.sapphire.ui.SapphireWizardPagePart;
import org.eclipse.sapphire.ui.SapphireWizardPart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.WizardDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class SapphireWizard<M extends IExecutableModelElement> extends Wizard
{
    private M element;
    private DefinitionLoader.Reference<WizardDef> definition;
    private SapphireWizardPart part;
    
    public SapphireWizard( final M element,
                           final DefinitionLoader.Reference<WizardDef> definition )
    {
        init( element, definition );
    }

    protected SapphireWizard()
    {
    }
    
    protected void init( final M element,
                         final DefinitionLoader.Reference<WizardDef> definition )
    {
        if( element == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( definition == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.element = element;
        this.definition = definition;
        
        this.part = new SapphireWizardPart();
        this.part.init( null, this.element, this.definition.resolve(), Collections.<String,String>emptyMap() );
        
        setWindowTitle( this.part.getLabel() );
        
        this.part.attach
        (
            new FilteredListener<SapphirePart.ImageChangedEvent>()
            {
                @Override
                protected void handleTypedEvent( final ImageChangedEvent event )
                {
                    refreshImage();
                }
            }
        );
        
        refreshImage();
        
        setNeedsProgressMonitor( true );
    }
    
    public final M getModelElement()
    {
        return this.element;
    }
    
    public final WizardDef definition()
    {
        return ( this.definition == null ? null : this.definition.resolve() );
    }
    
    @Override
    public void addPages()
    {
        for( SapphireWizardPagePart pagePart : this.part.getPages() )
        {
            addPage( new SapphireWizardPage( pagePart ) );
        }
    }

    @Override
    public final boolean performFinish()
    {
        DelayedTasksExecutor.sweep();
        
        final Status[] result = new Status[ 1 ];
        
        final IRunnableWithProgress runnable = new IRunnableWithProgress()
        {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                result[ 0 ] = performFinish( ProgressMonitorBridge.create( monitor ) );
            }
        };
        
        try
        {
            getContainer().run( true, false, runnable );
        }
        catch( InvocationTargetException e )
        {
            SapphireUiFrameworkPlugin.log( e.getTargetException() );
            return false;
        }
        catch( InterruptedException e )
        {
            return false;
        }
        
        final Status st = result[ 0 ];
        
        if( st.ok() )
        {
            performPostFinish();
            
            return true;
        }
        else
        {
            SapphireStatusDialog.open( getShell(), st );
            
            return false;
        }
    }
    
    protected Status performFinish( final ProgressMonitor monitor )
    {
        return this.element.execute( monitor );
    }
    
    protected void performPostFinish()
    {
        // The default implementation doesn't do anything.
    }
    
    protected final void openFileEditors( final IFile... files )
    {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
        if( window != null )
        {
            final IWorkbenchPage page = window.getActivePage();
            
            for( IFile file : files )
            {
                if( file != null && file.isAccessible() )
                {
                    try
                    {
                        IDE.openEditor( page, file );
                    } 
                    catch( PartInitException e ) 
                    {
                        SapphireUiFrameworkPlugin.log( e );
                    }
                }
            }
        }
    }

    protected final void openFileEditor( final IFile file )
    {
        openFileEditor( file, null );
    }
    
    protected final void openFileEditor( final IFile file,
                                         final String editor )
    {
        final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        
        if( window != null )
        {
            final IWorkbenchPage page = window.getActivePage();
            
            try
            {
                if( editor == null )
                {
                    IDE.openEditor( page, file );
                }
                else
                {
                    IDE.openEditor( page, file, editor );
                    IDE.setDefaultEditor( file, editor );
                }
            } 
            catch( PartInitException e ) 
            {
                SapphireUiFrameworkPlugin.log( e );
            }
        }
    }

    private final void refreshImage()
    {
        setDefaultPageImageDescriptor( toImageDescriptor( this.part.getImage() ) );
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        this.element = null;
        
        this.part.dispose();
        this.part = null;
        
        this.definition.dispose();
        this.definition = null;
    }
    
}
