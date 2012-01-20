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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireMoveNodeFeature extends DefaultMoveShapeFeature 
{
	public SapphireMoveNodeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	@Override
	protected void internalMove(IMoveShapeContext context) 
	{
		super.internalMove(context);
 		PictogramElement pe = context.getPictogramElement();
 		Object bo = getBusinessObjectForPictogramElement(pe);
 		if (bo instanceof DiagramNodePart)
 		{
 			DiagramNodePart nodePart = (DiagramNodePart)bo;
 			nodePart.setNodePosition(context.getX(), context.getY());
 		}
	}
}
