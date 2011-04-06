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
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDoubleClickNodeFeature extends AbstractCustomFeature 
{
	private DiagramNodePart diagramNodePart;
	
	public SapphireDoubleClickNodeFeature(IFeatureProvider fp, DiagramNodePart diagramNodePart)
	{
		super(fp);
		this.diagramNodePart = diagramNodePart;
	}
	
	@Override
	public String getName() 
	{
		return this.diagramNodePart.getDefaultAction().getLabel();
	}

	@Override
	public String getDescription() 
	{		
		return this.diagramNodePart.getDefaultAction().getLabel();
	}	
	
	@Override
	public boolean canExecute(ICustomContext context) 
	{
		SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)this.diagramNodePart.getDefaultActionHandler();
		return handler.canExecute(this.diagramNodePart);
	}
	
	public void execute(ICustomContext context) 
	{
		SapphireRenderingContext renderingCtx = ((SapphireDiagramFeatureProvider)this.getFeatureProvider()).getRenderingContext(this.diagramNodePart);
		this.diagramNodePart.getDefaultActionHandler().execute(renderingCtx);
	}
		
	@Override
	public boolean hasDoneChanges() 
	{
		SapphireDiagramActionHandler handler = (SapphireDiagramActionHandler)this.diagramNodePart.getDefaultActionHandler();
		return handler.hasDoneModelChanges();
	}
	
}
