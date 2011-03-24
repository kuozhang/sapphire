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
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
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
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramPartListener;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@SuppressWarnings("restriction")
public class SapphireDiagramEditor extends DiagramEditor 
{
	private SapphireDiagramEditorPart diagramPart;
	private DiagramGeometryWrapper diagramGeometry;
	private IDiagramPageDef diagramPageDef;
	private SapphireDiagramPartListener diagramPartListener;
	
	public SapphireDiagramEditor(final IModelElement rootModelElement, final IPath pageDefinitionLocation)
	{
        final String bundleId = pageDefinitionLocation.segment( 0 );
        final String pageId = pageDefinitionLocation.lastSegment();
        final String relPath = pageDefinitionLocation.removeFirstSegments( 1 ).removeLastSegments( 1 ).toPortableString();
        
        final ISapphireUiDef def = SapphireUiDefFactory.load( bundleId, relPath );
        
        this.diagramPageDef = (IDiagramPageDef) def.getPartDef( pageId, true, IDiagramPageDef.class );
		
		this.diagramPart = new SapphireDiagramEditorPart();
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
		};
		
		this.diagramPart.addListener(this.diagramPartListener);
	}
	
	public SapphireDiagramEditorPart getDiagramEditorPart()
	{
		return this.diagramPart;
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		boolean editorIsActive = getSite().getPage().isPartVisible(this);
		if (editorIsActive)
		{
			super.selectionChanged(part, selection);
		}
		else 
		{
			// Check if we are a page of the active multipage editor
			IEditorPart activeEditor = getSite().getPage().getActiveEditor();
			if (getSite().getPage().isPartVisible(activeEditor) && 
					activeEditor instanceof MultiPageEditorPart)
			{
				MultiPageEditorPart me = (MultiPageEditorPart)activeEditor;
				if (me.getActivePage() == 0)
				{
					updateActions(getSelectionActions());
				}
			}
		}
	}
	
	@Override
	protected void setInput(IEditorInput input) 
	{
		super.setInput(input);
		SapphireDiagramEditorInput diagramInput = (SapphireDiagramEditorInput)input;
		IFile npFile = diagramInput.getNodePositionFile();
		this.diagramGeometry = new DiagramGeometryWrapper(npFile, getDiagramEditorPart());
	}
	
	@Override
	protected void initializeGraphicalViewer() 
	{
		super.initializeGraphicalViewer();
		syncDiagramWithModel();
		doSave(null);
	}
		
	@Override
	public void doSave(final IProgressMonitor monitor )
	{
		super.doSave(monitor);
		try
		{
			getDiagramGeometry().write();
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
		
		int nodeX = 50;
		int nodeY = 50;
		int xInc = 100;
		DiagramGeometryWrapper diagramGeometry = getDiagramGeometry();
		
		for (DiagramNodeTemplate nodeTemplate : this.diagramPart.getNodeTemplates())
		{
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes())
			{
				Bounds bounds = diagramGeometry.getNode(nodePart);
				if (bounds == null)
				{
					bounds = new Bounds(nodeX, nodeY, -1, -1);
					nodeX += xInc;
				}
				AddContext ctx = new AddContext();
				ctx.setNewObject(nodePart);
				ctx.setTargetContainer(diagram);
				ctx.setWidth(bounds.getWidth());
				ctx.setHeight(bounds.getHeight());
				ctx.setX(bounds.getX());
				ctx.setY(bounds.getY());
				IAddFeature ft = getDiagramTypeProvider().getFeatureProvider().getAddFeature(ctx);						
				ft.add(ctx);						
			}
			
		}		
	}
	
	private void reconstructConnections(final Diagram diagram)
	{
		DiagramGeometryWrapper diagramGeometry = getDiagramGeometry();
		
		// add the top level connections back to the diagram
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				addConnection(connPart, diagramGeometry);
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
					addConnection(connPart, diagramGeometry);
				}
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
	
	private Connection addConnection(DiagramConnectionPart connPart, DiagramGeometryWrapper diagramGeometry)
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
						diagramGeometry.getConnectionBendpoints(connPart) != null)
				{
					FreeFormConnection freeConn = (FreeFormConnection)conn;
					List<org.eclipse.sapphire.ui.Point> bps = 
						diagramGeometry.getConnectionBendpoints(connPart);
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

			DiagramNodePart srcNodePart = getDiagramEditorPart().getDiagramNodePart(connPart.getEndpoint1());
			DiagramNodePart targetNodePart = getDiagramEditorPart().getDiagramNodePart(connPart.getEndpoint2());
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
