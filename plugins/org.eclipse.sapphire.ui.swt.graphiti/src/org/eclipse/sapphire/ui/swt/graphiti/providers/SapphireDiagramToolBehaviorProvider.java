/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342098] Separate dependency on org.eclipse.core.runtime (part 1)
 *    Konstantin Komissarchik - [348808] Create common Sapphire.ShowInSource action
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;
import org.eclipse.sapphire.ui.diagram.def.DecoratorPlacement;
import org.eclipse.sapphire.ui.diagram.def.IDiagramDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeProblemDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.PaletteLocation;
import org.eclipse.sapphire.ui.diagram.def.ProblemDecoratorSize;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireCreateConnectionFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireCreateFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireCreateNodeFeature;
import org.eclipse.sapphire.ui.swt.graphiti.features.SapphireDoubleClickNodeFeature;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramToolBehaviorProvider extends DefaultToolBehaviorProvider 
{
    private static final int SMALL_ERROR_DECORATOR_WIDTH = 7;
    private static final int SMALL_ERROR_DECORATOR_HEIGHT = 8;
    private static final int LARGE_ERROR_DECORATOR_WIDTH = 16;
    private static final int LARGE_ERROR_DECORATOR_HEIGHT = 16;
    
    public SapphireDiagramToolBehaviorProvider(IDiagramTypeProvider dtp) 
    {
        super(dtp);
    }

    /**
     * Returning null to not display a floating palette around node
     * The context pad api is not compatible with sapphire action api.
     * There is no way to reconcile how action image is specified between them.
     */
    @Override
    public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) 
    {
        return null;
//        IContextButtonPadData data = super.getContextButtonPad(context);
//        PictogramElement pe = context.getPictogramElement();
//        
//        IFeatureProvider featureProvider = getFeatureProvider();
//        Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
//        if (bo instanceof DiagramNodePart)
//        {
//            // 1. set the generic context buttons
//            // note, we only add "Delete" button
//            setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE);
//    
//            // 2. add "show in source" context button
//            DiagramNodePart nodePart = (DiagramNodePart)bo;
//            SapphireActionHandler showInSourceHandler = 
//                    nodePart.getAction(SHOW_IN_SOURCE_ACTION_ID).getFirstActiveHandler();
//            SapphireActionCustomFeature sapphireActionFeature = 
//                    new SapphireActionCustomFeature(this.getFeatureProvider(), showInSourceHandler);
//            CustomContext cc = new CustomContext(new PictogramElement[] { pe });
//            ContextButtonEntry showInSourceButton = new ContextButtonEntry(sapphireActionFeature, cc);
//            String ccText = LabelTransformer.transform(showInSourceHandler.getLabel(), CapitalizationType.TITLE_STYLE, false);
//            showInSourceButton.setText(ccText);
//            showInSourceButton.setIconId(SapphireDiagramCommonImageProvider.IMG_SHOW_IN_SOURCE);
//            data.getDomainSpecificContextButtons().add(showInSourceButton);
//            
//            // 3. add one domain specific context-button, which offers all
//            // available connection-features as drag&drop features...
//    
//            // 3.a. create new CreateConnectionContext
//            CreateConnectionContext ccc = new CreateConnectionContext();
//            ccc.setSourcePictogramElement(pe);
//            Anchor anchor = null;
//            if (pe instanceof Anchor) 
//            {
//                anchor = (Anchor) pe;
//            } 
//            else if (pe instanceof AnchorContainer) 
//            {
//                // assume, that our shapes always have chopbox anchors
//                anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
//            }
//            ccc.setSourceAnchor(anchor);
//    
//            // 3.b. create context button and add all applicable features
//            ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
//            for (ICreateConnectionFeature feature : features) 
//            {
//                ContextButtonEntry button = new ContextButtonEntry(null, context);
//                if (feature.isAvailable(ccc) && feature.canStartConnection(ccc))
//                {
//                    button.addDragAndDropFeature(feature);
//                    button.setText("Create " + feature.getCreateName()); //$NON-NLS-1$
//                    if (feature.getCreateImageId() != null)
//                    {
//                        button.setIconId(feature.getCreateImageId());
//                    }
//                    else
//                    {
//                        button.setIconId(SapphireDiagramCommonImageProvider.IMG_CONNECTION);
//                    }
//                    // 3.c. add context button, if it contains at least one feature
//                    if (button.getDragAndDropFeatures().size() > 0) 
//                    {
//                        data.getDomainSpecificContextButtons().add(button);
//                    }
//                }
//            }
//    
//        }
//        return data;
    }
        
	/**
	 * Creates a connection and an object compartment.
	 * Adds all connection creation features and creation features. Connection 
	 * creation features and node creation features are added to the compartments
	 * according to what's specified in the connection/node definition.
	 * The features are sorted using their creation names
	 * 
	 * @return the palette entries
	 */
	@Override
	public IPaletteCompartmentEntry[] getPalette() 
	{
		SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)this.getDiagramTypeProvider().getDiagramEditor();
		IDiagramEditorPageDef pageDef = (IDiagramEditorPageDef)diagramEditor.getPart().getDefinition();
		
		List<IPaletteCompartmentEntry> compartments = new ArrayList<IPaletteCompartmentEntry>();

		IFeatureProvider featureProvider = getFeatureProvider();
		ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		
		List<SapphireCreateFeature> connectionFeatures = new ArrayList<SapphireCreateFeature>();
		List<SapphireCreateFeature> nodeFeatures = new ArrayList<SapphireCreateFeature>();
		
		if (createConnectionFeatures.length > 0) 
		{
			for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) 
			{
				SapphireCreateConnectionFeature sapphireConnFeature = (SapphireCreateConnectionFeature)createConnectionFeature;
				PaletteLocation paletteLoc = sapphireConnFeature.getConnectionDef().getToolPaletteLocation().getContent();
				if (paletteLoc == PaletteLocation.CONNECTION)
				{
					connectionFeatures.add(sapphireConnFeature);
				}
				else 
				{
					nodeFeatures.add(sapphireConnFeature);
				}
			}
		}

		for (ICreateFeature createFeature : createFeatures) 
		{
			SapphireCreateNodeFeature sapphireNodeFeature = (SapphireCreateNodeFeature)createFeature;
			PaletteLocation paletteLoc = sapphireNodeFeature.getNodeDef().getToolPaletteLocation().getContent();
			if (paletteLoc == PaletteLocation.CONNECTION)
			{
				connectionFeatures.add(sapphireNodeFeature);
			}
			else 
			{
				nodeFeatures.add(sapphireNodeFeature);
			}
		}

		SapphireCreateFeature[] connArr = connectionFeatures.toArray(new SapphireCreateFeature[connectionFeatures.size()]);
		Arrays.sort(connArr);
		SapphireCreateFeature[] nodeArr = nodeFeatures.toArray(new SapphireCreateFeature[nodeFeatures.size()]);
		Arrays.sort(nodeArr);
		
		String text = pageDef.getPaletteDefinition().getConnectionsGroupLabel().getContent();
		String connGroupLabel = LabelTransformer.transform(text, CapitalizationType.TITLE_STYLE, false);
		PaletteCompartmentEntry connEntry = new PaletteCompartmentEntry(connGroupLabel, null);
		compartments.add(connEntry);
		
		text = pageDef.getPaletteDefinition().getNodesGroupLabel().getContent();
		String nodeGroupLabel = LabelTransformer.transform(text, CapitalizationType.TITLE_STYLE, false);
		PaletteCompartmentEntry nodeEntry = new PaletteCompartmentEntry(nodeGroupLabel, null);
		compartments.add(nodeEntry);
		
		addToolsToCompartmentEntry(connArr, connEntry);
		addToolsToCompartmentEntry(nodeArr, nodeEntry);
		IPaletteCompartmentEntry[] res = compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);
		return res;
	}
    
	private void addToolsToCompartmentEntry(SapphireCreateFeature[] createFeatures, PaletteCompartmentEntry entry)
	{
		for (SapphireCreateFeature createFeature : createFeatures)
		{
			if (createFeature instanceof SapphireCreateConnectionFeature)
			{
				SapphireCreateConnectionFeature feature = (SapphireCreateConnectionFeature)createFeature;
				ConnectionCreationToolEntry ccTool = new ConnectionCreationToolEntry(feature.getCreateName(),
									feature.getCreateDescription(), feature.getCreateImageId(),
									feature.getCreateLargeImageId());
				ccTool.addCreateConnectionFeature(feature);
				entry.addToolEntry(ccTool);				
			}
			else if (createFeature instanceof SapphireCreateNodeFeature)
			{
				SapphireCreateNodeFeature feature = (SapphireCreateNodeFeature)createFeature;
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(feature.getCreateName(),
									feature.getCreateDescription(), feature.getCreateImageId(), 
									feature.getCreateLargeImageId(), feature);

				entry.addToolEntry(objectCreationToolEntry);				
			}
		}		
	}
	
    @Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) 
    {
        PictogramElement[] pes = context.getPictogramElements();
        for (PictogramElement pe : pes)
        {
            if (pe instanceof ContainerShape)
            {
                Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
                if (bo instanceof DiagramNodePart)
                {
                    DiagramNodePart nodePart = (DiagramNodePart)bo;
                    if (nodePart.getDefaultActionHandler() != null || nodePart.canEditLabel())
                    {
                        SapphireDoubleClickNodeFeature dblClikFeature = 
                            new SapphireDoubleClickNodeFeature(getFeatureProvider(), nodePart);
                        return dblClikFeature;
                    }
                }
            }
            else if (pe instanceof ConnectionDecorator && pe.getGraphicsAlgorithm() instanceof Text)
            {
                Object bo = getFeatureProvider().getBusinessObjectForPictogramElement((PictogramElement)pe.eContainer());
                if (bo instanceof DiagramConnectionPart)
                {
                    SapphireDoubleClickNodeFeature dblClikFeature = 
                            new SapphireDoubleClickNodeFeature(getFeatureProvider(), (DiagramConnectionPart)bo);
                    return dblClikFeature;
                }
            }
        }
        return null;
    }
    
    @Override
    public IDecorator[] getDecorators(PictogramElement pe) 
    {
        IFeatureProvider featureProvider = getFeatureProvider();
        Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramNodePart)
        {
            List<IDecorator> decoratorList = new ArrayList<IDecorator>();            
            DiagramNodePart nodePart = (DiagramNodePart)bo;
            
            if (nodePart.getProblemIndicatorDef().isShowDecorator().getContent())
            {
                addNodeProblemDecorator(pe, nodePart, decoratorList);
            }
            
            List<IDiagramImageDecoratorDef> imageDecorators = nodePart.getImageDecorators();
            for (IDiagramImageDecoratorDef imageDecorator : imageDecorators)
            {
                addNodeImageDecorator(pe, imageDecorator, decoratorList);
            }
            return decoratorList.toArray(new IDecorator[0]);
        }
        return super.getDecorators(pe);
    }
    
    @Override
    public String getToolTip(GraphicsAlgorithm ga) 
    {
//        if (ga instanceof Text)
//        {
//            Text text = (Text)ga;
//            if (text.getValue() != null)
//            {
//                org.eclipse.swt.graphics.Font swtFont = DataTypeTransformation.toSwtFont(text.getFont());
//                Dimension d = TextUtilities.INSTANCE.getStringExtents(text.getValue(), swtFont);
//                if (d.width > ga.getWidth())
//                {
//                    return text.getValue();
//                }
//            }
//        }
        PictogramElement pe = ga.getPictogramElement();
        Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramNodePart) 
        {
            String name = ((DiagramNodePart) bo).getLabel();
            if (name != null && name.length() > 0) 
            {
                return name;
            }
        }
        return super.getToolTip(ga);        
    }
    
	@Override
	public boolean isShowGuides()
	{
		SapphireDiagramEditor diagramEditor = 
				(SapphireDiagramEditor)this.getDiagramTypeProvider().getDiagramEditor();
		return diagramEditor.getPart().isGridVisible();
	}
    
    private void addNodeProblemDecorator(PictogramElement pe, DiagramNodePart nodePart, List<IDecorator> decoratorList)
    {
        IModelElement model = nodePart.getModelElement();
        IDiagramNodeProblemDecoratorDef decoratorDef = nodePart.getProblemIndicatorDef();
        Status status = model.validate();
        ImageDecorator imageRenderingDecorator = null;
        if (status.severity() != Status.Severity.OK)
        {
            if (status.severity() == Status.Severity.WARNING)
            {
                if (decoratorDef.getSize().getContent() == ProblemDecoratorSize.SMALL)
                {
                    imageRenderingDecorator = new ImageDecorator(SapphireDiagramCommonImageProvider.IMG_WARNING_DECORATOR);
                }
                else
                {
                    imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING);
                }
            }
            else if (status.severity() == Status.Severity.ERROR)
            {
                if (decoratorDef.getSize().getContent() == ProblemDecoratorSize.SMALL)
                {
                    imageRenderingDecorator = new ImageDecorator(SapphireDiagramCommonImageProvider.IMG_ERROR_DECORATOR);
                }
                else
                {
                    imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR);
                }
            }
        }
        if (imageRenderingDecorator != null)
        {
            int indicatorWidth = decoratorDef.getSize().getContent() == ProblemDecoratorSize.LARGE ? LARGE_ERROR_DECORATOR_WIDTH : SMALL_ERROR_DECORATOR_WIDTH;
            int indicatorHeight = decoratorDef.getSize().getContent() == ProblemDecoratorSize.LARGE ? LARGE_ERROR_DECORATOR_HEIGHT : SMALL_ERROR_DECORATOR_HEIGHT;
            
            Point pt = getDecoratorPosition(pe, decoratorDef, indicatorWidth, indicatorHeight);
            imageRenderingDecorator.setX(pt.getX());
            imageRenderingDecorator.setY(pt.getY());
            imageRenderingDecorator.setMessage(status.message());
            decoratorList.add(imageRenderingDecorator);
        }        
    }
    
    private void addNodeImageDecorator(PictogramElement pe, IDiagramImageDecoratorDef imageDecoratorDef,
                                List<IDecorator> decoratorList)
    {
        String imageId = imageDecoratorDef.getImageId().getContent();
        if (imageId != null)
        {
            ImageDecorator imageRenderingDecorator = new ImageDecorator(imageId);
            org.eclipse.swt.graphics.Image image = GraphitiUi.getImageService().getImageForId(imageId);
            int imageWidth = image.getImageData().width;
            int imageHeight = image.getImageData().height;
            Point pt = getDecoratorPosition(pe, imageDecoratorDef, imageWidth, imageHeight);
            imageRenderingDecorator.setX(pt.getX());
            imageRenderingDecorator.setY(pt.getY());
            decoratorList.add(imageRenderingDecorator);
        }
    }
    
    private Point getDecoratorPosition(PictogramElement pe, IDiagramDecoratorDef decoratorDef, int decoratorWidth, int decoratorHeight)
    {
        GraphicsAlgorithm referencedGA = null;
        Text text = null;
        ContainerShape containerShape = (ContainerShape)pe;
        EList<Shape> children = containerShape.getChildren();
        for (Shape child : children)
        {
            GraphicsAlgorithm ga = child.getGraphicsAlgorithm();
            if (ga instanceof Image)
            {
                if (decoratorDef.getDecoratorPlacement().getContent() == DecoratorPlacement.IMAGE)
                {
                    referencedGA = ga;
                    break;
                }                
            }
            else if (ga instanceof Text)
            {
                if (decoratorDef.getDecoratorPlacement().getContent() == DecoratorPlacement.LABEL)
                {
                    referencedGA = ga;
                    break;
                }
                if (text == null)
                {
                    text = (Text)ga;
                }
            }
        }        
        if (referencedGA == null)
        {
            referencedGA = text;
        }
        
        if (referencedGA != null)
        {
            HorizontalAlignment horizontalAlign = decoratorDef.getHorizontalAlignment().getContent();                        
            int offsetX = 0;
            int offsetY = 0;
            if (horizontalAlign == HorizontalAlignment.RIGHT)
            {
                offsetX = referencedGA.getWidth() - decoratorWidth;
                offsetX -= decoratorDef.getHorizontalMargin().getContent();
            }
            else if (horizontalAlign == HorizontalAlignment.LEFT)
            {
                offsetX += decoratorDef.getHorizontalMargin().getContent();
            }
            else if (horizontalAlign == HorizontalAlignment.CENTER)
            {
                offsetX = (referencedGA.getWidth() - decoratorWidth) >> 1;
            }
            
            VerticalAlignment verticalAlign = decoratorDef.getVerticalAlignment().getContent();
            
            if (verticalAlign == VerticalAlignment.BOTTOM)
            {
                offsetY = referencedGA.getHeight() - decoratorHeight;
                offsetY -= decoratorDef.getVerticalMargin().getContent();
            }
            else if (verticalAlign == VerticalAlignment.TOP)
            {
                offsetY += decoratorDef.getVerticalMargin().getContent();
            }
            else if (verticalAlign == VerticalAlignment.CENTER)
            {
                offsetY = (referencedGA.getHeight() - decoratorHeight) / 2;
            }
            
            return new Point(offsetX + referencedGA.getX(), offsetY + referencedGA.getY());
        }
        return new Point(0, 0);
    }
    
}
