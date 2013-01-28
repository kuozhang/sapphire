/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.xml.internal;

import org.eclipse.sapphire.ConversionService;
import org.eclipse.sapphire.modeling.ModelElement;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.w3c.dom.Element;

/**
 * ConversionService implementation for ModelElement to DOM Element conversions.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ModelElementToDomElementConversionService extends ConversionService<ModelElement,Element>
{
    public ModelElementToDomElementConversionService()
    {
        super( ModelElement.class, Element.class );
    }

    @Override
    public Element convert( final ModelElement element )
    {
        final Resource resource = element.resource();
        
        if( resource instanceof XmlResource )
        {
            final XmlElement xmlElement = ( (XmlResource) resource ).getXmlElement();
            
            if( xmlElement != null )
            {
                return xmlElement.getDomNode();
            }
        }
        
        return null;
    }
    
}
