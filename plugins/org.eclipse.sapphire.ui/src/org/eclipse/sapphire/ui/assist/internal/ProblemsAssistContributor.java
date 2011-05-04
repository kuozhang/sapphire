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

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistSection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProblemsAssistContributor

    extends PropertyEditorAssistContributor
    
{
    private final Status status;
    
    public ProblemsAssistContributor( final Status status )
    {
        setId( ID_PROBLEMS_CONTRIBUTOR );
        setPriority( PRIORITY_PROBLEMS_CONTRIBUTOR );
        this.status = status;
    }
    
    @Override
    public void contribute( final PropertyEditorAssistContext context )
    {
        if( this.status.children().isEmpty() )
        {
            contribute( context, this.status );
        }
        else
        {
            for( Status child : this.status.children() )
            {
                contribute( context, child );
            }
        }
    }
    
    private static void contribute( final PropertyEditorAssistContext context,
                                    final Status status )
    {
        final Status.Severity valResultSeverity = status.severity();
        String imageKey = null;
        Image image = null;
        
        if( valResultSeverity == Status.Severity.ERROR )
        {
            imageKey = "error";
            image = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
        }
        else if( valResultSeverity == Status.Severity.WARNING )
        {
            imageKey = "error";
            image = PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );
        }
        
        if( imageKey != null )
        {
            final PropertyEditorAssistContribution contribution = new PropertyEditorAssistContribution();
            contribution.setText( "<li style=\"image\" value=\"" + imageKey + "\">" + escapeForXml( status.message() ) + "</li>" );
            contribution.setImage( imageKey, image );
            
            final PropertyEditorAssistSection section = context.getSection( SECTION_ID_PROBLEMS );
            section.addContribution( contribution );
        }
    }
    
}
