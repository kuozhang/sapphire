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

package org.eclipse.sapphire.samples.jee;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface DescribableExt extends Describable
{
    ModelElementType TYPE = new ModelElementType( DescribableExt.class );
    
    // *** DisplayName ***
    
    @Label( standard = "display name" )
    @XmlBinding( path = "display-name" )
    
    @Documentation
    (
        content = "A short name that is intended to be displayed by tools. The display name need not be uniqueu."
    )
    
    ValueProperty PROP_DISPLAY_NAME = new ValueProperty( TYPE, "DisplayName" );
    
    Value<String> getDisplayName();
    void setDisplayName( String value );
    
    // *** SmallIcon ***
    
    @Type( base = Path.class )
    @Label( standard = "small icon" )
    @XmlBinding( path = "icon/small-icon" )
    
    @Documentation
    (
        content = "The path to a file containing a small (16x16) icon image. The path is relative to the root of " +
                  "the module. The image must be either in JPEG or GIF format. The icon is intended to be used by tools."
    )
    
    ValueProperty PROP_SMALL_ICON = new ValueProperty( TYPE, "SmallIcon" );
    
    Value<Path> getSmallIcon();
    void setSmallIcon( String value );
    void setSmallIcon( Path value );
    
    // *** LargeIcon ***
    
    @Type( base = Path.class )
    @Label( standard = "large icon" )
    @XmlBinding( path = "icon/large-icon" )
    
    @Documentation
    (
        content = "The path to a file containing a large (32x32) icon image. The path is relative to the root of " +
                  "the module. The image must be either in JPEG or GIF format. The icon is intended to be used by tools."
    )
    
    ValueProperty PROP_LARGE_ICON = new ValueProperty( TYPE, "LargeIcon" );
    
    Value<Path> getLargeIcon();
    void setLargeIcon( String value );
    void setLargeIcon( Path value );
    
}
