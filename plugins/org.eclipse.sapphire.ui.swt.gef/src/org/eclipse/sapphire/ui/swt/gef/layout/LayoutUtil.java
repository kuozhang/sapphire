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
package org.eclipse.sapphire.ui.swt.gef.layout;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.sapphire.ui.diagram.shape.def.MarginConstraintDef;

/**
 * 
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 *  
 */
public class LayoutUtil {
	
	private LayoutUtil() {
	}

	public static Insets calculateMargin(MarginConstraintDef def) {
		Integer margin = def.getMargin().getContent(false);
		Integer horizontalMargin = def.getHorizontalMargin().getContent(false);
		Integer verticalMargin = def.getVerticalMargin().getContent(false);
		Integer topMargin = def.getTopMargin().getContent(false);
		Integer bottomMargin = def.getBottomMargin().getContent(false);
		Integer leftMargin = def.getLeftMargin().getContent(false);
		Integer rightMargin = def.getRightMargin().getContent(false);
		
		int top, bottom, left, right;
		top = bottom = left = right = def.getMargin().getContent();
		
		if (topMargin != null) {
			top = topMargin.intValue();
		} else if (verticalMargin != null) {
			top = verticalMargin.intValue();
		} else if (margin != null) {
			top = margin.intValue();
		}
		if (bottomMargin != null) {
			bottom = bottomMargin.intValue();
		} else if (verticalMargin != null) {
			bottom = verticalMargin.intValue();
		} else if (margin != null) {
			bottom = margin.intValue();
		}
		if (leftMargin != null) {
			left = leftMargin.intValue();
		} else if (horizontalMargin != null) {
			left = horizontalMargin.intValue();
		} else if (margin != null) {
			left = margin.intValue();
		}
		if (rightMargin != null) {
			right = rightMargin.intValue();
		} else if (horizontalMargin != null) {
			right = horizontalMargin.intValue();
		} else if (margin != null) {
			right = margin.intValue();
		}
	
		return new Insets(top, left, bottom, right);
	}

}