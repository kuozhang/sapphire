/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.diagram.def.DiagramPaletteCompartmentConstants;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPaletteCompartmentDef;

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
	static PaletteRoot createPalette(IDiagramEditorPageDef diagramPageDef, DiagramImageCache imageCache) {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createModelIndependentTools(palette));
		
		List<DiagramPaletteDrawer> drawers = new ArrayList<DiagramPaletteDrawer>();
		Map<String, List<ToolEntry>> entries = new HashMap<String, List<ToolEntry>>();
		ModelElementList<IDiagramPaletteCompartmentDef> compartmentDefs = diagramPageDef.getPaletteCompartments();
		PaletteContainer defaultContainer = null;
		if (compartmentDefs.size() == 0)
		{
			String label = LabelTransformer.transform(DiagramPaletteCompartmentConstants.CONNECTIONS_COMPARTMENT_LABEL, 
					CapitalizationType.TITLE_STYLE, true);
			DiagramPaletteDrawer connectionDrawer = new DiagramPaletteDrawer(label, DiagramPaletteCompartmentConstants.CONNECTIONS_COMPARTMENT_ID);
			drawers.add(connectionDrawer);

			String label2 = LabelTransformer.transform(DiagramPaletteCompartmentConstants.NODES_COMPARTMENT_LABEL, 
					CapitalizationType.TITLE_STYLE, true);			
			DiagramPaletteDrawer nodesDrawer = new DiagramPaletteDrawer(label2, DiagramPaletteCompartmentConstants.NODES_COMPARTMENT_ID);
			drawers.add(nodesDrawer);

			defaultContainer = connectionDrawer;
		}
		else
		{
			for (IDiagramPaletteCompartmentDef compartmentDef : compartmentDefs)
			{
				String label = LabelTransformer.transform(compartmentDef.getLabel().getContent(), 
						CapitalizationType.TITLE_STYLE, true);		
				DiagramPaletteDrawer drawer = new DiagramPaletteDrawer(label, compartmentDef.getId().getContent());
				drawers.add(drawer);
				
				if (defaultContainer == null) {
					defaultContainer = drawer;
				}
			}
		}
		
        for (IDiagramConnectionDef connDef : diagramPageDef.getDiagramConnectionDefs()) {
            IDiagramImageChoice image = connDef.getToolPaletteImage().element();
            ImageDescriptor imageDescriptor = imageCache.getImageDescriptor(image);
            
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
			if (tpLabel != null) {
	    		ToolEntry tool = new ConnectionCreationToolEntry(tpLabel, tpDesc, factory, imageDescriptor, imageDescriptor);
	    		
	    		DiagramPaletteDrawer drawer = getDiagramPaletteDrawer(drawers, connDef.getToolPaletteCompartment().getContent());
	    		List<ToolEntry> list = entries.get(drawer.getId());
	    		if (list == null) {
	    			list = new ArrayList<ToolEntry>();
	    			entries.put(drawer.getId(), list);
	    		}
	    		list.add(tool);
			} else {
				// TODO which case is this?? 
			}
        }
		
        for (IDiagramNodeDef nodeDef : diagramPageDef.getDiagramNodeDefs()) {
            IDiagramImageChoice image = nodeDef.getToolPaletteImage().element();
            ImageDescriptor imageDescriptor = imageCache.getImageDescriptor(image);

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
    		ToolEntry tool = new CombinedTemplateCreationEntry(tpLabel, tpDesc, factory, imageDescriptor, imageDescriptor);

    		// find the right drawer
    		DiagramPaletteDrawer drawer = getDiagramPaletteDrawer(drawers, nodeDef.getToolPaletteCompartment().getContent());
    		List<ToolEntry> list = entries.get(drawer.getId());
    		if (list == null) {
    			list = new ArrayList<ToolEntry>();
    			entries.put(drawer.getId(), list);
    		}
    		list.add(tool);
        }
        
        // sort the drawers
        for (DiagramPaletteDrawer drawer : drawers) {
    		List<ToolEntry> list = entries.get(drawer.getId());
    		Collections.sort(list, new Comparator<ToolEntry>() {
				public int compare(ToolEntry x, ToolEntry y) {
		        	return x.getLabel().compareTo(y.getLabel());
				}
    		});
    		drawer.addAll(list);
    		palette.add(drawer);
        }

        return palette;
	}
	
	private static DiagramPaletteDrawer getDiagramPaletteDrawer(List<DiagramPaletteDrawer> drawers, String id) {
		for (DiagramPaletteDrawer drawer : drawers) {
			if (id.equals(drawer.getId())) {
				return drawer;
			}
		}
		return drawers.get(0);
	}
	
	/** Create the "Tools" group. */
	private static PaletteContainer createModelIndependentTools(PaletteRoot palette) {
		PaletteGroup group = new PaletteGroup("Tools");

		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		group.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		group.add(new MarqueeToolEntry());

		return group;
	}

	/** Utility class. */
	private SapphireDiagramEditorPaletteFactory() {
	}

}