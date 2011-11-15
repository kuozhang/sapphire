/******************************************************************************
 * Copyright (c) 2011 Oracle and Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Rob Cernich - [360362] Allow creation of custom form editor pages
 ******************************************************************************/

package org.eclipse.sapphire.samples.architecture.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.architecture.IArchitecture;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.SapphireEditorFormPage;
import org.eclipse.sapphire.ui.StandardFormEditorPage;
import org.eclipse.sapphire.ui.def.FormEditorPageDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a> 
 */

public final class ArchitectureEditor extends SapphireEditor
{
    private IArchitecture model;
    private StructuredTextEditor pageSource;
    private SapphireDiagramEditor pageDiagram;
    private MasterDetailsEditorPage pageDetails;
    private SapphireEditorFormPage pageComponentsForm;
    
    public ArchitectureEditor()
    {
        super( "org.eclipse.sapphire.samples" );
    }

    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.pageSource = new StructuredTextEditor();
        this.pageSource.setEditorPart( this );
        final FileEditorInput rootEditorInput = (FileEditorInput) getEditorInput();
        
        int index = addPage( this.pageSource, rootEditorInput );
        setPageText( index, "Source" );
    }

    @Override
    protected IModelElement createModel() 
    {
        this.model = IArchitecture.TYPE.instantiate( new RootXmlResource( new XmlEditorResourceStore( this, this.pageSource ) ) );
        return this.model;
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        IPath path = new Path( "org.eclipse.sapphire.samples/org/eclipse/sapphire/samples/architecture/ArchitectureEditor.sdef/DiagramPage" );
        this.pageDiagram = new SapphireDiagramEditor( this.model, path );
        SapphireDiagramEditorInput diagramEditorInput = null;
        
        try
        {
            diagramEditorInput = SapphireDiagramEditorFactory.createEditorInput( this.model.adapt( IFile.class ) );
        }
        catch( Exception e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }

        if( diagramEditorInput != null )
        {
            addPage( 0, this.pageDiagram, diagramEditorInput );
            setPageText( 0, "Diagram" );
            setPageId( this.pages.get( 0 ), "Diagram", this.pageDiagram.getPart() );
        }
    }
    
    @Override
    protected void createFormPages() throws PartInitException 
    {
        IPath path = new Path( "org.eclipse.sapphire.samples/org/eclipse/sapphire/samples/architecture/ArchitectureEditor.sdef/DetailsPage" );
        this.pageDetails = new MasterDetailsEditorPage( this, this.model, path );
        addPage( 1, this.pageDetails );
        setPageText( 1, "Details" );
        setPageId( this.pages.get( 1 ), "Details", this.pageDetails.getPart() );        

        ISapphireUiDef uiDef = SapphireUiDefFactory.load("org.eclipse.sapphire.samples", "org/eclipse/sapphire/samples/architecture/ArchitectureEditor.sdef");
        this.pageComponentsForm = StandardFormEditorPage
                .createFormEditorPage(this, getModelElement(), (FormEditorPageDef) uiDef.getPartDef("ComponentsPage", true, FormEditorPageDef.class));
        addPage( 2, this.pageComponentsForm );
        setPageId( this.pages.get( 2 ), "Components", this.pageComponentsForm.getPart() );        
    }

    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        super.doSave( monitor );        
        this.pageDiagram.doSave( monitor );
    }
    
}
