/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.graphiti.actions.AddNodeAction;
import org.eclipse.sapphire.ui.swt.graphiti.actions.SapphireActionHandlerDelegate;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@SuppressWarnings("restriction")

public class SapphireDiagramEditorContextMenuProvider extends ContextMenuProvider
{
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
			if (selectedPart instanceof SapphireDiagramEditorPagePart)
			{
				SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)selectedPart;
				SapphireActionGroup actionGroup = diagramPart.getNodeAddActionGroup();
				// diagram page context menu
				// First add "Add" submenu for each node type
				SapphireAction action = actionGroup.getAction("Sapphire.Diagram.Add");
				if (action.getActiveHandlers().size() == 1)
				{
					final SapphireActionHandler actionHandler = action.getActiveHandlers().get(0);
					AddNodeAction addNodeAction = new AddNodeAction(this.sapphireDiagramEditor, action.getActiveHandlers().get(0))
					{
						@Override
						public String getText()
						{
							String text = NLS.bind(Resources.singleAdd, actionHandler.getLabel());
							return LabelTransformer.transform(text, CapitalizationType.TITLE_STYLE, true);
						}
					};
					
					menuMgr.add(addNodeAction);
				}
				else
				{
					MenuManager addMenuMgr = new MenuManager(Resources.add);
					addMenuMgr.setParent(menuMgr);
					menuMgr.add(addMenuMgr);
					for (SapphireActionHandler handler : action.getActiveHandlers())
					{
						AddNodeAction addNodeAction = new AddNodeAction(this.sapphireDiagramEditor, handler);
						addMenuMgr.add(addNodeAction);
					}
				}
				// TODO Print, Save AS Image, Zoom context menus
			}
			else if (selectedPart instanceof DiagramNodePart)
			{
				// diagram node context menu
				// First add node default action
				DiagramNodePart nodePart = (DiagramNodePart)selectedPart;
				SapphireActionHandler defaultHandler = nodePart.getDefaultActionHandler();
				if (defaultHandler != null)
				{
					SapphireActionHandlerDelegate handlerWrapper = 
							new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, defaultHandler);
					menuMgr.add(handlerWrapper);
				}
				// add "show in source" action
				SapphireActionHandler showInSourceHandler = nodePart.getShowInSourceActionHandler();
				if (showInSourceHandler != null)
				{
					SapphireActionHandlerDelegate handlerWrapper = 
							new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, showInSourceHandler);
					menuMgr.add(handlerWrapper);					
				}
				
				// add other node actions
				List<SapphireAction> otherActions = nodePart.getAllOtherActions();
				if (otherActions.size() > 0)
				{
					menuMgr.add(new Separator());
					for (SapphireAction action : otherActions)
					{
						SapphireActionHandler handler = action.getFirstActiveHandler();
						if (handler != null)
						{
							SapphireActionHandlerDelegate handlerWrapper = 
									new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, handler);
							menuMgr.add(handlerWrapper);												
						}
					}
				}
			}
			else if (selectedPart instanceof DiagramConnectionPart)
			{
				DiagramConnectionPart connPart = (DiagramConnectionPart)selectedPart;
				// add "show in source" action
				SapphireActionHandler showInSourceHandler = connPart.getShowInSourceActionHandler();
				if (showInSourceHandler != null)
				{
					SapphireActionHandlerDelegate handlerWrapper = 
							new SapphireActionHandlerDelegate(this.sapphireDiagramEditor, showInSourceHandler);
					menuMgr.add(handlerWrapper);					
				}				
			}
		}
	}

	private static final class Resources extends NLS    
	{
		public static String add;
		public static String singleAdd;
		
	    static
	    {
	        initializeMessages( SapphireDiagramEditorContextMenuProvider.class.getName(), Resources.class );
	    }
	}
	
}
