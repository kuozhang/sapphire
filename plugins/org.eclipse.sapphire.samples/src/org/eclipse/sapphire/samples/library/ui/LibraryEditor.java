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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.library.ILibrary;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPart;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.editor.views.masterdetails.MasterDetailsPage;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
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
	private SapphireDiagramEditor libraryDiagramEditor;
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
		this.libraryDiagramEditor = new SapphireDiagramEditor(this.modelLibrary, path);
		SapphireDiagramEditorInput diagramEditorInput = null;
		try
		{
			diagramEditorInput = SapphireDiagramEditorFactory.createEditorInput(this.modelLibrary.adapt(IFile.class));
		}
		catch (Exception e)
		{
			SapphireUiFrameworkPlugin.log( e );
		}
		if (diagramEditorInput != null)
		{
			addPage(0, this.libraryDiagramEditor, diagramEditorInput);
			setPageText( 0, "Diagram" );
			setPageId(this.pages.get(0), "Diagram");
		}
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
		
		this.libraryDiagramEditor.doSave(monitor);
	}
	
}
