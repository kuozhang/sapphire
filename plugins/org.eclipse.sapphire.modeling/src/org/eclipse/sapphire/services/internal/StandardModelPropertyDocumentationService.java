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
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyService;
import org.eclipse.sapphire.modeling.ModelPropertyServiceFactory;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.DocumentationMergeStrategy;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.services.FactsAggregationService;
import org.eclipse.sapphire.services.ModelPropertyDocumentationService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StandardModelPropertyDocumentationService extends ModelPropertyDocumentationService
{
    private final static String LINE_BREAK = "[br/]";
    private final static String PARAGRAPH_BREAK = "[pbr/]";
    
    private String content;
    private List<Topic> topics;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        final StringBuilder content = new StringBuilder();
        final List<Topic> topics = new ArrayList<Topic>();
        
        init( property, content, topics );
        
        final List<String> facts = element.service( property, FactsAggregationService.class ).facts();
        
        if( ! facts.isEmpty() )
        {
            if( content.length() > 0 )
            {
                content.append( PARAGRAPH_BREAK );
            }
            
            boolean first = true;
            
            for( String item : facts )
            {
                if( first )
                {
                    first = false;
                }
                else
                {
                    content.append( LINE_BREAK );
                }
                
                content.append( item );
            }
        }
        
        this.content = content.toString();
        this.topics = Collections.unmodifiableList( topics );
    }
    
    private static void init( final ModelProperty property,
                              final StringBuilder content,
                              final List<Topic> topics )
    {
        if( property == null )
        {
            return;
        }
        
        final Documentation docAnnotation = property.getAnnotation( Documentation.class, true );
        
        if( docAnnotation == null || docAnnotation.mergeStrategy() != DocumentationMergeStrategy.REPLACE )
        {
            init( property.getBase(), content, topics );
        }
        
        if( docAnnotation != null )
        {
            final LocalizationService localization = property.getLocalizationService();
            final DocumentationMergeStrategy docMergeStrategy = docAnnotation.mergeStrategy();
            final String docAnnotationContent = localization.text( docAnnotation.content().trim(), CapitalizationType.NO_CAPS, false );
            
            if( docAnnotationContent.length() > 0 )
            {
                if( docMergeStrategy == DocumentationMergeStrategy.REPLACE || content.length() == 0 )
                {
                    content.append( docAnnotationContent );
                }
                else if( docMergeStrategy == DocumentationMergeStrategy.APPEND )
                {
                    content.append( PARAGRAPH_BREAK );
                    content.append( docAnnotationContent );
                }
                else
                {
                    content.insert( 0, PARAGRAPH_BREAK );
                    content.insert( 0, docAnnotationContent );
                }
            }
            
            final List<Topic> docAnnotationTopics = convert( docAnnotation.topics(), localization );
            
            if( ! docAnnotationTopics.isEmpty() )
            {
                if( docMergeStrategy != DocumentationMergeStrategy.PREPEND || topics.isEmpty() )
                {
                    topics.addAll( docAnnotationTopics );
                }
                else
                {
                    topics.addAll( 0, docAnnotationTopics );
                }
            }
        }
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
    
    public static final class Factory extends ModelPropertyServiceFactory
    {
        @Override
        public boolean applicable( final IModelElement element,
                                   final ModelProperty property,
                                   final Class<? extends ModelPropertyService> service )
        {
            for( ModelProperty p = property; p != null ; p = p.getBase() )
            {
                if( p.hasAnnotation( Documentation.class ) )
                {
                    return true;
                }
            }
            
            if( ! element.service( property, FactsAggregationService.class ).facts().isEmpty() )
            {
                return true;
            }
            
            return false;
        }
    
        @Override
        public ModelPropertyService create( final IModelElement element,
                                            final ModelProperty property,
                                            final Class<? extends ModelPropertyService> service )
        {
            return new StandardModelPropertyDocumentationService();
        }
    }
    
}
