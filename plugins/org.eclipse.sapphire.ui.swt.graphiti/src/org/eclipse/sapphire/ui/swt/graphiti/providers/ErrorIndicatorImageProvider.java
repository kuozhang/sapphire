package org.eclipse.sapphire.ui.swt.graphiti.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

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
		addImageFilePath(IMG_ERROR_DECORATOR, "icons/error.gif"); //$NON-NLS-1$
		addImageFilePath(IMG_WARNING_DECORATOR, "icons/warning.png"); //$NON-NLS-1$
	}

}
