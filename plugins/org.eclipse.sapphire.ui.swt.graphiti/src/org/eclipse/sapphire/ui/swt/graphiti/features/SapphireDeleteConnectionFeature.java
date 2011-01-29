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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDeleteConnectionFeature extends DefaultDeleteFeature 
{
	private boolean shouldDelete;
	
	public SapphireDeleteConnectionFeature(IFeatureProvider fp)
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
		if (bo instanceof DiagramConnectionPart) 
		{
			final DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
			final IModelElement element = connPart.getLocalModelElement();
			final ModelElementList<?> list = (ModelElementList<?>) element.parent();
			list.remove(element);						
		}
	}
	
	@Override
	public boolean hasDoneChanges() 
	{
		return this.shouldDelete;
	}
}
