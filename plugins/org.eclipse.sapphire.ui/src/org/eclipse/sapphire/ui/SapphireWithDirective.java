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

package org.eclipse.sapphire.ui;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhindent;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.EditFailedException;
import org.eclipse.sapphire.modeling.ElementProperty;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ModelElementHandle;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.localization.LabelTransformer;
import org.eclipse.sapphire.ui.def.ISapphireCompositeDef;
import org.eclipse.sapphire.ui.def.ISapphireLabelDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;
import org.eclipse.sapphire.ui.def.ISapphireWithDirectiveDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.internal.binding.RadioButtonsGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireWithDirective

    extends SapphirePageBook
    
{
    private ModelPath path;
    private IModelElement element;
    private ElementProperty property;
    private IModelElement elementForChildParts;
    private ModelPropertyListener listener;
    
    @Override
    protected void init()
    {
        final ISapphireWithDirectiveDef def = (ISapphireWithDirectiveDef) this.definition;
        
        final String pathString = def.getPath().getText();
        this.path = new ModelPath( pathString );
        
        this.element = getModelElement();
        
        for( int i = 0, n = this.path.length(); i < n; i++ )
        {
            final ModelPath.Segment segment = this.path.segment( i );
            
            if( segment instanceof ModelPath.ModelRootSegment )
            {
                this.element = (IModelElement) this.element.root();
            }
            else if( segment instanceof ModelPath.ParentElementSegment )
            {
                IModelParticle parent = this.element.parent();
                
                if( ! ( parent instanceof IModelElement ) )
                {
                    parent = parent.parent();
                }
                
                this.element = (IModelElement) parent;
            }
            else if( segment instanceof ModelPath.PropertySegment )
            {
                final ModelProperty prop = resolve( this.element, ( (ModelPath.PropertySegment) segment ).getPropertyName() );
                
                if( prop instanceof ImpliedElementProperty )
                {
                    this.element = this.element.read( (ImpliedElementProperty) prop );
                }
                else if( prop instanceof ElementProperty )
                {
                    this.property = (ElementProperty) prop;
                    
                    if( i + 1 != n )
                    {
                        throw new RuntimeException( NLS.bind( Resources.invalidPath, pathString ) );
                    }
                }
            }
            else
            {
                throw new RuntimeException( NLS.bind( Resources.invalidPath, pathString ) );
            }
        }

        super.init();
        
        setExposePageValidationState( true );
        
        if( this.property == null )
        {
            changePage( this.element, ClassBasedKey.create( this.element ) );
        }
        else
        {
            this.listener = new ModelPropertyListener()
            {
                @Override
                public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                {
                    updateCurrentPage( false );
                }
            };
            
            this.element.addListener( this.listener, this.property.getName() );
            
            updateCurrentPage( true );
        }
    }
    
    @Override
    protected ISapphireCompositeDef initDefaultPageDef()
    {
        final ISapphireUiDef root = ISapphireUiDef.TYPE.instantiate();
        final ISapphireCompositeDef composite = (ISapphireCompositeDef) root.getPartDefs().addNewElement( ISapphireCompositeDef.TYPE );
        final ISapphireLabelDef label = (ISapphireLabelDef) composite.getContent().addNewElement( ISapphireLabelDef.TYPE );
        label.setText( Resources.noAdditionalPropertiesMessage );
        
        return composite;
    }
    
    public ModelPath getPath()
    {
        return this.path;
    }
    
    public IModelElement getLocalModelElement()
    {
        return this.element;
    }

    public ElementProperty getProperty()
    {
        return this.property;
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        final ISapphireWithDirectiveDef def = (ISapphireWithDirectiveDef) this.definition;
        
        final Composite composite = new Composite( context.getComposite(), SWT.NONE );
        composite.setLayoutData( gdhspan( gdhfill(), 2 ) );
        composite.setLayout( glayout( 1, 0, 0 ) );
        context.adapt( composite );
        
        if( this.property != null )
        {
            final List<ModelElementType> allPossibleTypes = this.property.getAllPossibleTypes();
            final int allPossibleTypesCount = allPossibleTypes.size();
            final IModelElement element = this.element;
            final ElementProperty property = this.property;
            final ModelPropertyListener modelPropertyListener;
            
            final Style defaultStyle;
            
            if( allPossibleTypesCount == 1 )
            {
                defaultStyle = Style.CHECKBOX;
            }
            else if( allPossibleTypesCount <= 3 )
            {
                defaultStyle = Style.RADIO_BUTTONS;
            }
            else
            {
                defaultStyle = Style.CHECKBOX;
            }
            
            Style style = Style.decode( def.getHint( ISapphireWithDirectiveDef.HINT_STYLE ) );
            
            if( style == null || ( style == Style.CHECKBOX && allPossibleTypesCount != 1 ) )
            {
                style = defaultStyle;
            }

            if( style == Style.CHECKBOX )
            {
                final Button masterCheckBox = new Button( composite, SWT.CHECK );
                masterCheckBox.setLayoutData( gdhindent( gdhspan( gdhfill(), 2 ), 10 ) );
                masterCheckBox.setText( LabelTransformer.transform( def.getLabel().getLocalizedText(), CapitalizationType.FIRST_WORD_ONLY, true ) );
                context.adapt( masterCheckBox );
    
                modelPropertyListener = new ModelPropertyListener()
                {
                    @Override
                    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                    {
                        final IModelElement subModelElement = element.read( property ).element();
                        
                        masterCheckBox.setSelection( subModelElement != null );
                        masterCheckBox.setEnabled( element.isPropertyEnabled( property ) );
                        
                        if( subModelElement != null )
                        {
                            context.setHelp( masterCheckBox, subModelElement, property );
                        }
                    }
                };
                
                masterCheckBox.addSelectionListener
                (
                    new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected( final SelectionEvent event )
                        {
                            try
                            {
                                final ModelElementHandle<?> handle = element.read( property );
                                
                                if( masterCheckBox.getSelection() == true )
                                {
                                    handle.element( true );
                                }
                                else
                                {
                                    handle.remove();
                                }
                            }
                            catch( Exception e )
                            {
                                // Note that the EditFailedException is ignored here because the user has already
                                // been notified and likely has taken action that led to the exception (such as
                                // declining to make a file writable).
                                
                                final EditFailedException editFailedException = EditFailedException.findAsCause( e );
                                
                                if( editFailedException == null )
                                {
                                    SapphireUiFrameworkPlugin.log( e );
                                }
                            }
                        }
                    }
                );
            }
            else if( style == Style.RADIO_BUTTONS )
            {
                final RadioButtonsGroup radioButtonsGroup = new RadioButtonsGroup( context, composite, false );
                radioButtonsGroup.setLayoutData( gdhindent( gdhspan( gdhfill(), 2 ), 10 ) );
                context.adapt( radioButtonsGroup );
                
                final Button noneButton = radioButtonsGroup.addRadioButton( Resources.noneSelection );
                final Map<ModelElementType,Button> typeToButton = new HashMap<ModelElementType,Button>();
                final Map<Button,ModelElementType> buttonToType = new HashMap<Button,ModelElementType>();
                
                for( ModelElementType type : allPossibleTypes )
                {
                    final String label = type.getLabel( true, CapitalizationType.FIRST_WORD_ONLY, false );
                    final Button button = radioButtonsGroup.addRadioButton( label );
                    typeToButton.put( type, button );
                    buttonToType.put( button, type );
                }
                
                modelPropertyListener = new ModelPropertyListener()
                {
                    @Override
                    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                    {
                        final IModelElement subModelElement = element.read( property ).element();
                        final Button button;
                        
                        if( subModelElement == null )
                        {
                            button = noneButton;
                        }
                        else
                        {
                            button = typeToButton.get( subModelElement.getModelElementType() );
                        }
                        
                        if( radioButtonsGroup.getSelection() != button )
                        {
                            radioButtonsGroup.setSelection( button );
                        }
                        
                        radioButtonsGroup.setEnabled( element.isPropertyEnabled( property ) );
                    }
                };
                
                radioButtonsGroup.addSelectionListener
                (
                    new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected( final SelectionEvent event )
                        {
                            try
                            {
                                final ModelElementHandle<?> handle = element.read( property );
                                final Button button = radioButtonsGroup.getSelection();
                                
                                if( handle.element() != null && button == noneButton )
                                {
                                    handle.remove();
                                }
                                else
                                {
                                    final ModelElementType type = buttonToType.get( button );
                                    handle.element( true, type );
                                }
                            }
                            catch( Exception e )
                            {
                                // Note that the EditFailedException is ignored here because the user has already
                                // been notified and likely has taken action that led to the exception (such as
                                // declining to make a file writable).
                                
                                final EditFailedException editFailedException = EditFailedException.findAsCause( e );
                                
                                if( editFailedException == null )
                                {
                                    SapphireUiFrameworkPlugin.log( e );
                                }
                            }
                        }
                    }
                );
            }
            else if( style == Style.DROP_DOWN_LIST )
            {
                final Combo combo = new Combo( composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY );
                combo.setLayoutData( gdhindent( gdhspan( gdhfill(), 2 ), 10 ) );
                context.adapt( combo );
                
                combo.add( Resources.noneSelection );
                
                final Map<ModelElementType,Integer> typeToIndex = new HashMap<ModelElementType,Integer>();
                final Map<Integer,ModelElementType> indexToType = new HashMap<Integer,ModelElementType>();
                
                int index = 1;
                
                for( ModelElementType type : allPossibleTypes )
                {
                    final String label = type.getLabel( true, CapitalizationType.FIRST_WORD_ONLY, false );
                    combo.add( label );
                    typeToIndex.put( type, index );
                    indexToType.put( index, type );
                    
                    index++;
                }
                
                modelPropertyListener = new ModelPropertyListener()
                {
                    @Override
                    public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
                    {
                        final IModelElement subModelElement = element.read( property ).element();
                        final int index;
                        
                        if( subModelElement == null )
                        {
                            index = 0;
                        }
                        else
                        {
                            index = typeToIndex.get( subModelElement.getModelElementType() );
                        }
                        
                        if( combo.getSelectionIndex() != index )
                        {
                            combo.select( index );
                        }
                        
                        combo.setEnabled( element.isPropertyEnabled( property ) );
                    }
                };
                
                combo.addSelectionListener
                (
                    new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected( final SelectionEvent event )
                        {
                            try
                            {
                                final ModelElementHandle<?> handle = element.read( property );
                                final int index = combo.getSelectionIndex();
                                
                                if( handle.element() != null && index == 0 )
                                {
                                    handle.remove();
                                }
                                else
                                {
                                    final ModelElementType type = indexToType.get( index );
                                    handle.element( true, type );
                                }
                            }
                            catch( Exception e )
                            {
                                // Note that the EditFailedException is ignored here because the user has already
                                // been notified and likely has taken action that led to the exception (such as
                                // declining to make a file writable).
                                
                                final EditFailedException editFailedException = EditFailedException.findAsCause( e );
                                
                                if( editFailedException == null )
                                {
                                    SapphireUiFrameworkPlugin.log( e );
                                }
                            }
                        }
                    }
                );
            }
            else
            {
                throw new IllegalStateException();
            }
        
            final Composite separatorComposite = new Composite( composite, SWT.NONE );
            separatorComposite.setLayoutData( gdhindent( gdhspan( gdhfill(), 2 ), 10 ) );
            separatorComposite.setLayout( glayout( 1, 0, 5 ) );
            context.adapt( separatorComposite );
            
            final Label separator = new Label( separatorComposite, SWT.SEPARATOR | SWT.HORIZONTAL );
            separator.setLayoutData( gdhfill() );
            
            modelPropertyListener.handlePropertyChangedEvent( null );
            element.addListener( modelPropertyListener, property.getName() );
            
            composite.addDisposeListener
            (
                new DisposeListener()
                {
                    public void widgetDisposed( final DisposeEvent event )
                    {
                        element.removeListener( modelPropertyListener, property.getName() );
                    }
                }
            );
        }
        
        super.render( new SapphireRenderingContext( this, context, composite ) );
    }

    @Override
    protected Object parsePageKey( final String pageKeyString )
    {
        final ISapphireUiDef rootdef = this.definition.nearest( ISapphireUiDef.class );
        final Class<?> cl = rootdef.resolveClass( pageKeyString );
        return ClassBasedKey.create( cl );
    }

    private void updateCurrentPage( final boolean force )
    {
        final IModelElement child = this.element.read( this.property ).element();
        
        if( force == true || this.elementForChildParts != child )
        {
            this.elementForChildParts = child;

            if( this.elementForChildParts == null )
            {
                changePage( this.element, null );
            }
            else
            {
                changePage( this.elementForChildParts, ClassBasedKey.create( this.elementForChildParts ) );
            }
        }
    }
    
    @Override
    public boolean setFocus( final ModelPath path )
    {
        if( this.path.isPrefixOf( path ) )
        {
            final ModelPath tail = path.makeRelativeTo( this.path );
            
            if( this.property == null || ( this.element.isPropertyEnabled( this.property ) && this.element.read( this.property ) != null ) )
            {
                return super.setFocus( tail );
            }
        }
        
        return false;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if( this.listener != null )
        {
            this.element.removeListener( this.listener, this.property.getName() );
        }
    }
    
    private enum Style
    {
        CHECKBOX( "checkbox" ),
        RADIO_BUTTONS( "radio.buttons" ),
        DROP_DOWN_LIST( "drop.down.list" );
        
        public static Style decode( final String text )
        {
            if( text != null )
            {
                for( Style style : Style.values() )
                {
                    if( style.text.equals( text ) )
                    {
                        return style;
                    }
                }
            }
            
            return null;
        }

        private final String text;
        
        private Style( final String text )
        {
            this.text = text;
        }
        
        @Override
        public String toString()
        {
            return this.text;
        }
    }

    private static final class Resources extends NLS
    {
        public static String noneSelection;
        public static String noAdditionalPropertiesMessage;
        public static String invalidPath;
        
        static
        {
            initializeMessages( SapphireWithDirective.class.getName(), Resources.class );
        }
    }

}
