/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [341856] NPE when a diagram connection doesn't define a label
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class IdUtil 
{
    private static final String NODE_ID_SEPARATOR = "#";
    private static final String CONNECTION_ID_SEPARATOR = "&";

    public static String computeNodeId(DiagramNodePart nodePart)
    {
        StringBuffer buffer = new StringBuffer(nodePart.getNodeTypeId());
        buffer.append(NODE_ID_SEPARATOR);
        String instanceId = nodePart.getInstanceId();
        if (instanceId != null && instanceId.length() > 0)
        {
            buffer.append(nodePart.getInstanceId());
        }
        else
        {
            List<DiagramNodePart> nodeParts = nodePart.getDiagramNodeTemplate().getDiagramNodes();            
            int index = nodeParts.indexOf(nodePart);
            buffer.append(index);
        }
        return buffer.toString();
    }
    
    public static String computeConnectionId(DiagramConnectionPart connPart)
    {
        StringBuffer buffer = new StringBuffer(connPart.getConnectionTypeId());
        buffer.append(CONNECTION_ID_SEPARATOR);
        String instanceId = connPart.getInstanceId();
        if (instanceId != null && instanceId.length() > 0)
        {
            buffer.append(connPart.getInstanceId());
        }
        else
        {
            IModelElement srcNodeElement = null;
            if (connPart instanceof DiagramEmbeddedConnectionPart)
            {
                DiagramEmbeddedConnectionPart embeddedConn = (DiagramEmbeddedConnectionPart)connPart;
                srcNodeElement = embeddedConn.getSourceNodePart().getLocalModelElement();
            }
            List<DiagramConnectionPart> connParts = connPart.getDiagramConnectionTemplate().getDiagramConnections(srcNodeElement);
            int index = connParts.indexOf(connPart);
            buffer.append(index);                
        }
        return buffer.toString();        
    }

    public static DiagramNodePart getNodePart(SapphireDiagramEditorPagePart diagramPart, String nodeId)
    {
        int index = nodeId.indexOf(NODE_ID_SEPARATOR);
        if (index == -1)
        {
            return null;
        }
        String nodePartId = nodeId.substring(0, index);
        String subId = nodeId.substring(index + 1);
        int nodeIndex;
        try
        {
            nodeIndex = Integer.valueOf(subId);
        }
        catch (NumberFormatException ne)
        {
            nodeIndex = -1;
        }
        for (DiagramNodeTemplate nodeTemplate : diagramPart.getNodeTemplates())
        {
            if (nodeTemplate.getNodeTypeId().equals(nodePartId))
            {
                List<DiagramNodePart> nodeParts = nodeTemplate.getDiagramNodes();
                for (int i = 0; i < nodeParts.size(); i++)
                {
                    DiagramNodePart nodePart = nodeParts.get(i);
                    String nodeId2 = nodePart.getInstanceId();
                    if (subId != null && nodeId2 != null && subId.equals(nodeId2))
                    {
                        return nodePart;
                    }
                    else if (nodeIndex == i)
                    {
                        return nodePart;
                    }
                }
            }
        }
        return null;
    }
    
    public static DiagramConnectionPart getConnectionPart(SapphireDiagramEditorPagePart diagramPart, String connId)
    {
        int index = connId.indexOf(CONNECTION_ID_SEPARATOR);
        if (index == -1)
        {
            return null;
        }
        String connPartId = connId.substring(0, index);
        String subId = connId.substring(index + 1);
        int connIndex; 
        try 
        {
            connIndex = Integer.valueOf(subId);
        }
        catch (NumberFormatException ne)
        {
            connIndex = -1;
        }
        for (DiagramConnectionTemplate connTemplate : diagramPart.getConnectionTemplates())
        {
            if (connTemplate.getConnectionTypeId().equals(connPartId))
            {
                List<DiagramConnectionPart> connParts = connTemplate.getDiagramConnections(null);
                for (int i = 0; i < connParts.size(); i++)
                {
                    DiagramConnectionPart connPart = connParts.get(i);
                    String connId2 = connPart.getInstanceId();
                    if (subId != null && connId2 != null && subId.equals(connId2))
                    {
                        return connPart;
                    }
                    else if (i == connIndex)
                    {
                        return connPart;
                    }
                }
            }
        }
        return null;
    }
    
    public static DiagramConnectionPart getConnectionPart(DiagramNodePart nodePart, String connId)
    {
        int index = connId.indexOf(CONNECTION_ID_SEPARATOR);
        if (index == -1)
        {
            return null;
        }
        String connPartId = connId.substring(0, index);
        String subId = connId.substring(index + 1);
        int connIndex; 
        try 
        {
            connIndex = Integer.valueOf(subId);
        }
        catch (NumberFormatException ne)
        {
            connIndex = -1;
        }
        
        DiagramNodeTemplate nodeTemplate = nodePart.getDiagramNodeTemplate();
        DiagramEmbeddedConnectionTemplate connTemplate = 
            nodeTemplate.getEmbeddedConnectionTemplate();
        if (connTemplate != null && connTemplate.getConnectionTypeId().equals(connPartId))
        {
            List<DiagramConnectionPart> connParts = connTemplate.getDiagramConnections(nodePart.getLocalModelElement());
            for (int i = 0; i < connParts.size(); i++)
            {
                DiagramConnectionPart connPart = connParts.get(i);
                String connId2 = connPart.getInstanceId();
                if (subId != null && connId2 != null && subId.equals(connId2))
                {
                    return connPart;
                }
                else if (i == connIndex)
                {
                    return connPart;
                }                
            }
        }
        return null;
    }
    
}
