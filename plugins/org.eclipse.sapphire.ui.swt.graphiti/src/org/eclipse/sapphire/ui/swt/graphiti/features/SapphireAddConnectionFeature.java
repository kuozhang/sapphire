/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.ILayoutService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.internal.services.GraphitiUiInternal;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.def.ConnectionEndpointType;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.swt.graphiti.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireAddConnectionFeature extends AbstractAddFeature 
{
    private static final IColorConstant DEFAULT_LINK_COLOR = new ColorConstant(51, 51, 153);
    
    public SapphireAddConnectionFeature(IFeatureProvider fp)
    {
        super(fp);
    }
    
    public boolean canAdd(IAddContext context) 
    {
        // return true if given business object is an DiagramConnectionPart
        // note, that the context must be an instance of IAddConnectionContext
        if (context instanceof IAddConnectionContext && 
                context.getNewObject() instanceof DiagramConnectionPart) 
        {
            return true;
        }
        return false;
    }

    public PictogramElement add(IAddContext context) 
    {
        IAddConnectionContext addConContext = (IAddConnectionContext) context;
        DiagramConnectionPart connectionPart = (DiagramConnectionPart) context.getNewObject();
        
        IDiagramConnectionDef connDef = connectionPart.getConnectionDef();
        IColorConstant linkColor = getLinkColor(connDef);
        LineStyle linkStyle = getLinkStyle(connDef);

        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        // CONNECTION WITH POLYLINE
        Connection connection = peCreateService.createFreeFormConnection(getDiagram());
        connection.setStart(addConContext.getSourceAnchor());
        connection.setEnd(addConContext.getTargetAnchor());
        
        if (connectionPart.getConnectionBendpoints().isEmpty())
        {
        	Point bendPoint = SapphireConnectionRouter.getInstance().route((FreeFormConnection)connection);
        	if (bendPoint != null)
        	{
        		((FreeFormConnection)connection).getBendpoints().add(Graphiti.getCreateService().createPoint(bendPoint.x, bendPoint.y));
        		connectionPart.addBendpoint(0, bendPoint.x, bendPoint.y);
        	}
        }
        else
        {
        	SapphireConnectionRouter.getInstance().addConnection((FreeFormConnection)connection);
        }

        IGaService gaService = Graphiti.getGaService();
        Polyline polyline = gaService.createPolyline(connection);
        
        polyline.setForeground(manageColor(linkColor));
        polyline.setLineWidth(connDef.getLineWidth().getContent());
        polyline.setLineStyle(linkStyle);
       
        // create link and wire it
        link(connection, connectionPart);

        // add dynamic text decorator for the reference name
        ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
        Diagram diagram = (Diagram)context.getTargetContainer();
        //Text text = gaService.createDefaultText(diagram, textDecorator, connectionPart.getLabel());
        Text text = TextUtil.createDefaultText(diagram, textDecorator, connectionPart.getLabel());

        text.setForeground(manageColor(linkColor));
        if (connectionPart.getLabelPosition() != null)
        {        	
        	gaService.setLocation(text, connectionPart.getLabelPosition().getX(), connectionPart.getLabelPosition().getY());  
        }
        else
        {
        	Point labelPos = getTextLocation(connection);
        	gaService.setLocation(text, labelPos.x, labelPos.y);
        }
        
        // add static graphical decorators (composition and navigable)
        createEndpointDecorator(connection, connDef.getEndpoint1(), linkColor, true);
        createEndpointDecorator(connection, connDef.getEndpoint2(), linkColor, false);

        // provide information to support direct-editing directly 

        // after object creation (must be activated additionally)
        IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();

        // set container shape for direct editing after object creation
        directEditingInfo.setMainPictogramElement(textDecorator);

        // set shape and graphics algorithm where the editor for
        // direct editing shall be opened after object creation
        directEditingInfo.setPictogramElement(textDecorator);
        directEditingInfo.setGraphicsAlgorithm(text);
        
        // Create a rendering context for the connection
        DiagramRenderingContext renderingCtx = new DiagramRenderingContext(
                        connectionPart, 
                        (SapphireDiagramEditor)getDiagramEditor(), 
                        connection);
        SapphireDiagramFeatureProvider sfp = (SapphireDiagramFeatureProvider)getFeatureProvider();
        sfp.addRenderingContext(connectionPart, renderingCtx);
        
        return connection;
    }

    private ConnectionDecorator createEndpointDecorator(Connection connection, IDiagramConnectionEndpointDef endpointDef, 
                        IColorConstant color, boolean begin)
    {
        IPeCreateService peCreateService = Graphiti.getPeCreateService();
        ConnectionDecorator cd = null;
        if (begin)
        {
            cd = peCreateService.createConnectionDecorator(connection, false, 0, true);
        }
        else
        {
            cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
        }
        if (endpointDef.getType().getContent() == ConnectionEndpointType.ARROW)
        {                        
            Polygon polygon = Graphiti.getGaCreateService().createPolygon(cd, new int[] { -8, 5, 0, 0, -8, -5 });
            polygon.setBackground(manageColor(color));
            polygon.setFilled(true);
        }
        else if (endpointDef.getType().getContent() == ConnectionEndpointType.CIRCLE)
        {
            Ellipse ellipse = Graphiti.getGaCreateService().createEllipse(cd);
            ellipse.setHeight(8);
            ellipse.setWidth(8);
            ellipse.setBackground(manageColor(color));
            ellipse.setFilled(true);            
        }
        else if (endpointDef.getType().getContent() == ConnectionEndpointType.ELLIPSE)
        {
            Ellipse ellipse = Graphiti.getGaCreateService().createEllipse(cd);
            ellipse.setHeight(6);
            ellipse.setWidth(10);
            ellipse.setBackground(manageColor(color));
            ellipse.setFilled(true);            
        }
        return cd;
    }
    
    private IColorConstant getLinkColor(IDiagramConnectionDef def)
    {
        IColorConstant linkColor = DEFAULT_LINK_COLOR;
        if (def != null)
        {
            Color color = def.getLineColor().getContent();
            if (color != null)
            {
                linkColor = new ColorConstant(color.getRed(), color.getGreen(), color.getBlue());
            }
        }
        return linkColor;        
    }
    
    private LineStyle getLinkStyle(IDiagramConnectionDef def)
    {    
        LineStyle linkStyle = LineStyle.SOLID;
        if (def != null)
        {
            org.eclipse.sapphire.ui.LineStyle style = def.getLineStyle().getContent();
            if (style == org.eclipse.sapphire.ui.LineStyle.DASH )
            {
                linkStyle = LineStyle.DASH;
            }
            else if (style == org.eclipse.sapphire.ui.LineStyle.DOT)
            {
                linkStyle = LineStyle.DOT;
            }
            else if (style == org.eclipse.sapphire.ui.LineStyle.DASH_DOT)
            {
                linkStyle = LineStyle.DASHDOT;
            }
        }            
        return linkStyle;
    }
    
    private Point getTextLocation(Connection conn)
    {
		Anchor endAnchor = conn.getEnd();
		final ILayoutService layoutService = Graphiti.getLayoutService();
		ILocation endLocation = layoutService.getLocationRelativeToDiagram(endAnchor);
		
		Point endPoint = new Point(endLocation.getX(), endLocation.getY());
		Point midPoint = GraphitiUiInternal.getGefService().getConnectionPointAt(conn, 0.5);
		
		if (midPoint != null)
		{
			int deltaX, deltaY;
			
			if (Math.signum(endPoint.x - midPoint.x) == Math.signum(endPoint.y - midPoint.y))
			{
				deltaX = 3;
				deltaY = -10;
			}
			else
			{
				deltaX = 3;
				deltaY = 0;
			}
	    	return new Point(deltaX, deltaY);
		}
		return null;
    }
    
}
