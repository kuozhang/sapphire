/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.swt.gef.presentation.SpacerPresentation;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SpacerFigure extends Figure 
{
	private SpacerPresentation spacerPresentation;
	
	public SpacerFigure(SpacerPresentation spacerPresentation)
	{
		this.spacerPresentation = spacerPresentation;
	}
	
	/**
	 * @see IFigure#getMinimumSize(int, int)
	 */
	public Dimension getMinimumSize(int w, int h) 
	{
		Point size = this.spacerPresentation.getMinimunSize();
		Dimension d = new Dimension(size.getX(), size.getY());
		return d;
	}

	/**
	 * @see IFigure#getMaximumSize(int, int)
	 */
	public Dimension getMaximumSize(int w, int h) 
	{
		Point size = this.spacerPresentation.getMaximumSize();
		Dimension d = new Dimension(size.getX(), size.getY());
		return d;
	}

	/**
	 * @see IFigure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int w, int h) 
	{
		Point size = this.spacerPresentation.getSize();
		Dimension d = new Dimension(size.getX(), size.getY());
		return d;
	}

	/**
	 * @see IFigure#getSize(int, int)
	 */
	public Dimension getSize(int w, int h) 
	{
		return getPreferredSize(w, h);
	}
}
