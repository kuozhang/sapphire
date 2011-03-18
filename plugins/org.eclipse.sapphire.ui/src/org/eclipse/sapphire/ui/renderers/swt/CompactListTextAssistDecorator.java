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

package org.eclipse.sapphire.ui.renderers.swt;

import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_ASSIST_CONTRIBUTORS;
import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_SUPPRESS_ASSIST_CONTRIBUTORS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ProblemsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class CompactListTextAssistDecorator //extends PropertyEditorAssistDecorator
{
    
//	private SapphirePropertyEditor parentPropertyEditor;
//	private ModelProperty childProperty;
//    private CompactTextBinding binding;
//    
//    public CompactListTextAssistDecorator( final SapphirePropertyEditor propertyEditor,
//    									   final ModelProperty property,
//                                           final SapphireRenderingContext context,
//                                           final Composite parent )
//    {
//    	super(propertyEditor, context, parent);
//    	
//    	this.parentPropertyEditor = propertyEditor;
//    	this.childProperty = property;
//    }
//    
//    public CompactTextBinding getBinding() {
//		return this.binding;
//	}
//
//	public void setBinding(CompactTextBinding binding) {
//		this.binding = binding;
//	}
//    
//    public void refresh()
//    {
//    	//final IModelElement element = getBinding() != null ? getBinding().getModelElement() : null;
//    	final IModelElement element = null;
//        final boolean enabled 
//            = ( element == null ? false : element.isPropertyEnabled( this.childProperty ) );
//        
//        if( enabled )
//        {
//            if( this.childProperty instanceof ValueProperty )
//            {
//                final Value<?> value = element.read( (ValueProperty) this.childProperty );
//                this.problem = value.validate();
//            }
//            else if( this.childProperty instanceof ListProperty )
//            {
//                final ModelElementList<?> list = element.read( (ListProperty) this.childProperty );
//                this.problem = list.validate();
//            }
//            else
//            {
//                throw new IllegalStateException( this.childProperty.getClass().getName() );
//            }
//            
//	        final SapphirePropertyEditor propertyEditor = element != null ? this.parentPropertyEditor.getChildPropertyEditor( element, this.childProperty ) : null;
//            this.assistContext = element != null ? new PropertyEditorAssistContext( propertyEditor, getUiContext() ) : null;
//            
//            final List<PropertyEditorAssistContributor> contributors 
//                = new ArrayList<PropertyEditorAssistContributor>( SYSTEM_CONTRIBUTORS );
//            
//            contributors.add( new ProblemsAssistContributor( this.problem ) );
//            
//
//            Collection<String> contributorsToSuppress = propertyEditor != null ? propertyEditor.getRenderingHint( HINT_SUPPRESS_ASSIST_CONTRIBUTORS, Collections.<String>emptyList() ) : null;
//            for( String id : contributorsToSuppress )
//            {
//                for( Iterator<PropertyEditorAssistContributor> itr = contributors.iterator(); itr.hasNext(); )
//                {
//                    final PropertyEditorAssistContributor contributor = itr.next();
//                    
//                    if( contributor.getId().equals( id ) )
//                    {
//                        itr.remove();
//                        break;
//                    }
//                }
//            }
//            
//            Collection<Class<?>> additionalContributors = propertyEditor.getRenderingHint( HINT_ASSIST_CONTRIBUTORS, Collections.<Class<?>>emptyList() );
//            for( Class<?> cl : additionalContributors )
//            {
//                try
//                {
//                    contributors.add( (PropertyEditorAssistContributor) cl.newInstance() );
//                }
//                catch( Exception e )
//                {
//                    SapphireUiFrameworkPlugin.log( e );
//                }
//            }
//
//            Collections.sort
//            ( 
//                contributors, 
//                new Comparator<PropertyEditorAssistContributor>()
//                {
//                    public int compare( final PropertyEditorAssistContributor c1,
//                                        final PropertyEditorAssistContributor c2 )
//                    {
//                        return ( c1.getPriority() - c2.getPriority() ); 
//                    }
//                }
//            );
//            
//            for( PropertyEditorAssistContributor c : contributors )
//            {
//                c.contribute( this.assistContext );
//            }
//            
//            if( this.assistContext.isEmpty() )
//            {
//                this.assistContext = null;
//            }
//            else
//            {
//                final int valResultSeverity = this.problem.getSeverity();
//                
//                if( valResultSeverity != Status.ERROR && valResultSeverity != Status.WARNING && valResultSeverity != Status.INFO )
//                {
//                    this.problem = null;
//                }
//            }
//        }
//        else
//        {
//            this.assistContext = null;
//            this.problem = null;
//        }
//
//        refreshImageAndCursor();
//    }
    
}
