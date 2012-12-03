/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Ling Hao - [383924] Extend Sapphire Diagram Framework to support SQL Schema diagram like editors
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.figures.TextFigure;
import org.eclipse.sapphire.ui.swt.gef.model.TextModel;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextEditPart extends ShapeEditPart 
{
	public TextEditPart(DiagramConfigurationManager configManager)
	{
		super(configManager);
	}

	@Override
	protected IFigure createFigure() 
	{
		TextModel textModel = (TextModel)getModel();
		TextPart textPart = (TextPart)textModel.getSapphirePart();
		int textAlignment = ShapeUtil.getTextAlignment(textPart.getLayoutConstraint());
		TextFigure figure = new TextFigure(textModel.getNodeModel().getDiagramModel().getResourceCache(), 
				textPart.getText(), textPart.getTextColor(), textPart.getFontDef(), textAlignment);
		return figure;
	}
		
	@Override 
	protected void refreshVisuals() 
	{
		TextModel textModel = (TextModel)getModel();
		TextPart textPart = (TextPart)textModel.getSapphirePart();
		((TextFigure)getFigure()).setText(textPart.getText());
	}
	
	@Override
	public void performRequest(Request request) 
	{
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
		{
			if (!(request instanceof DirectEditRequest))
			{
				// Direct edit invoked using key command
				
			}
		}
		else if (request.getType().equals(REQ_OPEN))
		{
			int i = 0;
		}
	}
	
}
