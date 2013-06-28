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

package org.eclipse.sapphire.ui.swt.renderer.actions;

import java.io.File;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireBrowseActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.swt.widgets.DirectoryDialog;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class AbsoluteFolderPathBrowseActionHandler extends SapphireBrowseActionHandler
{
    public static final String ID = "Sapphire.Browse.Folder.Absolute";
    
    @Override
    public void init( final SapphireAction action,
                      final ActionHandlerDef def )
    {
        super.init( action, def );

        setId( ID );
        setLabel( Resources.label );
        addImage( ImageData.readFromClassLoader( AbsoluteFolderPathBrowseActionHandler.class, "Folder.png" ).required() );
    }

    @Override
    protected String browse( final SapphireRenderingContext context )
    {
        final Property property = property();
        
        final DirectoryDialog dialog = new DirectoryDialog( context.getShell() );
        dialog.setText( property.definition().getLabel( true, CapitalizationType.FIRST_WORD_ONLY, false ) );
        dialog.setMessage( createBrowseDialogMessage( property.definition().getLabel( true, CapitalizationType.NO_CAPS, false ) ) );
        
        final Value<?> value = (Value<?>) property;
        final Path path = (Path) value.content();
        
        if( path != null )
        {
            File f = new File( path.toOSString() );
            
            while( f != null && ! f.exists() )
            {
                f = f.getParentFile();
            }
            
            if( f != null )
            {
                dialog.setFilterPath( f.getAbsolutePath() );
            }
        }
        
        return dialog.open();
    }
    
    private static final class Resources extends NLS 
    {
        public static String label;

        static 
        {
            initializeMessages( AbsoluteFolderPathBrowseActionHandler.class.getName(), Resources.class );
        }
    }

}