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

package org.eclipse.sapphire.ui.listeners;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PropertyEditorListener
{
    private SapphireRenderingContext context = null;
    private Element element = null;
    private PropertyDef property = null;
    
    public final void initialize( final SapphireRenderingContext context,
                                  final Element element,
                                  final PropertyDef property )
    {
        this.context = context;
        this.element = element;
        this.property = property;
    }
    
    public final Element getModelElement()
    {
        return this.element;
    }
    
    public final PropertyDef getProperty()
    {
        return this.property;
    }
    
    public final SapphireRenderingContext getUiContext()
    {
        return this.context;
    }
    
}
