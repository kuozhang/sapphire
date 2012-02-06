/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FailSafeFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.el.ModelElementFunctionContext;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.def.HorizontalAlignment;
import org.eclipse.sapphire.ui.def.VerticalAlignment;
import org.eclipse.sapphire.ui.diagram.def.DecoratorPlacement;
import org.eclipse.sapphire.ui.diagram.def.IDiagramDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeProblemDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.ProblemDecoratorSize;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.gef.diagram.editor.DiagramImageCache;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.DecoratorImageFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class NodeDecorator {

    private static final ImageDescriptor IMG_ERROR_SMALL
    	= SwtRendererUtil.createImageDescriptor( NodeDecorator.class, "error_small.png" );
    private static final ImageDescriptor IMG_ERROR
		= SwtRendererUtil.createImageDescriptor( NodeDecorator.class, "error.gif" );
    private static final ImageDescriptor IMG_WARNING_SMALL
		= SwtRendererUtil.createImageDescriptor( NodeDecorator.class, "warning_small.png" );
    private static final ImageDescriptor IMG_WARNING
		= SwtRendererUtil.createImageDescriptor( NodeDecorator.class, "warning.gif" );

    private static final int SMALL_ERROR_DECORATOR_WIDTH = 7;
    private static final int SMALL_ERROR_DECORATOR_HEIGHT = 8;
    private static final int LARGE_ERROR_DECORATOR_WIDTH = 16;
    private static final int LARGE_ERROR_DECORATOR_HEIGHT = 16;

    private DiagramNodeModel nodeModel;
	private Bounds labelBounds;
	private Bounds imageBounds;

	public NodeDecorator(DiagramNodeModel nodeModel, Bounds labelBounds, Bounds imageBounds) {
		this.nodeModel = nodeModel;
		this.labelBounds = labelBounds;
		this.imageBounds = imageBounds;
	}

	public List<IFigure> decorate(NodeFigure figure) {
		List<IFigure> figureList = new ArrayList<IFigure>();

		List<ImageDecorator> decoratorList = getImageDecorators();
		for (ImageDecorator imageDecorator : decoratorList) {
			IFigure decorateFigure = decorateFigure(figure, imageDecorator);
			figureList.add(decorateFigure);
		}

		return figureList;
	}

	private IFigure decorateFigure(IFigure parentFigure, ImageDecorator imageDecorator) {
		String messageText = imageDecorator.getMessage();
		final String imageId = imageDecorator.getImageId();
		final Image image;
		if (imageId != null) {
			DiagramImageCache imageCache = nodeModel.getDiagramModel().getImageCache();
			ImageDescriptor imageDescriptor = imageCache.getImageDescriptor(imageId);
			image = nodeModel.getModelPart().getImageCache().getImage(imageDescriptor);
		} else {
			image = imageDecorator.getImage();
		}
		
		ImageFigure decoratorFigure = new DecoratorImageFigure(image);
		org.eclipse.swt.graphics.Rectangle bounds = decoratorFigure.getImage().getBounds();
		Rectangle boundsForDecoratorFigure = new Rectangle(imageDecorator.getX(), imageDecorator.getY(), bounds.width, bounds.height);

		decoratorFigure.setVisible(true);
		if (messageText != null && messageText.length() > 0) {
			decoratorFigure.setToolTip(new Label(messageText));
		}
		parentFigure.add(decoratorFigure);
		parentFigure.setConstraint(decoratorFigure, boundsForDecoratorFigure);

		return decoratorFigure;
	}

	private List<ImageDecorator> getImageDecorators() {
		List<ImageDecorator> decoratorList = new ArrayList<ImageDecorator>();
		DiagramNodePart nodePart = nodeModel.getModelPart();

		if (nodePart.getProblemIndicatorDef().isShowDecorator().getContent()) {
			addNodeProblemDecorator(nodePart, decoratorList);
		}

		List<IDiagramImageDecoratorDef> imageDecorators = nodePart.getImageDecorators();
		for (IDiagramImageDecoratorDef imageDecorator : imageDecorators) {
			addNodeImageDecorator(imageDecorator, decoratorList);
		}
		return decoratorList;
	}

	private void addNodeProblemDecorator(DiagramNodePart nodePart, List<ImageDecorator> decoratorList) {
		IModelElement model = nodePart.getModelElement();
		IDiagramNodeProblemDecoratorDef decoratorDef = nodePart.getProblemIndicatorDef();
		Status status = model.validate();
		ImageDecorator imageRenderingDecorator = null;
		if (status.severity() != Status.Severity.OK) {
			if (status.severity() == Status.Severity.WARNING) {
				if (decoratorDef.getSize().getContent() == ProblemDecoratorSize.SMALL) {
					Image image = nodeModel.getModelPart().getImageCache().getImage(IMG_WARNING_SMALL);
					imageRenderingDecorator = new ImageDecorator(image);
				} else {
					Image image = nodeModel.getModelPart().getImageCache().getImage(IMG_WARNING);
					imageRenderingDecorator = new ImageDecorator(image);
				}
			} else if (status.severity() == Status.Severity.ERROR) {
				if (decoratorDef.getSize().getContent() == ProblemDecoratorSize.SMALL) {
					Image image = nodeModel.getModelPart().getImageCache().getImage(IMG_ERROR_SMALL);
					imageRenderingDecorator = new ImageDecorator(image);
				} else {
					Image image = nodeModel.getModelPart().getImageCache().getImage(IMG_ERROR);
					imageRenderingDecorator = new ImageDecorator(image);
				}
			}
		}
		if (imageRenderingDecorator != null) {
            int indicatorWidth = decoratorDef.getSize().getContent() == ProblemDecoratorSize.LARGE ? LARGE_ERROR_DECORATOR_WIDTH : SMALL_ERROR_DECORATOR_WIDTH;
            int indicatorHeight = decoratorDef.getSize().getContent() == ProblemDecoratorSize.LARGE ? LARGE_ERROR_DECORATOR_HEIGHT : SMALL_ERROR_DECORATOR_HEIGHT;

			Point pt = getDecoratorPosition(decoratorDef, indicatorWidth, indicatorHeight);
			imageRenderingDecorator.setX(pt.getX());
			imageRenderingDecorator.setY(pt.getY());
			imageRenderingDecorator.setMessage(status.message());
			decoratorList.add(imageRenderingDecorator);
		}
	}

	private void addNodeImageDecorator(IDiagramImageDecoratorDef imageDecoratorDef, List<ImageDecorator> decoratorList) {
        final ISapphirePart part = nodeModel.getModelPart();
        FunctionContext functionContext = new ModelElementFunctionContext( part.getLocalModelElement(), part.definition().adapt( LocalizationService.class ) );
        final Function imageFunction = FailSafeFunction.create( imageDecoratorDef.getImage().getContent(), Literal.create( ImageData.class ) );
        final FunctionResult imageFunctionResult = imageFunction.evaluate( functionContext ); 
        final ImageData data = (ImageData) imageFunctionResult.value();
        Image image = part.getImageCache().getImage(data);
		imageFunctionResult.dispose();

		ImageDecorator imageRenderingDecorator = new ImageDecorator(image);
		Point pt = getDecoratorPosition(imageDecoratorDef, image.getImageData().width, image.getImageData().height);
		imageRenderingDecorator.setX(pt.getX());
		imageRenderingDecorator.setY(pt.getY());
		decoratorList.add(imageRenderingDecorator);
	}

	private Point getDecoratorPosition(IDiagramDecoratorDef decoratorDef, int decoratorWidth, int decoratorHeight) {
		Bounds bounds;
		if (decoratorDef.getDecoratorPlacement().getContent() == DecoratorPlacement.IMAGE && imageBounds.getWidth() > 0 && imageBounds.getHeight() > 0) {
			bounds = imageBounds;
		} else {
			bounds = labelBounds;
		}

		HorizontalAlignment horizontalAlign = decoratorDef.getHorizontalAlignment().getContent();
		int offsetX = 0;
		int offsetY = 0;
		if (horizontalAlign == HorizontalAlignment.RIGHT) {
			offsetX = bounds.getWidth() - decoratorWidth;
			offsetX -= decoratorDef.getHorizontalMargin().getContent();
		} else if (horizontalAlign == HorizontalAlignment.LEFT) {
			offsetX += decoratorDef.getHorizontalMargin().getContent();
		} else if (horizontalAlign == HorizontalAlignment.CENTER) {
			offsetX = (bounds.getWidth() - decoratorWidth) >> 1;
		}

		VerticalAlignment verticalAlign = decoratorDef.getVerticalAlignment().getContent();

		if (verticalAlign == VerticalAlignment.BOTTOM) {
			offsetY = bounds.getHeight() - decoratorHeight;
			offsetY -= decoratorDef.getVerticalMargin().getContent();
		} else if (verticalAlign == VerticalAlignment.TOP) {
			offsetY += decoratorDef.getVerticalMargin().getContent();
		} else if (verticalAlign == VerticalAlignment.CENTER) {
			offsetY = (bounds.getHeight() - decoratorHeight) / 2;
		}

		return new Point(offsetX + bounds.getX(), offsetY + bounds.getY());
	}

	private class ImageDecorator {

		private String imageId;
		private Image image;
		private int x;
		private int y;
		private String message;

		public ImageDecorator(final Image image) {
			this.image = image;
		}

		public String getImageId() {
			return this.imageId;
		}

		public Image getImage() {
			return this.image;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

}
