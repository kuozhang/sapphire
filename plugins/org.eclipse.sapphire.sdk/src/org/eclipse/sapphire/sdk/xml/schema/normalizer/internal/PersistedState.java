/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.sdk.xml.schema.normalizer.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.Exclusion;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.RootElement;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.TypeSubstitution;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface PersistedState extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( PersistedState.class );
    
    // *** RootElements ***
    
    @Type( base = RootElement.class )
    
    ListProperty PROP_ROOT_ELEMENTS = new ListProperty( TYPE, "RootElements" );
    
    ModelElementList<RootElement> getRootElements();
    
    // *** Exclusions ***
    
    @Type( base = Exclusion.class )
    
    ListProperty PROP_EXCLUSIONS = new ListProperty( TYPE, "Exclusions" );
    
    ModelElementList<Exclusion> getExclusions();
    
    // *** TypeSubstitutions ***

    @Type( base = TypeSubstitution.class )
    
    ListProperty PROP_TYPE_SUBSTITUTIONS = new ListProperty( TYPE, "TypeSubstitutions" );
    
    ModelElementList<TypeSubstitution> getTypeSubstitutions();
    
    // *** SortSequenceContent ***
    
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    
    ValueProperty PROP_SORT_SEQUENCE_CONTENT = new ValueProperty( TYPE, "SortSequenceContent" );
    
    Value<Boolean> getSortSequenceContent();
    void setSortSequenceContent( String value );
    void setSortSequenceContent( Boolean value );

}
