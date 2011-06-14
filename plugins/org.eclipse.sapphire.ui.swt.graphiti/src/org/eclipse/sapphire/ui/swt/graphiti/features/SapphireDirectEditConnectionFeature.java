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
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDirectEditConnectionFeature extends AbstractDirectEditingFeature 
{
    public SapphireDirectEditConnectionFeature(IFeatureProvider fp)
    {
        super(fp);
    }
    
    public int getEditingType() 
    {
        return TYPE_TEXT;
    }

    @Override
    public boolean canDirectEdit(IDirectEditingContext context) 
    {
        PictogramElement pe = (PictogramElement)context.getPictogramElement().eContainer();
        Object bo = getBusinessObjectForPictogramElement(pe);
        // support direct editing, if it is a DiagramConnectionPart, and the 
        // DiagramConnectionPart contains editable label
        if (bo instanceof DiagramConnectionPart && 
                ((DiagramConnectionPart)bo).canEditLabel()) 
        {
            return true;
        }
        // direct editing not supported in all other cases
        return false;
    }
    
    public String getInitialValue(IDirectEditingContext context) 
    {
        PictogramElement pe = (PictogramElement)context.getPictogramElement().eContainer();
        Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramConnectionPart)
        {
            DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
            return connPart.getLabel();
        }
        return null;
    }

    public void setValue(String value, IDirectEditingContext context) 
    {
        PictogramElement pe = (PictogramElement)context.getPictogramElement().eContainer();
        Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramConnectionPart)
        {
            DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
            connPart.setLabel(value);
            updatePictogramElement(pe);
        }
    }

}
