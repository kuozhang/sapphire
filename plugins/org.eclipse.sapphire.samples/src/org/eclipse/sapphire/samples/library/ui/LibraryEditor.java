/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.library.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.library.ILibrary;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPart;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.editor.views.masterdetails.MasterDetailsPage;
import org.eclipse.sapphire.ui.xml.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class LibraryEditor extends SapphireEditor 
{
	private ILibrary modelLibrary;
	private StructuredTextEditor librarySourceEditor;
	private SapphireDiagramEditorPart libraryDiagramPart;
	private MasterDetailsPage libraryOverviewPage;
	
    public LibraryEditor()
    {
        super( "org.eclipse.sapphire.samples" );
    }

	@Override
	protected void createSourcePages() throws PartInitException 
	{
		this.librarySourceEditor = new StructuredTextEditor();
		this.librarySourceEditor.setEditorPart(this);
        final FileEditorInput rootEditorInput = (FileEditorInput) getEditorInput();
        
        int index = addPage( this.librarySourceEditor, rootEditorInput );
        setPageText( index, "library.xml" );
	}

	@Override
	protected IModelElement createModel() 
	{
		this.modelLibrary = ILibrary.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.librarySourceEditor)));
		return this.modelLibrary;
	}

	@Override
	protected void createDiagramPages() throws PartInitException
	{
		IPath path = new Path( "org.eclipse.sapphire.samples/sdef/LibraryEditor.sdef/diagram" );
		this.libraryDiagramPart = new SapphireDiagramEditorPart(this, this.modelLibrary, path);
		SapphireDiagramEditor diagramEditor = this.libraryDiagramPart.getDiagramEditor();
		SapphireDiagramEditorInput diagramEditorInput = this.libraryDiagramPart.getDiagramEditorInput();
		addPage(0, diagramEditor, diagramEditorInput);
		setPageText( 0, "Diagram" );
		setPageId(this.pages.get(0), "Diagram");
	}
	
	@Override
	protected void createFormPages() throws PartInitException 
	{
		IPath path = new Path( "org.eclipse.sapphire.samples/sdef/LibraryEditor.sdef/overview" );
		this.libraryOverviewPage = new MasterDetailsPage(this, this.modelLibrary, path);
        addPage(1, this.libraryOverviewPage);
        setPageText(1, "Overview");
        setPageId(this.pages.get(1), "Overview");		
	}

	public ILibrary getMap()
	{
		return this.modelLibrary;
	}
	
	@Override
	public void doSave( final IProgressMonitor monitor )
	{
		super.doSave(monitor);
		SapphireDiagramEditor diagramEditor = this.libraryDiagramPart.getDiagramEditor();
		diagramEditor.doSave(monitor);
	}
	
    @Override
    public void dispose() 
    {
    	super.dispose();
    	if (this.libraryDiagramPart != null)
    	{
    		this.libraryDiagramPart.dispose();
    	}
    }
}
