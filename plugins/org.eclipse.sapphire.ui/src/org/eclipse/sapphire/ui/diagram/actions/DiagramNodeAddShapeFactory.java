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

package org.eclipse.sapphire.ui.diagram.actions;

import java.util.List;

import org.eclipse.sapphire.java.JavaType;
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
        DiagramNodePart nodePart = (DiagramNodePart)getPart();
        ShapePart shapePart = nodePart.getShapePart();
        ShapeFactoryPart shapeFactoryPart = null;
        if (shapePart instanceof ShapeFactoryPart)
        {
        	shapeFactoryPart = (ShapeFactoryPart)shapePart;
        }
        else if (shapePart instanceof ContainerShapePart)
        {
        	shapeFactoryPart = ((ContainerShapePart)shapePart).getShapeFactoryPart();
        }
        if (shapeFactoryPart != null)
        {
        	for (JavaType javaType : shapeFactoryPart.getSupportedTypes())
        	{
        		final DiagramNodeAddShapeActionHandler handler = 
        				new DiagramNodeAddShapeActionHandler(nodePart, shapeFactoryPart, javaType);
        		handlers.add(handler);
        	}
        }
        
		return handlers.result();
	}

}
