/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.forms;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.ui.forms.swt.presentation.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.presentation.SwtPresentation;
import org.eclipse.sapphire.ui.forms.swt.presentation.internal.FormPresentation;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class WithImpliedPart extends ContainerPart<FormComponentPart>
{
    private ModelPath path;
    private Element element;
    
    @Override
    protected void init()
    {
        final WithDef def = (WithDef) this.definition;
        
        this.path = new ModelPath( substituteParams( def.getPath().text() ) );
        
        final Property property = getModelElement().property( this.path );
        
        if( property == null )
        {
            throw new IllegalStateException();
        }
        
        this.element = ( (ElementHandle<?>) property ).content();
    }
    
    protected Children initChildren()
    {
        return new Children()
        {
            @Override
            protected void init( final ListFactory<FormComponentPart> childPartsListFactory )
            {
                final Element element = getLocalModelElement();
                final WithDef def = definition();
                final FormDef formdef;
                
                if( def.getDefaultPage().getContent().size() > 0 )
                {
                    formdef = def.getDefaultPage();
                }
                else
                {
                    formdef = def.getPages().get( 0 );
                }
                
                for( final FormComponentDef childPartDef : formdef.getContent() )
                {
                    childPartsListFactory.add( (FormComponentPart) create( WithImpliedPart.this, element, childPartDef, WithImpliedPart.this.params ) );
                }
            }
        };
    }

    @Override
    public WithDef definition()
    {
        return (WithDef) super.definition();
    }
    
    public ModelPath getPath()
    {
        return this.path;
    }
    
    @Override
    public Element getLocalModelElement()
    {
        return this.element;
    }
    
    @Override
    public FormComponentPresentation createPresentation( final SwtPresentation parent, final Composite composite )
    {
        return new FormPresentation( this, parent, composite );
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        this.path = null;
        this.element = null;
    }

}
