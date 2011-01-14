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

package org.eclipse.sapphire.ui.diagram.graphiti.features;

import java.util.Iterator;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDeleteNodeFeature extends DefaultDeleteFeature 
{
	private boolean shouldDelete;
	
	public SapphireDeleteNodeFeature(IFeatureProvider fp)
	{
		super(fp);
	}
	
	@Override
	protected boolean getUserDecision() 
	{
		this.shouldDelete = super.getUserDecision();
		return this.shouldDelete;
	}
	
	@Override
	public void preDelete(IDeleteContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Shape)
		{
			Shape shape = (Shape)pe;
		
			for (Iterator<Anchor> iter = shape.getAnchors().iterator(); iter.hasNext();) 
			{
				Anchor anchor = iter.next();
				
				Iterator<Connection> connIt = Graphiti.getPeService().getAllConnections(anchor).iterator();
				while (connIt.hasNext())
				{
					Connection connection = connIt.next();				
					IDeleteContext dc = new DeleteContext(connection);
					SapphireInternalDeleteConnectionFeature df = new SapphireInternalDeleteConnectionFeature(getFeatureProvider());
					df.delete(dc);
				}
			}
		}		
	}
	
	@Override
	public void delete(IDeleteContext context) 
	{
		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
		if (businessObjectsForPictogramElement != null && businessObjectsForPictogramElement.length > 0) 
		{
			if (!getUserDecision()) 
			{
				return;
			}			
		}
		
		preDelete(context);
		deleteBusinessObjects(businessObjectsForPictogramElement);
		postDelete(context);
	}
	
	@Override
	protected void deleteBusinessObject(Object bo) 
	{
		if (bo instanceof DiagramNodePart)
		{
			DiagramNodePart nodePart = (DiagramNodePart)bo;
			IModelElement element = nodePart.getLocalModelElement();
			ModelElementList<?> list = (ModelElementList<?>) element.parent();
			list.remove(element);			
		}
	}
	
	@Override
	public boolean hasDoneChanges() 
	{
		return this.shouldDelete;
	}
	
}
