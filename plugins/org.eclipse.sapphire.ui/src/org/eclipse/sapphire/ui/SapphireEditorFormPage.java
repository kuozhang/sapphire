/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.help.IContext;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.internal.SapphireActionManager;
import org.eclipse.ui.forms.editor.FormPage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class SapphireEditorFormPage

    extends FormPage
    implements ISapphirePart
    
{
    private final SapphireEditor editor;
    private final IModelElement rootModelElement;
    private final SapphireActionManager actionsManager;
    
    public SapphireEditorFormPage( final SapphireEditor editor,
                                   final IModelElement rootModelElement ) 
    {
        super( editor, null, null );

        this.editor = editor;
        this.rootModelElement = rootModelElement;
        this.actionsManager = new SapphireActionManager( this, getActionContexts() );
    }
    
    public SapphireEditor getEditor()
    {
        return this.editor;
    }
    
    public IModelElement getRootModelElement()
    {
        return this.rootModelElement;
    }
    
    public abstract String getId();
    
    public final Preferences getGlobalPreferences( final boolean createIfNecessary )
    
        throws BackingStoreException
        
    {
        Preferences prefs = this.editor.getGlobalPreferences( createIfNecessary );
        final String pageName = getPartName();
        
        if( prefs != null && ( prefs.nodeExists( pageName ) || createIfNecessary ) )
        {
            return prefs.node( pageName );
        }
        
        return null;
    }
    
    public final Preferences getInstancePreferences( final boolean createIfNecessary )
    
        throws BackingStoreException
        
    {
        Preferences prefs = this.editor.getInstancePreferences( createIfNecessary );
        final String pageName = getPartName();
        
        if( prefs != null && ( prefs.nodeExists( pageName ) || createIfNecessary ) )
        {
            return prefs.node( pageName );
        }
        
        return null;
    }
    
    // *********************
    // ISapphirePart Methods
    // *********************
    
    public ISapphirePart getParentPart()
    {
        return this.editor;
    }
    
    @SuppressWarnings( "unchecked" )
    public final <T> T getNearestPart( final Class<T> partType )
    {
        if( partType.isAssignableFrom( getClass() ) )
        {
            return (T) this;
        }
        else
        {
            if( this.editor != null )
            {
                return this.editor.getNearestPart( partType );
            }
            else
            {
                return null;
            }
        }
    }
    
    public IModelElement getModelElement()
    {
        return this.rootModelElement;
    }
    
    public IStatus getValidationState()
    {
        throw new UnsupportedOperationException();
    }
    
    public IContext getDocumentationContext()
    {
        return null;
    }

    public SapphireImageCache getImageCache()
    {
        return this.editor.getImageCache();
    }
    
    public void collectAllReferencedProperties( final Set<ModelProperty> collection )
    {
        throw new UnsupportedOperationException();
    }
    
    public void addListener( final SapphirePartListener listener )
    {
        throw new UnsupportedOperationException();
    }
    
    public void removeListener( final SapphirePartListener listener )
    {
        throw new UnsupportedOperationException();
    }
    
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_EDITOR_PAGE );
    }
    
    public final String getMainActionContext()
    {
        return this.actionsManager.getMainActionContext();
    }
    
    public final SapphireActionGroup getActions()
    {
        return this.actionsManager.getActions();
    }
    
    public final SapphireActionGroup getActions( final String context )
    {
        return this.actionsManager.getActions( context );
    }
    
    public final SapphireAction getAction( final String id )
    {
        return this.actionsManager.getAction( id );
    }
    
    public void dispose()
    {
        this.actionsManager.dispose();
    }

}