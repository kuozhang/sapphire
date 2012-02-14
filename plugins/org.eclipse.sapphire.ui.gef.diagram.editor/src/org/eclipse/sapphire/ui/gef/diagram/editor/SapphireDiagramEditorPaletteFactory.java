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

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

final class SapphireDiagramEditorPaletteFactory {

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @return a new PaletteRoot
	 */
	static PaletteRoot createPalette(IDiagramEditorPageDef diagramPageDef) {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createModelIndependentTools(palette));
		palette.add(createConnectionsDrawer(diagramPageDef));
		palette.add(createObjectsDrawer(diagramPageDef));
		return palette;
	}

	/** Create the "Tools" group. */
	private static PaletteContainer createModelIndependentTools(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");

		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());

		return toolbar;
	}

	/** Create the "Connections" group. */
	private static PaletteContainer createConnectionsDrawer(IDiagramEditorPageDef diagramPageDef) {
		PaletteDrawer drawer = new PaletteDrawer("Connections");

        for (IDiagramConnectionDef connDef : diagramPageDef.getDiagramConnectionDefs()) {
            IDiagramImageChoice image = connDef.getToolPaletteImage().element();
            CreationFactory factory = new ConnectionCreationFactory(connDef);
			String tpLabel = connDef.getToolPaletteLabel().getContent();
			if (tpLabel != null)
			{
				tpLabel = IDiagramConnectionDef.PROP_TOOL_PALETTE_LABEL.getLocalizationService().text(
								tpLabel, CapitalizationType.TITLE_STYLE, false);
			}
			String tpDesc = connDef.getToolPaletteDescription().getContent();
			if (tpDesc != null)
			{
				tpDesc = IDiagramConnectionDef.PROP_TOOL_PALETTE_DESCRIPTION.getLocalizationService().text(
								tpDesc, CapitalizationType.TITLE_STYLE, false);
			}
    		ToolEntry tool = new ConnectionCreationToolEntry(tpLabel, tpDesc, factory, null, null);
    		drawer.add(tool);
        }
		
		return drawer;
	}

	/** Create the "Objects" group. */
	private static PaletteContainer createObjectsDrawer(IDiagramEditorPageDef diagramPageDef) {
		PaletteDrawer drawer = new PaletteDrawer("Connections");

        for (IDiagramNodeDef nodeDef : diagramPageDef.getDiagramNodeDefs()) {
            IDiagramImageChoice image = nodeDef.getToolPaletteImage().element();
            CreationFactory factory = new NodeCreationFactory(nodeDef);

			String tpLabel = nodeDef.getToolPaletteLabel().getContent();
			if (tpLabel != null)
			{
				tpLabel = IDiagramNodeDef.PROP_TOOL_PALETTE_LABEL.getLocalizationService().text(
								tpLabel, CapitalizationType.TITLE_STYLE, false);
			}
			String tpDesc = nodeDef.getToolPaletteDescription().getContent();
			if (tpDesc != null)
			{
				tpDesc = IDiagramNodeDef.PROP_TOOL_PALETTE_DESCRIPTION.getLocalizationService().text(
								tpDesc, CapitalizationType.TITLE_STYLE, false);
			}
    		ToolEntry tool = new CombinedTemplateCreationEntry(tpLabel, tpDesc, factory, null, null);
    		drawer.add(tool);
        }
		
		return drawer;
	}

	/** Utility class. */
	private SapphireDiagramEditorPaletteFactory() {
		// Utility class
	}

}