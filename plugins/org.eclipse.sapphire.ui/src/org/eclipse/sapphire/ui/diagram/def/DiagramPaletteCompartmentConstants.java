/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.util.NLS;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramPaletteCompartmentConstants 
{
	public static final String CONNECTIONS_COMPARTMENT_ID = "Sapphire.Diagram.Palette.Connections";
	public static final String NODES_COMPARTMENT_ID = "Sapphire.Diagram.Palette.Nodes";
	public static final String CONNECTIONS_COMPARTMENT_LABEL = Resources.connectionsCompartmentLabel;
	public static final String NODES_COMPARTMENT_LABEL = Resources.nodesCompartmentLabel;
	
	private static final class Resources extends NLS
	{
		public static String connectionsCompartmentLabel;
		public static String nodesCompartmentLabel;
		
	    static
	    {
	        initializeMessages( DiagramPaletteCompartmentConstants.class.getName(), Resources.class );
	    }
		
	}
	
}
