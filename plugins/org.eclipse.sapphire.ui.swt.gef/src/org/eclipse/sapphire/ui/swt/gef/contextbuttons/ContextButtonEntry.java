/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *  Gregory Amerson - [376200] Support floating palette around diagram node
 *******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.contextbuttons;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;
import org.eclipse.sapphire.ui.swt.gef.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public class ContextButtonEntry 
{
	private SapphireDiagramEditor editor;
	private ISapphirePart sapphirePart;
	private SapphireAction action;
	private SapphireActionHandler handler;
	private List<ContextButtonEntry> contextButtonMenuEntries = new ArrayList<ContextButtonEntry>();
	private static ImageDescriptor defaultImageDescriptor = null;

	/**
	 * Add a menu feature. Will be triggered when you click the button.
	 * 
	 * @param contextButtonEntry
	 *            the context button entry
	 */
	public void addContextButtonMenuEntry(ContextButtonEntry contextButtonEntry) {
		this.contextButtonMenuEntries.add(contextButtonEntry);
	}

	/**
	 * Gets the context button menu entries.
	 * 
	 * @return returns the menu features
	 */
	public List<ContextButtonEntry> getContextButtonMenuEntries() {
		return this.contextButtonMenuEntries;
	}

	public ContextButtonEntry(SapphireDiagramEditor editor, ISapphirePart part, SapphireAction action, SapphireActionHandler handler)
	{
		this.editor = editor;
		this.sapphirePart = part;
		this.action = action;
		this.handler = handler;
	}

	public SapphireActionHandler getActionHandler()
	{
		return this.handler;
	}

	public SapphireAction getAction()
    {
        return this.action;
    }

	public String getText()
	{
	    if (this.action.getActiveHandlers().size() > 1 && this.handler != null)
	    {
    		return this.handler.getLabel();
	    }
	    else
	    {
	        return this.action.getLabel();
	    }
	}

	public String getDescription()
	{
	    if (this.action.getActiveHandlers().size() > 1 && this.handler != null)
        {
            return this.handler.getDescription();
        }
        else
        {
            return this.action.getDescription();
        }
	}

	public Image getImage()
	{
	    ImageData imageData = null;

        if (this.action.getActiveHandlers().size() > 1 && this.handler != null)
        {
            imageData = this.getActionHandler().getImage(16);
        }
        else
        {
            imageData = this.getAction().getImage(16);
        }

        ImageDescriptor imageDescriptor;
		if (imageData == null)
		{
			imageDescriptor = getDefaultImageDescriptor();
		}
		else
		{
			imageDescriptor = SwtRendererUtil.toImageDescriptor(imageData);
		}
		return imageDescriptor.createImage();
	}

	public boolean canExecute()
	{
        if (this.action.getActiveHandlers().size() > 1 && this.handler != null)
        {
    		return this.handler.isEnabled();
        }
        else
        {
    		return this.action.isEnabled();
        }
	}

	public void execute()
    {
        if (handler != null)
        {
            DiagramRenderingContext context =
                    this.editor.getConfigurationManager().getDiagramRenderingContextCache().get(this.sapphirePart);
            handler.execute(context);
        }
    }

	private static ImageDescriptor getDefaultImageDescriptor()
	{
		if (defaultImageDescriptor == null)
		{
			Bundle bundle = Platform.getBundle("org.eclipse.sapphire.ui");
			URL url = bundle.getResource("org/eclipse/sapphire/ui/actions/Default.png");
			defaultImageDescriptor = ImageDescriptor.createFromURL(url);
		}
		return defaultImageDescriptor;
	}
}
