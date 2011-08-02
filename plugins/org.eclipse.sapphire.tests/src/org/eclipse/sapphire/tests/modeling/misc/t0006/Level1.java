package org.eclipse.sapphire.tests.modeling.misc.t0006;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;

@GenerateImpl

public interface Level1 extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( Level1.class );

    @GenerateImpl

    public interface Level2 extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Level2.class );

        @GenerateImpl

        public interface Level3 extends IModelElement
        {
            ModelElementType TYPE = new ModelElementType( Level3.class );
        }
    }

    @GenerateImpl( packageName = "org.eclipse.sapphire.tests.modeling.misc.t0006.explicit" )

    public interface Level2ExplicitPackageName extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Level2ExplicitPackageName.class );
    }
    
    @GenerateImpl( className = "Level2ExpClass" )

    public interface Level2ExplicitClassName extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Level2ExplicitClassName.class );
    }
    
    @GenerateImpl( packageName = "org.eclipse.sapphire.tests.modeling.misc.t0006.explicit", className = "Level2ExpBoth" )

    public interface Level2ExplicitBoth extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( Level2ExplicitBoth.class );
    }
    
    @GenerateImpl

    public interface ILevel2 extends IModelElement
    {
        ModelElementType TYPE = new ModelElementType( ILevel2.class );

        @GenerateImpl

        public interface ILevel3 extends IModelElement
        {
            ModelElementType TYPE = new ModelElementType( ILevel3.class );
        }
    }

}