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

package org.eclipse.sapphire.ui.swt.gef.internal;

import java.util.List;

import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TraverseUtil 
{
	public static void gotoNextTextPart(TextPart textPart)
	{
    	DiagramNodePart nodePart = textPart.nearest(DiagramNodePart.class);
    	List<TextPart> textParts = nodePart.getContainedTextParts();
    	int index = textParts.indexOf(textPart);
    	SapphireDiagramEditorPagePart editorPagePart = nodePart.nearest(SapphireDiagramEditorPagePart.class);
    	for (int i = index + 1; i < textParts.size(); i++)
    	{
    		TextPart nextSibling = textParts.get(i);
    		if (nextSibling.isEditable())
    		{
    			editorPagePart.selectAndDirectEdit(nextSibling);
    			break;
    		}                		
    	}
		
	}
}
