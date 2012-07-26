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

package org.eclipse.sapphire.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.ui.def.ConditionalDef;
import org.eclipse.sapphire.ui.def.PartDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ConditionalPart extends SapphirePart
{
    private ConditionalDef def;
    private FunctionResult conditionFunctionResult;
    private List<SapphirePart> currentBranchContent;
    private List<SapphirePart> currentBranchContentReadOnly;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.def = (ConditionalDef) this.definition;
        
        this.conditionFunctionResult = initExpression
        (
            getModelElement(),
            this.def.getCondition().getContent(), 
            Boolean.class,
            Literal.FALSE,
            new Runnable()
            {
                public void run()
                {
                    handleConditionChanged();
                }
            }
        );
        
        this.currentBranchContent = new ArrayList<SapphirePart>();
        this.currentBranchContentReadOnly = Collections.unmodifiableList( this.currentBranchContent );
        
        handleConditionChanged( true );
    }
    
    private void handleConditionChanged()
    {
        handleConditionChanged( false );
    }
    
    private void handleConditionChanged( final boolean initializing )
    {
        boolean newConditionState = (Boolean) this.conditionFunctionResult.value();
        
        for( SapphirePart part : this.currentBranchContent )
        {
            part.dispose();
        }
        
        this.currentBranchContent.clear();
        
        final IModelElement element = getLocalModelElement();
        
        final Listener childPartListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof ValidationChangedEvent )
                {
                    updateValidationState();
                }
            }
        };
    
        for( PartDef childPartDef : ( newConditionState ? this.def.getThenContent() : this.def.getElseContent() ) )
        {
            final SapphirePart childPart = create( this, element, childPartDef, this.params );
            this.currentBranchContent.add( childPart );
            childPart.attach( childPartListener );
        }
        
        updateValidationState();
        
        if( ! initializing )
        {
            broadcast( new StructureChangedEvent( ConditionalPart.this ) );
        }
    }
    
    public List<SapphirePart> getCurrentBranchContent()
    {
        return this.currentBranchContentReadOnly;
    }
    
    public void render( final SapphireRenderingContext context )
    {
        for( SapphirePart child : this.currentBranchContent )
        {
            child.render( context );
        }
    }
    
    @Override
    protected Status computeValidationState()
    {
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();

        for( SapphirePart child : this.currentBranchContent )
        {
            factory.merge( child.getValidationState() );
        }
        
        return factory.create();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        for( SapphirePart child : this.currentBranchContent )
        {
            child.dispose();
        }
        
        if( this.conditionFunctionResult != null )
        {
            this.conditionFunctionResult.dispose();
        }
    }

}
