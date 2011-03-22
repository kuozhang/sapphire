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

package org.eclipse.sapphire.ui.swt.graphiti;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramRenderingContext extends SapphireRenderingContext 
{
	private ContainerShape containerShape;
	
	public DiagramRenderingContext(ISapphirePart part, ContainerShape containerShape)
	{
		super(part, null);
		this.containerShape = containerShape;
	}
	
	public ContainerShape getContainerShape()
	{
		return this.containerShape;
	}
}
