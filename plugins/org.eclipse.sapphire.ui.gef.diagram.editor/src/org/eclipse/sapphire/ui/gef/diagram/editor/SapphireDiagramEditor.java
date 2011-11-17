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
import org.eclipse.draw2d.geometry.Point;
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
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramModelBase;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.SapphireDiagramEditorEditPartFactory;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditor extends GraphicalEditorWithFlyoutPalette {

    private DiagramGeometryWrapper diagramGeometry;
	private PaletteRoot root;
    private IDiagramEditorPageDef diagramPageDef;
    private SapphireDiagramEditorPagePart diagramPart;
    private DiagramModel diagramModel;
    private SapphireDiagramPartListener diagramPartListener;
    private List<SapphirePart> selectedParts = null;
    private boolean editorIsDirty = false;
    
	private Point mouseLocation;


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
		    	markEditorDirty();
		    	updateConnectionBendpoint((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionRemoveBendpointEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionRemoveBendpointEvent");
		    	markEditorDirty();
		    	updateConnectionBendpoint((DiagramConnectionPart)event.getPart());
		    }

		    public void handleConnectionMoveBendpointEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionMoveBendpointEvent");
		    	markEditorDirty();
		    	updateConnectionBendpoint((DiagramConnectionPart)event.getPart());
		    }
			
		    public void handleConnectionMoveLabelEvent(final DiagramConnectionEvent event)
		    {
            	System.out.println("handleConnectionMoveLabelEvent");
		    	markEditorDirty();
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
		return this.editorIsDirty;
	}
	
	protected void markEditorDirty() {
		this.editorIsDirty = true;
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}
	
	protected void removeConnection(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}

		diagramModel.removeConnection(connPart);
	}

	protected void addConnectionIfPossible(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}

		diagramModel.addConnection(connPart);
	}
	
	private void refreshConnectionNodes(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramConnectionModel connectionModel = diagramModel.getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			connectionModel.handleUpdateConnection();
		}
	}

	protected void updateConnectionEndpoint(DiagramConnectionPart connPart) {
		refreshConnectionNodes(connPart);
	}

	protected void updateConnection(DiagramConnectionPart connPart) {
		refreshConnectionNodes(connPart);
	}

	protected void updateConnectionBendpoint(DiagramConnectionPart connPart) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramConnectionModel connectionModel = diagramModel.getDiagramConnectionModel(connPart);
		if (connectionModel != null) {
			connectionModel.handleUpdateBendPoints();
		}
	}

	protected void moveNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(part);
		if (nodeModel != null) {
			nodeModel.handleMoveNode();
		}
	}

	protected void removeNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		diagramModel.handleRemoveNode(part);
	}

	protected void addNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		diagramModel.handleAddNode(part);
	}

	protected void updateNode(DiagramNodePart part) {
		if (diagramModel == null) {
			return;
		}
		
		DiagramNodeModel nodeModel = diagramModel.getDiagramNodeModel(part);
		if (nodeModel != null) {
			nodeModel.handleUpdateNode();
		}
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
					if (editPart != null && editPart.getModel() instanceof DiagramModelBase) {
						SapphirePart sp = ((DiagramModelBase)editPart.getModel()).getSapphirePart();
						partList.add(sp);
					}
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
		try {
			this.diagramGeometry.write();
			this.editorIsDirty = false;
			firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
		} catch (Exception e) {
			SapphireUiFrameworkPlugin.log(e);
		}
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);

		// Is this the right place?
		setEditDomain(new DefaultEditDomain(this));

		SapphireDiagramEditorInput diagramInput = (SapphireDiagramEditorInput)input;
		IFile diagramFile = diagramInput.getDiagramFile();
		IFile layoutFile = diagramInput.getLayoutFile();
		diagramGeometry = new DiagramGeometryWrapper(layoutFile, getPart());
		System.out.println("setInput diagramFile: " + diagramFile);
		System.out.println("setInput layoutFile: " + layoutFile);
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		
		GraphicalViewer viewer = getGraphicalViewer();
		
		viewer.getControl().addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				setMouseLocation(e.x, e.y);
			}
		});

		// set the contents of this editor
		diagramModel = new DiagramModel(diagramPart);
		viewer.setContents(diagramModel); 

		// If the layout file doesn't exist, apply auto layout
		// TODO the handler is DiagramGraphLayoutActionHandler - define action handler in sapphire-extension.xml
//		SapphireDiagramEditorInput diagramInput = (SapphireDiagramEditorInput) getEditorInput();
//		if (diagramInput.noExistingLayout()) {
//			SapphireAction layoutAction = this.diagramPart.getAction("Sapphire.Diagram.Layout");
//			if (layoutAction != null) {
//				SapphireActionHandler layoutHandler = layoutAction.getFirstActiveHandler();
//				if (layoutHandler != null) {
//					SapphireRenderingContext context = new SapphireRenderingContext(diagramPart, null);
//					layoutHandler.execute(context);
//				}
//			}
//		}

		// listen for dropped parts
		viewer.addDropTargetListener(new TemplateTransferDropTargetListener(getGraphicalViewer()));

		this.editorIsDirty = false;
		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);		
	}
	
	public DiagramModel getDiagramModel() {
		return this.diagramModel;
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
	
	public Point getMouseLocation() {
		if (mouseLocation == null) {
			mouseLocation = new Point();
		}
		return mouseLocation;
	}

	void setMouseLocation(int x, int y) {
		getMouseLocation().setLocation(x, y);
	}
}
