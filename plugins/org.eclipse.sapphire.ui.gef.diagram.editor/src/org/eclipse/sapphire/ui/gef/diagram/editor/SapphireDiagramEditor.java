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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.SapphirePart;
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
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.SapphireDiagramEditorPageEditPart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditor extends GraphicalEditorWithFlyoutPalette {

    private DiagramGeometryWrapper diagramGeometry;
	private PaletteRoot root;
    private IDiagramEditorPageDef diagramPageDef;
    private SapphireDiagramEditorPagePart diagramPart;
    private SapphireDiagramPartListener diagramPartListener;
    private List<SapphirePart> selectedParts = null;

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
            	System.out.println("handleNodeUpdateEvent");
                updateNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeAddEvent(final DiagramNodeEvent event)
            {
            	System.out.println("handleNodeAddEvent");
                addNode((DiagramNodePart)event.getPart());
            }
            
            @Override
            public void handleNodeDeleteEvent(final DiagramNodeEvent event)
            {
            	System.out.println("handleNodeDeleteEvent");
                removeNode((DiagramNodePart)event.getPart());
            }

			@Override
		    public void handleNodeMoveEvent(final DiagramNodeEvent event)
		    {
            	System.out.println("handleNodeMoveEvent");
		    	moveNode((DiagramNodePart)event.getPart());
		    }
			
			@Override
			public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
			{
            	System.out.println("handleConnectionUpdateEvent");
				updateConnection((DiagramConnectionPart)event.getPart());
			}

            @Override
            public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
            {
            	System.out.println("handleConnectionEndpointEvent");
                updateConnectionEndpoint((DiagramConnectionPart)event.getPart());
            }

            @Override
            public void handleConnectionAddEvent(final DiagramConnectionEvent event)
            {
            	System.out.println("handleConnectionAddEvent");
                addConnectionIfPossible((DiagramConnectionPart)event.getPart());
            }

			@Override
			public void handleConnectionDeleteEvent(final DiagramConnectionEvent event)
			{
            	System.out.println("handleConnectionDeleteEvent");
				removeConnection((DiagramConnectionPart)event.getPart());
			}
			
		    public void handleConnectionAddBendpointEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionAddBendpointEvent");
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionRemoveBendpointEvent");
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionMoveBendpointEvent");
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }
			
		    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionMoveLabelEvent");
		    	updateConnection((DiagramConnectionPart)event.getPart());
		    }

		    @Override
			public void handleGridStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, new Boolean(diagramPart.isGridVisible()));
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, new Boolean(diagramPart.isGridVisible()));
				markEditorDirty();
			}
			
			@Override
			public void handleGuideStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, new Boolean(diagramPart.isShowGuides()));
				markEditorDirty();
			}

			@Override
			public void handleDiagramUpdateEvent(final DiagramPageEvent event)
			{
            	System.out.println("handleDiagramUpdateEvent");
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
		EditPart editPart = getEditPart(part);
		if (editPart == null) {
			return;
		}

		IModelElement endpoint1 = part.getEndpoint1();
		IModelElement endpoint2 = part.getEndpoint2();

		DiagramNodePart nodePart1 = this.diagramPart.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.diagramPart.getDiagramNodePart(endpoint2);

		EditPart editPart1 = getEditPart(nodePart1);
		EditPart editPart2 = getEditPart(nodePart2);

		if (editPart1 != null) {
			((DiagramNodeEditPart)editPart1).handleSourceConnectionRemoved(part);
		}
		if (editPart2 != null) {
			((DiagramNodeEditPart)editPart2).handleTargetConnectionRemoved(part);
		}
	}

	protected void addConnectionIfPossible(DiagramConnectionPart connPart) {
		// TODO more specific adds?
		refreshConnectionNodes(connPart);
	}
	
	private void refreshConnectionNodes(DiagramConnectionPart connPart) {
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

	protected void updateConnectionEndpoint(DiagramConnectionPart connPart) {
		// TODO more specific updates?
		refreshConnectionNodes(connPart);
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
		EditPart editPart = getEditPart(part);
		if (editPart == null) {
			return;
		}

		SapphireDiagramEditorPageEditPart rootEditPart = (SapphireDiagramEditorPageEditPart)getRootEditPart();
		rootEditPart.handleNodeRemoved(part);
	}

	protected void addNode(DiagramNodePart part) {
		// TODO more specific add
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
		// If not the active editor, ignore selection changed.
		boolean editorIsActive = getSite().getPage().isPartVisible(this);
		if (!editorIsActive) {
			// Check if we are a page of the active multipage editor
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (activeEditor != null) {
				// Check if the top level editor if it is active
				editorIsActive = getSite().getPage().isPartVisible(activeEditor);
				if ( activeEditor instanceof FormEditor ) {
                    if ( !( this.equals( ( (FormEditor) activeEditor).getActiveEditor() ) ) ) {
                        editorIsActive = false;
                    }
                }
            }
        }
        if (editorIsActive) {

            // long start = System.nanoTime();
            // this is where we should check the selection source (part)
            // * for CNF view the link flag must be obeyed
            // this would however require a dependency to
            // org.eclipse.ui.navigator
//            if (part instanceof CommonNavigator) {
//                if (!((CommonNavigator) part).isLinkingEnabled()) {
//                    return;
//                }
//            }
            // useful selection ??
//            if (selection instanceof IStructuredSelection) {
//                IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//                List<PictogramElement> peList = new ArrayList<PictogramElement>();
//                // Collect all Pictogram Elements for all selected domain
//                // objects into one list
//                for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
//                    Object object = iterator.next();
//                    if (object instanceof EObject) {
//                        // Find the Pictogram Elements for the given domain
//                        // object via the standard link service
//                        List<PictogramElement> referencingPes = Graphiti.getLinkService().getPictogramElements(
//                                getDiagramTypeProvider().getDiagram(), (EObject) object);
//                        if (referencingPes.size() > 0) {
//                            peList.addAll(referencingPes);
//                        }
//                    } else {
//                        // For non-EMF domain objects use the registered
//                        // notification service for finding
//                        PictogramElement[] relatedPictogramElements = getDiagramTypeProvider().getNotificationService()
//                                .calculateRelatedPictogramElements(new Object[] { object });
//                        for (int i = 0; i < relatedPictogramElements.length; i++) {
//                            peList.add(relatedPictogramElements[i]);
//                        }
//                    }
//                }
//
//                // Do the selection in the diagram (in case there is something
//                // to select)
//                PictogramElement[] pes = null;
//                if (peList.size() > 0) {
//                    pes = peList.toArray(new PictogramElement[peList.size()]);
//                }
//                if (pes != null && pes.length > 0) {
//                    selectPictogramElements(pes);
//                }
//
//			}
			updateActions(getSelectionActions());
			
			// Bug 339360 - MultiPage Editor's selectionProvider does not notify PropertySheet (edit) 
			// bypass the selection provider
			if (selection instanceof StructuredSelection) 
			{
				StructuredSelection structuredSelection = (StructuredSelection) selection;
				List<SapphirePart> partList = new ArrayList<SapphirePart>();
				for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) 
				{
					Object object = iterator.next();
					EditPart editPart = null;
					if (object instanceof EditPart) 
					{
						editPart = (EditPart) object;
					}
					else if (object instanceof IAdaptable) 
					{
						editPart = (EditPart) ((IAdaptable) object).getAdapter(EditPart.class);
					}
					if (editPart != null && editPart.getModel() instanceof SapphirePart) {
						SapphirePart sp = (SapphirePart)editPart.getModel();
						partList.add(sp);
					}
//					if (editPart != null && editPart.getModel() instanceof PictogramElement) 
//					{					
//						PictogramElement pe = (PictogramElement) editPart.getModel();
//						if (pe instanceof Diagram)
//						{
//							partList.add(getPart());
//						}
//						else 
//						{
//							SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getDiagramTypeProvider().getFeatureProvider();
//							Object bo = sfp.getBusinessObjectForPictogramElement(pe);							
//							if (bo instanceof SapphirePart) 
//							{
//								partList.add((SapphirePart)bo);
//							}
//						}
//					}
					if (partList.size() == 1)
					{
						getPart().setSelection(partList.get(0));
					}
					else
					{
						getPart().setSelection(null);
					}
					this.selectedParts = partList;
				}
				
			}
			
		}
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
		ContextMenuProvider cmProvider = new SapphireDiagramEditorContextMenuProvider(this);
		viewer.setContextMenu(cmProvider);
	}

	public SapphireDiagramEditorPagePart getPart() {
		return this.diagramPart;
	}
	
	@Override
	public GraphicalViewer getGraphicalViewer() {
		return super.getGraphicalViewer();
	}

	public List<SapphirePart> getSelectedParts()
	{
		return this.selectedParts;
	}
	
	private EditPart getRootEditPart() {
		GraphicalViewer viewer = getGraphicalViewer();
		if (viewer != null) {
			EditPart viewerEditPart = viewer.getContents(); 
			if (viewerEditPart.getModel() == diagramPart) {
				return viewerEditPart;
			}
		}
		return null;
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
