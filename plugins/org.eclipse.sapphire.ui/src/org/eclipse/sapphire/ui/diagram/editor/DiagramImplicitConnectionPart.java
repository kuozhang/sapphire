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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramImplicitConnectionPart extends DiagramConnectionPart 
{
    private IModelElement srcNodeModel;
    private IModelElement targetNodeModel;
    private DiagramImplicitConnectionTemplate connectionTemplate;
    
    public DiagramImplicitConnectionPart(IModelElement srcNodeModel, IModelElement targetNodeModel)
    {
        this.srcNodeModel = srcNodeModel;
        this.targetNodeModel = targetNodeModel;
    }
    
    @Override
    protected void init()
    {   
        this.connectionTemplate = (DiagramImplicitConnectionTemplate)getParentPart();
        
        this.definition = (IDiagramConnectionDef)definition(); 
        this.modelElement = getModelElement();
    }
    
    @Override
    public IModelElement getEndpoint1()
    {
        return this.srcNodeModel;
    }
    
    @Override
    public IModelElement getEndpoint2()
    {
        return this.targetNodeModel;
    }
    
    @Override
    public void resetEndpoint1()
    {        
    }

    @Override
    public void resetEndpoint2()
    {        
    }
    
    @Override
    public void dispose()
    {
    }
    
    @Override
    public DiagramConnectionTemplate getDiagramConnectionTemplate()
    {
        return this.connectionTemplate;
    }
    
    @Override
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        return null;
    }
    
    @Override
    public void addModelListener()
    {    	
    }
    
    @Override
    public void removeModelListener()
    {        
    }
    
}
