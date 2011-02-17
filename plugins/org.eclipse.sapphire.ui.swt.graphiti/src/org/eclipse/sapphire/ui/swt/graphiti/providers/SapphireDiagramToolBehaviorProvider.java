/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDoubleClickNodeFeature;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider 
{
	public SapphireDiagramToolBehaviorProvider(IDiagramTypeProvider dtp) 
	{
		super(dtp);
	}

	/**
	 * Override super class impl to not include "Remove"
	 */
	@Override
	protected void setGenericContextButtons(IContextButtonPadData data, PictogramElement pe, int identifiers) 
	{
		identifiers -= CONTEXT_BUTTON_REMOVE;
		super.setGenericContextButtons(data, pe, identifiers);
	}
	
	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) 
	{
		PictogramElement[] pes = context.getPictogramElements();
		for (PictogramElement pe : pes)
		{
			if (pe instanceof ContainerShape)
			{
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
				if (bo instanceof DiagramNodePart)
				{
					DiagramNodePart nodePart = (DiagramNodePart)bo;
					if (nodePart.getDefaultActionPart() != null)
					{
						SapphireDoubleClickNodeFeature dblClikFeature = 
							new SapphireDoubleClickNodeFeature(getFeatureProvider(), nodePart.getDefaultActionPart());
						return dblClikFeature;
					}
				}
			}
		}
		return null;
	}
}
