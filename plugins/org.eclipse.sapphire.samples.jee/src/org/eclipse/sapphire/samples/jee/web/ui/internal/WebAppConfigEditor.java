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

package org.eclipse.sapphire.samples.jee.web.ui.internal;

import org.eclipse.sapphire.samples.jee.web.WebAppConfig;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class WebAppConfigEditor

    extends SapphireEditorForXml
    
{
    public WebAppConfigEditor()
    {
        super( "org.eclipse.sapphire.samples.jee" );
        
        setRootModelElementType( WebAppConfig.TYPE );
        setEditorDefinitionPath( "org.eclipse.sapphire.samples.jee/org/eclipse/sapphire/samples/jee/web/ui/internal/WebAppConfigEditor.sdef/EditorPage" );
    }
    
}
