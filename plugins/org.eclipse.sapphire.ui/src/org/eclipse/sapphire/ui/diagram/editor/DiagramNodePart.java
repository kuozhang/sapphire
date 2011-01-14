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

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodePart extends SapphirePart 
{
	private DiagramNodeTemplate nodeTemplate;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;
	private FunctionResult labelFunctionResult;
	private FunctionResult idFunctionResult;
	private FunctionResult imageFunctionResult;
	private ValueProperty labelProperty;
	
	public DiagramNodePart(DiagramNodeTemplate nodeTemplate)
	{
		this.nodeTemplate = nodeTemplate;
	}
	
    @Override
    protected void init()
    {
        super.init();
        
        this.definition = (IDiagramNodeDef)super.definition;
        this.modelElement = getModelElement();
        this.labelFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.definition.getLabel().element().getContent(), 
            new Runnable()
            {
                public void run()
                {
                	refreshLabel();
                }
            }
        );
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, this.labelFunctionResult);
        
        this.idFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.definition.getInstanceId(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
        
        if (this.definition.getImage().element() != null)
        {
	        this.imageFunctionResult = initExpression
	        ( 
	        	this.modelElement,
	            this.definition.getImage().element().getValue(), 
	            new Runnable()
	            {
	                public void run()
	                {
	                	refreshImage();
	                }
	            }
	        );
        }
    }
    
    public DiagramNodeTemplate getDiagramNodeTemplate()
    {
    	return this.nodeTemplate;
    }
    
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
    }    
        
	@Override
	public void render(SapphireRenderingContext context)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose()
	{
		super.dispose();
		if (this.labelFunctionResult != null)
		{
			this.labelFunctionResult.dispose();
		}
		
		if (this.idFunctionResult != null)
		{
			this.idFunctionResult.dispose();
		}
	}
	
	public String getLabel()
	{
        String label = null;
        
        if( this.labelFunctionResult != null )
        {
            label = (String) this.labelFunctionResult.value();
        }
        
        if( label == null )
        {
            label = "#null#";
        }
        
        return label;
	}

	public void setLabel(String newValue)
	{
		if (this.labelProperty != null)
		{
			this.modelElement.write(this.labelProperty, newValue);
		}
	}
	
	public void refreshLabel()
	{
		updateNodePE();
	}
	
	public void refreshImage()
	{
		updateNodePE();
	}
	
	public boolean canEditLabel()
	{
		return this.labelProperty != null;
	}
	
	public String getInstanceId()
	{
        String id = null;
        
        if( this.idFunctionResult != null )
        {
            id = (String) this.idFunctionResult.value();
        }
        
        if( id == null )
        {
            id = "#null#";
        }
        
        return id;		
	}
	
	public boolean canResizeShape()
	{
		return this.definition.getHint("resizable", true);
	}
	
	public String getImageId()
	{
        if( this.imageFunctionResult != null )
        {
            String idStr = (String) this.imageFunctionResult.value();
            return idStr;
        }
        return null;		
	}
		
	private void updateNodePE()
	{
		final SapphireDiagramEditorPart diagramEditor = (SapphireDiagramEditorPart)getParentPart();
		final IFeatureProvider fp = 
						diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
		final PictogramElement pe = getDiagramNodeTemplate().getContainerShape(fp, this);
		if (pe != null)
		{
			UpdateContext context = new UpdateContext(pe);
			fp.updateIfPossible(context);
		}
	}
}
