/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Ling Hao - [bugzilla 329115] support more details link for long descriptions
 *                 [bugzilla 329114] rewrite context help binding feature
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import static org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil.twd;
import static org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil.twlayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;

import java.util.Collections;
import java.util.Set;

import org.eclipse.help.IContext;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.ui.def.ISapphireDocumentation;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationRef;
import org.eclipse.sapphire.ui.def.ISapphireSectionDef;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentationManager;
import org.eclipse.sapphire.ui.swt.renderer.SapphireToolBarActionPresentation;
import org.eclipse.sapphire.ui.util.SapphireHelpSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireSection

    extends SapphireComposite
    
{
    private ISapphireSectionDef definition;
    private SapphireCondition visibleWhenCondition;
    private Section section;
    private FunctionResult titleFunctionResult;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.definition = (ISapphireSectionDef) super.definition;
        
        this.visibleWhenCondition = null;
        
        final Class<?> visibleWhenConditionClass = this.definition.getVisibleWhenConditionClass().resolve();
        
        if( visibleWhenConditionClass != null )
        {
            final String parameter = this.definition.getVisibleWhenConditionParameter().getText();
            this.visibleWhenCondition = SapphireCondition.create( this, visibleWhenConditionClass, parameter );
        }
    }
    
    @Override
    protected Composite createOuterComposite( final SapphireRenderingContext context )
    {
        final FormToolkit toolkit = new FormToolkit( context.getDisplay() );
        
        this.section = toolkit.createSection( context.getComposite(), Section.TITLE_BAR );
        this.section.setLayoutData( twd() );
        
        this.titleFunctionResult = initExpression
        ( 
            this.definition.getLabel(), 
            new Runnable()
            {
                public void run()
                {
                    refreshTitle();
                }
            }
        );
        
        refreshTitle();
        
        final Composite outerComposite = new Composite( this.section, SWT.NONE );
        outerComposite.setLayout( twlayout( 1, 0, 0, 0, 0 ) );
        context.adapt( outerComposite );
        
        final Composite innerComposite = new Composite( outerComposite, SWT.NONE );
        innerComposite.setLayout( glayout( 2, 0, 0, 0, 0 ) );
        innerComposite.setLayoutData( twd() );
        context.adapt( innerComposite );
        
        final SapphireActionGroup actions = getActions();
        final SapphireActionPresentationManager actionPresentationManager = new SapphireActionPresentationManager( context, actions );
        final SapphireToolBarActionPresentation toolBarActionsPresentation = new SapphireToolBarActionPresentation( actionPresentationManager );
        
        final ToolBar toolbar = new ToolBar( this.section, SWT.FLAT | SWT.HORIZONTAL );
        toolBarActionsPresentation.setToolBar( toolbar );
        toolBarActionsPresentation.render();
        this.section.setTextClient( toolbar );
        
        toolkit.paintBordersFor( this.section );
        this.section.setClient( outerComposite );
        
        return innerComposite;
    }
    
    private void refreshTitle()
    {
        String title = null;
        
        if( this.titleFunctionResult != null )
        {
            title = (String) this.titleFunctionResult.value();
        }
        
        if( title == null )
        {
            title = "#null#";
        }
        else
        {
            title = title.trim();
            title = this.definition.adapt( LocalizationService.class ).transform( title, CapitalizationType.TITLE_STYLE, false );
        }
        
        this.section.setText( title.trim() );
    }

    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_SECTION );
    }

    @Override
    public IContext getDocumentationContext()
    {
        final ISapphireDocumentation doc = this.definition.getDocumentation().element();
        
        if( doc != null )
        {
            ISapphireDocumentationDef docdef = null;
            
            if( doc instanceof ISapphireDocumentationDef )
            {
                docdef = (ISapphireDocumentationDef) doc;
            }
            else
            {
                docdef = ( (ISapphireDocumentationRef) doc ).resolve();
            }
            
            if( docdef != null )
            {
                return SapphireHelpSystem.getContext( docdef );
            }
        }
        
        return null;
    }

    public SapphireCondition getVisibleWhenCondition()
    {
        return this.visibleWhenCondition;
    }
    
    public boolean checkVisibleWhenCondition()
    {
        if( this.visibleWhenCondition != null )
        {
            return this.visibleWhenCondition.getConditionState();
        }
        
        return true;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.visibleWhenCondition != null )
        {
            this.visibleWhenCondition.dispose();
        }
    }

}
