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
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface PossibleValuesGallery extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( PossibleValuesGallery.class );
    
    // *** Color ***
    
    @Label( standard = "color" )
    @XmlBinding( path = "color" )
    
    @PossibleValues
    (
        values =
        {
            "Red",
            "Blue",
            "Green",
            "Yellow",
            "Orange",
            "Violet"
        }
    )
    
    ValueProperty PROP_COLOR = new ValueProperty( TYPE, "Color" );
    
    Value<String> getColor();
    void setColor( String value );
    
    // *** Shape ***
    
    @Label( standard = "shape" )
    @XmlBinding( path = "shape" )
    
    @PossibleValues
    (
        values =
        {
            "Circle",
            "Rectangle",
            "Square",
            "Triangle"
        },
        invalidValueSeverity = Severity.WARNING
    )
    
    ValueProperty PROP_SHAPE = new ValueProperty( TYPE, "Shape" );
    
    Value<String> getShape();
    void setShape( String value );
    
}
