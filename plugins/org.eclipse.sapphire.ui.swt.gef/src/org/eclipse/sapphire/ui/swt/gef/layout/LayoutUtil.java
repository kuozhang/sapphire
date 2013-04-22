/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.layout;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.sapphire.ui.diagram.shape.def.MarginPresentation;

/**
 * 
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class LayoutUtil {
	
	private LayoutUtil() {
	}

	public static Insets calculateMargin(MarginPresentation def) {
		final int top = def.getTopMargin().content();
		final int bottom = def.getBottomMargin().content();
		final int left = def.getLeftMargin().content();
		final int right = def.getRightMargin().content();
	
		return new Insets(top, left, bottom, right);
	}

}