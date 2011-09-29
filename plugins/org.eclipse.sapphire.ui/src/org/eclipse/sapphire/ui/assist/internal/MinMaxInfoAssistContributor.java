/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui.assist.internal;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.util.internal.SapphireCommonUtil;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContribution;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistSection;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MinMaxInfoAssistContributor

    extends PropertyEditorAssistContributor
    
{
    public MinMaxInfoAssistContributor()
    {
        setId( ID_MIN_MAX_INFO_CONTRIBUTOR );
        setPriority( PRIORITY_MIN_MAX_INFO_CONTRIBUTOR );
    }
    
    @Override
    public void contribute( final PropertyEditorAssistContext context )
    {
        final IModelElement element = context.getModelElement();
        final ModelProperty property = context.getProperty();
        
        if( property instanceof ValueProperty && 
            element.read( (ValueProperty) property ).getText( false ) != null )
        {
            final NumericRange range = property.getAnnotation( NumericRange.class );
            
            if( range != null )
            {
                final ValueProperty valprop = (ValueProperty) property;
                final PropertyEditorAssistSection section = context.getSection( SECTION_ID_INFO );

                final String min = range.min();
                
                if( min.length() > 0 )
                {
                    String label = NLS.bind( Resources.minValueInfoMessage, SapphireCommonUtil.normalizeForDisplay( valprop, min ) );
                    label = "<p>" + escapeForXml( label ) + "</p>";

                    final PropertyEditorAssistContribution contribution = new PropertyEditorAssistContribution();
                    contribution.setText( label );
                    section.addContribution( contribution );
                }
                
                final String max = range.max();
                
                if( max.length() > 0 )
                {
                    String label = NLS.bind( Resources.maxValueInfoMessage, SapphireCommonUtil.normalizeForDisplay( valprop, max ) );
                    label = "<p>" + escapeForXml( label ) + "</p>";

                    final PropertyEditorAssistContribution contribution = new PropertyEditorAssistContribution();
                    contribution.setText( label );
                    section.addContribution( contribution );
                }
            }
        }
    }
    
    private static final class Resources
    
        extends NLS
    
    {
        public static String minValueInfoMessage;
        public static String maxValueInfoMessage;
        
        static
        {
            initializeMessages( MinMaxInfoAssistContributor.class.getName(), Resources.class );
        }
    }
    
}