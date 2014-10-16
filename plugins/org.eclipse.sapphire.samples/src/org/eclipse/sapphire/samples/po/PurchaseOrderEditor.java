/******************************************************************************
 * Copyright (c) 2014 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Gregory Amerson - [444202] lazy loading of editor pages
 ******************************************************************************/

package org.eclipse.sapphire.samples.po;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public final class PurchaseOrderEditor extends SapphireEditor
{
    private static final String PAGE_ENTRIES = "Entries";
    private static final String PAGE_GENERAL = "General";

    private PurchaseOrder model;
    private StructuredTextEditor pageSource;
    private Reference<EditorPageDef> generalDef;
    private Reference<EditorPageDef> entriesDef;

    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.pageSource = new StructuredTextEditor();
        this.pageSource.setEditorPart( this );
        
        int index = addPage( this.pageSource, getEditorInput() );
        setPageText( index, "Source" );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        addInitialPage( 0, PAGE_GENERAL );
        addInitialPage( 1, PAGE_ENTRIES );
    }

    @Override
    protected Element createModel()
    {
        this.model = PurchaseOrder.TYPE.instantiate( new RootXmlResource( new XmlEditorResourceStore( this, this.pageSource ) ) );
        return this.model;
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String pageName )
    {
        if( PAGE_GENERAL.equals( pageName ) )
        {
            if( this.generalDef == null )
            {
                this.generalDef = DefinitionLoader.sdef( getClass() ).page( "GeneralPage" );
            }
            return this.generalDef;
        }
        else if( PAGE_ENTRIES.equals( pageName ) )
        {
            if( this.entriesDef == null )
            {
                this.entriesDef = DefinitionLoader.sdef( getClass() ).page( "EntriesPage" );
            }

            return this.entriesDef;
        }

        return null;
    }

}
