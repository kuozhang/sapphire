/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.form.editors.masterdetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.SapphireMultiStatus;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionContext;
import org.eclipse.sapphire.modeling.el.FunctionException;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.localization.LocalizationService;
import org.eclipse.sapphire.ui.IPropertiesViewContributorPart;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.PropertiesViewContributionManager;
import org.eclipse.sapphire.ui.PropertiesViewContributionPart;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.SapphireCondition;
import org.eclipse.sapphire.ui.SapphireImageCache;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphirePartEvent;
import org.eclipse.sapphire.ui.SapphirePartListener;
import org.eclipse.sapphire.ui.SapphirePropertyEnabledCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.SapphireSection;
import org.eclipse.sapphire.ui.def.ISapphireParam;
import org.eclipse.sapphire.ui.def.ISapphireSectionDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeFactoryEntry;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeFactoryRef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeListEntry;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.IMasterDetailsContentNodeRef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class MasterDetailsContentNode

    extends SapphirePart
    implements IPropertiesViewContributorPart
    
{
    public static final String HINT_HIDE_IF_DISABLED = "hide.if.disabled"; //$NON-NLS-1$
    
    private MasterDetailsContentOutline contentTree;
    private IMasterDetailsContentNodeDef definition;
    private IModelElement modelElement;
    private ImpliedElementProperty modelElementProperty;
    private ModelElementListener modelElementListener;
    private MasterDetailsContentNode parentNode;
    private FunctionResult labelFunctionResult;
    private Set<String> listProperties;
    private ImageManager imageManager;
    private SapphirePartListener childPartListener;
    private List<Object> rawChildren;
    private List<SapphireSection> sections;
    private List<SapphireSection> sectionsReadOnly;
    private PropertiesViewContributionManager propertiesViewContributionManager;
    private boolean expanded;
    private SapphireCondition visibleWhenCondition;
    private final List<SapphireCondition> allConditions = new ArrayList<SapphireCondition>();
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
        
        this.contentTree = nearest( MasterDetailsEditorPagePart.class ).getContentOutline();
        this.definition = (IMasterDetailsContentNodeDef) super.definition;
        
        this.modelElementProperty = (ImpliedElementProperty) resolve( this.definition.getProperty().getContent() );
        
        if( this.modelElementProperty != null )
        {
            this.modelElement = getModelElement().read( this.modelElementProperty );
            
            this.modelElementListener = new ModelElementListener()
            {
                @Override
                public void propertyChanged( final ModelPropertyChangeEvent event )
                {
                    handleModelElementChange( event );
                }
            };
            
            this.modelElement.addListener( this.modelElementListener );
        }
        else
        {
            this.modelElement = getModelElement();
        }
        
        this.visibleWhenCondition = null;

        Class<?> visibleWhenConditionClass = null;
        String visibleWhenConditionParameter = null;
        
        final IStatus visibleWhenConditionClassValidation = this.definition.getVisibleWhenConditionClass().validate();
        
        if( visibleWhenConditionClassValidation.getSeverity() != IStatus.ERROR )
        {
            final JavaType visibleWhenConditionType = this.definition.getVisibleWhenConditionClass().resolve();
            
            if( visibleWhenConditionType != null )
            {
                visibleWhenConditionClass = visibleWhenConditionType.artifact();
                visibleWhenConditionParameter = this.definition.getVisibleWhenConditionParameter().getText();
            }
        }
        else
        {
            SapphireUiFrameworkPlugin.log( visibleWhenConditionClassValidation );
        }
        
        if( visibleWhenConditionClass == null && this.modelElementProperty != null )
        {
            final String hideIfDisabled 
                = this.definition.getHint( IMasterDetailsContentNodeDef.HINT_HIDE_IF_DISABLED );
            
            if( Boolean.parseBoolean( hideIfDisabled ) )
            {
                visibleWhenConditionClass = SapphirePropertyEnabledCondition.class;
                visibleWhenConditionParameter = this.modelElementProperty.getName();
            }
        }
        
        if( visibleWhenConditionClass != null )
        {
            this.visibleWhenCondition = SapphireCondition.create( this, visibleWhenConditionClass, visibleWhenConditionParameter );
            
            if( this.visibleWhenCondition != null )
            {
                this.allConditions.add( this.visibleWhenCondition );
                
                this.visibleWhenCondition.addListener
                (
                    new SapphireCondition.Listener()
                    {
                        @Override
                        public void handleConditionChanged()
                        {
                            getContentTree().refresh();
                        }
                    }
                );
            }
        }
        
        this.expanded = false;
        
        this.childPartListener = new SapphirePartListener()
        {
            @Override
            public void handleValidateStateChange( final IStatus oldValidateState,
                                                   final IStatus newValidationState )
            {
                updateValidationState();
            }
        };
        
        final SapphirePartListener validationStateListener = new SapphirePartListener()
        {
            @Override
            public void handleValidateStateChange( final IStatus oldValidateState,
                                                   final IStatus newValidationState )
            {
                getContentTree().notifyOfNodeUpdate( MasterDetailsContentNode.this );
            }
        };
        
        addListener( validationStateListener );
        
        // Sections
        
        this.sections = new ArrayList<SapphireSection>();
        this.sectionsReadOnly = Collections.unmodifiableList( this.sections );
        
        for( ISapphireSectionDef secdef : this.definition.getSections() )
        {
            final SapphireSection section = new SapphireSection();
            section.init( this, this.modelElement, secdef, this.params );
            section.addListener( this.childPartListener );
            
            this.sections.add( section );
        }
        
        // Child Nodes
        
        this.rawChildren = new ArrayList<Object>();
        
        for( IMasterDetailsContentNodeListEntry entry : this.definition.getChildNodes() )
        {
            final Map<String,String> params = new HashMap<String,String>( this.params );

            if( entry instanceof IMasterDetailsContentNodeDef || entry instanceof IMasterDetailsContentNodeRef )
            {
                final IMasterDetailsContentNodeDef def;
                
                if( entry instanceof IMasterDetailsContentNodeDef )
                {
                    def = (IMasterDetailsContentNodeDef) entry;
                }
                else
                {
                    final IMasterDetailsContentNodeRef ref = (IMasterDetailsContentNodeRef) entry;
                    def = ref.resolve();
                    
                    if( def == null )
                    {
                        final String msg = NLS.bind( Resources.couldNotResolveNode, ref.getPart() );
                        throw new RuntimeException( msg );
                    }

                    for( ISapphireParam param : ref.getParams() )
                    {
                        final String paramName = param.getName().getText();
                        final String paramValue = param.getValue().getText();
                        
                        if( paramName != null && paramValue != null )
                        {
                            params.put( paramName, paramValue );
                        }
                    }
                }
                
                final MasterDetailsContentNode node = new MasterDetailsContentNode();
                node.init( this, this.modelElement, def, params );
                node.addListener( this.childPartListener );
                
                this.rawChildren.add( node );
            }
            else if( entry instanceof IMasterDetailsContentNodeFactoryDef || entry instanceof IMasterDetailsContentNodeFactoryRef )
            {
                final IMasterDetailsContentNodeFactoryDef def;
                
                if( entry instanceof IMasterDetailsContentNodeFactoryDef )
                {
                    def = (IMasterDetailsContentNodeFactoryDef) entry;
                }
                else
                {
                    final IMasterDetailsContentNodeFactoryRef ref = (IMasterDetailsContentNodeFactoryRef) entry;
                    def = ref.resolve();
                    
                    for( ISapphireParam param : ref.getParams() )
                    {
                        final String paramName = param.getName().getText();
                        final String paramValue = param.getValue().getText();
                        
                        if( paramName != null && paramValue != null )
                        {
                            params.put( paramName, paramValue );
                        }
                    }
                }
                
                final ListProperty listProperty = (ListProperty) resolve( getLocalModelElement(), def.getListProperty().getContent(), params );
                
                SapphireCondition factoryVisibleWhenCondition = null;
                
                final JavaType factoryVisibleWhenConditionClass = def.getVisibleWhenConditionClass().resolve();
                
                if( factoryVisibleWhenConditionClass != null )
                {
                    final String parameter = def.getVisibleWhenConditionParameter().getText();
                    factoryVisibleWhenCondition = SapphireCondition.create( this, factoryVisibleWhenConditionClass.artifact(), parameter );
                    
                    if( factoryVisibleWhenCondition != null )
                    {
                        this.allConditions.add( factoryVisibleWhenCondition );
                    }
                }
                
                final ListPropertyNodeFactory factory = new ListPropertyNodeFactory( this.modelElement, listProperty, factoryVisibleWhenCondition )
                {
                    protected MasterDetailsContentNode createNode( final IModelElement listEntryModelElement )
                    {
                        IMasterDetailsContentNodeDef listEntryNodeDef = null;
                        
                        for( IMasterDetailsContentNodeFactoryEntry entry : def.getTypeSpecificDefinitions() )
                        {
                            final JavaType type = entry.getType().resolve();
                            
                            if( type == null )
                            {
                                listEntryNodeDef = entry;
                                break;
                            }
                            else
                            {
                                final Class<?> cl = type.artifact();

                                if( cl == null || cl.isAssignableFrom( listEntryModelElement.getClass() ) )
                                {
                                    listEntryNodeDef = entry;
                                    break;
                                }
                            }
                        }
                        
                        if( listEntryNodeDef == null )
                        {
                            throw new RuntimeException();
                        }
                        
                        final MasterDetailsContentNode node = new MasterDetailsContentNode();
                        node.init( MasterDetailsContentNode.this, listEntryModelElement, listEntryNodeDef, params );
                        node.addListener( MasterDetailsContentNode.this.childPartListener );
                        node.transformLabelCase = false;
                        
                        return node;
                    }
                };
                
                this.rawChildren.add( factory );
            }
        }
        
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
                    getContentTree().notifyOfNodeUpdate( MasterDetailsContentNode.this );
                }
            }
        );
        
        // Image
        
        final Literal defaultImageLiteral = Literal.create( ( hasChildNodes() ? SapphireImageCache.OBJECT_CONTAINER_NODE : SapphireImageCache.OBJECT_LEAF_NODE ) );
        final Function imageFunction;
        
        if( this.definition.getUseModelElementImage().getContent() )
        {
            imageFunction = new Function()
            {
                @Override
                public String name()
                {
                    return "ImageFromModelElement";
                }
                
                @Override
                public FunctionResult evaluate( final FunctionContext context )
                {
                    return new FunctionResult( this, context )
                    {
                        @Override
                        protected Object evaluate() throws FunctionException
                        {
                            final Image img = getImageCache().getImage( getLocalModelElement() );
                            return ImageDescriptor.createFromImage( img );
                        }
                    };
                }
            };
            
            imageFunction.init();
        }
        else
        {
            imageFunction = this.definition.getImage().getContent();
        }
        
        this.imageManager = new ImageManager( this.modelElement, imageFunction, defaultImageLiteral );
        
        addListener
        (
            new SapphirePartListener()
            {
                @Override
                public void handleEvent( final SapphirePartEvent event )
                {
                    if( event instanceof SapphirePart.ImageChangedEvent )
                    {
                        getContentTree().notifyOfNodeUpdate( MasterDetailsContentNode.this );
                    }
                }
            }
        );
        
        // Listeners
        
        this.listProperties = new HashSet<String>();
        
        for( Object entry : this.rawChildren )
        {
            if( entry instanceof ListPropertyNodeFactory )
            {
                this.listProperties.add( ( (ListPropertyNodeFactory) entry ).getListProperty().getName() );
            }
        }
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

    public boolean isVisible()
    {
        if( this.visibleWhenCondition != null )
        {
            return this.visibleWhenCondition.getConditionState();
        }
        
        return true;
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
            for( MasterDetailsContentNode child : getChildNodes() )
            {
                if( child.hasChildNodes() )
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
            
            for( MasterDetailsContentNode child : getChildNodes() )
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
        return this.sectionsReadOnly;
    }
    
    public List<ListProperty> getChildListProperties()
    {
        final ArrayList<ListProperty> listProperties = new ArrayList<ListProperty>();
        
        for( Object object : this.rawChildren )
        {
            if( object instanceof ListPropertyNodeFactory )
            {
                final ListPropertyNodeFactory factory = (ListPropertyNodeFactory) object;
                
                if( factory.isVisible() )
                {
                    listProperties.add( factory.getListProperty() );
                }
            }
        }
        
        return listProperties;
    }
    
    public boolean hasChildNodes()
    {
        return ! this.rawChildren.isEmpty();
    }
    
    public List<MasterDetailsContentNode> getChildNodes()
    {
        final ArrayList<MasterDetailsContentNode> nodes = new ArrayList<MasterDetailsContentNode>();
        
        for( Object entry : this.rawChildren )
        {
            if( entry instanceof MasterDetailsContentNode )
            {
                final MasterDetailsContentNode node = (MasterDetailsContentNode) entry;
                
                if( node.isVisible() )
                {
                    nodes.add( node );
                }
            }
            else if( entry instanceof ListPropertyNodeFactory )
            {
                final ListPropertyNodeFactory factory = (ListPropertyNodeFactory) entry;
                
                if( factory.isVisible() )
                {
                    nodes.addAll( factory.createNodes() );
                }
            }
            else
            {
                throw new IllegalStateException( entry.getClass().getName() );
            }
        }
        
        return nodes;
    }
    
    public MasterDetailsContentNode getChildNodeByLabel( final String label )
    {
        for( MasterDetailsContentNode child : getChildNodes() )
        {
            if( label.equals( child.getLabel() ) )
            {
                return child;
            }
        }
        
        return null;
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
    protected IStatus computeValidationState()
    {
        final SapphireMultiStatus st = new SapphireMultiStatus();
        
        for( SapphirePart child : this.sections )
        {
            st.add( child.getValidationState() );
        }

        for( SapphirePart child : getChildNodes() )
        {
            st.add( child.getValidationState() );
        }
        
        return st;
    }
    
    @Override
    protected void handleModelElementChange( final ModelPropertyChangeEvent event )
    {
        super.handleModelElementChange( event );
        
        final ModelProperty property = event.getProperty();
        
        if( this.listProperties != null && this.listProperties.contains( property.getName() ) )
        {
            final Runnable notifyOfStructureChangeOperation = new Runnable()
            {
                public void run()
                {
                    getContentTree().notifyOfNodeStructureChange( MasterDetailsContentNode.this );
                    updateValidationState();
                }
            };
            
            Display.getDefault().asyncExec( notifyOfStructureChangeOperation );
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.modelElementListener != null )
        {
            this.modelElement.removeListener( this.modelElementListener );
        }
        
        for( SapphirePart child : this.sections )
        {
            child.dispose();
        }
        
        for( SapphirePart child : getChildNodes() )
        {
            child.dispose();
        }
        
        for( SapphireCondition condition : this.allConditions )
        {
            condition.dispose();
        }
        
        if( this.labelFunctionResult != null )
        {
            this.labelFunctionResult.dispose();
        }
        
        if( this.imageManager != null )
        {
            this.imageManager.dispose();
        }
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        throw new UnsupportedOperationException();
    }
    
    private static abstract class ListPropertyNodeFactory
    {
        private final IModelElement modelElement;
        private final ListProperty listProperty;
        private Map<Object,MasterDetailsContentNode> nodesCache;
        private final SapphireCondition visibleWhenCondition;
        
        public ListPropertyNodeFactory( final IModelElement modelElement,
                                        final ListProperty listProperty,
                                        final SapphireCondition visibleWhenCondition )
        {
            if( modelElement == null )
            {
                throw new IllegalArgumentException();
            }
            
            if( listProperty == null )
            {
                throw new IllegalArgumentException();
            }
            
            this.modelElement = modelElement;
            this.listProperty = listProperty;
            this.nodesCache = null;
            this.visibleWhenCondition = visibleWhenCondition;
        }
        
        public final boolean isVisible()
        {
            if( this.visibleWhenCondition != null )
            {
                return this.visibleWhenCondition.getConditionState();
            }
            
            return true;
        }
        
        public ListProperty getListProperty()
        {
            return this.listProperty;
        }
        
        public List<MasterDetailsContentNode> createNodes()
        {
            final Map<Object,MasterDetailsContentNode> newCache = new HashMap<Object,MasterDetailsContentNode>();
            final List<MasterDetailsContentNode> nodes = new ArrayList<MasterDetailsContentNode>();
            final ModelElementList<?> list = this.modelElement.read( this.listProperty );
            
            for( IModelElement listEntryModelElement : list )
            {
                MasterDetailsContentNode node = ( this.nodesCache != null ? this.nodesCache.get( listEntryModelElement ) : null );
                
                if( node == null )
                {
                    node = createNode( listEntryModelElement );
                }
                
                nodes.add( node );
                newCache.put( listEntryModelElement, node );
            }
            
            this.nodesCache = newCache;
            
            return nodes;
        }
        
        protected abstract MasterDetailsContentNode createNode( final IModelElement listElement );
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
