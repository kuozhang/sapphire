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

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class DiagramConnectionPart extends SapphirePart 
{
	public abstract boolean removable();
	
	public abstract void remove();
	
	public abstract String getId();
	
	public abstract DiagramConnectionPart reconnect(DiagramNodePart newSrc, DiagramNodePart newTarget);
	
	public abstract boolean canEditLabel();
}
