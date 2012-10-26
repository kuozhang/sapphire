/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails.internal;

import java.util.List;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.FormPart;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.PageBookPart;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.SectionPart;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode.NodeListEvent;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class OutlineNodeShowNextProblemActionHandler extends SapphireActionHandler
{
    private final Status.Severity severity;
    private final Listener nodeListener;
    private final Listener sectionListener;
    
    public OutlineNodeShowNextProblemActionHandler( final Status.Severity severity )
    {
        this.severity = severity;
        
        this.nodeListener = new FilteredListener<NodeListEvent>()
        {
            @Override
            protected void handleTypedEvent( final NodeListEvent event )
            {
                attach( event.part() );
                refreshVisibility();
            }
        };
        
        this.sectionListener = new FilteredListener<PartValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( final PartValidationEvent event )
            {
                refreshVisibility();
            }
        };
    }
    
    @Override
    public void init( final SapphireAction action,
                      final ActionHandlerDef def )
    {
        super.init( action, def );
        
        attach( ( (MasterDetailsContentNode) getPart() ).nearest( MasterDetailsEditorPagePart.class ).outline().getRoot() );
        
        attach
        (
            new FilteredListener<DisposeEvent>()
            {
                @Override
                protected void handleTypedEvent( final DisposeEvent event )
                {
                    detach( ( (MasterDetailsContentNode) getPart() ).nearest( MasterDetailsEditorPagePart.class ).outline().getRoot() );
                }
            }
        );
        
        refreshVisibility();
    }
    
    private void attach( final MasterDetailsContentNode node )
    {
        node.attach( this.nodeListener );
        
        for( MasterDetailsContentNode child : node.nodes() )
        {
            attach( child );
        }
        
        for( SectionPart section : node.getSections() )
        {
            section.attach( this.sectionListener );
        }
    }

    private void detach( final MasterDetailsContentNode node )
    {
        node.detach( this.nodeListener );
        
        for( MasterDetailsContentNode child : node.nodes() )
        {
            detach( child );
        }
        
        for( SectionPart section : node.getSections() )
        {
            section.detach( this.sectionListener );
        }
    }
    
    private void refreshVisibility()
    {
        final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();
        final MasterDetailsContentNode nextProblemNode = findNextProblem( node );
        
        setVisible( nextProblemNode != null );
    }

    private MasterDetailsContentNode findNextProblem( final MasterDetailsContentNode node )
    {
        for( MasterDetailsContentNode child : node.nodes().visible() )
        {
            final MasterDetailsContentNode result = findNextProblemDown( child );
            
            if( result != null )
            {
                return result;
            }
        }
        
        return findNextProblemUp( node );
    }
    
    private MasterDetailsContentNode findNextProblemDown( final MasterDetailsContentNode node )
    {
        for( SectionPart section : node.getSections() )
        {
            if( section.visible() && section.validation().severity() == this.severity )
            {
                return node;
            }
        }

        for( MasterDetailsContentNode child : node.nodes().visible() )
        {
            final MasterDetailsContentNode result = findNextProblemDown( child );
            
            if( result != null )
            {
                return result;
            }
        }
        
        return null;
    }
    
    private MasterDetailsContentNode findNextProblemUp( final MasterDetailsContentNode node )
    {
        final ISapphirePart p = node.getParentPart();
        
        if( p instanceof MasterDetailsContentNode )
        {
            final MasterDetailsContentNode parent = (MasterDetailsContentNode) p;
            boolean seenInputNode = false;
            
            for( MasterDetailsContentNode n : parent.nodes().visible() )
            {
                if( seenInputNode )
                {
                    final MasterDetailsContentNode res = findNextProblemDown( n );
                    
                    if( res != null )
                    {
                        return res;
                    }
                }
                else if( n == node )
                {
                    seenInputNode = true;
                }
            }
            
            return findNextProblemUp( parent );
        }
        
        return null;
    }
    
    private PropertyEditorPart findFirstProblem( final List<SectionPart> sections )
    {
        for( SectionPart section : sections )
        {
            final PropertyEditorPart res = findFirstProblem( section );
            
            if( res != null )
            {
                return res;
            }
        }
        
        return null;
    }
    
    private PropertyEditorPart findFirstProblem( final SapphirePart part )
    {
        if( part != null )
        {
            if( part instanceof PropertyEditorPart )
            {
                if( part.validation().severity() == this.severity )
                {
                    return (PropertyEditorPart) part;
                }
            }
            else if( part instanceof FormPart )
            {
                for( SapphirePart p : ( (FormPart) part ).getChildParts() )
                {
                    final PropertyEditorPart result = findFirstProblem( p );
                    
                    if( result != null )
                    {
                        return result;
                    }
                }
            }
            else if( part instanceof PageBookPart )
            {
                return findFirstProblem( ( (PageBookPart) part ).getCurrentPage() );
            }
        }
        
        return null;
    }

    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final MasterDetailsContentNode node = (MasterDetailsContentNode) getPart();
        final MasterDetailsContentNode nextProblemNode = findNextProblem( node );
        
        if( nextProblemNode != null )
        {
            nextProblemNode.select();
            
            final PropertyEditorPart firstProblemPropertyEditor = findFirstProblem( nextProblemNode.getSections() );
            
            if( firstProblemPropertyEditor != null )
            {
                firstProblemPropertyEditor.setFocus();
            }
        }
        
        return null;
    }
   
}
