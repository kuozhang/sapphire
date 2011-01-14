/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.graphiti.editor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
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
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper.Bounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPart;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
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
	
	public SapphireDiagramEditor(SapphireDiagramEditorPart diagramPart)
	{
		this.diagramPart = diagramPart;
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
	public void doSave(final IProgressMonitor monitor )
	{
		super.doSave(monitor);
		try
		{
			this.diagramPart.getDiagramGeometry().write();
		}
        catch( Exception e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }		
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
		DiagramGeometryWrapper diagramGeometry = this.diagramPart.getDiagramGeometry();
		
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
			
			// add embedded connections
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
	
	private void reconstructConnections(final Diagram diagram)
	{
		DiagramGeometryWrapper diagramGeometry = this.diagramPart.getDiagramGeometry();
		
		// add the top level connections back to the diagram
		for (DiagramConnectionTemplate connTemplate : this.diagramPart.getConnectionTemplates())
		{
			for (DiagramConnectionPart connPart : connTemplate.getDiagramConnections(null))
			{
				addConnection(connPart, diagramGeometry);
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
					List<org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper.Point> bps = 
						diagramGeometry.getConnectionBendpoints(connPart);
					List<Point> bendpoints = freeConn.getBendpoints();
					int index = 0;
					for (org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper.Point pt : bps)
					{
						Point newPoint = Graphiti.getGaService().createPoint(pt.getX(), pt.getY());										
						bendpoints.add(index++, newPoint);										
					}
				}
			}							
		}
		
		return conn;
	}
	
}
