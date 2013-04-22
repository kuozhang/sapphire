/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Greg Amerson - [342656] read.only rendering hint is not parsed as Boolean 
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.PropertyEnablementEvent;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.PropertyValidationEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.el.AndFunction;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.ui.def.ISapphireHint;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.PartDef;
import org.eclipse.sapphire.ui.def.PropertyEditorDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.renderers.swt.BooleanPropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.CheckBoxListPropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.DefaultListPropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.DefaultValuePropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.EnumPropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.NamedValuesPropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRendererFactory;
import org.eclipse.sapphire.ui.renderers.swt.SlushBucketPropertyEditor;
import org.eclipse.sapphire.ui.swt.internal.PopUpListFieldPropertyEditorPresentation;
import org.eclipse.sapphire.ui.swt.internal.PopUpListFieldStyle;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyEditorPart extends FormComponentPart
{
    public static final String RELATED_CONTROLS = "related-controls";
    public static final String BROWSE_BUTTON = "browse-button";
    public static final String DATA_BINDING = "binding";
    
    private static final List<PropertyEditorRendererFactory> FACTORIES = new ArrayList<PropertyEditorRendererFactory>();
    
    static
    {
        FACTORIES.add( new BooleanPropertyEditorRenderer.Factory() );
        FACTORIES.add( new EnumPropertyEditorRenderer.Factory() );
        FACTORIES.add( new NamedValuesPropertyEditorRenderer.Factory() );
        FACTORIES.add( new DefaultValuePropertyEditorRenderer.Factory() );
        FACTORIES.add( new CheckBoxListPropertyEditorRenderer.EnumFactory() );
        FACTORIES.add( new SlushBucketPropertyEditor.Factory() );
        FACTORIES.add( new DefaultListPropertyEditorRenderer.Factory() );
    }
    
    private Property property;
    private List<ModelPath> childPropertyPaths;
    private Map<Element,Map<ModelPath,PropertyEditorPart>> childPropertyEditors;
    private Map<String,Object> hints;
    private List<SapphirePart> relatedContentParts;
    private Listener propertyValidationListener;
    private FunctionResult labelFunctionResult;
    private PropertyEditorRenderer presentation;
    
    @Override
    protected void init()
    {
        super.init();
        
        final ISapphireUiDef rootdef = this.definition.nearest( ISapphireUiDef.class );
        final PropertyEditorDef propertyEditorPartDef = (PropertyEditorDef) this.definition;
        
        this.property = getModelElement().property( new ModelPath( propertyEditorPartDef.getProperty().text() ) );
        
        if( this.property == null )
        {
            throw new RuntimeException( NLS.bind( Resources.invalidPath, propertyEditorPartDef.getProperty().text() ) );
        }
        
        // Read the property to ensure that initial events are broadcast and avoid being surprised
        // by them later.
        
        this.property.empty();
        
        // Child properties.        
        
        final ListFactory<ModelPath> childPropertiesListFactory = ListFactory.start();
        final ElementType type = this.property.definition().getType();
        
        if( type != null )
        {
            if( propertyEditorPartDef.getChildProperties().isEmpty() )
            {
                for( PropertyDef childProperty : type.properties() )
                {
                    if( childProperty instanceof ValueProperty )
                    {
                        childPropertiesListFactory.add( new ModelPath( childProperty.name() ) );
                    }
                }
            }
            else
            {
                for( PropertyEditorDef childPropertyEditor : propertyEditorPartDef.getChildProperties() )
                {
                    final ModelPath childPropertyPath = new ModelPath( childPropertyEditor.getProperty().content() );
                    boolean invalid = false;
                    
                    if( childPropertyPath.length() == 0 )
                    {
                        invalid = true;
                    }
                    else
                    {
                        ElementType t = type;
                        
                        for( int i = 0, n = childPropertyPath.length(); i < n && ! invalid; i++ )
                        {
                            final ModelPath.Segment segment = childPropertyPath.segment( i );
                            
                            if( segment instanceof ModelPath.PropertySegment )
                            {
                                final PropertyDef p = t.property( ( (ModelPath.PropertySegment) segment ).getPropertyName() );
                                
                                if( p instanceof ValueProperty )
                                {
                                    if( i + 1 != n )
                                    {
                                        invalid = true;
                                    }
                                }
                                else if( p instanceof ImpliedElementProperty )
                                {
                                    if( i + 1 == n )
                                    {
                                        invalid = true;
                                    }
                                    else
                                    {
                                        t = p.getType();
                                    }
                                }
                                else
                                {
                                    invalid = true;
                                }
                            }
                            else
                            {
                                invalid = true;
                            }
                        }
                    }
                    
                    if( invalid )
                    {
                        final String msg = NLS.bind( Resources.invalidChildPropertyPath, this.property.name(), childPropertyPath.toString() );
                        SapphireUiFrameworkPlugin.logError( msg );
                    }
                    else
                    {
                        childPropertiesListFactory.add( childPropertyPath );
                    }
                }
            }
        }
        
        this.childPropertyPaths = childPropertiesListFactory.result();
        this.childPropertyEditors = new HashMap<Element,Map<ModelPath,PropertyEditorPart>>();
        
        // Listen for PropertyValidationEvent and update property editor's validation.
        
        if( this.property instanceof ElementList )
        {
            this.propertyValidationListener = new FilteredListener<PropertyEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyEvent event )
                {
                    if( event instanceof PropertyValidationEvent || event instanceof PropertyEnablementEvent ||
                        ( event instanceof PropertyContentEvent && event.property() == property() ) )
                    {
                        refreshValidation();
                    }
                }
            };
            
            for( ModelPath childPropertyPath : this.childPropertyPaths )
            {
                this.property.attach( this.propertyValidationListener, childPropertyPath );
            }
        }
        else
        {
            this.propertyValidationListener = new FilteredListener<PropertyEvent>()
            {
                @Override
                protected void handleTypedEvent( final PropertyEvent event )
                {
                    if( event instanceof PropertyValidationEvent || event instanceof PropertyEnablementEvent )
                    {
                        refreshValidation();
                    }
                }
            };
        }

        this.property.attach( this.propertyValidationListener );
        
        // Hints

        this.hints = new HashMap<String,Object>();
        
        for( ISapphireHint hint : propertyEditorPartDef.getHints() )
        {
            final String name = hint.getName().text();
            final String valueString = hint.getValue().text();
            Object parsedValue = valueString;
            
            if( name.equals( PropertyEditorDef.HINT_SHOW_HEADER ) ||
                name.equals( PropertyEditorDef.HINT_BORDER ) ||
                name.equals( PropertyEditorDef.HINT_BROWSE_ONLY ) ||
                name.equals( PropertyEditorDef.HINT_PREFER_COMBO ) ||
                name.equals( PropertyEditorDef.HINT_PREFER_RADIO_BUTTONS ) ||
                name.equals( PropertyEditorDef.HINT_PREFER_VERTICAL_RADIO_BUTTONS ) ||
                name.equals( PropertyEditorDef.HINT_READ_ONLY ) )
            {
                parsedValue = Boolean.parseBoolean( valueString );
            }
            else if( name.startsWith( PropertyEditorDef.HINT_FACTORY ) ||
                     name.startsWith( PropertyEditorDef.HINT_AUX_TEXT_PROVIDER ) )
            {
                parsedValue = rootdef.resolveClass( valueString );
            }
            else if( name.equals( PropertyEditorDef.HINT_LISTENERS ) )
            {
                final List<Class<?>> contributors = new ArrayList<Class<?>>();
                
                for( String segment : valueString.split( "," ) )
                {
                    final Class<?> cl = rootdef.resolveClass( segment.trim() );
                    
                    if( cl != null )
                    {
                        contributors.add( cl );
                    }
                }
                
                parsedValue = contributors;
            }
            
            this.hints.put( name, parsedValue );
        }
        
        final ListFactory<SapphirePart> relatedContentPartsListFactory = ListFactory.start();
        
        final Listener relatedContentPartListener = new FilteredListener<PartValidationEvent>()
        {
            @Override
            protected void handleTypedEvent( PartValidationEvent event )
            {
                refreshValidation();
            }
        };

        for( PartDef relatedContentPartDef : propertyEditorPartDef.getRelatedContent() )
        {
            final SapphirePart relatedContentPart = create( this, this.property.element(), relatedContentPartDef, this.params );
            relatedContentPart.attach( relatedContentPartListener );
            relatedContentPartsListFactory.add( relatedContentPart );
        }
        
        this.relatedContentParts = relatedContentPartsListFactory.result();
        
        this.labelFunctionResult = initExpression
        (
            propertyEditorPartDef.getLabel().content(), 
            String.class,
            Literal.create( this.property.definition().getLabel( false, CapitalizationType.NO_CAPS, true ) ),
            new Runnable()
            {
                public void run()
                {
                    broadcast( new LabelChangedEvent( PropertyEditorPart.this ) );
                }
            }
        );
    }

    @Override
    protected Function initVisibleWhenFunction()
    {
        return AndFunction.create
        (
            super.initVisibleWhenFunction(),
            createVersionCompatibleFunction( property() )
        );
    }
    
    @Override
    public PropertyEditorDef definition()
    {
        return (PropertyEditorDef) super.definition();
    }
    
    @Override
    public Element getLocalModelElement()
    {
        return this.property.element();
    }
    
    public Property property()
    {
        return this.property;
    }
    
    public List<ModelPath> getChildProperties()
    {
        return this.childPropertyPaths;
    }
    
    public PropertyEditorPart getChildPropertyEditor( final Element element,
                                                      final PropertyDef property )
    {
        return getChildPropertyEditor( element, new ModelPath( property.name() ) );
    }
    
    public PropertyEditorPart getChildPropertyEditor( final Element element,
                                                      final ModelPath property )
    {
        Map<ModelPath,PropertyEditorPart> propertyEditorsForElement = this.childPropertyEditors.get( element );
        
        if( propertyEditorsForElement == null )
        {
            propertyEditorsForElement = new HashMap<ModelPath,PropertyEditorPart>();
            this.childPropertyEditors.put( element, propertyEditorsForElement );
            
            final Map<ModelPath,PropertyEditorPart> finalPropertyEditorsForElement = propertyEditorsForElement;
            
            element.attach
            (
                new FilteredListener<ElementDisposeEvent>()
                {
                    @Override
                    protected void handleTypedEvent( final ElementDisposeEvent event )
                    {
                        for( PropertyEditorPart propertyEditor : finalPropertyEditorsForElement.values() )
                        {
                            propertyEditor.dispose();
                        }
                        
                        PropertyEditorPart.this.childPropertyEditors.remove( element );
                    }
                }
            );
        }
        
        PropertyEditorPart childPropertyEditorPart = propertyEditorsForElement.get( property );
        
        if( childPropertyEditorPart == null )
        {
            PropertyEditorDef childPropertyEditorDef = ( (PropertyEditorDef) this.definition ).getChildPropertyEditor( property );
            
            if( childPropertyEditorDef == null )
            {
                childPropertyEditorDef = PropertyEditorDef.TYPE.instantiate();
                childPropertyEditorDef.setProperty( property.toString() );
            }
            
            childPropertyEditorPart = new PropertyEditorPart();
            childPropertyEditorPart.init( this, element, childPropertyEditorDef, this.params );
            
            propertyEditorsForElement.put( property, childPropertyEditorPart );
        }
        
        return childPropertyEditorPart;
    }
    
    public String getLabel( final CapitalizationType capitalizationType,
                            final boolean includeMnemonic )
    {
        final String label = (String) this.labelFunctionResult.value();
        return LabelTransformer.transform( label, capitalizationType, includeMnemonic );
    }
    
    public boolean getShowLabel()
    {
        return definition().getShowLabel().content();
    }
    
    public boolean getSpanBothColumns()
    {
        return definition().getSpanBothColumns().content();
    }
    
    public int getWidth( final int defaultValue )
    {
        final Integer width = definition().getWidth().content();
        return ( width == null || width < 1 ? defaultValue : width );
    }
    
    public int getHeight( final int defaultValue )
    {
        final Integer height = definition().getHeight().content();
        return ( height == null || height < 1 ? defaultValue : height );
    }
    
    public int getMarginLeft()
    {
        int marginLeft = definition().getMarginLeft().content();
        
        if( marginLeft < 0 )
        {
            marginLeft = 0;
        }
        
        return marginLeft;
    }

    @SuppressWarnings( "unchecked" )
    
    public <T> T getRenderingHint( final String name,
                                   final T defaultValue )
    {
        final Object hintValue = this.hints == null ? null : this.hints.get( name );
        return hintValue == null ? defaultValue : (T) hintValue;
    }

    public boolean getRenderingHint( final String name,
                                     final boolean defaultValue )
    {
        final Object hintValue = this.hints == null ? null : this.hints.get( name );
        return hintValue == null ? defaultValue : (Boolean) hintValue;
    }
    
    public List<SapphirePart> getRelatedContent()
    {
        return this.relatedContentParts;
    }
    
    public int getRelatedContentWidth()
    {
        final Value<Integer> relatedContentWidth = definition().getRelatedContentWidth();
        
        if( relatedContentWidth.validation().ok() )
        {
            return relatedContentWidth.content();
        }
        else
        {
            return relatedContentWidth.getDefaultContent();
        }
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        if( this.presentation != null )
        {
            this.presentation.dispose();
            this.presentation = null;
        }
        
        if( ! visible() )
        {
            return;
        }
        
        final String style = definition().getStyle().text();
        
        if( style == null )
        {
            PropertyEditorRendererFactory factory = null;
            
            try
            {
                final Class<PropertyEditorRendererFactory> factoryClass 
                    = getRenderingHint( PropertyEditorDef.HINT_FACTORY, (Class<PropertyEditorRendererFactory>) null );
                
                if( factoryClass != null )
                {
                    factory = factoryClass.newInstance();
                }
            }
            catch( Exception e )
            {
                SapphireUiFrameworkPlugin.log( e );
            }
            
            if( factory == null )
            {
                for( PropertyEditorRendererFactory f : FACTORIES )
                {
                    if( f.isApplicableTo( this ) )
                    {
                        factory = f;
                        break;
                    }
                }
            }
    
            if( factory != null )
            {
                this.presentation = factory.create( context, this );
            }
        }
        else
        {
            if( style.startsWith( "Sapphire.PropertyEditor.PopUpListField" ) )
            {
                if( this.property.definition() instanceof ValueProperty && this.property.service( PossibleValuesService.class ) != null )
                {
                    PopUpListFieldStyle popUpListFieldPresentationStyle = null;
                    
                    if( style.equals( "Sapphire.PropertyEditor.PopUpListField" ) )
                    {
                        if( Enum.class.isAssignableFrom( this.property.definition().getTypeClass() ) )
                        {
                            popUpListFieldPresentationStyle = PopUpListFieldStyle.STRICT;
                        }
                        else
                        {
                            final PossibleValues possibleValuesAnnotation = this.property.definition().getAnnotation( PossibleValues.class );
                            
                            if( possibleValuesAnnotation != null )
                            {
                                popUpListFieldPresentationStyle 
                                    = ( possibleValuesAnnotation.invalidValueSeverity() == Severity.ERROR 
                                        ? PopUpListFieldStyle.STRICT : PopUpListFieldStyle.EDITABLE );
                            }
                            else
                            {
                                popUpListFieldPresentationStyle = PopUpListFieldStyle.EDITABLE;
                            }
                        }
                    }
                    else if( style.equals( "Sapphire.PropertyEditor.PopUpListField.Editable" ) )
                    {
                        popUpListFieldPresentationStyle = PopUpListFieldStyle.EDITABLE;
                    }
                    else if( style.equals( "Sapphire.PropertyEditor.PopUpListField.Strict" ) )
                    {
                        popUpListFieldPresentationStyle = PopUpListFieldStyle.STRICT;
                    }
                    
                    if( popUpListFieldPresentationStyle != null )
                    {
                        this.presentation = new PopUpListFieldPropertyEditorPresentation( context, this, popUpListFieldPresentationStyle );
                    }
                }
            }
        }
        
        if( this.presentation != null )
        {
            this.presentation.create( context.getComposite() );
        }
        else
        {
            throw new IllegalStateException( this.property.toString() );
        }
        
    }

    @Override
    protected Status computeValidation()
    {
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();
        
        if( property().enabled() )
        {
            factory.merge( this.property.validation() );
            
            if( this.property instanceof ElementList )
            {
                for( Element child : (ElementList<?>) this.property )
                {
                    for( ModelPath childPropertyPath : this.childPropertyPaths )
                    {
                        factory.merge( child.property( childPropertyPath ).validation() );
                    }
                }
            }
        }
        
        for( SapphirePart relatedContentPart : this.relatedContentParts )
        {
            factory.merge( relatedContentPart.validation() );
        }
        
        return factory.create();
    }
    
    @Override
    
    public boolean setFocus()
    {
        if( property().enabled() )
        {
            broadcast( new FocusReceivedEvent( this ) );
            return true;
        }
        
        return false;
    }

    @Override
    
    public boolean setFocus( final ModelPath path )
    {
        final ModelPath.Segment head = path.head();
        
        if( head instanceof ModelPath.PropertySegment )
        {
            final String propertyName = ( (ModelPath.PropertySegment) head ).getPropertyName();
            
            if( propertyName.equals( this.property.name() ) )
            {
                return setFocus();
            }
        }
        
        return false;
    }

    public String getActionContext()
    {
        final String context;
        
        if( this.property.definition() instanceof ValueProperty )
        {
            context = SapphireActionSystem.CONTEXT_VALUE_PROPERTY_EDITOR;
        }
        else if( this.property.definition() instanceof ElementProperty )
        {
            context = SapphireActionSystem.CONTEXT_ELEMENT_PROPERTY_EDITOR;
        }
        else if( this.property.definition() instanceof ListProperty )
        {
            context = SapphireActionSystem.CONTEXT_LIST_PROPERTY_EDITOR;
        }
        else
        {
            throw new IllegalStateException();
        }
        
        return context;
    }

    @Override
    public Set<String> getActionContexts()
    {
        return Collections.singleton( getActionContext() );
    }
    
    @Override
    public boolean isSingleLinePart()
    {
        if( this.property.definition() instanceof ValueProperty && ! this.property.definition().hasAnnotation( LongString.class ) )
        {
            return true;
        }
        
        return false;
    }
    
    public boolean isReadOnly()
    {
        return ( this.property.definition().isReadOnly() || getRenderingHint( PropertyEditorDef.HINT_READ_ONLY, false ) );        
    }

    @Override
    public void dispose()
    {
        if( this.presentation != null )
        {
            this.presentation.dispose();
        }
        
        if( this.propertyValidationListener != null )
        {
            this.property.detach( this.propertyValidationListener );
            
            if( this.property instanceof ElementList )
            {
                for( ModelPath childPropertyPath : this.childPropertyPaths )
                {
                    this.property.detach( this.propertyValidationListener, childPropertyPath );
                }
            }
        }
        
        if( this.labelFunctionResult != null )
        {
            this.labelFunctionResult.dispose();
        }
        
        for( Map<ModelPath,PropertyEditorPart> propertyEditorsForElement : this.childPropertyEditors.values() )
        {
            for( PropertyEditorPart propertyEditor : propertyEditorsForElement.values() )
            {
                propertyEditor.dispose();
            }
        }
        
        super.dispose();
    }
    
    private static final class Resources extends NLS
    {
        public static String invalidPath;
        public static String invalidChildPropertyPath;
        
        static
        {
            initializeMessages( PropertyEditorPart.class.getName(), Resources.class );
        }
    }
    
}
