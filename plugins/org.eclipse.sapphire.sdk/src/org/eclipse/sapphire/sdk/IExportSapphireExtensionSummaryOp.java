/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.sdk;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.sdk.internal.ExportSapphireExtensionSummaryOpMethods;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IExportSapphireExtensionSummaryOp

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( IExportSapphireExtensionSummaryOp.class );
    
    // *** DocumentTitle ***
    
    @Label( standard = "document title" )
    @DefaultValue( text = "Sapphire Extensions" )
    
    ValueProperty PROP_DOCUMENT_TITLE = new ValueProperty( TYPE, "DocumentTitle" );
    
    Value<String> getDocumentTitle();
    void setDocumentTitle( String value );
    
    // *** DocumentBodyTitle ***
    
    @Label( standard = "document body title" )
    
    ValueProperty PROP_DOCUMENT_BODY_TITLE = new ValueProperty( TYPE, "DocumentBodyTitle" );
    
    Value<String> getDocumentBodyTitle();
    void setDocumentBodyTitle( String value );
    
    // *** EmbedDefaultStyle ***
    
    @Type( base = Boolean.class )
    @Label( standard = "embed default style" )
    @DefaultValue( text = "true" )
    
    ValueProperty PROP_EMBED_DEFAULT_STYLE = new ValueProperty( TYPE, "EmbedDefaultStyle" );
    
    Value<Boolean> getEmbedDefaultStyle();
    void setEmbedDefaultStyle( String value );
    void setEmbedDefaultStyle( Boolean value );
    
    // *** Method: execute ***
    
    @DelegateImplementation( ExportSapphireExtensionSummaryOpMethods.class )
    
    String execute( List<ISapphireExtensionDef> extensions,
                    IProgressMonitor monitor );
    
}
