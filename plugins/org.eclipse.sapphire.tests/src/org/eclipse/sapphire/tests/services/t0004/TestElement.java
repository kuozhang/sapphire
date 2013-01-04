/*******************************************************************************
 * Copyright (c) 2012 Accenture Services Pvt Ltd. and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 ******************************************************************************/

package org.eclipse.sapphire.tests.services.t0004;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public interface TestElement extends IModelElement {
    ModelElementType TYPE = new ModelElementType(TestElement.class);

    // *** Colors ***

    @PossibleValues(values = { "Red", "Green", "Blue", "Orange" })
    
    ValueProperty PROP_COLORS = new ValueProperty(TYPE, "Colors");

    Value<String> getColors();
    void setColors(String value);

    // *** Shapes ***

    @Service(impl = ShapePossibleValueService.class)
    
    ValueProperty PROP_SHAPES = new ValueProperty(TYPE, "Shapes");

    Value<String> getShapes();
    void setShapes(String value);

}
