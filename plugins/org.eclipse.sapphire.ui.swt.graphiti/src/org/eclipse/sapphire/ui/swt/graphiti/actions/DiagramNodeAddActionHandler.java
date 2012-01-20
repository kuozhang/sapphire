/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeAddActionHandler extends SapphireDiagramActionHandler 
{
	private DiagramNodeTemplate nodeTemplate;
	
	public DiagramNodeAddActionHandler(DiagramNodeTemplate nodeTemplate)
	{
		this.nodeTemplate = nodeTemplate;
	}
	
    @Override
    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
    	super.init(action, def);
		if (this.nodeTemplate.getToolPaletteLabel() != null)
		{
			setLabel(this.nodeTemplate.getToolPaletteLabel());
		}
		final ImageData typeSpecificAddImage = this.nodeTemplate.getNodeType().image();
		if (typeSpecificAddImage != null)
		{
			addImage(typeSpecificAddImage);
		}
    }
    
    @Override
    public boolean isEnabled()
    {
    	SapphireDiagramEditorPagePart diagramPart = 
    			(SapphireDiagramEditorPagePart)this.nodeTemplate.getParentPart();
    	return diagramPart.isNodeTemplateVisible(this.nodeTemplate);
    }
    
	@Override
	public boolean canExecute(Object obj) 
	{
		return true;
	}

	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		DiagramNodePart nodePart = this.nodeTemplate.createNewDiagramNode();
		DiagramRenderingContext drc = (DiagramRenderingContext)context;
		Point pt = drc.getCurrentMouseLocation();
		drc.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider().getDirectEditingInfo().setActive(true);
		nodePart.setNodePosition(pt.getX(), pt.getY());
		return nodePart;
	}	

}
