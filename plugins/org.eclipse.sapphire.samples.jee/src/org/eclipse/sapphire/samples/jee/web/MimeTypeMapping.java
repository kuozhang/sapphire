/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.jee.web;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "MIME type mapping" )

@Documentation
(
    content = "MIME type mappings tell the container how to set the content type when serving files with unrecognized " +
              "extensions."
)

@GenerateImpl

public interface MimeTypeMapping extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( MimeTypeMapping.class );
    
    // *** FileExtension ***
    
    @Label( standard = "file extension" )
    @Required
    @XmlBinding( path = "extension" )
    
    ValueProperty PROP_FILE_EXTENSION = new ValueProperty( TYPE, "FileExtension" );
    
    Value<String> getFileExtension();
    void setFileExtension( String value );
    
    // *** MimeType ***
    
    @Label(standard = "MIME Type")
    @Required
    @XmlBinding( path = "mime-type" )
    
    ValueProperty PROP_MIME_TYPE = new ValueProperty( TYPE, "MimeType" );
    
    Value<String> getMimeType();
    void setMimeType( String value );

}
