/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphirePaletteCompartmentEntry extends PaletteCompartmentEntry 
{
	private String paletteEntryId;
	
	public SapphirePaletteCompartmentEntry(String label, String id, String iconId) 
	{
		super(label, iconId);
		this.paletteEntryId = id;
	}
	
	public String getPaletteEntryId()
	{
		return this.paletteEntryId;
	}
}
