/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.def.internal;

import java.util.SortedSet;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.PropertyContentEvent;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.ui.diagram.def.DiagramPaletteCompartmentConstants;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPaletteCompartmentDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public final class ToolPaletteCompartmentPossibleValuesService extends PossibleValuesService 
{
    private Listener listener;
    
    @Override
    protected void init()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                broadcast();
            }
        };
        
        context( IDiagramEditorPageDef.class ).attach( this.listener, "PaletteCompartments/Id" );
    }

	@Override
	protected void fillPossibleValues(SortedSet<String> values) 
	{
		IDiagramEditorPageDef diagramPageDef = context(IDiagramEditorPageDef.class);
		ModelElementList<IDiagramPaletteCompartmentDef> compartments = diagramPageDef.getPaletteCompartments();
		if (compartments.size() == 0)
		{
			values.add(DiagramPaletteCompartmentConstants.NODES_COMPARTMENT_ID);
			values.add(DiagramPaletteCompartmentConstants.CONNECTIONS_COMPARTMENT_ID);
		}
		else
		{
			for (IDiagramPaletteCompartmentDef compartment : compartments)
			{
				values.add(compartment.getId().getContent());
			}
		}
	}

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            context( IDiagramEditorPageDef.class ).detach( this.listener, "PaletteCompartments/Id" );
            this.listener = null;
        }
    }

}
