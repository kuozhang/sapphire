/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342098] Separate dependency on org.eclipse.core.runtime (part 1)
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ErrorIndicatorImageProvider extends AbstractImageProvider 
{
	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "org.eclipse.sapphire.ui.swt.graphiti."; //$NON-NLS-1$

	// The image identifier for an EReference.
	public static final String IMG_ERROR_DECORATOR = PREFIX + "error"; //$NON-NLS-1$
	public static final String IMG_WARNING_DECORATOR = PREFIX + "warning"; //$NON-NLS-1$

	public ErrorIndicatorImageProvider()
	{
		super();
	}
	
	@Override
	protected void addAvailableImages() 
	{
		// register the path for each image identifier
		addImageFilePath(IMG_ERROR_DECORATOR, "icons/error-indicator.png"); //$NON-NLS-1$
		addImageFilePath(IMG_WARNING_DECORATOR, "icons/warning.png"); //$NON-NLS-1$
	}

}
