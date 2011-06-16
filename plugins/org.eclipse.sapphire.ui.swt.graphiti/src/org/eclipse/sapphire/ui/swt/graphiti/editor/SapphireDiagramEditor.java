/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [335539] Create editor for sdef files
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@SuppressWarnings("restriction")
public class SapphireDiagramEditor extends DiagramEditor 
{
    private SapphireDiagramEditorPagePart diagramPart;
    private DiagramGeometryWrapper diagramGeometry;
    private IDiagramEditorPageDef diagramPageDef;
    private SapphireDiagramPartListener diagramPartListener;
    private int defaultX = 50;
    private int defaultY = 50;
    private static int xInc = 100;
    private static int yInc = 0;
    private List<SapphirePart> selectedParts = null;
    private boolean gridVisibilityChanged = false;
    
    public SapphireDiagramEditor(final IModelElement rootModelElement, final IPath pageDefinitionLocation)
    {
        final String bundleId = pageDefinitionLocation.segment( 0 );
        final String pageId = pageDefinitionLocation.lastSegment();
        final String relPath = pageDefinitionLocation.removeFirstSegments( 1 ).removeLastSegments( 1 ).toPortableString();
        
        final ISapphireUiDef def = SapphireUiDefFactory.load( bundleId, relPath );
        
        this.diagramPageDef = (IDiagramEditorPageDef) def.getPartDef( pageId, true, IDiagramEditorPageDef.class );
        
        this.diagramPart = new SapphireDiagramEditorPagePart();
        this.diagramPart.init(null, rootModelElement, this.diagramPageDef, Collections.<String,String>emptyMap());
        
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
			
			@Override
			public void handleGridStateChangeEvent(final DiagramPageEvent event)
			{
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, 
						new Boolean(diagramPart.isGridVisible()));
				getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, 
						new Boolean(diagramPart.isGridVisible()));
				gridVisibilityChanged = true;
				firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY); 
			}
			
		};
		
		this.diagramPart.addListener(this.diagramPartListener);
	}
	
	public SapphireDiagramEditorPagePart getPart()
	{
		return this.diagramPart;
	}
	
	@Override
	protected ContextMenuProvider createContextMenuProvider() 
	{
		return new SapphireDiagramEditorContextMenuProvider(this);
	}	
	
	protected boolean shouldRegisterContextMenu() 
	{
		return false;
	}
	
	@Override
	public boolean isDirty()
	{
		boolean dirty = super.isDirty();
		dirty |= this.gridVisibilityChanged;
		return dirty;
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
//                if (activeEditor instanceof MultiPageEditorPart) {
//                    int activePage = ((MultiPageEditorPart) activeEditor).getActivePage();
//                    if (activePage != 0) {
//                        // Editor is active but the diagram sub editor is not
//                        // its active page
//                        editorIsActive = false;
//                    }
//                }
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
            if (selection instanceof IStructuredSelection) {
                IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                List<PictogramElement> peList = new ArrayList<PictogramElement>();
                // Collect all Pictogram Elements for all selected domain
                // objects into one list
                for (Iterator<?> iterator = structuredSelection.iterator(); iterator.hasNext();) {
                    Object object = iterator.next();
                    if (object instanceof EObject) {
                        // Find the Pictogram Elements for the given domain
                        // object via the standard link service
                        List<PictogramElement> referencingPes = Graphiti.getLinkService().getPictogramElements(
                                getDiagramTypeProvider().getDiagram(), (EObject) object);
                        if (referencingPes.size() > 0) {
                            peList.addAll(referencingPes);
                        }
                    } else {
                        // For non-EMF domain objects use the registered
                        // notification service for finding
                        PictogramElement[] relatedPictogramElements = getDiagramTypeProvider().getNotificationService()
                                .calculateRelatedPictogramElements(new Object[] { object });
                        for (int i = 0; i < relatedPictogramElements.length; i++) {
                            peList.add(relatedPictogramElements[i]);
                        }
                    }
                }

                // Do the selection in the diagram (in case there is something
                // to select)
                PictogramElement[] pes = null;
                if (peList.size() > 0) {
                    pes = peList.toArray(new PictogramElement[peList.size()]);
                }
                if (pes != null && pes.length > 0) {
                    selectPictogramElements(pes);
                }

			}
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
					if (editPart != null && editPart.getModel() instanceof PictogramElement) 
					{					
						PictogramElement pe = (PictogramElement) editPart.getModel();
						if (pe instanceof Diagram)
						{
							partList.add(getPart());
						}
						else 
						{
							SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getDiagramTypeProvider().getFeatureProvider();
							Object bo = sfp.getBusinessObjectForPictogramElement(pe);							
							if (bo instanceof SapphirePart) 
							{
								partList.add((SapphirePart)bo);
							}
						}
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
		
	@Override
	protected void setInput(IEditorInput input) 
	{
		super.setInput(input);
		SapphireDiagramEditorInput diagramInput = (SapphireDiagramEditorInput)input;
		IFile npFile = diagramInput.getLayoutFile();
		this.diagramGeometry = new DiagramGeometryWrapper(npFile, getPart());
		if (this.diagramGeometry.isGridPropertySet())
		{
			this.diagramPart.syncGridStateWithDiagramLayout(this.diagramGeometry.isGridVisible());
		}
	}
	
	@Override
	protected void initializeGraphicalViewer() 
	{
		super.initializeGraphicalViewer();
		syncDiagramWithModel();
		boolean isGridVisibleInViewer = (Boolean) getGraphicalViewer()
				.getProperty(SnapToGrid.PROPERTY_GRID_VISIBLE);
		if (this.diagramPart.isGridVisible() != isGridVisibleInViewer)
		{
			getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, this.diagramPart.isGridVisible());
			getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, this.diagramPart.isGridVisible());			
		}
		doSave(null);
	}
		
	@Override
	public void doSave(final IProgressMonitor monitor )
	{
		super.doSave(monitor);
		try
		{
			getDiagramGeometry().write();
			this.gridVisibilityChanged = false;
			firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
		}
        catch( Exception e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }		
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		this.diagramPart.dispose();
	}
	
	public List<SapphirePart> getSelectedParts()
	{
		return this.selectedParts;
	}
	
	public org.eclipse.sapphire.ui.Point getDefaultNodePosition()
	{
		int x = defaultX;
		int y = defaultY;
		defaultX += xInc;
		defaultY += yInc;
		return new org.eclipse.sapphire.ui.Point(x, y);
	}
	
	public DiagramGeometryWrapper getDiagramGeometry()
	{
		return this.diagramGeometry;
	}
	
	public void syncDiagramWithModel()
	{
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		
		TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
		ted.getCommandStack().execute(new RecordingCommand(ted) 
		{
			protected void doExecute() 
			{		
				removeConnections(diagram);
				removeNodes(diagram);
				
				reconstructNodes(diagram);
				reconstructConnections(diagram);
			}
		});
	}
	
	private void removeConnections(final Diagram diagram) 
	{
		List<Connection> cons = diagram.getConnections();
		Iterator<Connection> consIt = cons.iterator();
		// don't cause ConcurrentModificationException
		Collection<Connection> allCons = new HashSet<Connection>();
		while (consIt.hasNext()) 
		{
			Connection con = consIt.next();
			allCons.add(con);
		}
		consIt = allCons.iterator();
		while (consIt.hasNext()) 
		{
			Connection con = consIt.next();			
			Graphiti.getPeService().deletePictogramElement(con);
		}		
	}
	
	private void removeNodes(final Diagram diagram)
	{
		List<Shape> containerShapes = diagram.getChildren();
		Iterator<Shape> shapeIt = containerShapes.iterator();
		// don't cause ConcurrentModificationException
		Collection<Shape> allShapes = new HashSet<Shape>();
		while (shapeIt.hasNext())
		{
			Shape shape = shapeIt.next();
			allShapes.add(shape);
		}
		shapeIt = allShapes.iterator();
		while (shapeIt.hasNext())
		{
			Shape containerShape = shapeIt.next();
			Graphiti.getPeService().deletePictogramElement(containerShape);
		}
	}
	
	private void reconstructNodes(final Diagram diagram)
	{
		// Add the nodes back to the diagram thus re-establishing mapping between
		// pictograms and business objects
		
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				Bounds bounds = nodePart.getNodeBounds();
				if (bounds.getX() == -1 || bounds.getY() == -1)
				{
					org.eclipse.sapphire.ui.Point pt = getDefaultNodePosition();
					bounds.setX(pt.getX());
					bounds.setY(pt.getY());
				}
				AddContext ctx = new AddContext();
				ctx.setNewObject(nodePart);
				ctx.setTargetContainer(diagram);
				
				// Revert the change for bug 344175. Dropping a node using center position
				// causes difficulties in merging code paths for adding node
				// Need to convert the node left top coordinate into node middle point
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=344175
//				int width = SapphireAddNodeFeature.getNodeWidth(nodePart, bounds.getWidth());
//				int height = SapphireAddNodeFeature.getNodeHeight(nodePart, bounds.getHeight());
//				int middleX = bounds.getX() + (width >> 1);
//				int middleY = bounds.getY() + (height >> 1);
//				ctx.setX(middleX);
//				ctx.setY(middleY);
				ctx.setX(bounds.getX());
				ctx.setY(bounds.getY());
				ctx.setWidth(bounds.getWidth());
				ctx.setHeight(bounds.getHeight());
				IAddFeature ft = getDiagramTypeProvider().getFeatureProvider().getAddFeature(ctx);						
				ft.add(ctx);						
			}
			
		}		
	}
	
	private void reconstructConnections(final Diagram diagram)
	{
		
		// add the top level connections back to the diagram
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				addConnection(connPart);
			}
		}
		
		// Add embedded connections. This needs to be done after all the nodes have been added.
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			DiagramEmbeddedConnectionTemplate embeddedConnTemplate = 
				nodeTemplate.getEmbeddedConnectionTemplate();
			if (embeddedConnTemplate != null)
			{
				for (DiagramConnectionPart connPart : embeddedConnTemplate.getDiagramConnections(null))
				{
					addConnection(connPart);
				}
			}
		}
		
		// Add Implicit connections
		for (DiagramImplicitConnectionTemplate implicitConnTemplate : this.diagramPart.getImplicitConnectionTemplates())
		{
			for (DiagramImplicitConnectionPart implicitConn : implicitConnTemplate.getImplicitConnections())
			{
				addConnection(implicitConn);
			}
		}
	}
	
	private ContainerShape getContainerShape(Object bo)
	{
		ContainerShape containerShape = null;
		PictogramElement [] pictograms = 
			getDiagramTypeProvider().getFeatureProvider().getAllPictogramElementsForBusinessObject(bo);
		for (PictogramElement pictogram : pictograms)
		{
			if (pictogram instanceof ContainerShape)
			{
				containerShape = (ContainerShape)pictogram;
				break;
			}
		}
		return containerShape;
	}
	
	private Connection getConnection(Object bo)
	{
		PictogramElement [] pictograms = 
			getDiagramTypeProvider().getFeatureProvider().getAllPictogramElementsForBusinessObject(bo);
		for (PictogramElement pictogram : pictograms)
		{
			if (pictogram instanceof Connection)
			{
				return (Connection)pictogram;
			}
		}
		return null;
	}
	
	private Connection addConnection(DiagramConnectionPart connPart)
	{
		Connection conn = null;
		
		IModelElement endpoint1 = connPart.getEndpoint1();
		IModelElement endpoint2 = connPart.getEndpoint2();
		DiagramNodePart nodePart1 = this.diagramPart.getDiagramNodePart(endpoint1);
		DiagramNodePart nodePart2 = this.diagramPart.getDiagramNodePart(endpoint2);
		if (nodePart1 != null && nodePart2 != null)
		{
			ContainerShape sourceShape = getContainerShape(nodePart1);
			ContainerShape targetShape = getContainerShape(nodePart2);
			if (sourceShape != null && targetShape != null)
			{
				Anchor sourceAnchor = sourceShape.getAnchors().get(0);
				Anchor targetAnchor = targetShape.getAnchors().get(0);
				AddConnectionContext connCtx = new AddConnectionContext(sourceAnchor, targetAnchor);
				connCtx.setNewObject(connPart);
				IAddFeature fcfp = getDiagramTypeProvider().getFeatureProvider().getAddFeature(connCtx);
				
				conn = (Connection)fcfp.add(connCtx);
				if (conn instanceof FreeFormConnection && 
						connPart.getConnectionBendpoints().size() > 0)
				{
					FreeFormConnection freeConn = (FreeFormConnection)conn;
					List<org.eclipse.sapphire.ui.Point> bps = connPart.getConnectionBendpoints();
					List<Point> bendpoints = freeConn.getBendpoints();
					int index = 0;
					for (org.eclipse.sapphire.ui.Point pt : bps)
					{
						Point newPoint = Graphiti.getGaService().createPoint(pt.getX(), pt.getY());										
						bendpoints.add(index++, newPoint);										
					}
				}
			}							
		}
		
		return conn;
	}
	
	private void updateNode(final DiagramNodePart nodePart)
	{
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final PictogramElement pe = getContainerShape(nodePart);
		if (pe != null)
		{
			UpdateContext context = new UpdateContext(pe);
			fp.updateIfPossible(context);
			
			// When the validation status changes for a node, we'd need to refresh the
			// diagram to update the problem indicator for the node.
			Display.getCurrent().asyncExec(new Runnable()
			{
				public void run() 
				{
					refresh();
				}				
			});
		}		
	}
	
	private void addNode(final DiagramNodePart nodePart)
	{
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		AddContext ctx = new AddContext();
		ctx.setNewObject(nodePart);
		org.eclipse.sapphire.ui.Point pt = nodePart.getNodePosition();
		ctx.setX(pt.getX());
		ctx.setY(pt.getY());
		ctx.setTargetContainer(diagram);
		fp.addIfPossible(ctx);				
	}
	
	private void removeNode(final DiagramNodePart nodePart)
	{
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
		
		PictogramElement pe = getContainerShape(nodePart);
		final IRemoveContext rc = new RemoveContext(pe);
		final IRemoveFeature removeFeature = fp.getRemoveFeature(rc);
		if (removeFeature != null) 
		{
			ted.getCommandStack().execute(new RecordingCommand(ted) 
			{
				protected void doExecute() 
				{			    					
					removeFeature.remove(rc);
				}
			});
		}		
	}
	
	private void moveNode(final DiagramNodePart nodePart)
	{
		ContainerShape nodeShape = getContainerShape(nodePart);	
		if (nodeShape == null)
			return;
		
		int oldX = nodeShape.getGraphicsAlgorithm().getX();
		int oldY = nodeShape.getGraphicsAlgorithm().getY();
		
		Bounds newBounds = nodePart.getNodeBounds();
		int newX = newBounds.getX();
		int newY = newBounds.getY();
		
		if (newX != oldX || newY != oldY)
		{
			final Diagram diagram = getDiagramTypeProvider().getDiagram();
			final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);			
			final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
			
			final MoveShapeContext moveContext = new MoveShapeContext(nodeShape);
			moveContext.setX(newX);
			moveContext.setY(newY);
			final IMoveShapeFeature moveFeature = fp.getMoveShapeFeature(moveContext);
			if (moveFeature != null)
			{
				ted.getCommandStack().execute(new RecordingCommand(ted) 
				{
					protected void doExecute() 
					{			    					
						moveFeature.moveShape(moveContext);
					}
				});
			}
		}
	}
	
	private void updateConnection(final DiagramConnectionPart connPart)
	{
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final PictogramElement pe = getConnection(connPart);
		if (pe != null)
		{
			UpdateContext context = new UpdateContext(pe);
			fp.updateIfPossible(context);
		}		
	}
	
	private void updateConnectionEndpoint(final DiagramConnectionPart connPart)
	{
		removeConnection(connPart);
		addConnectionIfPossible(connPart);
	}
	
	private void removeConnection(final DiagramConnectionPart connPart)
	{
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
		
		// remove the existing connection pe from the diagram
		PictogramElement pe = getConnection(connPart);
		if (pe != null)
		{
			final IRemoveContext rc = new RemoveContext(pe);
			final IRemoveFeature removeFeature = fp.getRemoveFeature(rc);
			if (removeFeature != null) 
			{
				ted.getCommandStack().execute(new RecordingCommand(ted) 
				{
					protected void doExecute() 
					{			    					
						removeFeature.remove(rc);
					}
				});
			}
		}
	}
	
	private void addConnectionIfPossible(final DiagramConnectionPart connPart)
	{
		if (connPart.getEndpoint1() != null && connPart.getEndpoint2() != null)
		{
			final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
			final Diagram diagram = getDiagramTypeProvider().getDiagram();
			final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);

            DiagramNodePart srcNodePart = getPart().getDiagramNodePart(connPart.getEndpoint1());
            DiagramNodePart targetNodePart = getPart().getDiagramNodePart(connPart.getEndpoint2());
            ContainerShape srcNode = getContainerShape(srcNodePart);
            ContainerShape targetNode = getContainerShape(targetNodePart);
            final AddConnectionContext addContext = 
                    new AddConnectionContext(srcNode.getAnchors().get(0), targetNode.getAnchors().get(0));
            addContext.setNewObject(connPart);
            final IAddFeature addFeature = fp.getAddFeature(addContext);
            if (addFeature != null) 
            {
                ted.getCommandStack().execute(new RecordingCommand(ted) 
                {
                    protected void doExecute() 
                    {                                    
                        addFeature.add(addContext);
                    }
                });
            }            
        }                         
    }
}
