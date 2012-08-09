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

package org.eclipse.sapphire.ui.form.editors.masterdetails;

import static org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil.twd;
import static org.eclipse.sapphire.ui.swt.renderer.SwtUtil.runOnDisplayThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.ElementValidationEvent;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.PropertyContentEvent;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.modeling.PropertyValidationEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.el.AndFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.SapphireSection;
import org.eclipse.sapphire.ui.def.ISapphireParam;
import org.eclipse.sapphire.ui.def.ISapphireSectionDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeChildDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeFactoryCaseDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeInclude;
import org.eclipse.sapphire.util.ListFactory;
import org.eclipse.sapphire.util.MapFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class MasterDetailsContentNode

    extends SapphirePart
    implements IPropertiesViewContributorPart
    
{
    private static final ImageData IMG_CONTAINER_NODE
        = ImageData.createFromClassLoader( MasterDetailsContentNode.class, "ContainerNode.png" );

    private static final ImageData IMG_LEAF_NODE
        = ImageData.createFromClassLoader( MasterDetailsContentNode.class, "LeafNode.png" );

    private MasterDetailsContentOutline contentTree;
    private MasterDetailsContentNodeDef definition;
    private IModelElement modelElement;
    private ImpliedElementProperty modelElementProperty;
    private Listener modelElementListener;
    private MasterDetailsContentNode parentNode;
    private FunctionResult labelFunctionResult;
    private ImageManager imageManager;
    private Listener childPartListener;
    private List<Object> rawChildren;
    private MasterDetailsContentNodeList nodes = new MasterDetailsContentNodeList( Collections.<MasterDetailsContentNode>emptyList() );
    private List<SapphireSection> sections;
    private PropertiesViewContributionManager propertiesViewContributionManager;
    private boolean expanded;
    private boolean transformLabelCase = true;
    
    @Override
    protected void init()
    {
        super.init();
        
        final ISapphirePart parent = getParentPart();

        if( parent instanceof MasterDetailsContentNode )
        {
            this.parentNode = (MasterDetailsContentNode) parent;
        }
        else
        {
            this.parentNode = null;
        }
        
        this.contentTree = nearest( MasterDetailsEditorPagePart.class ).outline();
        this.definition = (MasterDetailsContentNodeDef) super.definition;
        
        this.modelElementProperty = (ImpliedElementProperty) resolve( this.definition.getProperty().getContent() );
        
        if( this.modelElementProperty != null )
        {
            this.modelElement = getModelElement().read( this.modelElementProperty );
            
            this.modelElementListener = new FilteredListener<PropertyEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyEvent event )
                {
                    handleModelElementChange( event );
                }
            };
            
            this.modelElement.attach( this.modelElementListener );
        }
        else
        {
            this.modelElement = getModelElement();
        }
        
        this.expanded = false;
        
        final Listener elementValidationListener = new FilteredListener<ElementValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( final ElementValidationEvent event )
            {
                updateValidationState();
            }
        };
        
        this.modelElement.attach( elementValidationListener );
        
        this.childPartListener = new Listener()
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
        
        // Label
        
        this.labelFunctionResult = initExpression
        ( 
            this.modelElement, 
            this.definition.getLabel().getContent(),
            String.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    broadcast( new LabelChangedEvent( MasterDetailsContentNode.this ) );
                }
            }
        );
        
        // Image
        
        final Literal defaultImageLiteral = Literal.create( ( this.definition.getChildNodes().isEmpty() ? IMG_LEAF_NODE : IMG_CONTAINER_NODE ) );
        final Function imageFunction = this.definition.getImage().getContent();
        
        this.imageManager = new ImageManager( this.modelElement, imageFunction, defaultImageLiteral );
        
        // Sections and Child Nodes
        
        this.rawChildren = new ArrayList<Object>();
        
        final ListFactory<SapphireSection> sectionsListFactory = ListFactory.start();
        
        for( ISapphireSectionDef secdef : this.definition.getSections() )
        {
            final SapphireSection section = new SapphireSection()
            {
                @Override
                protected Object createSectionLayoutData()
                {
                    return twd();
                }
            };
            
            section.init( this, this.modelElement, secdef, this.params );
            section.attach( this.childPartListener );
            
            sectionsListFactory.add( section );
        }
        
        this.sections = sectionsListFactory.result();
        
        for( MasterDetailsContentNodeChildDef entry : this.definition.getChildNodes() )
        {
            final Map<String,String> params = new HashMap<String,String>( this.params );
            
            if( entry instanceof MasterDetailsContentNodeInclude )
            {
                final MasterDetailsContentNodeInclude inc = (MasterDetailsContentNodeInclude) entry;
                entry = inc.resolve();
                
                if( entry == null )
                {
                    final String msg = NLS.bind( Resources.couldNotResolveNode, inc.getPart() );
                    throw new RuntimeException( msg );
                }

                for( ISapphireParam param : inc.getParams() )
                {
                    final String paramName = param.getName().getText();
                    final String paramValue = param.getValue().getText();
                    
                    if( paramName != null && paramValue != null )
                    {
                        params.put( paramName, paramValue );
                    }
                }
            }

            if( entry instanceof MasterDetailsContentNodeDef )
            {
                final MasterDetailsContentNodeDef def = (MasterDetailsContentNodeDef) entry;
                
                final MasterDetailsContentNode node = new MasterDetailsContentNode();
                node.init( this, this.modelElement, def, params );
                node.attach( this.childPartListener );
                
                this.rawChildren.add( node );
            }
            else if( entry instanceof MasterDetailsContentNodeFactoryDef )
            {
                final MasterDetailsContentNodeFactoryDef def = (MasterDetailsContentNodeFactoryDef) entry;
                
                final ModelProperty property = resolve( getLocalModelElement(), def.getProperty().getContent(), params );
                final NodeFactory factory;
                
                if( property instanceof ListProperty )
                {
                    final ListProperty prop = (ListProperty) property;
                    
                    factory = new NodeFactory( def, params )
                    {
                        @Override
                        public ModelProperty property()
                        {
                            return prop;
                        }
    
                        @Override
                        protected List<IModelElement> elements()
                        {
                            return ListFactory.unmodifiable( getLocalModelElement().read( prop ) );
                        }
                    };
                }
                else if( property instanceof ElementProperty )
                {
                    final ElementProperty prop = (ElementProperty) property;
                    
                    factory = new NodeFactory( def, params )
                    {
                        @Override
                        public ModelProperty property()
                        {
                            return prop;
                        }
    
                        @Override
                        protected List<IModelElement> elements()
                        {
                            final IModelElement element = getLocalModelElement().read( prop ).element();
                            
                            if( element == null )
                            {
                                return Collections.emptyList();
                            }
                            else
                            {
                                return Collections.singletonList( element );
                            }
                        }
                    };
                }
                else
                {
                    throw new IllegalStateException();
                }
                
                this.rawChildren.add( factory );
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        
        refreshNodes();
        
        attach
        (
            new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof VisibilityChangedEvent || event instanceof NodeListEvent )
                    {
                        getContentTree().refreshSelection();
                    }
                    else if( event instanceof DisposeEvent )
                    {
                        getModelElement().detach( elementValidationListener );
                    }
                }
            }
        );
    }
    
    public MasterDetailsContentOutline getContentTree()
    {
        return this.contentTree;
    }

    public MasterDetailsContentNode getParentNode()
    {
        return this.parentNode;
    }

    public boolean isAncestorOf( final MasterDetailsContentNode node )
    {
        MasterDetailsContentNode n = node;
        
        while( n != null )
        {
            if( n == this )
            {
                return true;
            }
            
            n = n.getParentNode();
        }
        
        return false;
    }

    @Override
    public IModelElement getLocalModelElement()
    {
        return this.modelElement;
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
        else
        {
            label = label.trim();
            
            final CapitalizationType capType = ( this.transformLabelCase ? CapitalizationType.TITLE_STYLE : CapitalizationType.NO_CAPS );
            label = this.definition.adapt( LocalizationService.class ).transform( label, capType, false );
        }
        
        return label;
    }

    public ImageDescriptor getImage()
    {
        return this.imageManager.getImage();
    }

    public boolean isExpanded()
    {
        return this.expanded;
    }
    
    public void setExpanded( final boolean expanded )
    {
        setExpanded( expanded, false );
    }
    
    public void setExpanded( final boolean expanded,
                             final boolean applyToChildren )
    {
        if( this.parentNode != null && ! this.parentNode.isExpanded() && expanded == true )
        {
            this.parentNode.setExpanded( true );
        }
        
        if( this.expanded != expanded )
        {
            if( ! expanded )
            {
                final MasterDetailsContentNode selection = getContentTree().getSelectedNode();
                
                if( selection != null && isAncestorOf( selection ) )
                {
                    select();
                }
            }
            
            if( expanded )
            {
                this.expanded = expanded;
                getContentTree().notifyOfNodeExpandedStateChange( this );
            }
        }
            
        if( applyToChildren )
        {
            for( MasterDetailsContentNode child : this.nodes )
            {
                if( ! child.nodes().visible().isEmpty() )
                {
                    child.setExpanded( expanded, applyToChildren );
                }
            }
        }

        if( this.expanded != expanded )
        {
            if( ! expanded )
            {
                this.expanded = expanded;
                getContentTree().notifyOfNodeExpandedStateChange( this );
            }
        }
    }
    
    public List<MasterDetailsContentNode> getExpandedNodes()
    {
        final List<MasterDetailsContentNode> result = new ArrayList<MasterDetailsContentNode>();
        getExpandedNodes( result );
        return result;
    }
    
    public void getExpandedNodes( final List<MasterDetailsContentNode> result )
    {
        if( isExpanded() )
        {
            result.add( this );
            
            for( MasterDetailsContentNode child : this.nodes )
            {
                child.getExpandedNodes( result );
            }
        }
    }
    
    public void select()
    {
        getContentTree().setSelectedNode( this );
    }
    
    public List<SapphireSection> getSections()
    {
        return this.sections;
    }
    
    public List<ModelProperty> getChildNodeFactoryProperties()
    {
        final ArrayList<ModelProperty> properties = new ArrayList<ModelProperty>();
        
        for( Object object : this.rawChildren )
        {
            if( object instanceof NodeFactory )
            {
                final NodeFactory factory = (NodeFactory) object;
                
                if( factory.visible() )
                {
                    properties.add( factory.property() );
                }
            }
        }
        
        return properties;
    }
    
    public boolean isChildNodeFactoryProperty( final ModelProperty property )
    {
        if( this.rawChildren != null )
        {
            for( Object object : this.rawChildren )
            {
                if( object instanceof NodeFactory )
                {
                    final NodeFactory factory = (NodeFactory) object;
                    
                    if( factory.visible() && factory.property() == property )
                    {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public final MasterDetailsContentNodeList nodes()
    {
        return this.nodes;
    }
    
    public MasterDetailsContentNode findNode( final String label )
    {
        for( MasterDetailsContentNode child : this.nodes )
        {
            if( label.equals( child.getLabel() ) )
            {
                return child;
            }
        }
        
        return null;
    }
    
    public MasterDetailsContentNode findNode( final IModelElement element )
    {
        if( getModelElement() == element )
        {
            return this;
        }

        for( MasterDetailsContentNode child : this.nodes )
        {
            final MasterDetailsContentNode res = child.findNode( element );
            
            if( res != null )
            {
                return res;
            }
        }
        
        return null;
    }
    
    private void refreshNodes()
    {
        final ListFactory<MasterDetailsContentNode> nodeListFactory = ListFactory.start();
        
        for( Object entry : this.rawChildren )
        {
            if( entry instanceof MasterDetailsContentNode )
            {
                nodeListFactory.add( (MasterDetailsContentNode) entry );
            }
            else if( entry instanceof NodeFactory )
            {
                nodeListFactory.add( ( ((NodeFactory) entry) ).nodes() );
            }
            else
            {
                throw new IllegalStateException( entry.getClass().getName() );
            }
        }
        
        final MasterDetailsContentNodeList nodes = new MasterDetailsContentNodeList( nodeListFactory.result() );
        
        if( ! this.nodes.equals( nodes ) )
        {
            this.nodes = nodes;
            broadcast( new NodeListEvent( this ) );
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
    
    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( SapphireActionSystem.CONTEXT_EDITOR_PAGE_OUTLINE_NODE );
    }

    @Override
    protected Status computeValidationState()
    {
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();
        
        if( basis() )
        {
            factory.merge( getModelElement().validation() );
        }
        
        for( SapphirePart section : this.sections )
        {
            if( section.visible() )
            {
                factory.merge( section.getValidationState() );
            }
        }

        for( SapphirePart node : this.nodes )
        {
            if( node.visible() )
            {
                factory.merge( node.getValidationState() );
            }
        }
        
        return factory.create();
    }
    
    private boolean basis()
    {
        final IModelElement element = getModelElement();
        
        if( element.parent() instanceof ModelElementList<?> )
        {
            final ISapphirePart parentPart = getParentPart();
            
            if( parentPart != null && parentPart instanceof MasterDetailsContentNode )
            {
                final MasterDetailsContentNode parentNode = (MasterDetailsContentNode) parentPart;
                
                return ( element != parentNode.getLocalModelElement() );
            }
        }
        
        return false;
    }
    
    @Override
    protected void handleModelElementChange( final Event event )
    {
        super.handleModelElementChange( event );
        
        if( event instanceof PropertyEvent && isChildNodeFactoryProperty( ( (PropertyEvent) event ).property() ) )
        {
            if( event instanceof PropertyContentEvent )
            {
                refreshNodes();
            }
            else if( event instanceof PropertyValidationEvent )
            {
                runOnDisplayThread
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            updateValidationState();
                        }
                    }
                );
            }
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.modelElementListener != null )
        {
            this.modelElement.detach( this.modelElementListener );
        }
        
        for( SapphirePart section : this.sections )
        {
            section.dispose();
        }
        
        for( SapphirePart node : this.nodes )
        {
            node.dispose();
        }
        
        if( this.labelFunctionResult != null )
        {
            this.labelFunctionResult.dispose();
        }
        
        if( this.imageManager != null )
        {
            this.imageManager.dispose();
        }
        
        for( Object object : this.rawChildren )
        {
            if( object instanceof NodeFactory )
            {
                ( (NodeFactory) object ).dispose();
            }
        }
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        throw new UnsupportedOperationException();
    }
    
    public boolean controls( final IModelElement element )
    {
        if( element == getModelElement() )
        {
            final ISapphirePart parentPart = getParentPart();
            
            if( parentPart != null && parentPart instanceof MasterDetailsContentNode )
            {
                final MasterDetailsContentNode parentNode = (MasterDetailsContentNode) parentPart;
                
                return ( element != parentNode.getLocalModelElement() );
            }
        }
        
        return false;
    }
    
    private abstract class NodeFactory
    {
        private final MasterDetailsContentNodeFactoryDef definition;
        private final Map<String,String> params;
        private final FunctionResult visibleWhenFunctionResult;
        private final Map<IModelElement,MasterDetailsContentNode> nodesCache = new IdentityHashMap<IModelElement,MasterDetailsContentNode>();
        
        public NodeFactory( final MasterDetailsContentNodeFactoryDef definition,
                            final Map<String,String> params )
        {
            this.definition = definition;
            this.params = params;
            
            this.visibleWhenFunctionResult = initExpression
            (
                getLocalModelElement(),
                definition.getVisibleWhen().getContent(), 
                Boolean.class,
                Literal.TRUE
            );
        }
        
        public final boolean visible()
        {
            return (Boolean) this.visibleWhenFunctionResult.value();
        }
        
        public abstract ModelProperty property();
        
        protected abstract List<IModelElement> elements();
        
        public final List<MasterDetailsContentNode> nodes()
        {
            final Map<IModelElement,MasterDetailsContentNode> oldCache = MapFactory.unmodifiable( this.nodesCache );
            final ListFactory<MasterDetailsContentNode> nodes = ListFactory.start();
            
            for( IModelElement element : elements() )
            {
                MasterDetailsContentNode node = this.nodesCache.get( element );
                
                if( node == null )
                {
                    MasterDetailsContentNodeDef relevantCaseDef = null;
                    
                    for( MasterDetailsContentNodeFactoryCaseDef entry : this.definition.getCases() )
                    {
                        final JavaType type = entry.getType().resolve();
                        
                        if( type == null )
                        {
                            relevantCaseDef = entry;
                            break;
                        }
                        else
                        {
                            final Class<?> cl = type.artifact();
        
                            if( cl == null || cl.isAssignableFrom( element.getClass() ) )
                            {
                                relevantCaseDef = entry;
                                break;
                            }
                        }
                    }
                    
                    if( relevantCaseDef == null )
                    {
                        throw new RuntimeException();
                    }
                    
                    node = new MasterDetailsContentNode()
                    {
                        @Override
                        protected Function initVisibleWhenFunction()
                        {
                            final Function baseVisibleWhenFunction = super.initVisibleWhenFunction();
                            
                            final Function nodeFactoryVisibleFunction = new Function()
                            {
                                @Override
                                public String name()
                                {
                                    return "NodeFactoryVisible";
                                }

                                @Override
                                public FunctionResult evaluate( final FunctionContext context )
                                {
                                    return new FunctionResult( this, context )
                                    {
                                        private Listener listener;
                                        
                                        @Override
                                        protected void init()
                                        {
                                            this.listener = new Listener()
                                            {
                                                @Override
                                                public void handle( final Event event )
                                                {
                                                    refresh();
                                                }
                                            };
                                            
                                            NodeFactory.this.visibleWhenFunctionResult.attach( this.listener );
                                        }

                                        @Override
                                        protected Object evaluate()
                                        {
                                            return NodeFactory.this.visible();
                                        }

                                        @Override
                                        public void dispose()
                                        {
                                            super.dispose();
                                            
                                            NodeFactory.this.visibleWhenFunctionResult.detach( this.listener );
                                        }
                                    };
                                }
                            };
                            
                            nodeFactoryVisibleFunction.init();
                            
                            if( baseVisibleWhenFunction == null )
                            {
                                return nodeFactoryVisibleFunction;
                            }
                            else
                            {
                                return AndFunction.create( nodeFactoryVisibleFunction, baseVisibleWhenFunction );
                            }
                        }
                    };
                    
                    // It is very important to put the node into the cache prior to initializing the node as
                    // initialization can case a re-entrant call into this function and we must avoid creating
                    // two nodes for the same element.
                    
                    this.nodesCache.put( element, node );
                    
                    node.init( MasterDetailsContentNode.this, element, relevantCaseDef, this.params );
                    node.attach( MasterDetailsContentNode.this.childPartListener );
                    node.transformLabelCase = false;
                }
                
                nodes.add( node );
            }
            
            for( Map.Entry<IModelElement,MasterDetailsContentNode> entry : oldCache.entrySet() )
            {
                if( ! this.nodesCache.containsKey( entry.getKey() ) )
                {
                    entry.getValue().dispose();
                }
            }
            
            return nodes.result();
        }
        
        public void dispose()
        {
            if( this.visibleWhenFunctionResult != null )
            {
                this.visibleWhenFunctionResult.dispose();
            }
        }
    }
    
    public static final class NodeListEvent extends PartEvent
    {
        public NodeListEvent( final MasterDetailsContentNode node )
        {
            super( node );
        }

        @Override
        public MasterDetailsContentNode part()
        {
            return (MasterDetailsContentNode) super.part();
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String couldNotResolveNode;
        
        static
        {
            initializeMessages( MasterDetailsContentNode.class.getName(), Resources.class );
        }
    }
    
}
