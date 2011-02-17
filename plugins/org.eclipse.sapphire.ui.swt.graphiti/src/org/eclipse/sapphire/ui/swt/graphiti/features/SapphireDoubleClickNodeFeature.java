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
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeDefaultActionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDoubleClickNodeFeature extends AbstractCustomFeature 
{
	private DiagramNodeDefaultActionPart defaultActionPart;
	
	public SapphireDoubleClickNodeFeature(IFeatureProvider fp, DiagramNodeDefaultActionPart actionPart)
	{
		super(fp);
		this.defaultActionPart = actionPart;
	}
	
	@Override
	public String getName() 
	{
		return this.defaultActionPart.getLabel();
	}

	@Override
	public String getDescription() 
	{
		return this.defaultActionPart.getDescription();
	}	
	
	@Override
	public boolean canExecute(ICustomContext context) 
	{
		if (this.defaultActionPart.getActionHandler() != null)
		{
			return true;
		}
		return false;
	}
	
	public void execute(ICustomContext context) 
	{
		if (this.defaultActionPart.getActionHandler() != null)
		{
			SapphireRenderingContext renderingCtx = new SapphireRenderingContext(defaultActionPart, null);
			this.defaultActionPart.getActionHandler().execute(renderingCtx);
		}
	}
		
}
