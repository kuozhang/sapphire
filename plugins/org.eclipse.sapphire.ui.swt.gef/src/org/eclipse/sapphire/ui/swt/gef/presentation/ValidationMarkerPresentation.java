/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.PartValidationEvent;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContext;
import org.eclipse.sapphire.ui.assist.PropertyEditorAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ActionsSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.FactsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.InfoSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ProblemsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ProblemsSectionAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDialog;
import org.eclipse.sapphire.ui.assist.internal.ResetActionsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.RestoreInitialValueActionsAssistContributor;
import org.eclipse.sapphire.ui.assist.internal.ShowInSourceActionAssistContributor;
import org.eclipse.sapphire.ui.diagram.editor.ContainerShapePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapePart;
import org.eclipse.sapphire.ui.diagram.editor.ValidationMarkerPart;
import org.eclipse.sapphire.ui.diagram.shape.def.ValidationMarkerSize;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;
import org.eclipse.sapphire.ui.swt.SwtResourceCache;
import org.eclipse.sapphire.ui.swt.gef.DiagramConfigurationManager;
import org.eclipse.sapphire.ui.swt.gef.DiagramRenderingContext;
import org.eclipse.sapphire.ui.swt.gef.figures.SmoothImageFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

@SuppressWarnings("restriction")
public class ValidationMarkerPresentation extends ShapePresentation 
{
    private static final ImageDescriptor IMG_ERROR_SMALL
			= SwtRendererUtil.createImageDescriptor( SmoothImageFigure.class, "error_small.png" );
    private static final ImageDescriptor IMG_ERROR
			= SwtRendererUtil.createImageDescriptor( SmoothImageFigure.class, "error.gif" );
    private static final ImageDescriptor IMG_WARNING_SMALL
			= SwtRendererUtil.createImageDescriptor( SmoothImageFigure.class, "warning_small.png" );
    private static final ImageDescriptor IMG_WARNING
			= SwtRendererUtil.createImageDescriptor( SmoothImageFigure.class, "warning.gif" );
    
    private static final List<Class<? extends PropertyEditorAssistContributor>> SYSTEM_CONTRIBUTORS
    		= new ArrayList<Class<? extends PropertyEditorAssistContributor>>();

	static
	{
	    SYSTEM_CONTRIBUTORS.add( InfoSectionAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( FactsAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( ProblemsSectionAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( ActionsSectionAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( ResetActionsAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( RestoreInitialValueActionsAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( ShowInSourceActionAssistContributor.class );
	    SYSTEM_CONTRIBUTORS.add( ProblemsAssistContributor.class );
	}
	
    private SwtResourceCache imageCache;
    private Element element;
	private SmoothImageFigure imageFigure;
	private PropertyEditorAssistContext assistContext;
	private Status problem;
	private final List<PropertyEditorAssistContributor> contributors;
	private Listener validationListener;
	
	public ValidationMarkerPresentation(ShapePresentation parent, ValidationMarkerPart validationMarkerPart, 
			DiagramConfigurationManager configManager)
	{
		super(parent, validationMarkerPart, configManager);
		DiagramNodePart nodePart = validationMarkerPart.nearest(DiagramNodePart.class);
		this.element = validationMarkerPart.getLocalModelElement();
		this.imageCache = nodePart.getSwtResourceCache();
		this.imageFigure = new SmoothImageFigure();
		setFigure(this.imageFigure);
		
		this.contributors = new ArrayList<PropertyEditorAssistContributor>();
		
        final List<Class<?>> contributorClasses = new ArrayList<Class<?>>();        
        contributorClasses.addAll( SYSTEM_CONTRIBUTORS );
        for( Class<?> cl : contributorClasses )
        {
            try
            {
                this.contributors.add( (PropertyEditorAssistContributor) cl.newInstance() );
            }
            catch( Exception e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }
        }
        final Listener contributorListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                Display.getDefault().asyncExec
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            refreshAssistContext();
                        }
                    }
                );
            }
        };
        
        for( PropertyEditorAssistContributor contributor : this.contributors )
        {
            try
            {
                contributor.init( validationMarkerPart );
            }
            catch( Exception e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }
            
            contributor.attach( contributorListener );
        }
        
        Collections.sort
        ( 
            this.contributors, 
            new Comparator<PropertyEditorAssistContributor>()
            {
                public int compare( final PropertyEditorAssistContributor c1,
                                    final PropertyEditorAssistContributor c2 )
                {
                    return ( c1.getPriority() - c2.getPriority() ); 
                }
            }
        );
        
        this.validationListener = new FilteredListener<PartValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( final PartValidationEvent event )
            {
            	refresh();
            }
        };
        getContainerPart().attach(this.validationListener);
        		
		addMouseListener();
		
		refresh();
	}

