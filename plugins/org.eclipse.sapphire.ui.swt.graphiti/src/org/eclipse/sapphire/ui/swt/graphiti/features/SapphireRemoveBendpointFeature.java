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
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveBendpointFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireRemoveBendpointFeature extends DefaultRemoveBendpointFeature 
{
	public SapphireRemoveBendpointFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	@Override
	public boolean canRemoveBendpoint(IRemoveBendpointContext context) 
	{
		FreeFormConnection freeFormConnection = context.getConnection();
		Object bo = getBusinessObjectForPictogramElement(freeFormConnection);
		if (bo instanceof DiagramImplicitConnectionPart)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void removeBendpoint(IRemoveBendpointContext context) 
	{
		super.removeBendpoint(context);
		
		FreeFormConnection freeFormConnection = context.getConnection();
		Object bo = getBusinessObjectForPictogramElement(freeFormConnection);
		if (bo instanceof DiagramConnectionPart && !(bo instanceof DiagramImplicitConnectionPart))
		{
			DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
			connPart.removeBendpoint(context.getBendpointIndex());

		}		
	}

}
