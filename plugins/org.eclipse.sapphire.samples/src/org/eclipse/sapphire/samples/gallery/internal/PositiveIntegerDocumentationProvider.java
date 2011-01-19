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

package org.eclipse.sapphire.samples.gallery.internal;

import org.eclipse.help.IHelpResource;
import org.eclipse.sapphire.modeling.annotations.DocumentationData;
import org.eclipse.sapphire.modeling.annotations.DocumentationProviderImpl;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class PositiveIntegerDocumentationProvider extends DocumentationProviderImpl {

    @Override
    public DocumentationData getDocumentationData() {
        return new DocumentationData() {

            @Override
            public String getContent() {
                return "Positive integers are all the whole numbers greater than zero: 1, 2, 3, 4, 5, ......";
            }

            @Override
            public IHelpResource[] getTopics() {
                IHelpResource topics[] = new IHelpResource[1];
                topics[0] = new IHelpResource() {
        
                    public String getHref() {
                        return "http://en.wikipedia.org/wiki/Positive_number";
                    }
        
                    public String getLabel() {
                        return "wikipedia positive number";
                    }
                    
                };
                return topics;
            }
        };
    }

}
