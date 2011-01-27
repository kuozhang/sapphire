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

package org.eclipse.sapphire.ui.diagram.graphiti.features;

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
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramLabelDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeImageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramGeometryWrapper;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.graphiti.providers.SapphireDiagramFeatureProvider;
import org.eclipse.sapphire.ui.diagram.graphiti.providers.SapphireDiagramPropertyKeys;

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
    private static int defaultX = 50;
    private static int defaultY = 50;
    private static int xInc = 100;
    private static int yInc = 0;
	
	public SapphireAddNodeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	public boolean canAdd(IAddContext context) 
	{
		Object newObj = context.getNewObject();
		if (newObj instanceof DiagramNodePart)
		{
			return true;
		}
		return false;
	}

	public PictogramElement add(IAddContext context)
	{
		DiagramNodePart nodePart = (DiagramNodePart)context.getNewObject();
		final Diagram targetDiagram = (Diagram) context.getTargetContainer();
		
        // define a default size for the shape
        IDiagramNodeDef nodeDef = (IDiagramNodeDef)nodePart.getDefinition();
        int width = nodeDef.getHint(ISapphirePartDef.HINT_WIDTH, DEFAULT_NODE_WIDTH);
        int height = nodeDef.getHint(ISapphirePartDef.HINT_HEIGHT, DEFAULT_NODE_HEIGHT);
        int x, y;
        if (context.getX() != -1)
        {
        	x = context.getX();
        }
        else
        {
        	x = defaultX;
        	defaultX += xInc;
        }
        if (context.getY() != -1)
        {
        	y = context.getY();
        }
        else
        {
        	y = defaultY;
        	defaultY += yInc;
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
        	IDiagramNodeImageDef imageDef = nodeDef.getImage().element();
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
            	int imageX = imageDef.getHint("x", 0);
            	int imageY = imageDef.getHint("y", 0);
                int imageWidth = imageDef.getHint(ISapphirePartDef.HINT_WIDTH, width);
                int imageHeight = imageDef.getHint(ISapphirePartDef.HINT_HEIGHT, -1);
        		Graphiti.getPeService().setPropertyValue(containerShape, 
        				SapphireDiagramPropertyKeys.NODE_IMAGE_ID, imageId);

    	        gaService.setLocationAndSize(image, imageX, imageY, imageWidth, imageHeight);
            }        	
        }

        if (nodeDef.getLabel().element() != null)
        {
            // create shape for text
            Shape shape = peCreateService.createShape(containerShape, false);
 
            // create and set text graphics algorithm
            Text text = gaService.createDefaultText(shape, nodePart.getLabel());
            text.setForeground(manageColor(DEFAULT_TEXT_FOREGROUND));
            text.getFont().setBold(true);
            text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
            text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);

            IDiagramLabelDef labelDef = nodeDef.getLabel().element();
            int labelX = labelDef.getHint("x", 0);
        	int labelY = labelDef.getHint("y", 0);            
            int labelWidth = labelDef.getHint(ISapphirePartDef.HINT_WIDTH, width);
            int labelHeight = labelDef.getHint(ISapphirePartDef.HINT_HEIGHT, 
            		nodePart.getImageId() == null ? height : DEFAULT_TEXT_HEIGHT);
            
            gaService.setLocationAndSize(text, labelX, labelY, labelWidth, labelHeight);
 
            // create link and wire it
            link(shape, nodePart);            
        }
        
        // add a chopbox anchor to the shape
        peCreateService.createChopboxAnchor(containerShape);
        
        // Save the node bounds
        DiagramGeometryWrapper diagramGeometry = 
        	((SapphireDiagramFeatureProvider)getFeatureProvider()).getDiagramGeometry();
        diagramGeometry.addNode(nodePart, x, y, width, height);
        
		return containerShape;
	}

}
