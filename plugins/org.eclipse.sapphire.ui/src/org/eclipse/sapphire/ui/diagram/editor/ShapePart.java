/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.shape.def.LayoutConstraintDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SelectionPresentation;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ShapePart extends SapphirePart implements IPropertiesViewContributorPart
{
	private ShapeDef shapeDef;
	private Element modelElement;
	private boolean isActive = false;
	private boolean isEditable = false;
	private PropertiesViewContributionManager propertiesViewContributionManager; 
	private SelectionPresentation selectionPresentation = null;

	@Override
    protected void init()
    {
        super.init();
        this.shapeDef = (ShapeDef)super.definition;
        this.modelElement = getModelElement();
    }

	public LayoutConstraintDef getLayoutConstraint()
	{
		return this.shapeDef.getSequenceLayoutConstraint();
	}
	
	@Override
	public void render(SapphireRenderingContext context) 
	{
		// TODO Auto-generated method stub

	}
	
	public boolean isActive()
	{
		return this.isActive;
	}
	
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public boolean isEditable()
	{
		return this.isEditable;
	}
	
	public void setEditable(boolean editable)
	{
		this.isEditable = editable;
	}
		
    @Override
    public Set<String> getActionContexts()
    {
        Set<String> contextSet = new HashSet<String>();
        if (isActive())
        {
        	contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_NODE_SHAPE);
        	contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_SHAPE_HIDDEN);
        }
        return contextSet;    	
    }
	
    @Override
    public Element getLocalModelElement()
    {
        return this.modelElement;
    }    
    
    public List<ShapePart> getActiveChildren()
    {
    	return Collections.emptyList();
    }
    
	protected DiagramNodePart getNodePart() 
	{
		DiagramNodePart nodePart = null;
		ISapphirePart part = this;
		while (part != null) {
			if (part instanceof DiagramNodePart) {
				nodePart = (DiagramNodePart)part;
				break;
			}
			part = part.getParentPart();
		}
		return nodePart;
	}
	
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        if( this.propertiesViewContributionManager == null )
        {
            this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, getLocalModelElement() );
        }
        
        return this.propertiesViewContributionManager.getPropertiesViewContribution();
    }
    
    public void setSelectionPresentation(SelectionPresentation selectionPresentation)
    {
    	this.selectionPresentation = selectionPresentation;
    }
    
    public SelectionPresentation getSelectionPresentation()
    {
    	return this.selectionPresentation;
    }
        
}
