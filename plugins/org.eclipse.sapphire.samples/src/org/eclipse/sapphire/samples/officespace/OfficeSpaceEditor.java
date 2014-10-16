/******************************************************************************
 * Copyright (c) 2014 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Gregory Amerson - [444202] lazy loading of editor pages
 ******************************************************************************/

package org.eclipse.sapphire.samples.officespace;

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
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public class OfficeSpaceEditor extends SapphireEditor 
{
    private static final String PAGE_DIAGRAM = "Diagram";

    private OfficeSpace model;
    private StructuredTextEditor sourceEditorPage;
    private SapphireDiagramEditor diagramEditorPage;
    private Reference<EditorPageDef> diagramDef;

    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.sourceEditorPage = new StructuredTextEditor();
        this.sourceEditorPage.setEditorPart( this );
        
        int index = addPage( this.sourceEditorPage, getEditorInput() );
        setPageText( index, "Source" );
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        addInitialPage( 0, PAGE_DIAGRAM );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
    }

    @Override
    protected Element createModel()
    {
        this.model = OfficeSpace.TYPE.instantiate( new RootXmlResource( new XmlEditorResourceStore( this, this.sourceEditorPage ) ) );
        return this.model;
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String pageName )
    {
        if( PAGE_DIAGRAM.equals( pageName ) )
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
    protected IEditorPart createDiagramPage( String pageName )
    {
        if( PAGE_DIAGRAM.equals( pageName ) )
        {
            this.diagramEditorPage = new SapphireDiagramEditor( this, getModelElement(), getDefinition( pageName ) );
            return this.diagramEditorPage;
        }

        return null;
    }
    
    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        this.diagramEditorPage.doSave( monitor );
        super.doSave(monitor);        
    }
    

}
