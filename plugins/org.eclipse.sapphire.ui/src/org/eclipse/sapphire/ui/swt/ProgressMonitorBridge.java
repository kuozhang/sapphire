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

package org.eclipse.sapphire.ui.swt;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ProgressMonitorBridge extends ProgressMonitor
{
    private final IProgressMonitor base;
    
    public ProgressMonitorBridge( final IProgressMonitor base )
    {
        this.base = base;
    }

    @Override
    public void beginTask( final String name,
                           final int totalWork )
    {
        this.base.beginTask( name, totalWork );
    }

    @Override
    public void done()
    {
        this.base.done();
    }

    @Override
    public void internalWorked( final double work )
    {
        this.base.internalWorked( work );
    }

    @Override
    public boolean isCanceled()
    {
        return this.base.isCanceled();
    }

    @Override
    public void setCanceled( final boolean value )
    {
        this.base.setCanceled( value );
    }

    @Override
    public void setTaskName( final String name )
    {
        this.base.setTaskName( name );
    }

    @Override
    public void subTask( final String name )
    {
        this.base.subTask( name );
    }

    @Override
    public void worked( final int work )
    {
        this.base.worked( work );
    }

}
