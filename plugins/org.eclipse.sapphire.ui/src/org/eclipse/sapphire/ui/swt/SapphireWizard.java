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

package org.eclipse.sapphire.ui.swt;

import static org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil.toImageDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.DelayedTasksExecutor;
import org.eclipse.sapphire.ui.PartVisibilityEvent;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePart.ImageChangedEvent;
import org.eclipse.sapphire.ui.SapphireWizardPagePart;
import org.eclipse.sapphire.ui.SapphireWizardPart;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.WizardDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class SapphireWizard<M extends IExecutableModelElement> implements IWizard
{
    private IExecutableModelElement element;
    private boolean elementInstantiatedLocally;
    private DefinitionLoader.Reference<WizardDef> definition;
    private SapphireWizardPart part;
    private Map<SapphireWizardPagePart,SapphireWizardPage> pages;
    private IWizardContainer container;
    private ImageDescriptor defaultPageImageDescriptor;
    private Image defaultPageImage;
    
    public SapphireWizard( final ElementType type,
                           final DefinitionLoader.Reference<WizardDef> definition )
    {
        init( type, definition );
    }

    public SapphireWizard( final M element,
                           final DefinitionLoader.Reference<WizardDef> definition )
    {
        init( element, definition );
    }

    protected SapphireWizard()
    {
    }
    
    protected void init( final ElementType type,
                         final DefinitionLoader.Reference<WizardDef> definition )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }
        
        if( ! IExecutableModelElement.class.isAssignableFrom( type.getModelElementClass() ) )
        {
            throw new IllegalArgumentException();
        }
        
        if( definition == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.elementInstantiatedLocally = true;
        
        init( (IExecutableModelElement) type.instantiate(), definition );
    }
    
    protected void init( final IExecutableModelElement element,
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
        
        this.pages = new LinkedHashMap<SapphireWizardPagePart,SapphireWizardPage>();
        
        final Listener pageVisibilityListener = new FilteredListener<PartVisibilityEvent>()
        {
            @Override
            protected void handleTypedEvent( final PartVisibilityEvent event )
            {
                getContainer().updateButtons();
            }
        };
        
        for( final SapphireWizardPagePart page : this.part.getPages() )
        {
            page.attach( pageVisibilityListener );
            this.pages.put( page, null );
        }
        
        refreshImage();
    }
    
    @SuppressWarnings( "unchecked" )
    
    public final M element()
    {
        return (M) this.element;
    }
    
    public final WizardDef definition()
    {
        return ( this.definition == null ? null : this.definition.resolve() );
    }
    
    @Override
    public IWizardPage[] getPages()
    {
        final List<IWizardPage> result = new ArrayList<IWizardPage>();
        
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( wizardPagePart.visible() )
            {
                result.add( getPage( wizardPagePart ) );
            }
        }
        
        return result.toArray( new IWizardPage[ result.size() ] );
    }

    @Override
    public int getPageCount()
    {
        int count = 0;
        
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( wizardPagePart.visible() )
            {
                count++;
            }
        }
        
        return count;
    }

    @Override
    public IWizardPage getPage( final String name )
    {
        if( name == null )
        {
            throw new IllegalArgumentException();
        }
        
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( wizardPagePart.visible() && name.equals( wizardPagePart.definition().getId().content() ) )
            {
                return getPage( wizardPagePart );
            }
        }
        
        return null;
    }

    private IWizardPage getPage( final SapphireWizardPagePart wizardPagePart )
    {
        SapphireWizardPage wizardPagePresentation = this.pages.get( wizardPagePart );
        
        if( wizardPagePresentation == null )
        {
            wizardPagePresentation = new SapphireWizardPage( wizardPagePart );
            wizardPagePresentation.setWizard( this );
            this.pages.put( wizardPagePart, wizardPagePresentation );
        }
        
        return wizardPagePresentation;
    }

    @Override
    public IWizardPage getStartingPage()
    {
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( wizardPagePart.visible() )
            {
                return getPage( wizardPagePart );
            }
        }
        
        return null;
    }

    @Override
    public IWizardPage getNextPage( final IWizardPage page )
    {
        boolean captureNextPage = false;
        
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( captureNextPage )
            {
                if( wizardPagePart.visible() )
                {
                    return getPage( wizardPagePart );
                }
            }
            else
            {
                if( getPage( wizardPagePart ) == page )
                {
                    captureNextPage = true;
                }
            }
        }
        
        return null;
    }

    @Override
    public IWizardPage getPreviousPage( final IWizardPage page )
    {
        SapphireWizardPagePart lastVisibleWizardPagePart = null;
        
        for( final SapphireWizardPagePart wizardPagePart : this.pages.keySet() )
        {
            if( getPage( wizardPagePart ) == page )
            {
                break;
            }
            else if( wizardPagePart.visible() )
            {
                lastVisibleWizardPagePart = wizardPagePart;
            }
        }
        
        if( lastVisibleWizardPagePart != null )
        {
            return getPage( lastVisibleWizardPagePart );
        }
        
        return null;
    }
    
    @Override
    public final void addPages()
    {
    }

    @Override
    public final boolean canFinish()
    {
        for( final Map.Entry<SapphireWizardPagePart,SapphireWizardPage> entry : this.pages.entrySet() )
        {
            final SapphireWizardPagePart wizardPagePart = entry.getKey();
            
            if( wizardPagePart.visible() && wizardPagePart.validation().severity() == Status.Severity.ERROR )
            {
                return false;
            }
        }
        
        return true;
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
            getContainer().run( true, true, runnable );
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
            SapphireStatusDialog.open( getContainer().getShell(), st );
            
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
    
    @Override
    public final boolean performCancel()
    {
        return true;
    }

    @Override
    public final IWizardContainer getContainer()
    {
        return this.container;
    }

    @Override
    public final void setContainer( final IWizardContainer container )
    {
        this.container = container;
    }

    @Override
    public final Image getDefaultPageImage()
    {
        if( this.defaultPageImage == null )
        {
            this.defaultPageImage = JFaceResources.getResources().createImageWithDefault( this.defaultPageImageDescriptor );
        }
        
        return this.defaultPageImage;
    }

    @Override
    public final RGB getTitleBarColor()
    {
        return null;
    }

    @Override
    public final String getWindowTitle()
    {
        return this.part.getLabel();
    }

    @Override
    public final IDialogSettings getDialogSettings()
    {
        return null;
    }

    @Override
    public boolean isHelpAvailable()
    {
        return false;
    }

    @Override
    public final boolean needsPreviousAndNextButtons()
    {
        return true;
    }

    @Override
    public final boolean needsProgressMonitor()
    {
        return true;
    }

    @Override
    public final void createPageControls( final Composite pageContainer )
    {
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
        if( this.defaultPageImage != null )
        {
            JFaceResources.getResources().destroyImage( this.defaultPageImageDescriptor );
            this.defaultPageImage = null;
        }

        this.defaultPageImageDescriptor = toImageDescriptor( this.part.getImage() );
        
        if( this.defaultPageImageDescriptor == null )
        {
            this.defaultPageImageDescriptor = JFaceResources.getImageRegistry().getDescriptor( Wizard.DEFAULT_IMAGE );
        }
    }

    @Override
    public void dispose()
    {
        if( this.element != null )
        {
            if( this.elementInstantiatedLocally )
            {
                this.element.dispose();
            }
            
            this.element = null;
        }
        
        if( this.part != null )
        {
            this.part.dispose();
            this.part = null;
        }
        
        if( this.definition != null )
        {
            this.definition.dispose();
            this.definition = null;
        }
        
        if( this.defaultPageImage != null )
        {
            JFaceResources.getResources().destroyImage( this.defaultPageImageDescriptor );
            this.defaultPageImage = null;
        }
        
        this.defaultPageImageDescriptor = null;
        this.container = null;
        this.pages = null;
    }
    
}
