/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.palette;

import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.ui.diagram.state.DiagramEditorPageState;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;


/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DefaultFlyoutPalettePreferences implements FlyoutPaletteComposite.FlyoutPreferences 
{
	private DiagramEditorPageState pageState;
	
	public DefaultFlyoutPalettePreferences(DiagramEditorPageState pageState) 
	{
		this.pageState = pageState;
	}

	public final int getDockLocation() 
	{
		return this.pageState.getPalettePreferences().getDockLocation().getContent();
	}

	public final void setDockLocation(int dockLocation) 
	{
		int currentLocation = this.pageState.getPalettePreferences().getDockLocation().getContent();
		if (currentLocation != dockLocation)
		{
            this.pageState.getPalettePreferences().setDockLocation(dockLocation);            
            try
            {
                this.pageState.resource().save();
            }
            catch( ResourceStoreException e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }			
		}
	}

	public final int getPaletteState() 
	{
		return this.pageState.getPalettePreferences().getPaletteState().getContent();
	}

	public final void setPaletteState(int paletteState) 
	{
		int currentState = this.pageState.getPalettePreferences().getPaletteState().getContent();
		if (currentState != paletteState)
		{
            this.pageState.getPalettePreferences().setPaletteState(paletteState);            
            try
            {
                this.pageState.resource().save();
            }
            catch( ResourceStoreException e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }			
		}
	}

	public final int getPaletteWidth() 
	{
		return this.pageState.getPalettePreferences().getPaletteWidth().getContent();
	}

	public final void setPaletteWidth(int paletteWidth) 
	{
		int currentWidth = this.pageState.getPalettePreferences().getPaletteWidth().getContent();
		if (currentWidth != paletteWidth)
		{
            this.pageState.getPalettePreferences().setPaletteWidth(paletteWidth);            
            try
            {
                this.pageState.resource().save();
            }
            catch( ResourceStoreException e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }			
		}
	}

}
