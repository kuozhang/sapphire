/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.annotations;

import org.eclipse.help.IHelpResource;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public abstract class DocumentationData {

    public final static IHelpResource[] EMPTY_RELATED_TOPICS = new IHelpResource[0];
    

    public abstract String getContent();
    
    public IHelpResource[] getTopics() 
    {
        return EMPTY_RELATED_TOPICS;
    }
    
    public DocumentationMergeStrategy getMergeStrategy()
    {
        return DocumentationMergeStrategy.PREPEND;
    }

}
