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

package org.eclipse.sapphire.ui.diagram.graphiti.features;

import java.util.Iterator;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDirectEditNodeFeature extends AbstractDirectEditingFeature 
{
	public SapphireDirectEditNodeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	public int getEditingType() 
	{
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// support direct editing, if it is a DiagramNodePart, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof DiagramNodePart && ga instanceof Text) 
		{
			return ((DiagramNodePart)bo).canEditLabel();
		}
		// direct editing not supported in all other cases
		return false;
	}

	public String getInitialValue(IDirectEditingContext context) 
	{
		// return the current label of the node
		PictogramElement pe = context.getPictogramElement();
		DiagramNodePart nodePart = (DiagramNodePart)getBusinessObjectForPictogramElement(pe);
		return nodePart.getLabel();
	}

	public void setValue(String value, IDirectEditingContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
				
		if (pe.eContainer() instanceof Shape)
		{
			DiagramNodePart nodePart = (DiagramNodePart)getBusinessObjectForPictogramElement(pe);
			nodePart.setLabel(value);
			IModelElement nodeElement = nodePart.getLocalModelElement();
			Shape nodeShape = (Shape)pe.eContainer();
			
			// go through all the connections associated with this bo and update them
			for (Iterator<Anchor> iter = nodeShape.getAnchors().iterator(); iter.hasNext();) 
			{
				Anchor anchor = iter.next();
				for (Iterator<Connection> iterator = Graphiti.getPeService().getAllConnections(anchor).iterator(); iterator.hasNext();) 
				{
					Connection connection = iterator.next();
					Object bo = getBusinessObjectForPictogramElement(connection);
					if (bo instanceof DiagramConnectionPart)
					{
						DiagramConnectionPart connectionPart = (DiagramConnectionPart)bo;
						IModelElement endpoint1 = connectionPart.getEndpoint1();
						IModelElement endpoint2 = connectionPart.getEndpoint2();
						if (endpoint1.equals(nodeElement) || endpoint2.equals(nodeElement))
						{
							connectionPart.removeModelListener();
							if (endpoint1.equals(nodeElement))
							{
								connectionPart.resetEndpoint1();
							}
							else if (endpoint2.equals(nodeElement))
							{
								connectionPart.resetEndpoint2();
							}
							connectionPart.addModelListener();
						}
					}
				}
			}			
		}
				
		// Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of Graphiti
		// (currently in discussion)

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the node
		//updatePictogramElement(((Shape)pe).getContainer());

	}

}
