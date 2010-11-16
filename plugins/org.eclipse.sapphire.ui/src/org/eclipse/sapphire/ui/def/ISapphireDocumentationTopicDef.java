/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

@Label( standard = "related topic" )
@GenerateImpl

public interface ISapphireDocumentationTopicDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireDocumentationTopicDef.class );
 
    // *** label ***
    
    @Label( standard = "label" )
    @XmlBinding( path = "label" )
    @NonNullValue

    ValueProperty PROP_LABEL = new ValueProperty( TYPE, "Label" ); //$NON-NLS-1$
    
    Value<String> getLabel();
    void setLabel( String label );
    
    // *** href ***
    
    @Label( standard = "href" )
    @XmlBinding( path = "href" )
    @NonNullValue
    
    ValueProperty PROP_HREF = new ValueProperty( TYPE, "Href" ); //$NON-NLS-1$
    
    Value<String> getHref();
    void setHref( String href );
}
