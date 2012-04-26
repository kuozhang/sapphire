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
 *******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.sapphire.ui.swt.gef.contextbuttons.ContextButtonEntry;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

/**
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ContextEntryCommand extends Command {

	private ContextButtonEntry contextEntry;

	public ContextEntryCommand(ContextButtonEntry contextEntry) {
		setContextEntry(contextEntry);
	}

	@Override
	public void execute() {
		getContextEntry().execute();
		super.execute();
	}

	private void setContextEntry(ContextButtonEntry contextEntry) {
		this.contextEntry = contextEntry;
	}

	public ContextButtonEntry getContextEntry() {
		return contextEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#getLabel()
	 */
	@Override
	public String getLabel() {
		return getContextEntry().getText();
	}

}
