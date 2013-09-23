/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.samples.po;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.SapphireWizard;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PurchaseComputerActionHandler extends SapphireActionHandler
{
    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final PurchaseComputerOp op = PurchaseComputerOp.TYPE.instantiate();
        
        try
        {
            op.setPurchaseOrder( context.getPart().getLocalModelElement().nearest( PurchaseOrder.class ) );
            
            final SapphireWizard<PurchaseComputerOp> wizard = new SapphireWizard<PurchaseComputerOp>
            ( 
                op,
                DefinitionLoader.context( PurchaseComputerOp.class ).sdef( "PurchaseOrderEditor" ).wizard( "PurchaseComputerWizard" )
            );
            
            final WizardDialog dialog = new WizardDialog( context.getShell(), wizard );
            
            dialog.open();
        }
        finally
        {
            op.dispose();
        }
        
        return null;
    }
    
}
