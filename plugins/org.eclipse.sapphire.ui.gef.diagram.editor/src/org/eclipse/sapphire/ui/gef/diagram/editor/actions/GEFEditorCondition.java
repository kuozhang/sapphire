/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.sapphire.ui.gef.diagram.editor.actions;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

// TODO remove this class when the GEF editor is ready to replace Graphiti based editor
public class GEFEditorCondition extends SapphireCondition 
{

	@Override
	protected boolean evaluate() 
	{
		ISapphirePart part = this.getPart();
		while (!(part instanceof SapphireDiagramEditorPagePart))
		{
			part = part.getParentPart();
		}
		SapphireDiagramEditorPagePart editorPart = (SapphireDiagramEditorPagePart)part;
		String editorType = editorPart.getEditorType();
		return editorType != null && editorType.equals("GEF");
	}

}
