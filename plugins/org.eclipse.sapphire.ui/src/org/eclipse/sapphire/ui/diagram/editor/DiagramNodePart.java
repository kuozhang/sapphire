/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [342897] Integrate with properties view
 *    Konstantin Komissarchik - [342775] Support EL in IMasterDetailsTreeNodeDef.ImagePath
 *    Ling Hao - [44319] Image specification for diagram parts inconsistent with the rest of sdef 
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramImageDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeProblemDecoratorDef;
import org.eclipse.sapphire.ui.diagram.def.ImagePlacement;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodePart 

    extends SapphirePart
    implements IPropertiesViewContributorPart
    
{
	private static final String DEFAULT_ACTION_ID = "Sapphire.Diagram.Node.Default";
	
	private DiagramNodeTemplate nodeTemplate;
	private IDiagramNodeDef definition;
	private IModelElement modelElement;
	private FunctionResult labelFunctionResult;
	private FunctionResult idFunctionResult;
	private FunctionResult imageDataFunctionResult;
	private List<FunctionResult> imageDecoratorFunctionResults;
	private List<FunctionResult> imageDecoratorDataFunctionResults;
	private ValueProperty labelProperty;
	private SapphireAction defaultAction;
	private SapphireActionHandler defaultActionHandler;
	private ModelPropertyListener modelPropertyListener;
	private PropertiesViewContributionManager propertiesViewContributionManager; 
	private Point leftTopPos = new Point(-1, -1);
	private int nodeWidth = -1;
	private int nodeHeight = -1;
		
    @Override
    protected void init()
    {
        super.init();
        this.nodeTemplate = (DiagramNodeTemplate)getParentPart();
        this.definition = (IDiagramNodeDef)super.definition;
        this.modelElement = getModelElement();
        this.labelFunctionResult = initExpression
        ( 
            this.modelElement,
            this.definition.getLabel().element().getText().getContent(),
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    refreshLabel();
                }
            }
        );
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, this.labelFunctionResult);
        
        this.idFunctionResult = initExpression
        ( 
            this.modelElement,
            this.definition.getInstanceId().getContent(), 
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
        
        if (this.definition.getImage().element() != null)
        {
            this.imageDataFunctionResult = initExpression
            ( 
                this.modelElement,
                this.definition.getImage().element().getImage().getContent(),
                ImageData.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                        refreshImage();
                    }
                }
            );
        }

        // Image decorator functions
        
        ModelElementList<IDiagramImageDecoratorDef> imageDecorators = this.definition.getImageDecorators();
        this.imageDecoratorFunctionResults = new ArrayList<FunctionResult>();
        for (IDiagramImageDecoratorDef imageDecorator : imageDecorators)
        {
            FunctionResult imageResult = initExpression
            ( 
                this.modelElement,
                imageDecorator.getVisibleWhen().getContent(),
                String.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                        refreshDecorator();
                    }
                }
            );
            this.imageDecoratorFunctionResults.add(imageResult);
        }
        
        this.imageDecoratorDataFunctionResults = new ArrayList<FunctionResult>();
        for (IDiagramImageDecoratorDef imageDecorator : imageDecorators)
        {
            FunctionResult imageResult = initExpression
            ( 
                this.modelElement,
                imageDecorator.getImage().getContent(),
                ImageData.class,
                null,
                new Runnable()
                {
                    public void run()
                    {
                        refreshDecorator();
                    }
                }
            );
            this.imageDecoratorDataFunctionResults.add(imageResult);
        }

        // Default Action handler
        this.defaultAction = getAction(DEFAULT_ACTION_ID);
        this.defaultActionHandler = this.defaultAction.getFirstActiveHandler();
        
        // Add model property listener. It listens to all the properties so that the
        // validation status change would trigger node update
        this.modelPropertyListener =  new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                notifyNodeUpdate();
            }
        };
        this.modelElement.addListener(this.modelPropertyListener, "*");
    }
    
    public DiagramNodeTemplate getDiagramNodeTemplate()
    {
        return this.nodeTemplate;
    }
    
    @Override
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
    }    
           
    public List<NodeImageDecorator> getImageDecorators()
    {
        List<NodeImageDecorator> imageDecorators = new ArrayList<NodeImageDecorator>();
        ModelElementList<IDiagramImageDecoratorDef> defs = this.definition.getImageDecorators();
        for (int i = 0; i < this.imageDecoratorFunctionResults.size(); i++)
        {
            FunctionResult result = this.imageDecoratorFunctionResults.get(i);
            if (result != null)
            {
                String show = (String)result.value();
                if (show != null && show.equals("true"))
                {
                    IDiagramImageDecoratorDef def = defs.get(i);
                	FunctionResult imageDataResult = this.imageDecoratorDataFunctionResults.get(i);
                	NodeImageDecorator nodeImageDecorator = new NodeImageDecorator((ImageData)imageDataResult.value(), def);
                    imageDecorators.add(nodeImageDecorator);
                }
            }
        }
        return imageDecorators;
    }
    
    @Override
    public void render(SapphireRenderingContext context)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_DIAGRAM_NODE );
    }
    
    public SapphireAction getDefaultAction()
    {
        return this.defaultAction;
    }
    
    public SapphireActionHandler getDefaultActionHandler()
    {        
        return this.defaultActionHandler;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        if (this.labelFunctionResult != null)
        {
            this.labelFunctionResult.dispose();
        }
        
        if (this.idFunctionResult != null)
        {
            this.idFunctionResult.dispose();
        }
        
        if (this.imageDataFunctionResult != null)
        {
            this.imageDataFunctionResult.dispose();
        }
    
        for (int i = 0; i < this.imageDecoratorFunctionResults.size(); i++)
        {
            FunctionResult result = this.imageDecoratorFunctionResults.get(i);
            if (result != null)
            {
                result.dispose();
            }
        }
        
        for (int i = 0; i < this.imageDecoratorDataFunctionResults.size(); i++)
        {
            FunctionResult result = this.imageDecoratorDataFunctionResults.get(i);
            if (result != null)
            {
                result.dispose();
            }
        }
        

        this.modelElement.removeListener(this.modelPropertyListener, "*");
    }
    
    public String getLabel()
    {
        String label = null;
        
        if( this.labelFunctionResult != null )
        {
            label = (String) this.labelFunctionResult.value();
        }
        
        if( label == null )
        {
            label = "#null#";
        }
        
        return label;
    }

    public void setLabel(String newValue)
    {
        if (this.labelProperty != null)
        {
            this.modelElement.write(this.labelProperty, newValue);
        }
    }
    
    public void refreshLabel()
    {
        notifyNodeUpdate();
    }
    
    public void refreshImage()
    {
        notifyNodeUpdate();
    }
    
    public void refreshDecorator()
    {
        notifyNodeUpdate();
    }

    public boolean canEditLabel()
    {
        return this.labelProperty != null;
    }
    
    public String getNodeTypeId()
    {
        return this.definition.getId().getContent();
    }
    
    public String getInstanceId()
    {
        String id = null;
        
        if( this.idFunctionResult != null )
        {
            id = (String) this.idFunctionResult.value();
        }
                
        return id;		
	}
	
	public boolean canResizeShape()
	{
		return this.definition.isResizable().getContent();
	}
	
	public int getNodeWidth()
	{
		if (this.nodeWidth == -1)
		{
			if (this.definition.getWidth().getContent() != null)
			{
				return this.definition.getWidth().getContent();
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return this.nodeWidth;
		}
	}
		
	public int getNodeHeight()
	{
		if (this.nodeHeight == -1)
		{
			if (this.definition.getHeight().getContent() != null)
			{
				return this.definition.getHeight().getContent();
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return this.nodeHeight;
		}
	}

	public void setNodeBounds(int x, int y, int width, int height)
	{
		setNodePosition(x, y);
		this.nodeWidth = width;
		this.nodeHeight = height;
	}
	
	public Bounds getNodeBounds()
	{
		return new Bounds(this.leftTopPos.getX(), this.leftTopPos.getY(), this.nodeWidth, this.nodeHeight);
	}
	
	public int getHorizontalSpacing()
	{
		if (this.definition.getHorizontalSpacing().getContent() != null)
		{
			return this.definition.getHorizontalSpacing().getContent();
		}
		return 0;
	}
	
	public int getVerticalSpacing()
	{
		if (this.definition.getVerticalSpacing().getContent() != null)
		{
			return this.definition.getVerticalSpacing().getContent();
		}
		return 0;
	}

    public ImageData getImage()
    {
        if( this.imageDataFunctionResult != null )
        {
        	return (ImageData) this.imageDataFunctionResult.value();
        }
        return null;        
    }
    
    public ImagePlacement getImagePlacement()
    {
        if (this.definition.getImage().element() != null)
        {
            return this.definition.getImage().element().getPlacement().getContent();
        }
        return null;
    }
    
    public int getImageWidth()
    {
        if (this.definition.getImage().element() != null)
        {
            if (this.definition.getImage().element().getWidth().getContent() != null)
            {
                this.definition.getImage().element().getWidth().getContent();
            }
        }
        return 0;
    }
    
    public int getImageHeight()
    {
        if (this.definition.getImage().element() != null)
        {
            if (this.definition.getImage().element().getHeight().getContent() != null)
            {
                this.definition.getImage().element().getHeight().getContent();
            }
        }
        return 0;
    }

    public int getLabelWidth()
    {
        if (this.definition.getLabel().element().getWidth().getContent() != null)
        {
            return this.definition.getLabel().element().getWidth().getContent();
        }
        return 0;
    }

	public int getLabelHeight()
	{
		if (this.definition.getLabel().element().getHeight().getContent() != null)
		{
			return this.definition.getLabel().element().getHeight().getContent();
		}
		return 0;
	}
	
	public IDiagramNodeProblemDecoratorDef getProblemIndicatorDef()
	{
		return this.definition.getProblemDecorator();
	}
		
	public void setNodePosition(int x, int y)
	{
		if (this.leftTopPos.getX() != x || this.leftTopPos.getY() != y)
		{
			this.leftTopPos.setX(x); 
			this.leftTopPos.setY(y);
			notifyNodeMove();
		}
	}
	
	public Point getNodePosition()
	{
		return this.leftTopPos;
	}
		
	private void notifyNodeUpdate()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent nue = new DiagramNodeEvent(this);
				((SapphireDiagramPartListener)listener).handleNodeUpdateEvent(nue);
			}
		}
	}
	
	private void notifyNodeMove()
	{
		Set<SapphirePartListener> listeners = this.getListeners();
		for(SapphirePartListener listener : listeners)
		{
			if (listener instanceof SapphireDiagramPartListener)
			{
				DiagramNodeEvent ne = new DiagramNodeEvent(this);
				((SapphireDiagramPartListener)listener).handleNodeMoveEvent(ne);
			}
		}		
	}
	
    public PropertiesViewContributionPart getPropertiesViewContribution()
    {
        if( this.propertiesViewContributionManager == null )
        {
            this.propertiesViewContributionManager = new PropertiesViewContributionManager( this, getLocalModelElement() );
        }
        
        return this.propertiesViewContributionManager.getPropertiesViewContribution();
    }
    
    public final static class NodeImageDecorator {
    	
    	ImageData imageData;
    	IDiagramImageDecoratorDef imageDecoratorDef;
    	
    	public NodeImageDecorator(ImageData imageData, IDiagramImageDecoratorDef imageDecoratorDef) {
    		this.imageData = imageData;
    		this.imageDecoratorDef = imageDecoratorDef;
    	}
    	
    	public ImageData getImageData() {
    		return this.imageData;
    	}
    	
    	public IDiagramImageDecoratorDef getImageDecoratorDef() {
    		return this.imageDecoratorDef;
    	}
    	
    }
    
}
