/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class SapphireEditorFormPage

    extends FormPage
    
{
    private final SapphireEditor editor;
    private final SapphireEditorPagePart part;
    
    public SapphireEditorFormPage( final SapphireEditor editor,
                                   final SapphireEditorPagePart editorPagePart ) 
    {
        super( editor, null, null );

        this.editor = editor;
        this.part = editorPagePart;
    }
    
    public final SapphireEditor getEditor()
    {
        return this.editor;
    }
    
    public SapphireEditorPagePart getPart()
    {
        return this.part;
    }
    
    public final IModelElement getModelElement()
    {
        return this.part.getModelElement();
    }
    
    public abstract String getId();
    
}