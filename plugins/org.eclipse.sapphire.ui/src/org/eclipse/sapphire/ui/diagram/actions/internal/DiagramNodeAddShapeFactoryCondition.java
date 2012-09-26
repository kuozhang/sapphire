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

package org.eclipse.sapphire.ui.diagram.actions.internal;

import org.eclipse.sapphire.ui.SapphireCondition;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeAddShapeFactoryCondition extends SapphireCondition 
{
    @Override
    protected boolean evaluate()
    {
    	DiagramNodePart nodePart = (DiagramNodePart)getPart();
    	ShapePart shapePart = nodePart.getShapePart();
    	if (shapePart instanceof ShapeFactoryPart)
    	{
    		return true;
    	}
    	else if (shapePart instanceof ContainerShapePart)
    	{
    		ShapeFactoryPart shapeFactoryPart = ((ContainerShapePart)shapePart).getShapeFactoryPart();
    		return shapeFactoryPart != null;
    	}
        return false;
    }


}
