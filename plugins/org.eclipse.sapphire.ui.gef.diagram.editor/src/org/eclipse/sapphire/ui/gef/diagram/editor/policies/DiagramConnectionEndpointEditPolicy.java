/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.policies;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.DiagramConnectionEditPart;
import org.eclipse.sapphire.ui.gef.diagram.editor.parts.SapphireDiagramEditorUtil;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramConnectionEndpointEditPolicy extends ConnectionEndpointEditPolicy {

    private static final org.eclipse.sapphire.ui.Color CONNECTION_FOREGROUND = new org.eclipse.sapphire.ui.Color(0xFF, 0xA5, 0x00);

    protected void addSelectionHandles() {
		super.addSelectionHandles();
		getConnectionFigure().setForegroundColor(SapphireDiagramEditorUtil.getColor(CONNECTION_FOREGROUND));
	}

	protected PolylineConnection getConnectionFigure() {
		return (PolylineConnection) ((GraphicalEditPart) getHost()).getFigure();
	}

	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		DiagramConnectionEditPart editPart = (DiagramConnectionEditPart)getHost();
		editPart.updateStyle(getConnectionFigure());
	}

}