	public ValidationMarkerPart getValidationMarkerPart()
	{
		return (ValidationMarkerPart)getPart();
	}
	
	public ValidationMarkerSize getSize()
	{
		return getValidationMarkerPart().getSize();
	}
	
	@Override
	public void dispose()
	{
		((ContainerShapePart)this.getPart().getParentPart()).detach(this.validationListener);
	}
	
	private ContainerShapePart getContainerPart()
	{
		return (ContainerShapePart)this.getPart().getParentPart();
	}
	
	private void refresh()
	{
		refreshAssistContext();
		
		Image image = null;
		
		Status status = getContainerPart().validation();
		ValidationMarkerSize size = getSize();
		if (status.severity() != Status.Severity.OK) 
		{
			if (status.severity() == Status.Severity.WARNING) 
			{
				if (size == ValidationMarkerSize.SMALL) 
				{
					image = imageCache.image(IMG_WARNING_SMALL);
				} 
				else 
				{
					image = imageCache.image(IMG_WARNING);					
				}
			} 
			else if (status.severity() == Status.Severity.ERROR) 
			{
				if (size == ValidationMarkerSize.SMALL) 
				{
					image = imageCache.image(IMG_ERROR_SMALL);
				} 
				else 
				{
					image = imageCache.image(IMG_ERROR);
				}
			}
		}
		this.imageFigure.setImage(image);
	}
	
	private void addMouseListener()
	{
		this.imageFigure.addMouseMotionListener(new MouseMotionListener.Stub() 
		{
			@Override
			public void mouseEntered(MouseEvent me) 
			{
				ValidationMarkerPresentation.this.imageFigure.setCursor( Display.getCurrent().getSystemCursor( SWT.CURSOR_HAND ) );
			}

		});
		
		this.imageFigure.addMouseListener(new MouseListener.Stub()
		{
			@Override
			public void mousePressed(MouseEvent me)
			{
				me.consume();
				openAssistDialog();
			}

		});		
	}
	
	private void openAssistDialog()
	{
		PropertyEditorAssistContext assistContext = getAssistContext();
        if( assistContext != null && ! assistContext.isEmpty() )
        {
        	// hide the context menu pad
        	getConfigurationManager().getDiagramEditor().getContextButtonManager().hideContextButtonsInstantly();
        	        	
            final org.eclipse.draw2d.geometry.Rectangle decoratorControlBounds = this.imageFigure.getBounds().getCopy();
            org.eclipse.draw2d.geometry.Point draw2dPosition = new org.eclipse.draw2d.geometry.Point( decoratorControlBounds.x + decoratorControlBounds.width + 2, decoratorControlBounds.y + 2 );
            FigureCanvas canvas = getConfigurationManager().getDiagramEditor().getFigureCanvas();
            this.imageFigure.translateToAbsolute(draw2dPosition);
            Point swtPosition = new Point(draw2dPosition.x, draw2dPosition.y);
            swtPosition = canvas.getDisplay().map(canvas, null, swtPosition);
            
            final PropertyEditorAssistDialog dialog = new PropertyEditorAssistDialog( getConfigurationManager().getDiagramEditor().getEditorSite().getShell(), 
            		swtPosition, this.assistContext );
            
            dialog.open();
        }
	}
	
	private PropertyEditorAssistContext getAssistContext()
	{
		if (this.assistContext == null)
		{
    		ShapePart parentPart = (ShapePart)this.getValidationMarkerPart().getParentPart();
    		DiagramRenderingContext context = getConfigurationManager().getDiagramRenderingContextCache().get(parentPart);	            
			
	        this.assistContext = new PropertyEditorAssistContext( this.getPart(), context );
	        this.problem = this.element.validation();
            for( PropertyEditorAssistContributor c : this.contributors )
            {
                c.contribute( this.assistContext );
            }	            
	        
	        if( this.assistContext.isEmpty() )
	        {
	            this.assistContext = null;
	        }
	        else
	        {
	            final Status.Severity valResultSeverity = this.problem.severity();
	            
	            if( valResultSeverity != Status.Severity.ERROR && valResultSeverity != Status.Severity.WARNING )
	            {
	                this.problem = null;
	            }
	        }					
		}
		return this.assistContext;
	}
	
	private void refreshAssistContext()
	{
		this.assistContext = null;
	}
}
