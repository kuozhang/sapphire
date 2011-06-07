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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDoubleClickNodeFeature extends AbstractCustomFeature 
{
	private ISapphirePart sapphirePart;
	
	public SapphireDoubleClickNodeFeature(IFeatureProvider fp, ISapphirePart sapphirePart)
	{
		super(fp);
		this.sapphirePart = sapphirePart;
	}
		
	@Override
	public boolean canExecute(ICustomContext context) 
	{
		if (context.getInnerGraphicsAlgorithm() instanceof Text)
		{
			return true;
		}
		else if (sapphirePart instanceof DiagramNodePart)
		{
			DiagramNodePart diagramNodePart = (DiagramNodePart)sapphirePart;
			SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)diagramNodePart.getDefaultActionHandler();
			if (handler != null)
			{
				return handler.canExecute(diagramNodePart);
			}			
		}
		return false;
	}
	
	public void execute(ICustomContext context) 
	{
		if (context.getInnerGraphicsAlgorithm() instanceof Text)
		{
			getFeatureProvider().getDirectEditingInfo().setGraphicsAlgorithm(context.getInnerGraphicsAlgorithm());
			getFeatureProvider().getDirectEditingInfo().setPictogramElement(context.getInnerPictogramElement());
			getFeatureProvider().getDirectEditingInfo().setMainPictogramElement(context.getPictogramElements()[0]);
			getFeatureProvider().getDirectEditingInfo().setActive(true);
			getFeatureProvider().getDiagramTypeProvider().getDiagramEditor().refresh();
		}
		else if (sapphirePart instanceof DiagramNodePart)
		{
			DiagramNodePart diagramNodePart = (DiagramNodePart)sapphirePart;
			SapphireRenderingContext renderingCtx = ((SapphireDiagramFeatureProvider)this.getFeatureProvider()).getRenderingContext(diagramNodePart);
			diagramNodePart.getDefaultActionHandler().execute(renderingCtx);
		}
	}
		
	@Override
	public boolean hasDoneChanges() 
	{
		if (sapphirePart instanceof DiagramNodePart)
		{
			DiagramNodePart diagramNodePart = (DiagramNodePart)sapphirePart;
			SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)diagramNodePart.getDefaultActionHandler();
			if (handler != null)
			{
				return handler.hasDoneModelChanges();
			}
		}
		return false;
	}
	
}
