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

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramNodeEditPart;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.SapphireDiagramEditorEditPartFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditor extends GraphicalEditorWithFlyoutPalette {

    private DiagramGeometryWrapper diagramGeometry;
	private PaletteRoot root;
    private IDiagramEditorPageDef diagramPageDef;
    private SapphireDiagramEditorPagePart diagramPart;
    private SapphireDiagramPartListener diagramPartListener;

    public SapphireDiagramEditor(final IModelElement rootModelElement, final IPath pageDefinitionLocation) {
		final String bundleId = pageDefinitionLocation.segment( 0 );
        final String pageId = pageDefinitionLocation.lastSegment();
        final String relPath = pageDefinitionLocation.removeFirstSegments( 1 ).removeLastSegments( 1 ).toPortableString();
        
        final ISapphireUiDef def = SapphireUiDefFactory.load( bundleId, relPath );
        
        this.diagramPageDef = (IDiagramEditorPageDef) def.getPartDef( pageId, true, IDiagramEditorPageDef.class );

        this.diagramPart = new SapphireDiagramEditorPagePart();
        this.diagramPart.init(null, rootModelElement, this.diagramPageDef, Collections.<String,String>emptyMap());

		setEditDomain(new DefaultEditDomain(this));

		this.diagramPartListener = new SapphireDiagramPartListener() 
        {
            @Override
            public void handleNodeUpdateEvent(final DiagramNodeEvent event)
            {
                updateNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
                addNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeDeleteEvent(final DiagramNodeEvent event)
            {
                removeNode((DiagramNodePart)event.getPart());
            }

			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {
		    	moveNode((DiagramNodePart)event.getPart());
		    }
			
			@Override
			public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
			{
				updateConnection((DiagramConnectionPart)event.getPart());
			}

            @Override
            public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
            {
                updateConnectionEndpoint((DiagramConnectionPart)event.getPart());
            }

            @Override
            public void handleConnectionAddEvent(final DiagramConnectionEvent event)
            {
                addConnectionIfPossible((DiagramConnectionPart)event.getPart());
            }

			@Override
			public void handleConnectionDeleteEvent(final DiagramConnectionEvent event)
			{
				removeConnection((DiagramConnectionPart)event.getPart());
			}
			
		    public void handleConnectionAddBendpointEvent(final DiagramConnectionEvent event)
		    {
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
		    {
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
		    {
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }
			
		    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
		    {
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    @Override
			public void handleGridStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, 
						new Boolean(diagramPart.isGridVisible()));
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, 
						new Boolean(diagramPart.isGridVisible()));
				markEditorDirty();
			}
			
			@Override
			public void handleGuideStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
						new Boolean(diagramPart.isShowGuides()));
				markEditorDirty();
			}

			@Override
			public void handleDiagramUpdateEvent(final DiagramPageEvent event)
			{
				refreshPalette();
			}
			
		};
		
		this.diagramPart.addListener(this.diagramPartListener);
    }
    
	@Override
	public boolean isDirty()
	{
		boolean dirty = super.isDirty();
		//dirty |= this.gridVisibilityChanged;
		return dirty;
	}
	
	protected void markEditorDirty() {
		System.out.println("TODO markEditorDirty");
	}
	
	protected void removeConnection(DiagramConnectionPart part) {
		System.out.println("TODO removeConnection");
	}

	protected void addConnectionIfPossible(DiagramConnectionPart connPart) {
		IModelElement endpoint1 = connPart.getEndpoint1();
		IModelElement endpoint2 = connPart.getEndpoint2();
		DiagramNodePart nodePart1 = this.diagramPart.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.diagramPart.getDiagramNodePart(endpoint2);
		if (nodePart1 == null || nodePart2 == null) {
			return;
		}

		EditPart editPart1 = getEditPart(nodePart1);
		EditPart editPart2 = getEditPart(nodePart2);
		if (editPart1 == null || editPart2 == null) {
			return;
		}
		editPart1.refresh();
		editPart2.refresh();
	}

	protected void updateConnectionEndpoint(DiagramConnectionPart part) {
		System.out.println("TODO updateConnectionEndpoint");
	}

	protected void updateConnection(DiagramConnectionPart part) {
		EditPart editPart = getEditPart(part);
		if (editPart == null) {
			return;
		}

		editPart.refresh();
	}

	protected void moveNode(DiagramNodePart part) {
		EditPart editPart = getEditPart(part);
		if (editPart == null) {
			return;
		}

		editPart.refresh();
	}

	protected void removeNode(DiagramNodePart part) {
		System.out.println("TODO removeNode");
	}

	protected void addNode(DiagramNodePart part) {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.getContents().refresh();
	}

	protected void updateNode(DiagramNodePart part) {
		EditPart editPart = getEditPart(part);
		if (editPart == null) {
			return;
		}

		editPart.refresh();
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//System.out.println("TODO selectionChanged " + selection);
	}    
	
	private void refreshPalette() {
		System.out.println("TODO refreshPalette");
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		if (root == null)
			root = SapphireDiagramEditorPaletteFactory.createPalette(diagramPageDef);
		return root;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		System.out.println("TODO doSave");
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		// Is this the right place?
		setEditDomain(new DefaultEditDomain(this));

		SapphireDiagramEditorInput diagramInput = (SapphireDiagramEditorInput)input;
		IFile diagramFile = diagramInput.getDiagramFile();
		IFile layoutFile = diagramInput.getLayoutFile();
		this.diagramGeometry = new DiagramGeometryWrapper(layoutFile, getPart());
		System.out.println("setInput diagramFile: " + diagramFile);
		System.out.println("setInput layoutFile: " + layoutFile);
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();
		
		// set the contents of this editor
		viewer.setContents(diagramPart); 

		// listen for dropped parts
		viewer.addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));
	}
	
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setEditPartFactory(new SapphireDiagramEditorEditPartFactory());
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

		// configure the context menu provider
//		ContextMenuProvider cmProvider = new ShapesEditorContextMenuProvider(
//				viewer, getActionRegistry());
//		viewer.setContextMenu(cmProvider);
//		getSite().registerContextMenu(cmProvider, viewer);
	}

	public SapphireDiagramEditorPagePart getPart() {
		return this.diagramPart;
	}
	
	private EditPart getEditPart(DiagramNodePart part) {
		GraphicalViewer viewer = getGraphicalViewer();
		if (viewer != null) {
			EditPart viewerEditPart = viewer.getContents(); 
			if (viewerEditPart.getModel() == part) {
				return viewerEditPart;
			}
			for (Object object : viewerEditPart.getChildren()) {
				EditPart childEditPart = (EditPart)object;
				if (childEditPart.getModel() == part) {
					return childEditPart;
				}
			}
		}
		return null;
	}
	
	private EditPart getEditPart(DiagramConnectionPart part) {
		GraphicalViewer viewer = getGraphicalViewer();
		if (viewer != null) {
			EditPart viewerEditPart = viewer.getContents(); 
			if (viewerEditPart.getModel() == part) {
				return viewerEditPart;
			}
			for (Object object : viewerEditPart.getChildren()) {
				if (object instanceof DiagramNodeEditPart) {
					DiagramNodeEditPart nodeEditPart = (DiagramNodeEditPart)object;
					for (Object conn : nodeEditPart.getSourceConnections()) {
						EditPart connEditPart = (EditPart)conn;
						if (connEditPart.getModel() == part) {
							return connEditPart;
						}
					}
				}
			}
		}
		return null;
	}
}
