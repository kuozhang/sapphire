/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.services;

import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.ModelElementService;

/**
 * @since 0.4
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class ModelElementDocumentationService

    extends ModelElementService
    
{
    public abstract String content();
    
    public List<Topic> topics() 
    {
        return Collections.emptyList();
    }
    
    /*public static final class Topic
    {
        private final String label;
        private final String url;
        
        public Topic( final String label,
                      final String url )
        {
            this.label = label;
            this.url = url;
        }
        
        public String label()
        {
            return this.label;
        }
        
        public String topic()
        {
            return this.url;
        }
    }*/

    public static final class Topic extends DocumentationTopic
    {
        public Topic( final String label,
                      final String url )
        {
            super( label, url );
        }
    }

}
