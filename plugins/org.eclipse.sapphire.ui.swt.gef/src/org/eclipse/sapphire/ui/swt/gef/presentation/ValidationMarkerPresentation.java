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

package org.eclipse.sapphire.ui.swt.gef.presentation;

import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarkerSize;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ValidationMarkerPresentation extends ShapePresentation 
{
	public ValidationMarkerPresentation(ShapePresentation parent, ValidationMarkerPart validationMarkerPart)
	{
		super(parent, validationMarkerPart);
	}

	public ValidationMarkerPart getValidationMarkerPart()
	{
		return (ValidationMarkerPart)getPart();
	}
	
	public ValidationMarkerSize getSize()
	{
		return getValidationMarkerPart().getSize();
	}
}
