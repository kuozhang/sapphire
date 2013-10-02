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

package org.eclipse.sapphire.samples.newfile;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.SapphireDialog;
import org.eclipse.sapphire.workspace.CreateWorkspaceFileOp;
import org.eclipse.sapphire.workspace.ui.CreateWorkspaceFileWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class OpenCreateWorkspaceFileWizardHandler extends AbstractHandler
{
    public Object execute( final ExecutionEvent event )
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow( event );
        
        final CreateWorkspaceFileOp operation = CreateWorkspaceFileOp.TYPE.instantiate();
        
        try
        {
            final SapphireDialog selectRootDialog = new SapphireDialog
            (
                window.getShell(), operation,
                DefinitionLoader.context( getClass() ).sdef( "OpenCreateWorkspaceFileWizard" ).dialog( "SelectRootDialog" )
            );
            
            if( selectRootDialog.open() == Dialog.OK )
            {
                final CreateWorkspaceFileWizard<CreateWorkspaceFileOp> createFileWizard = new CreateWorkspaceFileWizard<CreateWorkspaceFileOp>
                (
                    operation,
                    DefinitionLoader.context( getClass() ).sdef( "OpenCreateWorkspaceFileWizard" ).wizard( "CreateWorkspaceFileWizard" )
                );
                
                final WizardDialog createFileWizardDialog = new WizardDialog( window.getShell(), createFileWizard );
                
                createFileWizardDialog.open();
            }
        }
        finally
        {
            operation.dispose();
        }
        
        return null;
    }
    
}
