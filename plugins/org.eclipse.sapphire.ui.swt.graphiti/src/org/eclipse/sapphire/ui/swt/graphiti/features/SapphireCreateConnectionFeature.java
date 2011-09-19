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
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.features;

import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageChoice;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramEmbeddedConnectionTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireCreateConnectionFeature extends AbstractCreateConnectionFeature implements SapphireCreateFeature
{
    private SapphireDiagramEditorPagePart diagramPart;
    private IDiagramConnectionDef connDef;
    
    public SapphireCreateConnectionFeature(IFeatureProvider fp, SapphireDiagramEditorPagePart diagramPart, 
                    IDiagramConnectionDef connDef, String tpLabel, String tpDesc)
    {
        super(fp, tpLabel, tpDesc);
        this.diagramPart = diagramPart;
        this.connDef = connDef;        
    }
    
    public boolean canCreate(ICreateConnectionContext context) 
    {
        SapphirePart source = getEndpoint(context.getSourceAnchor());
        SapphirePart target = getEndpoint(context.getTargetAnchor());
        if (source instanceof DiagramNodePart && 
                target instanceof DiagramNodePart && source != target) 
        {
            DiagramConnectionTemplate connectionTemplate = getConnectionTemplate((DiagramNodePart)source);
            if (connectionTemplate != null)
            {
                return connectionTemplate.canCreateNewConnection((DiagramNodePart)source, 
                        (DiagramNodePart)target);
            }
        }
        return false;
    }

    public Connection create(ICreateConnectionContext context) 
    {
        Connection newConnection = null;

        // get model elements which should be connected
        SapphirePart source = getEndpoint(context.getSourceAnchor());
        SapphirePart target = getEndpoint(context.getTargetAnchor());

        if (source instanceof DiagramNodePart && target instanceof DiagramNodePart) 
        {
            DiagramNodePart sourceNode = (DiagramNodePart)source;
            DiagramNodePart targetNode = (DiagramNodePart)target;
            DiagramConnectionTemplate connectionTemplate = getConnectionTemplate(sourceNode);
            // create new business object
            if (connectionTemplate instanceof DiagramEmbeddedConnectionTemplate)
            {
                ((DiagramEmbeddedConnectionTemplate)connectionTemplate).removeModelListener(sourceNode.getLocalModelElement());
            }
            else
            {
                connectionTemplate.removeModelListener();
            }
            DiagramConnectionPart connectionPart = 
                connectionTemplate.createNewDiagramConnection(sourceNode, targetNode);

            if (connectionTemplate instanceof DiagramEmbeddedConnectionTemplate)
            {
                ((DiagramEmbeddedConnectionTemplate)connectionTemplate).addModelListener(sourceNode.getLocalModelElement());
            }
            else
            {
                connectionTemplate.addModelListener();
            }
            
            // add connection for business object
            AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
            addContext.setNewObject(connectionPart);
            newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
            
            // activate direct editing after object creation
            getFeatureProvider().getDirectEditingInfo().setActive(true);
        }

        return newConnection;
    }

    public boolean canStartConnection(ICreateConnectionContext context) 
    {
        // return true if start anchor belongs to an IModelElement
        SapphirePart part = getEndpoint(context.getSourceAnchor());
        if (part instanceof DiagramNodePart) 
        {
            DiagramNodePart nodePart = (DiagramNodePart)part;
            DiagramConnectionTemplate connTemplate = getConnectionTemplate(nodePart);
            if (connTemplate.canStartNewConnection(nodePart))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getCreateImageId() 
    {
        IDiagramImageChoice imageChoice = this.connDef.getToolPaletteImage().element();
        if (imageChoice != null)
        {
            return imageChoice.getImageId().getContent();
        }
        return null;
    }
    
    public IDiagramConnectionDef getConnectionDef()
    {
    	return this.connDef;
    }
    
	public int compareTo(SapphireCreateFeature o) 
	{
		if (!(o instanceof SapphireCreateNodeFeature || o instanceof SapphireCreateConnectionFeature))
		{
			throw new IllegalArgumentException();
		}
		String createName = getCreateName();
		String otherName = null;
		if (o instanceof SapphireCreateNodeFeature)
		{
			otherName = ((SapphireCreateNodeFeature)o).getCreateName();
		}
		else if (o instanceof SapphireCreateConnectionFeature)
		{
			otherName = ((SapphireCreateConnectionFeature)o).getCreateName();
		}
		return createName.compareTo(otherName);
	}
    
	public String getPaletteCompartmentId()
	{
		return getConnectionDef().getToolPaletteCompartmentId().getContent();
	}
	
    /**
     * Returns the SapphirePart belonging to the anchor, or null if not available.
     */
    private SapphirePart getEndpoint(Anchor anchor) 
    {
        if (anchor != null) 
        {
            Object obj = getBusinessObjectForPictogramElement(anchor.getParent());
            if (obj instanceof SapphirePart) 
            {
                return (SapphirePart) obj;
            }
        }
        return null;
    }
    
    private DiagramConnectionTemplate getConnectionTemplate(DiagramNodePart srcNode)
    {
        DiagramEmbeddedConnectionTemplate embeddedConn = srcNode.getDiagramNodeTemplate().getEmbeddedConnectionTemplate();
        if (embeddedConn != null && 
                embeddedConn.getConnectionId().equalsIgnoreCase(this.connDef.getId().getContent()))
        {
            return embeddedConn;
        }
        
        // check top level connections
        List<DiagramConnectionTemplate> connTemplates = this.diagramPart.getConnectionTemplates();
        for (DiagramConnectionTemplate connTemplate : connTemplates)
        {
            if (connTemplate.getConnectionId().equalsIgnoreCase(this.connDef.getId().getContent()))
            {
                return connTemplate;
            }
        }
        return null;
    }
}
