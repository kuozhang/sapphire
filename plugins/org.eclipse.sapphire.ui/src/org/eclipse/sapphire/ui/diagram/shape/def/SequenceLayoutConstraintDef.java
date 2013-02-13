/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@Label( standard = "sequence layout constraint" )

public interface SequenceLayoutConstraintDef extends LayoutConstraintDef 
{
	ModelElementType TYPE = new ModelElementType( SequenceLayoutConstraintDef.class );
	
    // *** MinWidth ***
    
    @Type( base = Integer.class )
    @Label( standard = "minimum width" )
    @XmlBinding( path = "min-width" )
    @DefaultValue( text = "-1" )
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_MIN_WIDTH = new ValueProperty( TYPE, "MinWidth" );
    
    Value<Integer> getMinWidth();
    void setMinWidth( String value );
    void setMinWidth( Integer value );
	
    // *** MinHeight ***
    
    @Type( base = Integer.class )
    @Label( standard = "minimum height" )
    @XmlBinding( path = "min-height" )
    @DefaultValue( text = "-1" )
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_MIN_HEIGHT = new ValueProperty( TYPE, "MinHeight" );
    
    Value<Integer> getMinHeight();
    void setMinHeight( String value );
    void setMinHeight( Integer value );
	
    // *** MaxWidth ***
    
    @Type( base = Integer.class )
    @Label( standard = "maximum width" )
    @XmlBinding( path = "max-width" )
    @DefaultValue( text = "-1" )
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_MAX_WIDTH = new ValueProperty( TYPE, "MaxWidth" );
    
    Value<Integer> getMaxWidth();
    void setMaxWidth( String value );
    void setMaxWidth( Integer value );
	
    // *** MaxHeight ***
    
    @Type( base = Integer.class )
    @Label( standard = "maximum height" )
    @XmlBinding( path = "max-height" )
    @DefaultValue( text = "-1" )
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_MAX_HEIGHT = new ValueProperty( TYPE, "MaxHeight" );
    
    Value<Integer> getMaxHeight();
    void setMaxHeight( String value );
    void setMaxHeight( Integer value );
    
    // *** ExpandCellHorizontally ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "expand-cell-horizontally" )
    @DefaultValue( text = "false" )
    @Label( standard = "expand cell horizontally")
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_EXPAND_CELL_HORIZONTALLY = new ValueProperty(TYPE, "ExpandCellHorizontally");
    
    Value<Boolean> isExpandCellHorizontally();
    void setExpandCellHorizontally( String value );
    void setExpandCellHorizontally( Boolean value );
    
    // *** ExpandCellVertically ***
    
    @Type( base = Boolean.class )
    @XmlBinding( path = "expand-cell-vertically" )
    @DefaultValue( text = "false" )
    @Label( standard = "expand cell vertically")
    @Enablement( expr = "${InstanceOf(parent().parent(), 'org.eclipse.sapphire.ui.diagram.shape.def.ContainerShapeDef') && parent().parent().Layout.Orientation != 'stacked'}" )
    
    ValueProperty PROP_EXPAND_CELL_VERTICALLY = new ValueProperty(TYPE, "ExpandCellVertically");
    
    Value<Boolean> isExpandCellVertically();
    void setExpandCellVertically( String value );
    void setExpandCellVertically( Boolean value );
        
}
