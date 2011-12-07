package org.eclipse.sapphire.ui.gef.diagram.editor.model;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeImageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.osgi.framework.Bundle;

public class DiagramImageCache {

	private final Map<String, ImageDescriptor> idToImageDescriptor = new HashMap<String, ImageDescriptor>();

	public DiagramImageCache(SapphireDiagramEditorPagePart diagramPart) {
		// Add node images
		List<DiagramNodeTemplate> nodeTemplates = diagramPart.getNodeTemplates();
		for (DiagramNodeTemplate nodeTemplate : nodeTemplates) {
			final IDiagramNodeDef nodeDef = nodeTemplate.getDefinition();
			final IDiagramNodeImageDef imageDef = nodeDef.getImage().element();

			if (imageDef != null) {
				ModelElementList<IDiagramImageChoice> images = imageDef.getPossibleImages();
				for (IDiagramImageChoice imageChoice : images) {
					registerImage(imageChoice);
				}
			}

			// register node tool palette image
			IDiagramImageChoice toolImage = nodeTemplate.getToolPaletteImage();
			if (toolImage != null) {
				registerImage(toolImage);
			}
		}

		// Add connection tool palette images
		List<IDiagramConnectionDef> connDefs = diagramPart.getDiagramConnectionDefs();
		for (IDiagramConnectionDef connDef : connDefs) {
			IDiagramImageChoice image = connDef.getToolPaletteImage().element();
			if (image != null) {
				registerImage(image);
			}
		}
	}

	private void registerImage(IDiagramImageChoice imageChoice) {
		ISapphireUiDef uiDef = imageChoice.nearest(ISapphireUiDef.class);
		String imageId = imageChoice.getImageId().getContent();
		String imagePath = imageChoice.getImagePath().getContent();

		// TODO should be relative to sdef and not bundle?
		final Bundle bundle = uiDef.adapt(Bundle.class);

		if (imageId != null && imagePath != null) {
	        final URL url = bundle.getResource(imagePath);
	        if (url != null) {
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
				idToImageDescriptor.put(imageId, descriptor);
	        }
		}
	}

	public ImageDescriptor getImageDescriptor(final String imageId) {
		return idToImageDescriptor.get(imageId);
	}

}
