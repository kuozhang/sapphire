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

package org.eclipse.sapphire.ui;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.ui.def.FormDef;
import org.eclipse.sapphire.ui.def.PartDef;
import org.eclipse.sapphire.ui.def.WithDef;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class WithPartImplied extends FormPart
{
    private ModelPath path;
    private Element element;
    private FormDef formdef;
    
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
        
        if( def.getDefaultPage().getContent().size() > 0 )
        {
            this.formdef = def.getDefaultPage();
        }
        else
        {
            this.formdef = def.getPages().get( 0 );
        }
        
        super.init();
    }

    @Override
    protected List<SapphirePart> initChildParts()
    {
        final Element element = getLocalModelElement();
        final ListFactory<SapphirePart> partsListFactory = ListFactory.start();
        
        for( PartDef childPartDef : this.formdef.getContent() )
        {
            partsListFactory.add( create( this, element, childPartDef, this.params ) );
        }
        
        return partsListFactory.result();
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

}
