/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def.internal;

import java.util.SortedSet;

import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class PaletteCompartmentIdService extends PossibleValuesService 
{

	@Override
	protected void fillPossibleValues(SortedSet<String> values) 
	{
		values.add("connections");
		values.add("nodes");
	}

}
