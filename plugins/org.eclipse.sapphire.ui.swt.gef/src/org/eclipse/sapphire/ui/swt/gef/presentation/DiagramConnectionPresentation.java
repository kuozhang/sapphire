/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramConnectionPresentation extends DiagramPresentation 
{
	public DiagramConnectionPresentation(final DiagramConnectionPart connPart, final DiagramPresentation parent, 
			final Shell shell, final DiagramConfigurationManager configManager, final DiagramResourceCache resourceCache)
	{
		super(connPart, parent, configManager, shell);
	}

	@Override
	public DiagramConnectionPart part()
	{
		return (DiagramConnectionPart)super.part();
	}
}
