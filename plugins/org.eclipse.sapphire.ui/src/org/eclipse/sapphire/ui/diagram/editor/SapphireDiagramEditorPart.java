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

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.help.IContext;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.SapphireImageCache;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.def.ISapphirePartDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.SapphireUiDefFactory;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPageDef;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.diagram.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramEditorPart
	implements ISapphirePart
{
    private final SapphireEditor editor;
    private final IModelElement rootModelElement;
    private IDiagramPageDef diagramPageDef = null;
    private DiagramGeometryWrapper diagramGeometry;
    private List<DiagramNodeTemplate> nodeTemplates;
    private List<DiagramConnectionTemplate> connectionTemplates;
    private SapphireDiagramEditorInput diagramInput;
    private SapphireDiagramEditor diagramEditor;
	
	public SapphireDiagramEditorPart(final SapphireEditor editor,
								final IModelElement rootModelElement,
								final IPath pageDefinitionLocation )
	{
		this(editor, rootModelElement, pageDefinitionLocation, null);
	}

	public SapphireDiagramEditorPart(final SapphireEditor editor,
								final IModelElement rootModelElement,
								final IPath pageDefinitionLocation,
								String pageName )
	{
		this.editor = editor;
		this.rootModelElement = rootModelElement;

        final String bundleId = pageDefinitionLocation.segment( 0 );
        final String pageId = pageDefinitionLocation.lastSegment();
        final String relPath = pageDefinitionLocation.removeFirstSegments( 1 ).removeLastSegments( 1 ).toPortableString();
        
        final ISapphireUiDef def = SapphireUiDefFactory.load( bundleId, relPath );
        
        for( IDiagramPageDef pg : def.getDiagramPageDefs() )
        {
            if( pageId.equals( pg.getId().getText() ) )
            {
                this.diagramPageDef = pg;
                break;
            }
        }
                
        this.nodeTemplates = new ArrayList<DiagramNodeTemplate>();
        ModelElementList<IDiagramNodeDef> nodeDefs = this.diagramPageDef.getDiagramNodeDefs();
        
        for (IDiagramNodeDef nodeDef : nodeDefs)
        {
        	DiagramNodeTemplate nodeTemplate = new DiagramNodeTemplate(this, nodeDef, this.rootModelElement);
        	this.nodeTemplates.add(nodeTemplate);
        }
        
        this.connectionTemplates = new ArrayList<DiagramConnectionTemplate>();
        ModelElementList<IDiagramConnectionDef> connectionDefs = this.diagramPageDef.getDiagramConnectionDefs();
        for (IDiagramConnectionDef connectionDef : connectionDefs)
        {
        	DiagramConnectionTemplate connectionTemplate = new DiagramConnectionTemplate(this, connectionDef, this.rootModelElement);
        	this.connectionTemplates.add(connectionTemplate);
        }       
        
        initializeDiagramEditor();
	}

	private void initializeDiagramEditor()
	{
        try
        {
        	this.diagramInput = SapphireDiagramEditorFactory.createEditorInput(this.editor.getEditorInput());
        	IFile npFile = this.diagramInput.getNodePositionFile();
        	this.diagramGeometry = new DiagramGeometryWrapper(npFile, this);
        }
        catch( Exception e )
        {
            SapphireUiFrameworkPlugin.log( e );
        }
    	this.diagramEditor = new SapphireDiagramEditor(this);
	}
	
	public SapphireDiagramEditorInput getDiagramEditorInput()
	{
		return this.diagramInput;
	}
	
	public SapphireDiagramEditor getDiagramEditor()
	{
		return this.diagramEditor;
	}
	
	public List<DiagramNodeTemplate> getNodeTemplates()
	{
		return this.nodeTemplates;
	}
	
	public List<DiagramConnectionTemplate> getConnectionTemplates()
	{
		return this.connectionTemplates;
	}
	
	public DiagramGeometryWrapper getDiagramGeometry()
	{
		return this.diagramGeometry;
	}
	
	public ISapphirePart getParentPart() 
	{	
		return this.editor;
	}

	public <T> T getNearestPart(Class<T> partType) {
		// TODO Auto-generated method stub
		return null;
	}

	public IModelElement getModelElement() 
	{	
		return this.rootModelElement;
	}
	
	public DiagramNodePart getDiagramNodePart(IModelElement nodeElement)
	{
		if (nodeElement == null)
			return null;
		
		List<DiagramNodeTemplate> nodeTemplates = this.getNodeTemplates();
		for (DiagramNodeTemplate nodeTemplate : nodeTemplates)
		{
			if (nodeTemplate.getNodeType().equals(nodeElement.getModelElementType()))
			{
				List<DiagramNodePart> nodeParts = nodeTemplate.getDiagramNodes();
				for (DiagramNodePart nodePart : nodeParts)
				{
					if (nodePart.getLocalModelElement().equals(nodeElement))
					{
						return nodePart;
					}
				}
			}
		}
		return null;
	}
	
	public IStatus getValidationState() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContext getDocumentationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public SapphireImageCache getImageCache() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addListener(SapphirePartListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void removeListener(SapphirePartListener listener) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() 
	{
		for (DiagramNodeTemplate nodeTemplate : this.nodeTemplates)
		{
			nodeTemplate.dispose();
		}
		for (DiagramConnectionTemplate connTemplate : this.connectionTemplates)
		{
			connTemplate.dispose();
		}
	}

	public ISapphirePartDef getDefinition() 
	{	
		return this.diagramPageDef;
	}

	public Set<String> getActionContexts() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMainActionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public SapphireActionGroup getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public SapphireActionGroup getActions(String context) {
		// TODO Auto-generated method stub
		return null;
	}

	public SapphireAction getAction(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
