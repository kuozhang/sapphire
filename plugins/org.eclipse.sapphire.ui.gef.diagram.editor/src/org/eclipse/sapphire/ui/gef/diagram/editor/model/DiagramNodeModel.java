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

package org.eclipse.sapphire.ui.gef.diagram.editor.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.def.ImagePlacement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeModel extends DiagramModelBase {
	
    public static final int DEFAULT_NODE_WIDTH = 100;
    public static final int DEFAULT_NODE_HEIGHT = 30;
    private static final int DEFAULT_TEXT_HEIGHT = 20;
    
    public final static String SOURCE_CONNECTIONS = "SOURCE_CONNECTIONS";
	public final static String TARGET_CONNECTIONS = "TARGET_CONNECTIONS";
	public final static String NODE_BOUNDS = "NODE_BOUNDS";
	public final static String NODE_UPDATES = "NODE_UPDATES";
	public final static String NODE_START_EDITING = "NODE_START_EDITING";
	
	private DiagramModel parent;
    private DiagramNodePart part;
	private List<DiagramConnectionModel> sourceConnection = new ArrayList<DiagramConnectionModel>();
	private List<DiagramConnectionModel> targetConnection = new ArrayList<DiagramConnectionModel>();
	
	public DiagramNodeModel(DiagramModel parent, DiagramNodePart part) {
		this.parent = parent;
		this.part = part;
	}
	
	public DiagramModel getDiagramModel() {
		return parent;
	}

	public SapphirePart getSapphirePart() {
		return getModelPart();
	}

	public DiagramNodePart getModelPart() {
		return part;
	}
	
	public String getLabel() {
		return getModelPart().getLabel();
	}
	
	public Image getImage() {
		String imageId = getModelPart().getImageId();
		if (imageId != null) {
			return getImage(imageId);
		}
		return null; 
	}
	
	public Bounds getNodeBounds() {
		Bounds bounds = getModelPart().getNodeBounds();
		if (bounds.getWidth() < 0 || bounds.getHeight() < 0) {
			
			DiagramNodePart nodePart = getModelPart();
			int labelWidth = nodePart.getLabelWidth();
			if (labelWidth <= 0) {
				labelWidth = DEFAULT_NODE_WIDTH;
			}
			String imageId = nodePart.getImageId();
			if (imageId != null) {
				int imageWidth = nodePart.getImageWidth();
				if (imageWidth == 0) {
					Image image = getImage(imageId);
					imageWidth = image.getImageData().width;
				}

				ImagePlacement imagePlacement = nodePart.getImagePlacement();
				if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM) {
					labelWidth = Math.max(labelWidth, imageWidth);
				} else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT) {
					int horizaontalSpacing = nodePart.getHorizontalSpacing();
					labelWidth = labelWidth + imageWidth + horizaontalSpacing;
				}
			}
			bounds.setWidth(labelWidth);
	        
	        
			int labelHeight = nodePart.getLabelHeight();
			if (labelHeight <= 0) {
				labelHeight = DEFAULT_NODE_HEIGHT;
			}
	        if (imageId != null) {
				int imageHeight = nodePart.getImageHeight();
				if (imageHeight == 0) {
					Image image = getImage(imageId);
					imageHeight = image.getImageData().height;
				}

	            ImagePlacement imagePlacement = nodePart.getImagePlacement();
	            if (imagePlacement == ImagePlacement.TOP || imagePlacement == ImagePlacement.BOTTOM)
	            {
	                int verticalSpacing = nodePart.getVerticalSpacing();
	                labelHeight = labelHeight + imageHeight + verticalSpacing;
	            }
	            else if (imagePlacement == ImagePlacement.LEFT || imagePlacement == ImagePlacement.RIGHT)
	            {
	            	labelHeight = Math.max(labelHeight, imageHeight);
	            }
	        }
			bounds.setHeight(labelHeight);
		}
		return bounds;
	}
	
	public Bounds getImageBounds(Bounds nodeBounds) {
		DiagramNodePart nodePart = getModelPart();

		int imageWidth = nodePart.getImageWidth();
		int imageHeight = nodePart.getImageHeight();

		String imageId = nodePart.getImageId();
		if (imageId != null) {
			if (imageWidth == 0) {
				Image image = getImage(imageId);
				imageWidth = image.getImageData().width;
			}
			if (imageHeight == 0) {
				Image image = getImage(imageId);
				imageHeight = image.getImageData().height;
			}
		}

		int x = 0;
		int y = 0;
        ImagePlacement imagePlacement = nodePart.getImagePlacement();
        if (imagePlacement == ImagePlacement.LEFT)
        {
        	x = 0;
        	y = 0;
        }
        else if (imagePlacement == ImagePlacement.TOP)
        {
            int offsetX = (nodeBounds.getWidth() - imageWidth) >> 1;
			x = offsetX;
			y = 0;
        }
        else if (imagePlacement == ImagePlacement.BOTTOM)
        {
            int labelHeight = nodePart.getLabelHeight();
            int verticalSpacing = nodePart.getVerticalSpacing();
            int offsetX = (nodeBounds.getWidth() - imageWidth) >> 1;
			x = offsetX;
			y = labelHeight + verticalSpacing;
        }
        else if (imagePlacement == ImagePlacement.RIGHT )
        {
            int labelWidth = nodePart.getLabelWidth();
            int horizontalSpacing = nodePart.getHorizontalSpacing();
			x = labelWidth + horizontalSpacing;
			y = 0;
        }
		return new Bounds(x, y, imageWidth, imageHeight);
	}
	
	public Bounds getLabelBounds(Bounds nodeBounds) {
		DiagramNodePart nodePart = getModelPart();

		String imageId = nodePart.getImageId();
		int imageWidth = 0;
		int imageHeight = 0;
		if (imageId != null) {
			imageWidth = nodePart.getImageWidth();
			if (imageWidth == 0) {
				Image image = getImage(imageId);
				imageWidth = image.getImageData().width;
			}
			imageHeight = nodePart.getImageHeight();
			if (imageHeight == 0) {
				Image image = getImage(imageId);
				imageHeight = image.getImageData().height;
			}
		}

		int x = 0;
		int y = 0;
        int labelWidth = nodePart.getLabelWidth();
        int labelHeight = nodePart.getLabelHeight();
        labelWidth = labelWidth > 0 ? labelWidth : nodeBounds.getWidth();
        labelHeight = labelHeight > 0 ? labelHeight : DEFAULT_TEXT_HEIGHT;
        
		int verticalSpacing = nodePart.getVerticalSpacing();            

		ImagePlacement imagePlacement = nodePart.getImagePlacement();
        if (imagePlacement == ImagePlacement.TOP)
        {
            x = 0;
            y = imageHeight + verticalSpacing;
        }
        else if (imagePlacement == ImagePlacement.BOTTOM)
        {
        	x = 0;
        	y = 0;
        }
        else if (imagePlacement == ImagePlacement.RIGHT)
        {
        	x = 0;
        	y = 0;
        }
        else if (imagePlacement == ImagePlacement.LEFT )
        {
            int horizontalSpacing = nodePart.getHorizontalSpacing();
            x = imageWidth + horizontalSpacing;
            y = 0;
        }
        

        // center the label
        int offset = nodeBounds.getHeight() - imageHeight + verticalSpacing - labelHeight >> 1;
        if (offset > 0) {
        	y += offset;
        }

        return new Bounds(x, y, labelWidth, DEFAULT_TEXT_HEIGHT);
	}

	public void handleMoveNode() {
		firePropertyChange(NODE_BOUNDS, null, getModelPart().getNodeBounds());
	}
	
	public void handleUpdateNode() {
		firePropertyChange(NODE_UPDATES, null, getModelPart().getNodeBounds());
	}

	public List<DiagramConnectionModel> getSourceConnections() {
		return sourceConnection;
	}

	public List<DiagramConnectionModel> getTargetConnections() {
		return targetConnection;
	}

	public void addSourceConnection(DiagramConnectionModel connection) {
		sourceConnection.add(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void addTargetConnection(DiagramConnectionModel connection) {
		targetConnection.add(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

	public void removeSourceConnection(DiagramConnectionModel connection) {
		sourceConnection.remove(connection);
		firePropertyChange(SOURCE_CONNECTIONS, null, connection);
	}
	
	public void removeTargetConnection(DiagramConnectionModel connection) {
		targetConnection.remove(connection);
		firePropertyChange(TARGET_CONNECTIONS, null, connection);
	}

	public void handleStartEditing() {
		firePropertyChange(NODE_START_EDITING, null, null);
	}
	
	private Image getImage(final String imageId) {
		ImageDescriptor imageDescriptor = parent.getImageCache().getImageDescriptor(imageId);
		return part.getImageCache().getImage(imageDescriptor);
	}

}
