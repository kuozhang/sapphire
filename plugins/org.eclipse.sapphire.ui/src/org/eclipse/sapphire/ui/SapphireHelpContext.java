/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342098] Separate modeling dependency on org.eclipse.core.runtime
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.help.IContext;
import org.eclipse.help.IContext2;
import org.eclipse.help.IHelpResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelMetadataItem;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Documentation;
import org.eclipse.sapphire.modeling.annotations.Documentation.Topic;
import org.eclipse.sapphire.modeling.annotations.DocumentationData;
import org.eclipse.sapphire.modeling.annotations.DocumentationMergeStrategy;
import org.eclipse.sapphire.modeling.annotations.DocumentationProvider;
import org.eclipse.sapphire.modeling.annotations.DocumentationProviderImpl;
import org.eclipse.sapphire.modeling.annotations.DocumentationResource;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.modeling.util.internal.DocumentationUtil;
import org.eclipse.sapphire.modeling.util.internal.SapphireCommonUtil;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireHelpContext implements IContext, IContext2 {
    
    private final static String LINE_BREAK = "[br/]";
    
    private String title = null;
    private String content = null;
    private final List<DocumentationResource> topics = new ArrayList<DocumentationResource>();
    
    public SapphireHelpContext(final IModelElement modelElement, final ModelProperty property) {
        this.title = property.getLabel(true, CapitalizationType.TITLE_STYLE, false);
        
        ModelProperty p = property;
        while (p != null) {
            Documentation documentation = p.getAnnotation(Documentation.class, true);
            DocumentationProvider documentationProvider = p.getAnnotation(DocumentationProvider.class, true);
            if (documentation != null) {
                initContext(p, p.getName(), documentation, null);
                if (documentation.mergeStrategy() == DocumentationMergeStrategy.REPLACE) {
                    break;
                }
            } else if (documentationProvider != null) {
                DocumentationData data = initContext(documentationProvider, null);
                if (data != null && data.getMergeStrategy() == DocumentationMergeStrategy.REPLACE) {
                    break;
                }
            }
            p = p.getBase();
        }
        
        ModelElementType type = modelElement.getModelElementType();
        Documentation typeDocumentation = type.getAnnotation(Documentation.class);
        DocumentationProvider typeDocumentationProvider = type.getAnnotation(DocumentationProvider.class);
        if (typeDocumentation != null) {
            initContext(type, "$contentHelp$", typeDocumentation, DocumentationMergeStrategy.APPEND); //$NON-NLS-1$
        } else if (typeDocumentationProvider != null) {
            initContext(typeDocumentationProvider, DocumentationMergeStrategy.APPEND);
        }

        addGeneratedInformation(modelElement, property);
    }
    
    private void addGeneratedInformation(final IModelElement modelElement, final ModelProperty property) {
        StringBuffer buffer = new StringBuffer();
        
        if (property instanceof ValueProperty) {
            final ValueProperty valueProperty = (ValueProperty)property;
            
            final String defaultValue = SapphireCommonUtil.getDefaultValueLabel(modelElement, valueProperty);
            if (defaultValue != null) {
                buffer.append( NLS.bind(Resources.defaultValueInfoMessage, defaultValue) );
                buffer.append(LINE_BREAK); 
            }

            NumericRange rangeAnnotation = property.getAnnotation(NumericRange.class);
            if (rangeAnnotation != null) {
                final String min = rangeAnnotation.min();
                final String max = rangeAnnotation.max();
                if (min != null && min.length() > 0) {
                    buffer.append( NLS.bind(Resources.minValueInfoMessage, SapphireCommonUtil.normalizeForDisplay(valueProperty, min)) );
                    buffer.append(LINE_BREAK); 
                }
                if (max != null && max.length() > 0) {
                    buffer.append( NLS.bind(Resources.maxValueInfoMessage, SapphireCommonUtil.normalizeForDisplay(valueProperty, max)) );
                    buffer.append(LINE_BREAK); 
                }
            }
        }
        
        Required nonNullAnnotation = property.getAnnotation(Required.class);
        if (nonNullAnnotation != null) {
            buffer.append( NLS.bind(Resources.nonNullMessage, property.getLabel(false, CapitalizationType.FIRST_WORD_ONLY, false)) );
            buffer.append(LINE_BREAK); 
        }
        
        ReadOnly readOnlyAnnotation = property.getAnnotation(ReadOnly .class);
        if (readOnlyAnnotation != null) {
            buffer.append( NLS.bind(Resources.readOnlyMessage, property.getLabel(false, CapitalizationType.FIRST_WORD_ONLY, false)) );
            buffer.append(LINE_BREAK); 
        }
        
        if (buffer.length() > 0) {
            appendContent(buffer.toString());
        }
    }

    private void initContext(final ModelMetadataItem property, final String key, final Documentation documentation, DocumentationMergeStrategy strategy) {
        if (strategy == null) {
            strategy = documentation.mergeStrategy();
        }
        
        final LocalizationService localization = property.getLocalizationService();
        
        String res = localization.text( documentation.content(), CapitalizationType.NO_CAPS, false );
        if (strategy == DocumentationMergeStrategy.PREPEND) {
            prependContent(res);
        } else {
            appendContent(res);
        }

        final List<DocumentationResource> topics = new ArrayList<DocumentationResource>();
        
        for( Topic topic : documentation.topics() ) 
        {
            final String label = localization.text( topic.label(), CapitalizationType.TITLE_STYLE, false );
            topics.add( new DocumentationResource( label, topic.href() ) );
        }
        
        if (strategy == DocumentationMergeStrategy.PREPEND) {
            this.topics.addAll( 0, topics );
        } else {
            this.topics.addAll( topics );
        }
    }

    private DocumentationData initContext(final DocumentationProvider documentationProvider, DocumentationMergeStrategy strategy) {            
        final Class<? extends DocumentationProviderImpl> documentationProviderClass = documentationProvider.impl();
        
        try {
            DocumentationProviderImpl provider = documentationProviderClass.newInstance();
            DocumentationData data = provider.getDocumentationData();
            if (data != null) {
                if (strategy == null) {
                    strategy = data.getMergeStrategy(); 
                }
                if (strategy == DocumentationMergeStrategy.PREPEND) {
                    prependContent(data.getContent());
                    this.topics.addAll( 0, data.getTopics() );
                } else {
                    appendContent(data.getContent());
                    this.topics.addAll( data.getTopics() );
                }
                return data;
            }
        } catch( Exception e ) {
            LoggingService.log( e );
        }
        return null;
    }
    
    private void appendContent(final String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        
        final String decodedStr = DocumentationUtil.decodeDocumentationTags(str);
        
        if (this.content == null){
            this.content = decodedStr;
        } else {
            StringBuffer buf = new StringBuffer(this.content);
            if (this.content.endsWith(DocumentationUtil.NEW_LINE)) {
                buf.append(DocumentationUtil.NEW_LINE);   
            } else {
                buf.append(DocumentationUtil.NEW_LINE_2); 
            }
            buf.append(decodedStr);
            this.content = buf.toString();
        }
    }
    
    private void prependContent(final String str) {
        if (str == null || str.length() == 0) {
            return;
        }
        
        final String decodedStr = DocumentationUtil.decodeDocumentationTags(str);

        if (this.content == null){
            this.content = decodedStr;
        } else {
            StringBuffer buf = new StringBuffer(decodedStr);
            if (decodedStr.endsWith(DocumentationUtil.NEW_LINE)) {
                buf.append(DocumentationUtil.NEW_LINE);   
            } else {
                buf.append(DocumentationUtil.NEW_LINE_2); 
            }
            buf.append(this.content);
            this.content = buf.toString();
        }
    }
    
    public String getTitle() {
        return this.title;
    }

    public String getStyledText() {
        return null;
    }

    public String getCategory(IHelpResource topic) {
        return null;
    }

    public IHelpResource[] getRelatedTopics()
    {
        final IHelpResource[] res = new IHelpResource[ this.topics.size() ];
        
        for( int i = 0; i < res.length; i++ )
        {
            final DocumentationResource resource = this.topics.get( i );
            
            res[ i ] = new IHelpResource()
            {
                public String getLabel()
                {
                    return resource.getLabel();
                }
                
                public String getHref()
                {
                    return resource.getUrl();
                }
            };
        }
        
        return res;
    }

    public String getText() {
        return this.content;
    }
    
    private static final class Resources
    
        extends NLS
    
    {
        public static String minValueInfoMessage;
        public static String maxValueInfoMessage;
        public static String defaultValueInfoMessage;
        public static String nonNullMessage;
        public static String readOnlyMessage;
        
        static
        {
            initializeMessages( SapphireHelpContext.class.getName(), Resources.class );
        }
    }

}