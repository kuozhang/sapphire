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

package org.eclipse.sapphire.samples.gallery.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.annotations.DocumentationData;
import org.eclipse.sapphire.modeling.annotations.DocumentationProviderImpl;
import org.eclipse.sapphire.modeling.annotations.DocumentationResource;

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
            public List<DocumentationResource> getTopics() {
                final DocumentationResource resource = new DocumentationResource( "wikipedia positive number", "http://en.wikipedia.org/wiki/Positive_number" );
                return Collections.singletonList( resource );
            }
        };
    }

}
