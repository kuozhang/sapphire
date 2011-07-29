/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddBendpointFeature;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IMoveBendpointFeature;
import org.eclipse.graphiti.features.IMoveConnectionDecoratorFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveBendpointFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddBendpointContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveBendpointContext;
import org.eclipse.graphiti.features.context.IMoveConnectionDecoratorContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IRemoveBendpointContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.IIndependenceSolver;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireAddBendpointFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireAddConnectionFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireAddNodeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireCreateConnectionFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireCreateNodeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDeleteFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDirectEditConnectionFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDirectEditNodeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireMoveBendpointFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireMoveConnectionDecoratorFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireMoveNodeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireRemoveBendpointFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireRemoveFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireResizeShapeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireUpdateConnectionFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireUpdateNodeFeature;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramFeatureProvider extends DefaultFeatureProvider 
{
	
	public SapphireDiagramFeatureProvider(IDiagramTypeProvider dtp, IIndependenceSolver solver)
	{
		super(dtp);
		this.setIndependenceSolver(solver);
	}
		
	public DiagramGeometryWrapper getDiagramGeometry()
	{
		SapphireDiagramEditor diagramEditor = getDiagramEditor();
		return diagramEditor.getDiagramGeometry();
	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context) 
	{
		Object obj = context.getNewObject();
		if (obj instanceof DiagramNodePart)
		{
			return new SapphireAddNodeFeature(this);
		}
		else if (obj instanceof DiagramConnectionPart)
		{
			return new SapphireAddConnectionFeature(this);
		}
		else if (context.getTargetContainer() instanceof Diagram)
		{
			// There should be only one drop action handler per diagram page and it should handle
			// different drop objects
			SapphireAction dropAction = getDiagramPart().getAction("Sapphire.Drop");
			SapphireDiagramActionHandler dropHandler = (SapphireDiagramActionHandler)dropAction.getFirstActiveHandler();
			if (dropHandler != null && dropHandler.canExecute(obj))
			{
				return new SapphireAddNodeFeature(this);
			}
		}
		return super.getAddFeature(context);
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures() 
	{
		List<DiagramNodeTemplate> nodeTemplates = getDiagramPart().getNodeTemplates();
		ICreateFeature[] features = new ICreateFeature[nodeTemplates.size()];
		int i = 0;
		for (DiagramNodeTemplate nodeTemplate : nodeTemplates)
		{
			SapphireCreateNodeFeature createNodeFeature = 
				new SapphireCreateNodeFeature(this, nodeTemplate);
			features[i++] = createNodeFeature;
		}
		return features;
	}	
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) 
	{
		return new SapphireDeleteFeature(this);
	}
	
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) 
	{
		return new SapphireRemoveFeature(this);
	}
	
	@Override
    public ICreateConnectionFeature[] getCreateConnectionFeatures() 
	{
		SapphireDiagramEditorPagePart diagramPart = getDiagramPart();
		List<IDiagramConnectionDef> connectionDefs = diagramPart.getDiagramConnectionDefs();
		List<ICreateConnectionFeature> features = 
			new ArrayList<ICreateConnectionFeature>(connectionDefs.size());
		for (IDiagramConnectionDef connectionDef : connectionDefs)
		{	
			if (connectionDef.isImplicitConnection().getContent())
			{
				break;
			}
			String tpLabel = connectionDef.getToolPaletteLabel().getContent();
			if (tpLabel != null)
			{
				tpLabel = IDiagramConnectionDef.PROP_TOOL_PALETTE_LABEL.getLocalizationService().text(
								tpLabel, CapitalizationType.TITLE_STYLE, false);
			}
			String tpDesc = connectionDef.getToolPaletteDesc().getContent();
			if (tpDesc != null)
			{
				tpDesc = IDiagramConnectionDef.PROP_TOOL_PALETTE_DESC.getLocalizationService().text(
								tpDesc, CapitalizationType.TITLE_STYLE, false);
			}
			
			SapphireCreateConnectionFeature createConnectionFeature = 
				new SapphireCreateConnectionFeature(this, diagramPart, connectionDef, tpLabel, tpDesc);
			features.add(createConnectionFeature);
		}
		
		return features.toArray(new ICreateConnectionFeature[0]);
	}
	
	@Override
	public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement((PictogramElement) pe.eContainer());
		if (bo instanceof DiagramNodePart)
		{
			return new SapphireDirectEditNodeFeature(this);
		}
		else if (bo instanceof DiagramConnectionPart)
		{
			return new SapphireDirectEditConnectionFeature(this);
		}
		return super.getDirectEditingFeature(context);		
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) 
		{
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof DiagramNodePart) 
			{
				return new SapphireUpdateNodeFeature(this);
			}
		}
		else if (pe instanceof Connection)
		{
			Object bo = getBusinessObjectForPictogramElement(pe);
			if (bo instanceof DiagramConnectionPart)
			{
				return new SapphireUpdateConnectionFeature(this);
			}
		}
		return super.getUpdateFeature(context);
	}
	
	@Override
	public IAddBendpointFeature getAddBendpointFeature(IAddBendpointContext context) 
	{
		IAddBendpointFeature ret = new SapphireAddBendpointFeature(this);
		return ret;
	}
	
	@Override
	public IRemoveBendpointFeature getRemoveBendpointFeature(IRemoveBendpointContext context) 
	{
		IRemoveBendpointFeature ret = new SapphireRemoveBendpointFeature(this);
		return ret;
	}

    @Override
    public IMoveBendpointFeature getMoveBendpointFeature(IMoveBendpointContext context) 
    {
        IMoveBendpointFeature ret = new SapphireMoveBendpointFeature(this);
        return ret;
    }
    
    @Override
    public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) 
    {
        return new SapphireMoveNodeFeature(this);
    }
        	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) 
	{
		IResizeShapeFeature ret = new SapphireResizeShapeFeature(this);
		return ret;
	}
	
	@Override
	public IMoveConnectionDecoratorFeature getMoveConnectionDecoratorFeature(IMoveConnectionDecoratorContext context) 
	{
		IMoveConnectionDecoratorFeature ret = new SapphireMoveConnectionDecoratorFeature(this);
		return ret;
	}
	
	/**
	 * When a PE is removed from the diagram, we need to remove the corresponding
	 * key from the solver
	 * @param bo
	 */
	public void remove(Object bo)
	{
		((SapphireDiagramSolver)this.getIndependenceSolver()).removeBO(bo);
	}
	
	public void addRenderingContext(SapphirePart part, DiagramRenderingContext ctx)
	{
		((SapphireDiagramSolver)this.getIndependenceSolver()).addRendingContext(part, ctx);
	}
	
	public DiagramRenderingContext getRenderingContext(ISapphirePart part)
	{
		return ((SapphireDiagramSolver)this.getIndependenceSolver()).getRenderingContext(part);
	}
	
	private SapphireDiagramEditor getDiagramEditor()
	{
		SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)getDiagramTypeProvider().getDiagramEditor();
		return diagramEditor;
	}
	
	private SapphireDiagramEditorPagePart getDiagramPart()
	{
		SapphireDiagramEditor diagramEditor = getDiagramEditor();
		return diagramEditor.getPart();
	}
	    
}
