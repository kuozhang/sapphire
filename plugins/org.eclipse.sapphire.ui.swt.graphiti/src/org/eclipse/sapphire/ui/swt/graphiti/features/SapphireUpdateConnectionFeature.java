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
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireUpdateConnectionFeature extends AbstractUpdateFeature 
{
    public SapphireUpdateConnectionFeature(IFeatureProvider fp)
    {
        super(fp);
    }
    
    public boolean canUpdate(IUpdateContext context) 
    {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        return bo instanceof DiagramConnectionPart;
    }

    public IReason updateNeeded(IUpdateContext context) 
    {
        // retrieve name from pictogram model
        String pictogramName = null;
        PictogramElement pictogramElement = context.getPictogramElement();
        if (pictogramElement instanceof Connection) {
            Connection c = (Connection) pictogramElement;
            for (ConnectionDecorator cd : c.getConnectionDecorators()) {
                if (cd.getGraphicsAlgorithm() instanceof Text) {
                    Text text = (Text) cd.getGraphicsAlgorithm();
                    pictogramName = text.getValue();
                }
            }
        }

        // retrieve name from business model
        String businessName = null;
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof DiagramConnectionPart) 
        {
            DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
            businessName = connPart.getLabel();
        }

        // update needed, if names are different
        boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
                .equals(businessName)));
        if (updateNameNeeded) 
        {
            return Reason.createTrueReason("Name is out of date"); //$NON-NLS-1$
        } 
        else
        {
            return Reason.createFalseReason();
        }
    }

    public boolean update(IUpdateContext context) 
    {
        String businessName = null;
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof DiagramConnectionPart) 
        {
            DiagramConnectionPart connPart = (DiagramConnectionPart)bo;
            businessName = connPart.getLabel();
        }
        
        // Set name in pictogram model
        if (pe instanceof Connection) 
        {
            Connection c = (Connection) pe;
            for (ConnectionDecorator cd : c.getConnectionDecorators()) 
            {
                if (cd.getGraphicsAlgorithm() instanceof Text)
                {
                    Text text = (Text) cd.getGraphicsAlgorithm();
                    text.setValue(businessName);
                    return true;
                }
            }
        }
        
        return false;
    }

}
