/*******************************************************************************
 * Copyright (c) 2011 Accenture Services Pvt Ltd. and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 *******************************************************************************/

package org.eclipse.sapphire.services;

/**
 * Produces a value to assign to a property when the containing model element is created.
 * 
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class InitialValueService extends Service 
{
    public abstract String text();  
}
