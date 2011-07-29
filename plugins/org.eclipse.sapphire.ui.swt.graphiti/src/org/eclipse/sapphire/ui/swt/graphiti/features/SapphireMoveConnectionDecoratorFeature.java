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
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.impl.DefaultMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

public class SapphireMoveConnectionDecoratorFeature extends
		DefaultMoveConnectionDecoratorFeature 
{
	public SapphireMoveConnectionDecoratorFeature(IFeatureProvider fp)
	{
		super(fp);
	}

	@Override
	public void moveConnectionDecorator(IMoveConnectionDecoratorContext context) 
	{
		super.moveConnectionDecorator(context);
		Connection conn = context.getConnectionDecorator().getConnection();
		Object bo = getBusinessObjectForPictogramElement(conn);
		if (bo instanceof DiagramConnectionPart)
		{
			DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
			connPart.setLabelPosition(context.getX(), context.getY());
		}
	}

}
