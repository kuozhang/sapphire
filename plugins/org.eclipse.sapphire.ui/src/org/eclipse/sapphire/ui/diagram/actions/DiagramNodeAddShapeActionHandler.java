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

package org.eclipse.sapphire.ui.diagram.actions;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeAddShapeActionHandler extends SapphireActionHandler 
{
	private static final String ID_BASE = "Sapphire.Add.";
	private DiagramNodePart nodePart;
	private ShapeFactoryPart factory;
	private JavaType javaType;
	
	public DiagramNodeAddShapeActionHandler(DiagramNodePart nodePart, ShapeFactoryPart factory, JavaType javaType)
	{
		this.nodePart = nodePart;
		this.factory = factory;
		this.javaType = javaType;
	}
	
    @Override
    public void init( final SapphireAction action,
                      final ActionHandlerDef def )
    {
    	super.init(action, def);
    	setId( ID_BASE + this.javaType.name());
        final Class<?> cl = this.javaType.artifact();
        ModelElementType elementType = ModelElementType.read(cl);
    	setLabel(elementType.getSimpleName());
    	
		final ImageData typeSpecificAddImage = elementType.image();
		if (typeSpecificAddImage != null)
		{
			addImage(typeSpecificAddImage);
		}
    }
	
	@Override
	protected Object run(SapphireRenderingContext context) 
	{
		ShapePart shapePart = this.factory.newShape(this.javaType);
		this.nodePart.addShape(shapePart);
		SapphireDiagramEditorPagePart diagramPart = this.nodePart.nearest(SapphireDiagramEditorPagePart.class);
		if (shapePart.isEditable())
		{
			diagramPart.selectAndDirectEdit(shapePart);
		}
		return null;
	}

}
