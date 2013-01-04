/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.gallery;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface AncestorAccessGalleryLevel3 extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( AncestorAccessGalleryLevel3.class );
    
    // *** Text ***
    
    @XmlBinding( path = "text" )
    @Label( standard = "level 3 text" )
    
    ValueProperty PROP_TEXT = new ValueProperty( TYPE, "Text" );
    
    Value<String> getText();
    void setText( String value );
    
}
