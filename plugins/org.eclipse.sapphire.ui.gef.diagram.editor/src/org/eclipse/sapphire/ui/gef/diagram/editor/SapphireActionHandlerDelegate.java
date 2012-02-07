/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;

/**
 * Copied from org.eclipse.sapphire.ui.swt.graphiti
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireActionHandlerDelegate extends Action 
{
	private SapphireDiagramEditor diagramEditor;
	private SapphireActionHandler sapphireActionHandler;
	
	public SapphireActionHandlerDelegate(SapphireDiagramEditor diagramEditor, 
			SapphireActionHandler sapphireActionHandler)
	{
		this.diagramEditor = diagramEditor;
		this.sapphireActionHandler = sapphireActionHandler;
		setEnabled(this.sapphireActionHandler.isEnabled());
		setChecked(this.sapphireActionHandler.isChecked());
	}
	
	@Override
	public String getText()
	{
		String text;
		if (this.sapphireActionHandler.getAction().getActiveHandlers().size() == 1)
		{
			text = this.sapphireActionHandler.getAction().getLabel();
		}
		else
		{
			text = this.sapphireActionHandler.getLabel();
		}
		return LabelTransformer.transform(text, CapitalizationType.TITLE_STYLE, true);
	}

	@Override
	public ImageDescriptor getImageDescriptor() 
	{
		if (this.sapphireActionHandler.getImage(16) != null)
		{
			return SwtRendererUtil.toImageDescriptor( this.sapphireActionHandler.getImage(16) );
		}
		else
		{
			return SwtRendererUtil.toImageDescriptor( this.sapphireActionHandler.getAction().getImage(16) );
		}
	}
	
	@Override
	public void run() 
	{
		this.sapphireActionHandler.attach(new Listener() 
		{			
			@Override
			public void handle(Event event) 
			{
				if (event instanceof SapphireActionHandler.PostExecuteEvent)
				{
					handlePostExecutionEvent((SapphireActionHandler.PostExecuteEvent)event);
				}
			}
		});
		DiagramConfigurationManager configManager = this.diagramEditor.getConfigurationManager();
		DiagramRenderingContext context = configManager.getDiagramRenderingContextCache().get(this.sapphireActionHandler.getPart());
		Point pt = diagramEditor.getMouseLocation();
		context.setCurrentMouseLocation(pt.x, pt.y);
		this.sapphireActionHandler.execute(context);
	}
		
	public SapphireActionHandler getSapphireActionHandler()
	{
		return this.sapphireActionHandler;
	}
	
	protected void handlePostExecutionEvent(SapphireActionHandler.PostExecuteEvent event)
	{
		
	}
}
