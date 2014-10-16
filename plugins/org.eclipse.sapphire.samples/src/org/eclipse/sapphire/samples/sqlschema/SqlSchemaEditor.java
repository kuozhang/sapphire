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

package org.eclipse.sapphire.samples.sqlschema;

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

public class SqlSchemaEditor extends SapphireEditor 
{
    private static final String PAGE_DETAILS = "Details";
    private static final String PAGE_SCHEMA = "Schema";

    private Schema schemaModel;
    private StructuredTextEditor schemaSourceEditor;
    private SapphireDiagramEditor schemaDiagram;
    private Reference<EditorPageDef> schemaDef;
    private Reference<EditorPageDef> detailsDef;

    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.schemaSourceEditor = new StructuredTextEditor();
        this.schemaSourceEditor.setEditorPart(this);
        
        int index = addPage( this.schemaSourceEditor, getEditorInput() );
        setPageText( index, "Source" );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        addInitialPage( 0, PAGE_SCHEMA );
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        addInitialPage( 1, PAGE_DETAILS );
    }

    @Override
    protected Element createModel()
    {
        this.schemaModel = Schema.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.schemaSourceEditor)));
        return this.schemaModel;
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String pageName )
    {
        if( PAGE_SCHEMA.equals( pageName ) )
        {
            this.schemaDef = DefinitionLoader.sdef( getClass() ).page( "DiagramPage" );
            return this.schemaDef;
        }
        else if( PAGE_DETAILS.equals( pageName ) )
        {
            this.detailsDef = DefinitionLoader.sdef( getClass() ).page( "DetailsPage" );
            return this.detailsDef;
        }

        return null;
    }
    
    @Override
    protected IEditorPart createDiagramPage( String pageName )
    {
        if( PAGE_SCHEMA.equals( pageName ) )
        {
            this.schemaDiagram = new SapphireDiagramEditor( this, getModelElement(), getDefinition( pageName ) );
            return this.schemaDiagram;
        }

        return super.createDiagramPage( pageName );
    }

    public Schema getSchema()
    {
        return this.schemaModel;
    }
    
    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        this.schemaDiagram.doSave(monitor);
        super.doSave(monitor);        
    }
    

}
