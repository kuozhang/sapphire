/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - Add constructor which takes only ISapphirePart
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramRenderingContext extends SapphireRenderingContext 
{
	private SapphireDiagramEditor diagramEditor;
	
	public DiagramRenderingContext(ISapphirePart part, SapphireDiagramEditor diagramEditor) {
		super(part, diagramEditor.getSite().getShell());
		this.diagramEditor = diagramEditor;
	}
	
	public SapphireDiagramEditor getDiagramEditor()
	{
		return this.diagramEditor;
	}
	
}
