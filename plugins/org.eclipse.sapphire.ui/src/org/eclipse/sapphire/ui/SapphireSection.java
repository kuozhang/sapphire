/******************************************************************************
 * Copyright (c) 2010 Oracle
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
import static org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil.twdindent;
import static org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil.twlayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;

import java.util.Collections;
import java.util.Set;

import org.eclipse.help.IContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationRef;
import org.eclipse.sapphire.ui.def.ISapphireSectionDef;
import org.eclipse.sapphire.ui.swt.SapphireTextPopup;
import org.eclipse.sapphire.ui.swt.renderer.SapphireActionPresentationManager;
import org.eclipse.sapphire.ui.swt.renderer.SapphireToolBarActionPresentation;
import org.eclipse.sapphire.ui.util.SapphireHelpSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireSection

    extends SapphireComposite
    
{
    private final static String BREAK_TOKEN = "###brk###";
    
    private ISapphireSectionDef definition;
    private SapphireCondition visibleWhenCondition;
    
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
        final String title = this.definition.getLabel().getLocalizedText();
        final String description = this.definition.getDescription().getLocalizedText();
        
        final FormToolkit toolkit = new FormToolkit( context.getDisplay() );
        
        final Section section = toolkit.createSection( context.getComposite(), Section.TITLE_BAR );
        section.setLayoutData( twd() );
        section.setText( title.trim() );
        
        final Composite outerComposite = new Composite( section, SWT.NONE );
        outerComposite.setLayout( twlayout( 1, 0, 0, 0, 0 ) );
        context.adapt( outerComposite );
        
        if( description != null )
        {
            final int index = description.indexOf(BREAK_TOKEN);
            if (index > 0) {
                final String displayDescription = description.substring(0, index);

                final FormText text = new FormText( outerComposite, SWT.NONE );
                text.setLayoutData( twdindent( twd(), 9 ) );
                context.adapt( text );
                
                final StringBuilder buf = new StringBuilder();
                buf.append( "<form><p vspace=\"false\">");
                buf.append( displayDescription );
                buf.append( "<a href=\"action\" nowrap=\"true\">");
                buf.append( Resources.moreDetails );
                buf.append( "</a></p></form>" );
                
                text.setText( buf.toString(), true, false );
                
                text.addHyperlinkListener
                (
                    new HyperlinkAdapter()
                    {
                        @Override
                        public void linkActivated( final HyperlinkEvent event )
                        {
                            final Point cursor = text.getDisplay().getCursorLocation();
                            final Rectangle bounds = text.getBounds();
                            final Point location = text.toDisplay( new Point( bounds.x, bounds.y ) );
                            final Rectangle displayBounds = new Rectangle(location.x, location.y, bounds.width, bounds.height);
                            Point position;
                            if (displayBounds.contains(cursor)) {
                                position = cursor;
                            } else {
                                position = new Point(location.x, location.y + bounds.height + 2 );
                            }
                            StringBuffer buf = new StringBuffer();
                            buf.append(displayDescription);
                            buf.append(description.substring(index + BREAK_TOKEN.length(), description.length()));
                            
                            final SapphireTextPopup popup = new SapphireTextPopup(text.getDisplay(), position);
                            popup.setText(buf.toString());
                            popup.open();
                        }
                    }
                );
            }  
            else {
                final Label descriptionControl = new Label( outerComposite, SWT.WRAP );
                descriptionControl.setLayoutData( twdindent( twd(), 9 ) );
                descriptionControl.setText( description.trim() );
            }
        }
        
        final Composite innerComposite = new Composite( outerComposite, SWT.NONE );
        innerComposite.setLayout( glayout( 2, 0, 0, ( description != null ? 8 : 0 ), 0 ) );
        innerComposite.setLayoutData( twd() );
        context.adapt( innerComposite );
        
        final SapphireActionGroup actions = getActions();
        final SapphireActionPresentationManager actionPresentationManager = new SapphireActionPresentationManager( context, actions );
        final SapphireToolBarActionPresentation toolBarActionsPresentation = new SapphireToolBarActionPresentation( actionPresentationManager );
        
        final ToolBar toolbar = new ToolBar( section, SWT.FLAT | SWT.HORIZONTAL );
        toolBarActionsPresentation.setToolBar( toolbar );
        toolBarActionsPresentation.render();
        section.setTextClient( toolbar );
        
        toolkit.paintBordersFor( section );
        section.setClient( outerComposite );
        
        return innerComposite;
    }
    
    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_SECTION );
    }

    @Override
    public IContext getDocumentationContext()
    {
        final ISapphireDocumentationDef def = this.definition.getDocumentationDef().element();
        if ( def != null )
        {
            IContext context = SapphireHelpSystem.getContext( def );
            if ( context != null )
            {
                return context;
            }
        }

        final ISapphireDocumentationRef documentationRef = this.definition.getDocumentationRef().element();
        if ( documentationRef != null )
        {
            final ISapphireDocumentationDef documentationDef2 = documentationRef.resolve();
            if ( documentationDef2 != null ) 
            {
                return SapphireHelpSystem.getContext( documentationDef2 );
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

    private static final class Resources
    
        extends NLS
    
    {
        public static String moreDetails;
        
        static
        {
            initializeMessages( SapphireSection.class.getName(), Resources.class );
        }
    }

}
