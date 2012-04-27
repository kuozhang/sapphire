/******************************************************************************
 * Copyright (c) 2012 SAP and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP - initial implementation
 *    Shenxue Zhou - adaptation for Sapphire and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.contextbuttons;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Rectangle;

/**
 * A very simple implementation of {@link IContextButtonPadData} without any
 * real functionality.
 * 
 * Users may subclass this class.
 * <p>
 * NOTE: By doing so it is also possible to alter the standard behavior of the
 * editor (e.g. change the location of the standard context button pad). This
 * might lead to inconsistent behavior in different editor implemented on top of
 * Graphiti, which might be irritating to users. From a consistency point of
 * view it is advisable in such cases to stick to the Graphiti standard, and to
 * only change it in case you really need to.
 * 
 * @author SAP
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ContextButtonPadData {

	private List<ContextButtonEntry> topContextButtons;
	private List<ContextButtonEntry> rightContextButtons;
	private ContextButtonEntry collapseContextButton;
	private Rectangle location;

	/**
	 * Creates a new {@link ContextButtonPadData}.
	 */
	public ContextButtonPadData() {
		this.topContextButtons = new ArrayList<ContextButtonEntry>();
		this.rightContextButtons = new ArrayList<ContextButtonEntry>();
		this.location = new Rectangle(0, 0, 0, 0);
	}

	public List<ContextButtonEntry> getTopContextButtons() {
		return this.topContextButtons;
	}

	public List<ContextButtonEntry> getRightContextButtons() {
		return this.rightContextButtons;
	}

	public ContextButtonEntry getCollapseContextButton() {
		return this.collapseContextButton;
	}

	public void setCollapseContextButton(ContextButtonEntry collapseContextButton) {
		this.collapseContextButton = collapseContextButton;
	}

	public Rectangle getPadLocation() {
		return this.location;
	}
}
