/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [348811] Eliminate separate Sapphire.Diagram.Part.Delete action
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.editor.DiagramImplicitConnectionPart;
import org.eclipse.sapphire.ui.swt.graphiti.providers.SapphireDiagramFeatureProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDeleteFeature extends DefaultDeleteFeature 
{
    private static final String DELETE_ACTION_ID = "Sapphire.Delete";
    private boolean doneChanges = false;
    
    public SapphireDeleteFeature(IFeatureProvider fp)
    {
        super(fp);
    }
    
    @Override
    public boolean canDelete(IDeleteContext context) 
    {
        PictogramElement pe = context.getPictogramElement();
        Object bo = this.getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramImplicitConnectionPart)
        {
            return false;
        }
        return true;
    }
        
    /**
     * Copied from DefaultDeleteFeature mainly because we don't want to remove the PE here. 
     * We want to remove the PE after the BO is removed using the model listening mechanism. 
     * BO is deleted using sapphire diagram delete action which requires a sapphire rendering context. 
     * Removing the PE first would remove the rendering context cached at the solver.
     * In order to be consistent with the delete action that can be invoked from context menu,
     * we suppress user feedback as well. If feedback is needed, we need to provide at the sapphire
     * delete action.
     */
    
    public void delete(IDeleteContext context) 
    {
        PictogramElement pe = context.getPictogramElement();
        Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
        setDoneChanges(true);

        preDelete(context);

        deleteBusinessObjects(businessObjectsForPictogramElement);

        postDelete(context);
    }

    
    @Override
    protected void deleteBusinessObject(Object bo) 
    {
        if (bo instanceof ISapphirePart)
        {
            final ISapphirePart part = (ISapphirePart)bo;
            SapphireRenderingContext sapphireContext = 
                    ((SapphireDiagramFeatureProvider)this.getFeatureProvider()).getRenderingContext(part);
            SapphireActionHandler deleteActionHandler = part.getAction(DELETE_ACTION_ID).getFirstActiveHandler();
            deleteActionHandler.execute(sapphireContext);
//            if (!(connPart instanceof DiagramImplicitConnectionPart))
//            {
//                final IModelElement element = connPart.getLocalModelElement();
//                final ModelElementList<?> list = (ModelElementList<?>) element.parent();
//                list.remove(element);
//            }
        }
    }
    
    @Override
    public boolean hasDoneChanges() 
    {
        return doneChanges;
    }

    protected void setDoneChanges(boolean doneChanges) 
    {
        this.doneChanges = doneChanges;
    }
    
    
}
