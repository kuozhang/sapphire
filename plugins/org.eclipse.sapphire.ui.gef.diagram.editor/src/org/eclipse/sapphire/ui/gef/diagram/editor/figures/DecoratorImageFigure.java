/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DecoratorImageFigure extends ImageFigure { 
	
	public DecoratorImageFigure(Image image) {
		super(image);
	}
	
	@Override
	public void paintFigure(Graphics graphics) {
		graphics.setAntialias(SWT.ON);
		super.paintFigure(graphics);
	}
}
