/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.actions;

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
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
			return this.sapphireActionHandler.getImage(16);
		}
		else
		{
			return this.sapphireActionHandler.getAction().getImage(16);
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
		SapphireDiagramFeatureProvider fp = (SapphireDiagramFeatureProvider)this.diagramEditor.getDiagramTypeProvider().getFeatureProvider();
		DiagramRenderingContext context = fp.getRenderingContext((SapphirePart)this.sapphireActionHandler.getPart());
		ILocation loc = context.getDiagramEditor().getCurrentMouseLocation();
		context.setCurrentMouseLocation(loc.getX(), loc.getY());
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
