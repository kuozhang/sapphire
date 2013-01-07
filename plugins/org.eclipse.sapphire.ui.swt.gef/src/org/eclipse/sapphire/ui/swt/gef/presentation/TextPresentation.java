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

import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.diagram.shape.def.FontDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextPresentation extends ShapePresentation 
{
	public TextPresentation(ShapePresentation parent, TextPart textPart)
	{
		super(parent, textPart);
	}
	
	public Color getTextColor()
	{
		return getTextPart().getTextColor();
	}
	
	public String getContent()
	{
		return getTextPart().getContent();
	}

	public FontDef getFontDef()
	{
		return getTextPart().getFontDef();
	}
	
	public boolean truncatable()
	{
		return getTextPart().truncatable();
	}
	
	private TextPart getTextPart()
	{
		return (TextPart)this.getPart();
	}
}
