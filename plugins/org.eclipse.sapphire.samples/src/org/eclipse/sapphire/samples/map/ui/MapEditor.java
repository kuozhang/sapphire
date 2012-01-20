/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.map.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.map.IMap;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class MapEditor extends SapphireEditor 
{
    private IMap modelMap;
    private StructuredTextEditor mapSourceEditor;
    private SapphireDiagramEditor mapDiagram;
    private MasterDetailsEditorPage mapOverviewPage;
    
    public MapEditor()
    {
        super( "org.eclipse.sapphire.samples" );
    }

    @Override
    protected void createSourcePages() throws PartInitException 
    {
        this.mapSourceEditor = new StructuredTextEditor();
        this.mapSourceEditor.setEditorPart(this);
        final FileEditorInput rootEditorInput = (FileEditorInput) getEditorInput();
        
        int index = addPage( this.mapSourceEditor, rootEditorInput );
        setPageText( index, "map.xml" );
    }

    @Override
    protected IModelElement createModel() 
    {
        this.modelMap = IMap.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.mapSourceEditor)));
        return this.modelMap;
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        IPath path = new Path( "org.eclipse.sapphire.samples/org/eclipse/sapphire/samples/map/MapEditor.sdef/diagram" );
        this.mapDiagram = new SapphireDiagramEditor(this.modelMap, path);
        SapphireDiagramEditorInput diagramEditorInput = null;
        try
        {
            diagramEditorInput = SapphireDiagramEditorFactory.createEditorInput(this.modelMap.adapt(IFile.class), null, false);
        }
        catch (Exception e)
        {
            SapphireUiFrameworkPlugin.log( e );
        }

        if (diagramEditorInput != null)
        {
            addPage(0, mapDiagram, diagramEditorInput);
            setPageText( 0, "Diagram" );
            setPageId(this.pages.get(0), "Diagram", this.mapDiagram.getPart());
        }
    }
    
    @Override
    protected void createFormPages() throws PartInitException 
    {
        IPath path = new Path( "org.eclipse.sapphire.samples/org/eclipse/sapphire/samples/map/MapEditor.sdef/overview" );
        this.mapOverviewPage = new MasterDetailsEditorPage(this, this.modelMap, path);
        addPage(1, this.mapOverviewPage);
        setPageText(1, "Overview");
        setPageId(this.pages.get(1), "Overview", this.mapOverviewPage.getPart());        
    }

    public IMap getMap()
    {
        return this.modelMap;
    }
    
    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        super.doSave(monitor);        
        this.mapDiagram.doSave(monitor);
    }
    
}
