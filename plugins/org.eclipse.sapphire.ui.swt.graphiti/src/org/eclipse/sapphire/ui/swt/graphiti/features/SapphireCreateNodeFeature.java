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
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireCreateNodeFeature extends AbstractCreateFeature implements SapphireCreateFeature
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
		DiagramNodePart nodePart = this.nodeTemplate.createNewDiagramNode();
		nodePart.setNodePosition(context.getX(), context.getY());

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

	public IDiagramNodeDef getNodeDef()
	{
		return this.nodeTemplate.getDefinition();
	}
	
	public int compareTo(SapphireCreateFeature o) 
	{
		if (!(o instanceof SapphireCreateNodeFeature || o instanceof SapphireCreateConnectionFeature))
		{
			throw new IllegalArgumentException();
		}
		String createName = getCreateName();
		String otherName = null;
		if (o instanceof SapphireCreateNodeFeature)
		{
			otherName = ((SapphireCreateNodeFeature)o).getCreateName();
		}
		else if (o instanceof SapphireCreateConnectionFeature)
		{
			otherName = ((SapphireCreateConnectionFeature)o).getCreateName();
		}
		return createName.compareTo(otherName);
	}
	
	public String getPaletteCompartmentId()
	{
		return getNodeDef().getToolPaletteCompartment().getContent();
	}
}
