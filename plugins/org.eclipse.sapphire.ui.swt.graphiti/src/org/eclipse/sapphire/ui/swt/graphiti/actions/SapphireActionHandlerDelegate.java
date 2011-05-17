/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionSystemPart;
import org.eclipse.sapphire.ui.SapphireActionSystemPart.Event;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
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
	}
	
	@Override
	public String getText()
	{
		return LabelTransformer.transform(this.sapphireActionHandler.getLabel(), CapitalizationType.TITLE_STYLE, true);
	}

	@Override
	public ImageDescriptor getImageDescriptor() 
	{
		ImageDescriptor id = this.sapphireActionHandler.getImage(16);
		if (id == null)
		{
			id = this.sapphireActionHandler.getAction().getImage(16);
		}
		return id;
	}
	
	@Override
	public void run() 
	{
		this.sapphireActionHandler.addListener(new SapphireActionSystemPart.Listener() 
		{			
			@Override
			public void handleEvent(Event event) 
			{
				if (event instanceof SapphireActionHandler.PostExecuteEvent)
				{
					handlePostExecutionEvent((SapphireActionHandler.PostExecuteEvent)event);
				}
			}
		});
		SapphireDiagramFeatureProvider fp = (SapphireDiagramFeatureProvider)this.diagramEditor.getDiagramTypeProvider().getFeatureProvider();
		SapphireRenderingContext context = fp.getRenderingContext((SapphirePart)this.sapphireActionHandler.getPart());
		this.sapphireActionHandler.execute(context);
	}
	
	public SapphireDiagramEditor getSapphireDiagramEditor()
	{
		return this.diagramEditor;
	}
	
	public SapphireActionHandler getSapphireActionHandler()
	{
		return this.sapphireActionHandler;
	}
	
	protected void handlePostExecutionEvent(SapphireActionHandler.PostExecuteEvent event)
	{
		
	}
}
