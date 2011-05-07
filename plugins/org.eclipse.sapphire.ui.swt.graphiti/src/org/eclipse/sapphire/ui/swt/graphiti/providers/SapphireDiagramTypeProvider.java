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
 *    Konstantin Komissarchik - [342098] Separate dependency on org.eclipse.core.runtime (part 1)
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import java.util.List;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramEditor;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.internal.platform.ExtensionManager;
import org.eclipse.graphiti.ui.platform.IImageProvider;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeImageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramTypeProvider extends AbstractDiagramTypeProvider 
{
	private SapphireDiagramFeatureProvider featureProvider;
	private IToolBehaviorProvider[] toolBehaviorProviders;
	
	public SapphireDiagramTypeProvider()
	{
		this.featureProvider = 
			new SapphireDiagramFeatureProvider(this, new SapphireDiagramSolver());
		setFeatureProvider(this.featureProvider);
	}
	
	@Override
	public void init(Diagram diagram, IDiagramEditor diagramEditor)
	{
		super.init(diagram, diagramEditor);
		
		SapphireDiagramEditorPagePart editorPart = ((SapphireDiagramEditor)diagramEditor).getPart();
		DiagramRenderingContext renderingCtx = new DiagramRenderingContext(editorPart, diagram);
		this.featureProvider.addRenderingContext(editorPart, renderingCtx);
		
		ExtensionManager extManager = (ExtensionManager)GraphitiUi.getExtensionManager();
		IImageProvider imageProviders[] = extManager.getImageProviders();
		SapphireDiagramImageProvider sapphireImageProvider = null;
		for (IImageProvider imageProvider : imageProviders)
		{
			if (imageProvider instanceof SapphireDiagramImageProvider)
			{
				sapphireImageProvider = (SapphireDiagramImageProvider)imageProvider;
				break;
			}
		}
		if (sapphireImageProvider != null)
		{
			SapphireDiagramEditorPagePart diagramPart = getDiagramPart();
			List<IDiagramImageChoice> diagramImages = diagramPart.getImageDecorators();
			
			// Add diagram page images
			for (IDiagramImageChoice imageChoice : diagramImages)
			{
				registerImage(sapphireImageProvider, imageChoice);
			}
			
			// Add node images
			List<DiagramNodeTemplate> nodeTemplates = diagramPart.getNodeTemplates();
			for (DiagramNodeTemplate nodeTemplate : nodeTemplates)
			{
				final IDiagramNodeDef nodeDef = nodeTemplate.getDefinition();
				final IDiagramNodeImageDef imageDef = nodeDef.getImage().element();
				
				if (imageDef != null)
				{
					ModelElementList<IDiagramImageChoice> images = imageDef.getPossibleImages();
					for (IDiagramImageChoice imageChoice : images)
					{
						registerImage(sapphireImageProvider, imageChoice);
					}
				}
				
				// register node tool palette image
				IDiagramImageChoice toolImage = nodeTemplate.getToolPaletteImage();
				if (toolImage != null)
				{
					registerImage(sapphireImageProvider, toolImage);
				}
			}
			
			// Add connection tool palette images
			List<IDiagramConnectionDef> connDefs = diagramPart.getDiagramConnectionDefs();
			for (IDiagramConnectionDef connDef : connDefs)
			{
				IDiagramImageChoice image = connDef.getToolPaletteImage().element();
				if (image != null)
				{
					registerImage(sapphireImageProvider, image);
				}
			}
		}
	}
	
    @Override
    public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() 
    {
        if (this.toolBehaviorProviders == null) 
        {
            this.toolBehaviorProviders =
                new IToolBehaviorProvider[] { new SapphireDiagramToolBehaviorProvider(this) };
        }
        return this.toolBehaviorProviders;
    }
	    
    private void registerImage(SapphireDiagramImageProvider sapphireImageProvider, IDiagramImageChoice imageChoice)
    {
		ISapphireUiDef uiDef = imageChoice.nearest(ISapphireUiDef.class);
		String imageId = imageChoice.getImageId().getContent();
		String imagePath = imageChoice.getImagePath().getContent();
		String bundleId = resolveImageBundle(uiDef, imagePath);
		// Graphiti's image provider doesn't support images from different plugins.
		// See http://www.eclipse.org/forums/index.php?t=tree&th=201973&start=0&S=3813ad4d99f2ac8bd56a0072ffa6ebd9
		if (bundleId != null)
		{
			sapphireImageProvider.setPluginId(bundleId);
		}
		
		if (imageId != null && imagePath != null && 
				sapphireImageProvider.getImageFilePath(imageId) == null)
		{
			sapphireImageProvider.registerImage(imageId, imagePath);
		}						    	
    }
    
    private String resolveImageBundle(ISapphireUiDef def, String imagePath)
    {
        final Bundle bundle = def.adapt( Bundle.class );
        
        if( bundle != null )
        {
            return bundle.getSymbolicName();
        }

    	return null;
    }
    
	
	private SapphireDiagramEditorPagePart getDiagramPart()
	{
		SapphireDiagramEditor diagramEditor = (SapphireDiagramEditor)getDiagramEditor();
		return diagramEditor.getPart();
	}
    
}
