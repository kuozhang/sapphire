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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.ui.diagram.def.DiagramPaletteCompartmentConstants;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPaletteCompartmentDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class PaletteCompartmentIdService extends PossibleValuesService 
{

	@Override
	protected void fillPossibleValues(SortedSet<String> values) 
	{
		IModelElement model = this.context(IModelElement.class);
		IDiagramEditorPageDef diagramPageDef = model.nearest(IDiagramEditorPageDef.class);
		ModelElementList<IDiagramPaletteCompartmentDef> paletteDefs = diagramPageDef.getDiagramPaletteDefs();
		if (paletteDefs.size() == 0)
		{
			values.add(DiagramPaletteCompartmentConstants.NODES_COMPARTMENT_ID);
			values.add(DiagramPaletteCompartmentConstants.CONNECTIONS_COMPARTMENT_ID);
		}
		else
		{
			for (IDiagramPaletteCompartmentDef compartmentDef : paletteDefs)
			{
				values.add(compartmentDef.getId().getContent());
			}
		}
	}

}
