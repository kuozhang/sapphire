/******************************************************************************
 * Copyright (c) 2012 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Gregory Amerson - [374022] - SapphireGraphicalEditor init with SapphireEditor
 ******************************************************************************/

package org.eclipse.sapphire.samples.map.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.samples.map.IMap;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.gef.diagram.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
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
        
        int index = addPage( this.mapSourceEditor, getEditorInput() );
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
		this.mapDiagram = new SapphireDiagramEditor( this, this.modelMap, path );
        addPage(0, mapDiagram, getEditorInput());
        setPageText( 0, "Diagram" );
        setPageId(this.pages.get(0), "Diagram", this.mapDiagram.getPart());
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
        this.mapDiagram.doSave(monitor);
        super.doSave(monitor);        
    }
    
}
