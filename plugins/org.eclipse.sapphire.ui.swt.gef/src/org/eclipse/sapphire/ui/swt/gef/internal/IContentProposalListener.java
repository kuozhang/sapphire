/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.internal;

import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * This interface is used to listen to notifications from a
 * {@link ContentProposalAdapter}.
 * 
 * @since 3.2
 */
/**
 * copy from {@link org.eclipse.jface.fieldassist.IContentProposalListener}
 *
 * @author tds
 * @createtime 2014年11月18日 上午10:01:04
 */
public interface IContentProposalListener {
	/**
	 * A content proposal has been accepted.
	 * 
	 * @param proposal
	 *            the accepted content proposal
	 */
	public void proposalAccepted(IContentProposal proposal);
}
