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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramDropActionHandler;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.ImagePlacement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramPropertyKeys;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireAddNodeFeature extends AbstractAddShapeFeature 
{
    private static final IColorConstant DEFAULT_TEXT_FOREGROUND = new ColorConstant(51, 51, 153);
    private static final IColorConstant DEFAULT_NODE_FOREGROUND = new ColorConstant(51, 51, 153);
    private static final int DEFAULT_NODE_WIDTH = 100;
    private static final int DEFAULT_NODE_HEIGHT = 30;
    private static final int DEFAULT_TEXT_HEIGHT = 20;
    private DiagramNodeTemplate nodeTemplate;
    
	public SapphireAddNodeFeature(IFeatureProvider fp, DiagramNodeTemplate nodeTemplate)
	{
		super(fp);
		this.nodeTemplate = nodeTemplate;
	}
	
	public boolean canAdd(IAddContext context) 
	{
		Object newObj = context.getNewObject();
		if (newObj instanceof DiagramNodePart)
		{
			return true;
		}
		else if (context.getTargetContainer() instanceof Diagram)
		{
			SapphireDiagramDropActionHandler dropHandler = this.nodeTemplate.getDropActionHandler();
			if (dropHandler != null && dropHandler.canExecute(newObj))
			{
				return true;
			}
		}
		return false;
	}

	public PictogramElement add(IAddContext context)
	{
		Object newObj = context.getNewObject();
		DiagramNodePart nodePart = null;
		if (newObj instanceof DiagramNodePart)
		{
			nodePart = (DiagramNodePart)context.getNewObject();
		}
		else 
		{
			SapphireDiagramDropActionHandler dropHandler = this.nodeTemplate.getDropActionHandler();
			this.nodeTemplate.removeModelLister();
			IModelElement element = dropHandler.newModelElement(newObj);
			nodePart = this.nodeTemplate.createNewNodePart(element);
			this.nodeTemplate.addModelListener();			
		}
		final Diagram targetDiagram = (Diagram) context.getTargetContainer();
		
        // define a default size for the shape
        IDiagramNodeDef nodeDef = (IDiagramNodeDef)nodePart.getDefinition();
        int nodew = getNodeWidth(nodePart);
        int nodeh = getNodeHeight(nodePart);
        int width = nodew > 0 ? nodew : DEFAULT_NODE_WIDTH;
        int height = nodeh > 0 ? nodeh : DEFAULT_NODE_HEIGHT;
        
        int x, y;
        if (context.getX() != -1 && context.getY() != -1)
        {
        	x = context.getX();
        	y = context.getY();
        }
        else
        {
        	SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor();
        	Point np = diagramEditor.getDefaultNodePosition();
        	x = np.getX();
        	y = np.getY();
        }

        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        ContainerShape containerShape =  peCreateService.createContainerShape(targetDiagram, true);
        IGaService gaService = Graphiti.getGaService();
        
        // TODO clean this up
        // The temporary logic is to create a default rectangle if no icon is associated with the node
        if (nodePart.getImageId() == null)
        {
            // create and set graphics algorithm
            RoundedRectangle rectangle = gaService.createRoundedRectangle(containerShape, 8, 8);
            rectangle.setForeground(manageColor(DEFAULT_NODE_FOREGROUND));
            gaService.setRenderingStyle(rectangle, PredefinedColoredAreas.getBlueWhiteGlossAdaptions());
            rectangle.setLineWidth(1);
            gaService.setLocationAndSize(rectangle, x, y, width, height);
 
            // create link and wire it
            link(containerShape, nodePart);        		
        }
        else
        {
        	Rectangle rectangle = gaService.createRectangle(containerShape);
        	rectangle.setFilled(false);
        	rectangle.setLineVisible(false);
        	gaService.setLocationAndSize(rectangle, x, y, width, height);
        	
        	link(containerShape, nodePart);
        	
            // Shape with Image
            {
            	Shape shape = peCreateService.createShape(containerShape, false);
            	String imageId = nodePart.getImageId();
            	Image image = gaService.createImage(shape, imageId);
            	
            	int imageWidth = getImageWidth(nodePart);
            	int imageHeight = getImageHeight(nodePart);
            	Point imageLocation = getImageLocation(nodePart, width, height, imageWidth, imageHeight);
        		Graphiti.getPeService().setPropertyValue(containerShape, 
        				SapphireDiagramPropertyKeys.NODE_IMAGE_ID, imageId);

    	        gaService.setLocationAndSize(image, imageLocation.getX(), imageLocation.getY(),
    	        		imageWidth, imageHeight);
            }        	
        }

        if (nodeDef.getLabel().element() != null)
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Diagram diagram = (Diagram)context.getTargetContainer();
            
            Text text = TextUtil.createDefaultText(diagram, shape, nodePart.getLabel());
            //Text text = gaService.createDefaultText(diagram, shape, nodePart.getLabel());
            text.setForeground(manageColor(DEFAULT_TEXT_FOREGROUND));            
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
            text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

            Point labelLocation = getLabelLocation(nodePart);
            int labelWidth = nodePart.getLabelWidth();
            int labelHeight = nodePart.getLabelHeight();
            int lwidth = labelWidth > 0 ? labelWidth : width;
            int lheight = labelHeight > 0 ? labelHeight : (nodePart.getImageId() == null ? height : DEFAULT_TEXT_HEIGHT);
            
            gaService.setLocationAndSize(text, labelLocation.getX(), labelLocation.getY(), 
            		lwidth, lheight);
 
            // create link and wire it
            link(shape, nodePart); 
            
			// provide information to support direct-editing directly
			// after object creation (must be activated additionally)
			final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
			// set container shape for direct editing after object creation
			directEditingInfo.setMainPictogramElement(containerShape);
			// set shape and graphics algorithm where the editor for
			// direct editing shall be opened after object creation
			directEditingInfo.setPictogramElement(shape);
			directEditingInfo.setGraphicsAlgorithm(text);
            
        }
        
        // add a chopbox anchor to the shape
        peCreateService.createChopboxAnchor(containerShape);
        
        // Save the node bounds
        DiagramGeometryWrapper diagramGeometry = 
        	((SapphireDiagramFeatureProvider)getFeatureProvider()).getDiagramGeometry();
        diagramGeometry.addNode(nodePart, x, y, width, height);
        
        // Create a rendering context for the node
        DiagramRenderingContext renderingCtx = new DiagramRenderingContext(
        					nodePart, 
        					(SapphireDiagramEditor)getDiagramEditor(),
        					containerShape);
        SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getFeatureProvider();
        sfp.addRenderingContext(nodePart, renderingCtx);
		return containerShape;
	}

	private int getNodeWidth(DiagramNodePart nodePart)
	{
		if (nodePart.getNodeWidth() > 0)
		{
			return nodePart.getNodeWidth();
		}
		
		int width = 0;
		int labelWidth = nodePart.getLabelWidth();
		String imageId = nodePart.getImageId();
		if (imageId != null)
		{
			int imageWidth = getImageWidth(nodePart);

			ImagePlacement imagePlacement = nodePart.getImagePlacement();
			if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM)
			{
				width = Math.max(labelWidth, imageWidth);
			}
			else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT)
			{
				int horizaontalSpacing = nodePart.getHorizontalSpacing();
				width = labelWidth + imageWidth + horizaontalSpacing;
			}
		}
		else 
		{
			width = labelWidth;
		}
		return width;
	}

	private int getNodeHeight(DiagramNodePart nodePart)
	{
		if (nodePart.getNodeHeight() > 0)
		{
			return nodePart.getNodeHeight();
		}
		int height = 0;
		int labelHeight = nodePart.getLabelHeight();
		String imageId = nodePart.getImageId();
		if (imageId != null)
		{
			int imageHeight = getImageHeight(nodePart);
			
			ImagePlacement imagePlacement = nodePart.getImagePlacement();
			if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM)
			{
				int verticalSpacing = nodePart.getVerticalSpacing();
				height = labelHeight + imageHeight + verticalSpacing;
			}
			else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT)
			{
				height = Math.max(labelHeight, imageHeight);
			}
		}
		else 
		{
			height = labelHeight;
		}
		return height;
	}
	
	private Point getImageLocation(DiagramNodePart nodePart, int nodeWidth, int nodeHeight, 
						int imageWidth, int imageHeight)
	{
		ImagePlacement imagePlacement = nodePart.getImagePlacement();
		if (imagePlacement == ImagePlacement.LEFT)
		{
			return new Point(0, 0);
		}
		else if (imagePlacement == ImagePlacement.TOP)
		{
			int offsetX = (nodeWidth - imageWidth) >> 1;
			return new Point(offsetX, 0);
		}
		else if (imagePlacement == ImagePlacement.BOTTOM)
		{
			int labelHeight = nodePart.getLabelHeight();
			int verticalSpacing = nodePart.getVerticalSpacing();
			int offsetX = (nodeWidth - imageWidth) >> 1;
			return new Point(offsetX, labelHeight + verticalSpacing);
		}
		else if (imagePlacement == ImagePlacement.RIGHT )
		{
			int labelWidth = nodePart.getLabelWidth();
			int horizontalSpacing = nodePart.getHorizontalSpacing();
			return new Point(labelWidth + horizontalSpacing, 0);			
		}
		return new Point(0, 0);
	}
	
	private Point getLabelLocation(DiagramNodePart nodePart)
	{
		ImagePlacement imagePlacement = nodePart.getImagePlacement();
		if (imagePlacement == ImagePlacement.TOP)
		{
			int imageHeight = getImageHeight(nodePart);
			int verticalSpacing = nodePart.getVerticalSpacing();			
			return new Point(0, imageHeight + verticalSpacing);
		}
		else if (imagePlacement == ImagePlacement.BOTTOM || imagePlacement == ImagePlacement.RIGHT)
		{
			return new Point(0, 0);
		}
		else if (imagePlacement == ImagePlacement.LEFT )
		{
			int imageWidth = getImageWidth(nodePart);
			int horizontalSpacing = nodePart.getHorizontalSpacing();
			return new Point(imageWidth + horizontalSpacing, 0);			
		}
		return new Point(0, 0);
	}
	
	private int getImageWidth(DiagramNodePart nodePart)
	{
		String imageId = nodePart.getImageId();
		if (imageId != null)
		{
			int imageWidth = nodePart.getImageWidth();
			if (imageWidth == 0)
			{
				org.eclipse.swt.graphics.Image image = GraphitiUi.getImageService().getImageForId(imageId);
				imageWidth = image.getImageData().width;				
			}
			return imageWidth;
		}
		return 0;
	}

	private int getImageHeight(DiagramNodePart nodePart)
	{
		String imageId = nodePart.getImageId();
		if (imageId != null)
		{
			int imageHeight = nodePart.getImageHeight();
			if (imageHeight == 0)
			{
				org.eclipse.swt.graphics.Image image = GraphitiUi.getImageService().getImageForId(imageId);
				imageHeight = image.getImageData().height;				
			}
			return imageHeight;
		}
		return 0;
	}
}
