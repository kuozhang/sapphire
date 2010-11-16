/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.annotations;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public enum DocumentationMergeStrategy
{
    /**
     * All parent properties' content help will be prepended 
     */
    PREPEND,

    /**
     * All parent properties' content help will be appended 
     */
    APPEND,
    
    /**
     * Display the first properties' content help - parent contenct help will be disregarded
     */
    REPLACE
}
