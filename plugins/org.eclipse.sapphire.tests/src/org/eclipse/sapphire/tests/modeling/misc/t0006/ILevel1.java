package org.eclipse.sapphire.tests.modeling.misc.t0006;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;

@GenerateImpl

public interface ILevel1 extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( ILevel1.class );

}