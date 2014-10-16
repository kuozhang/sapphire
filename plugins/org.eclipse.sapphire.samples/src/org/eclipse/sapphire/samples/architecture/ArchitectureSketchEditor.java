/******************************************************************************
 * Copyright (c) 2014 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Shenxue Zhou - handle external file input
 *    Gregory Amerson - [374022] - SapphireGraphicalEditor init with SapphireEditor
 *                      [444202] lazy loading of editor pages
 ******************************************************************************/

package org.eclipse.sapphire.samples.architecture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public final class ArchitectureSketchEditor extends SapphireEditor
{
    private static final String PAGE_ARCHITECTURE = "Architecture";
    private static final String PAGE_DETAILS = "Details";

    private StructuredTextEditor pageSource;
    private SapphireDiagramEditor pageDiagram;
    private Reference<EditorPageDef> archDef;
    private Reference<EditorPageDef> diagramDef;

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
        addInitialPage( 0, PAGE_DETAILS );
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        addInitialPage( 0, PAGE_ARCHITECTURE );
    }

    @Override
    protected Element createModel()
    {
        return ArchitectureSketch.TYPE.instantiate( new RootXmlResource( new XmlEditorResourceStore( this, this.pageSource ) ) );
    }

    @Override
    protected IEditorPart createDiagramPage( String pageName )
    {
        if( PAGE_ARCHITECTURE.equals( pageName ) )
        {
            this.pageDiagram = new SapphireDiagramEditor( this, getModelElement(), getDefinition( pageName ) );
            return this.pageDiagram;
        }

        return super.createDiagramPage( pageName );
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String pageName )
    {
        if( PAGE_DETAILS.equals( pageName ) )
        {
            if( this.archDef == null )
            {
                this.archDef = DefinitionLoader.sdef( getClass() ).page( "DetailsPage" );
            }

            return this.archDef;
        }
        else if( PAGE_ARCHITECTURE.equals( pageName ) )
        {
            if( this.diagramDef == null )
            {
                this.diagramDef = DefinitionLoader.sdef( getClass() ).page( "DiagramPage" );
            }

            return this.diagramDef;
        }

        return null;
    }

    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        this.pageDiagram.doSave( monitor );
        super.doSave( monitor );        
    }
    
}
