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

package org.eclipse.sapphire.services.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementService;
import org.eclipse.sapphire.modeling.ModelElementServiceFactory;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.services.ModelElementDocumentationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardModelElementDocumentationService extends ModelElementDocumentationService
{
    private String content;
    private List<Topic> topics;
    
    @Override
    public void init( final IModelElement element,
                      final String[] params )
    {
        super.init( element, params );
        
        this.topics = new ArrayList<Topic>();
        
        final ModelElementType type = element.getModelElementType();
        final Documentation docAnnotation = type.getAnnotation( Documentation.class, true );
        
        if( docAnnotation != null )
        {
            final LocalizationService localization = type.getLocalizationService();
            final String docAnnotationContent = localization.text( docAnnotation.content().trim(), CapitalizationType.NO_CAPS, false );
            
            if( docAnnotationContent.length() > 0 )
            {
                this.content = docAnnotationContent;
            }
            
            final List<Topic> docAnnotationTopics = convert( docAnnotation.topics(), localization );
            
            if( ! docAnnotationTopics.isEmpty() )
            {
                this.topics.addAll( docAnnotationTopics );
            }
        }
        
        if( this.content == null )
        {
            this.content = "";
        }

        this.topics = Collections.unmodifiableList( this.topics );
    }
    
    private static List<Topic> convert( final Documentation.Topic[] topics,
                                        final LocalizationService localization )
    {
        if( topics.length == 0 )
        {
            return Collections.emptyList();
        }
        else if( topics.length == 1 )
        {
            return Collections.singletonList( convert( topics[ 0 ], localization ) );
        }
        else
        {
            final List<Topic> resources = new ArrayList<Topic>( topics.length );
            
            for( Documentation.Topic topic : topics )
            {
                resources.add( convert( topic, localization ) );
            }
            
            return resources;
        }
    }
    
    private static Topic convert( final Documentation.Topic topic,
                                  final LocalizationService localization )
    {
        final String label = localization.text( topic.label().trim(), CapitalizationType.NO_CAPS, false );
        return new Topic( label, topic.url() );
    }

    @Override
    public String content()
    {
        return this.content;
    }

    @Override
    public List<Topic> topics()
    {
        return this.topics;
    }
    
    public static final class Factory extends ModelElementServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final Class<? extends ModelElementService> service )
        {
            return element.getModelElementType().hasAnnotation( Documentation.class );
        }
    
        @Override
        public ModelElementService create( final IModelElement element,
                                           final Class<? extends ModelElementService> service )
        {
            return new StandardModelElementDocumentationService();
        }
    }
    
}
