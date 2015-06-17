/******************************************************************************
 * Copyright (c) 2015 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - fixes to case lookup logic
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.services.PossibleTypesService;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class DiagramNodeAddShapeFactory extends SapphireActionHandlerFactory 
{
	@Override
    public void init( final SapphireAction action,
                      final ActionHandlerFactoryDef def )
    {
        super.init( action, def );
    }
	
	@Override
	public List<SapphireActionHandler> create() 
	{
        final ListFactory<SapphireActionHandler> handlers = ListFactory.start();
        ShapePart shapePart = null;
        DiagramNodePart nodePart = null;
        if (getPart() instanceof DiagramNodePart)
        {
        	nodePart = (DiagramNodePart)getPart();
        	shapePart = nodePart.getShapePart();
        }
        else if (getPart() instanceof ShapePart )
        {
        	shapePart = (ShapePart)getPart();
        	nodePart = shapePart.getNodePart();
        }
        List<ShapeFactoryPart> shapeFactories = new ArrayList<ShapeFactoryPart>();
        if (shapePart instanceof ShapeFactoryPart)
        {
        	shapeFactories.add((ShapeFactoryPart)shapePart);
        }
        else if (shapePart instanceof ContainerShapePart)
        {
        	shapeFactories.addAll(((ContainerShapePart)shapePart).getShapeFactoryParts());
        }
        for (ShapeFactoryPart shapeFactoryPart : shapeFactories)
        {
            for( ElementType type : shapeFactoryPart.getModelElementList().service( PossibleTypesService.class ).types() )
            {
                final DiagramNodeAddShapeActionHandler handler = new DiagramNodeAddShapeActionHandler( nodePart, shapeFactoryPart, type );
                // add by tds
                String qname = type.getQualifiedName();
                if (qname.equals("com.chanjet.csp.ide.project.ui.bo.PrimaryEO")) {
                	continue;
                }
                //
                handlers.add(handler);
                // add by tds
                if (qname.equals("com.chanjet.csp.ide.project.ui.bo.Field")) {
                	final DiagramNodeAddShapeActionHandler shandler = new DiagramNodeAddShapeActionHandler( nodePart, shapeFactoryPart, type );
                	shandler.setCspParams("SystemField");
                	handlers.add(shandler);
                	
                	final DiagramNodeAddShapeActionHandler cshandler = new DiagramNodeAddShapeActionHandler( nodePart, shapeFactoryPart, type );
                	cshandler.setCspParams("VirtualField");
                	handlers.add(cshandler);
                	
                	final DiagramNodeAddShapeActionHandler fshandler = new DiagramNodeAddShapeActionHandler( nodePart, shapeFactoryPart, type );
                    fshandler.setCspParams("FormulaField");
                    handlers.add(fshandler);
                } else if (qname.equals("com.chanjet.csp.ide.project.ui.entity.Field")) {
                	final DiagramNodeAddShapeActionHandler shandler = new DiagramNodeAddShapeActionHandler( nodePart, shapeFactoryPart, type );
                	shandler.setCspParams("ForeignKeyField");
                	handlers.add(shandler);
                }
                //
            }
        }
        
		return handlers.result();
	}

}
