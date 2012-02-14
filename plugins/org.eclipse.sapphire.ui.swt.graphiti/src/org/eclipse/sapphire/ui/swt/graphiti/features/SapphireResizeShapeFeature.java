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

import org.eclipse.graphiti.features.DefaultResizeConfiguration;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeConfiguration;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireResizeShapeFeature extends DefaultResizeShapeFeature 
{
	public SapphireResizeShapeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
 		Object bo = getBusinessObjectForPictogramElement(pe);
 		if (bo instanceof DiagramNodePart)
 		{
 			DiagramNodePart nodePart = (DiagramNodePart)bo;
 			return nodePart.canResizeShape();
 		}
 		return super.canResizeShape(context);
	}
	
	@Override
	public void resizeShape(IResizeShapeContext context) 
	{
		super.resizeShape(context);
		
 		PictogramElement pe = context.getPictogramElement();
 		Object bo = getBusinessObjectForPictogramElement(pe);
 		if (bo instanceof DiagramNodePart)
 		{
			int x = context.getX();
			int y = context.getY();
			int width = context.getWidth();
			int height = context.getHeight();
			DiagramNodePart nodePart = (DiagramNodePart)bo;
			nodePart.setNodeBounds(x, y, width, height);
 		}
	}
	
	
	@Override
	public IResizeConfiguration getResizeConfiguration(IResizeShapeContext context) 
	{
		if (!canResizeShape(context))
		{
			return new NoResizeConfiguration();
		}
		return new DefaultResizeConfiguration();
	}
	
}
