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

package org.eclipse.sapphire.ui.diagram.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@GenerateImpl

public interface IDiagramImageChoice 

    extends IModelElement 
    
{
    ModelElementType TYPE = new ModelElementType( IDiagramImageChoice.class );
    
    // *** ImageId ***
    
    @Label( standard = "image ID" )
    @Required
    @XmlBinding( path = "id" )
    
    ValueProperty PROP_IMAGE_ID = new ValueProperty( TYPE, "ImageId" );
    
    Value<String> getImageId();
    void setImageId( String Id );

    // *** ImagePath ***
    
    @Label( standard = "image path" )
    @Required
    @XmlBinding( path = "path" )
    
    ValueProperty PROP_IMAGE_PATH = new ValueProperty( TYPE, "ImagePath" );
    
    Value<String> getImagePath();
    void setImagePath( String path );
    
}
