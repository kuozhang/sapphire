/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireCreateNodeFeature extends AbstractCreateFeature 
{
	private DiagramNodeTemplate nodeTemplate;
	
	public SapphireCreateNodeFeature(IFeatureProvider fp, DiagramNodeTemplate nodeTemplate)
	{
		super(fp, nodeTemplate.getToolPaletteLabel(), nodeTemplate.getToolPaletteDesc());
		this.nodeTemplate = nodeTemplate;		
	}

	public boolean canCreate(ICreateContext context) 
	{		
		return context.getTargetContainer() instanceof Diagram;
	}

	public Object[] create(ICreateContext context) 
	{
		// In general the model property listener in the node template should handle 
		// the addition of new element. But the passed in context contains x, y, width and
		// height of the new node, we are bypassing the listener mechanism here.
		this.nodeTemplate.removeModelLister();
		DiagramNodePart nodePart = this.nodeTemplate.createNewDiagramNode();
		this.nodeTemplate.addModelListener();
		
		addGraphicalRepresentation(context, nodePart);
		// activate direct editing after object creation
		getFeatureProvider().getDirectEditingInfo().setActive(true);
		
		return new Object[] { nodePart };
	}
	
	@Override
	public String getCreateImageId()
	{
		IDiagramImageChoice image = this.nodeTemplate.getToolPaletteImage();
		if (image != null)
		{
			return image.getImageId().getContent();
		}
		return super.getCreateImageId();
	}
}
