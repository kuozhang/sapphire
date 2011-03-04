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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.Color;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionBindingDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPageDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

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
		
		IDiagramConnectionBindingDef connBinding = (IDiagramConnectionBindingDef)connectionPart.getDefinition();
		IDiagramConnectionDef connDef = getDiagramConnectionDef(connBinding);
		IColorConstant linkColor = getLinkColor(connDef);
		LineStyle linkStyle = getLinkStyle(connDef);

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		// CONNECTION WITH POLYLINE
		Connection connection = peCreateService.createFreeFormConnection(getDiagram());
		connection.setStart(addConContext.getSourceAnchor());
		connection.setEnd(addConContext.getTargetAnchor());

		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(connection);
		
		polyline.setForeground(manageColor(linkColor));
		polyline.setLineWidth(connDef != null ? connDef.getHint("width", 1) : 1);
		polyline.setLineStyle(linkStyle);
       
		// create link and wire it
		link(connection, connectionPart);

		// add dynamic text decorator for the reference name
		ConnectionDecorator textDecorator = peCreateService.createConnectionDecorator(connection, true, 0.5, true);
		Text text = gaService.createDefaultText(textDecorator);
		text.setForeground(manageColor(linkColor));
		gaService.setLocation(text, 10, 0);		
		
		text.setValue(connectionPart.getLabel());

		// add static graphical decorators (composition and navigable)
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		createArrow(cd, linkColor);

        // provide information to support direct-editing directly 

        // after object creation (must be activated additionally)
        IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();

        // set container shape for direct editing after object creation
        directEditingInfo.setMainPictogramElement(connection);

        // set shape and graphics algorithm where the editor for
        // direct editing shall be opened after object creation
        directEditingInfo.setPictogramElement(textDecorator);
        directEditingInfo.setGraphicsAlgorithm(text);
		
		return connection;
	}

	private Polygon createArrow(GraphicsAlgorithmContainer gaContainer, IColorConstant color) 
	{
		Polygon polygon = Graphiti.getGaCreateService().createPolygon(gaContainer, new int[] { -8, 4, 0, 0, -8, -4, -5, 0 });
		polygon.setBackground(manageColor(color));
		polygon.setFilled(true);
		return polygon;
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
	
	private IDiagramConnectionDef getDiagramConnectionDef(IDiagramConnectionBindingDef connBindingDef)
	{
		IDiagramPageDef pageDef = connBindingDef.nearest(IDiagramPageDef.class);
		ModelElementList<IDiagramConnectionDef> connDefs = pageDef.getDiagramConnectionDefs();
		for (IDiagramConnectionDef connDef : connDefs)
		{
			if (connDef.getId().getContent().equalsIgnoreCase(connBindingDef.getId().getContent()))
			{
				return connDef;
			}
		}
		return null;
	}
}
