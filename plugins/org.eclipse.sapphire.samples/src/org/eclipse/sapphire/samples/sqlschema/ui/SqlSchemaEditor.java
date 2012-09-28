/******************************************************************************
 * Copyright (c) 2012 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.sqlschema.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.sqlschema.Schema;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SqlSchemaEditor extends SapphireEditor 
{
    private Schema schemaModel;
    private StructuredTextEditor schemaSourceEditor;
    private SapphireDiagramEditor schemaDiagram;
	
    public SqlSchemaEditor()
    {
        super( "org.eclipse.sapphire.samples" );
    }
    
    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.schemaSourceEditor = new StructuredTextEditor();
        this.schemaSourceEditor.setEditorPart(this);
        
        int index = addPage( this.schemaSourceEditor, getEditorInput() );
        setPageText( index, "Source" );
    }

    @Override
    protected IModelElement createModel() 
    {
        this.schemaModel = Schema.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.schemaSourceEditor)));
        return this.schemaModel;
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        IPath path = new Path( "org.eclipse.sapphire.samples/org/eclipse/sapphire/samples/sqlschema/SqlSchemaEditor.sdef/DiagramPage" );
		this.schemaDiagram = new SapphireDiagramEditor( this, this.schemaModel, path );
        addEditorPage(0, this.schemaDiagram);
    }
    
    @Override
    protected void createFormPages() throws PartInitException 
    {
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
