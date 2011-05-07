/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342098] Separate dependency on org.eclipse.core.runtime (part 1)
 ******************************************************************************/

package org.eclipse.sapphire.modeling.annotations;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public abstract class DocumentationData 
{
    public abstract String getContent();
    
    public List<DocumentationResource> getTopics() 
    {
        return Collections.emptyList();
    }
    
    public DocumentationMergeStrategy getMergeStrategy()
    {
        return DocumentationMergeStrategy.PREPEND;
    }

}
