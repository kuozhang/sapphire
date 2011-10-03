/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [348812] Eliminate separate Sapphire.Diagram.Add action
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.graphiti.actions.SapphireActionHandlerDelegate;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@SuppressWarnings("restriction")

public class SapphireDiagramEditorContextMenuProvider extends ContextMenuProvider
{
	private static final String DIAGRAM_NODE_DEFAULT_ACTION = "Sapphire.Diagram.Node.Default";
	private static final String SAPPHIRE_ADD_ACTION = "Sapphire.Add";
	private static final String DIAGRAM_DEFAULT_GROUP = "Diagram.Default";
	private String group;
	
	private SapphireDiagramEditor sapphireDiagramEditor;
	
	public SapphireDiagramEditorContextMenuProvider(SapphireDiagramEditor sapphireEditor)
	{
		super(sapphireEditor.getGraphicalViewer());
		this.sapphireDiagramEditor = sapphireEditor;
	}
	@Override
	public void buildContextMenu(IMenuManager menuMgr) 
	{
		List<SapphirePart> selectedParts = this.sapphireDiagramEditor.getSelectedParts();
		if (selectedParts.size() == 1)
		{
			SapphirePart selectedPart = selectedParts.get(0);
			
			String actionContext = null;
			if (selectedPart instanceof SapphireDiagramEditorPagePart)
			{
				actionContext = SapphireActionSystem.CONTEXT_DIAGRAM_EDITOR;
			}
			else if (selectedPart instanceof DiagramNodePart)
			{
				actionContext = SapphireActionSystem.CONTEXT_DIAGRAM_NODE;
			}
			else if (selectedPart instanceof DiagramConnectionPart)
			{
				actionContext = SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION;
			}
			
			SapphireActionGroup actionGroup = selectedPart.getActions(actionContext);
			
			for (SapphireAction action : actionGroup.getActions())
			{
				if (!(action.getId().equals(DIAGRAM_NODE_DEFAULT_ACTION)))
				{
					addActionToContextMenu(menuMgr, action);
				}
			}
			
		}
	}

	private void addActionToContextMenu(IMenuManager menuMgr, final SapphireAction action)
	{
		String group = action.getGroup();
		if ((this.group != null && group == null) || 
				(this.group == null && group != null) ||
				this.group != null && group != null && !this.group.equals(group))
		{
			// Start a new group. Add a separator to the context menu
			this.group = group;
			menuMgr.add(new Separator(this.group == null ? DIAGRAM_DEFAULT_GROUP : this.group));
		}
		else
		{
			this.group = group;
		}
		if (action.getActiveHandlers().size() == 1)
		{
			final SapphireActionHandler actionHandler = action.getActiveHandlers().get(0);
			if (!(action.getId().equals(SAPPHIRE_ADD_ACTION) && !actionHandler.isEnabled()))
			{
				SapphireActionHandlerDelegate actionDelegate = new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, actionHandler);
				menuMgr.add(actionDelegate);
			}
		}
		else if (action.getActiveHandlers().size() > 1)
		{			
			String menuText = LabelTransformer.transform(action.getLabel(), CapitalizationType.TITLE_STYLE, true);
			MenuManager addMenuMgr = new MenuManager(menuText, action.getImage(16), action.getId());
			addMenuMgr.setParent(menuMgr);
			menuMgr.add(addMenuMgr);
			for (SapphireActionHandler handler : action.getActiveHandlers())
			{
				if (!(action.getId().equals(SAPPHIRE_ADD_ACTION) && !handler.isEnabled()))
				{
					SapphireActionHandlerDelegate actionDelegate = 
									new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, handler);
					addMenuMgr.add(actionDelegate);
				}
			}
		}
		
	}
}
