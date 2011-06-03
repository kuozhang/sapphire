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
    public static final int DEFAULT_NODE_WIDTH = 100;
    public static final int DEFAULT_NODE_HEIGHT = 30;
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
        int width = getNodeWidth(nodePart, context.getWidth());
        int height = getNodeHeight(nodePart, context.getHeight());
        
        int ltX, ltY;
        if (context.getX() != -1 && context.getY() != -1)
        {
        	ltX = context.getX() - (width >> 1);
        	ltY = context.getY() - (height >> 1);
        }
        else
        {
        	SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)getFeatureProvider().getDiagramTypeProvider().getDiagramEditor();
        	Point np = diagramEditor.getDefaultNodePosition();
        	ltX = np.getX();
        	ltY = np.getY();
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
            gaService.setLocationAndSize(rectangle, ltX, ltY, width, height);
 
            // create link and wire it
            link(containerShape, nodePart);        		
        }
        else
        {
        	Rectangle rectangle = gaService.createRectangle(containerShape);
        	rectangle.setFilled(false);
        	rectangle.setLineVisible(false);
        	gaService.setLocationAndSize(rectangle, ltX, ltY, width, height);
        	
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
            int lheight = labelHeight > 0 ? labelHeight : DEFAULT_TEXT_HEIGHT;

            int textX = labelLocation.getX();
            if (lwidth == width)
            {
            	// Leave a few pixels in the left and right for direct editing cell
            	lwidth -= 6;
            	textX += 3;
            }
            // If the node doesn't have image, we need to center the text vertically.
            // We either give the text the entire node height and rely on text's 
            // vertical alignment to center the text vertically. Or we give the text
            // the default text height (20) and center it by calculating it's vertical
            // offset. For the first approach, we'd run into misalignment for direct
            // editing. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=344022
            int textY = labelLocation.getY();
            if (nodePart.getImageId() == null && lheight < height)
            {
            	textY += height - lheight >> 1;
            }
            
            gaService.setLocationAndSize(text, textX, textY, lwidth, lheight);
 
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
        diagramGeometry.addNode(nodePart, ltX, ltY, width, height);
        
        // Create a rendering context for the node
        DiagramRenderingContext renderingCtx = new DiagramRenderingContext(
        					nodePart, 
        					(SapphireDiagramEditor)getDiagramEditor(),
        					containerShape);
        SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getFeatureProvider();
        sfp.addRenderingContext(nodePart, renderingCtx);
		return containerShape;
	}

	/*
	 * Calculate node width based on passed in width, width returned from DiagramNodePart
	 * and width calculated based on the node image, label placement and margins.
	 */
	public static int getNodeWidth(DiagramNodePart nodePart, int widthParam)
	{
		if (widthParam > 0)
		{
			return widthParam;			
		}
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
		if (width <= 0)
		{
			width = DEFAULT_NODE_WIDTH;
		}
		return width;
	}

	/*
	 * Calculate node height based on passed in height, height returned from DiagramNodePart
	 * and height calculated based on the node image, label placement and margins.
	 */
	
	public static int getNodeHeight(DiagramNodePart nodePart, int heightParam)
	{
		if (heightParam > 0)
		{
			return heightParam;
		}
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
		if (height <= 0)
		{
			height = DEFAULT_NODE_HEIGHT;
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
	
	private static int getImageWidth(DiagramNodePart nodePart)
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

	private static int getImageHeight(DiagramNodePart nodePart)
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
