/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_READ_ONLY;
import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_SHOW_LABEL;
import static org.eclipse.sapphire.ui.SapphirePropertyEditor.HINT_SHOW_LABEL_ABOVE;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gd;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhindent;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhspan;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdvalign;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdwhint;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glayout;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.glspacing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.sapphire.ui.internal.EnhancedComposite;
import org.eclipse.sapphire.ui.internal.binding.AbstractBinding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class CompactListPropertyEditorRenderer

    extends ListPropertyEditorRenderer
    
{
    public static final String DATA_SELECTION_PROVIDER = "selection.provider";
    
    private Runnable refreshOperation;
    
    ValueProperty memberProperty;
    private Composite innerComposite;
    private List<Text> textFields = new ArrayList<Text>();
    private List<CompactListBinding> textFieldListeners = new ArrayList<CompactListBinding>();
    private FormText addText;
    private HyperlinkAdapter addTextHyperlinkAdapter;
    
    public CompactListPropertyEditorRenderer( final SapphireRenderingContext context,
                                              final SapphirePropertyEditor part )
    {
        super( context, part );
    }

    @Override
    protected void createContents( final Composite parent )
    {
        createContents( parent, false, false );
    }
    
    protected Control createContents( final Composite parent,
                                      final boolean suppressLabel,
                                      final boolean ignoreLeftMarginHint )
    {
        final SapphirePropertyEditor part = getPart();
        final IModelElement element = part.getModelElement();
        final ListProperty property = (ListProperty) part.getProperty();
        final boolean isReadOnly = ( property.isReadOnly() || part.getRenderingHint( HINT_READ_ONLY, false ) );

        final boolean showLabelAbove = part.getRenderingHint( HINT_SHOW_LABEL_ABOVE, false );
        final boolean showLabelInline = part.getRenderingHint( HINT_SHOW_LABEL, ( suppressLabel ? false : ! showLabelAbove ) );
        final int leftMargin = ( ignoreLeftMarginHint ? 0 : part.getLeftMarginHint() );
        final List<ModelProperty> allMemberProperties = property.getType().getProperties();
        if( allMemberProperties.size() == 1 )
        {
            final ModelProperty prop = allMemberProperties.get( 0 );
            
            if( prop instanceof ValueProperty )
            {
                this.memberProperty = (ValueProperty) prop;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        else
        {
            throw new IllegalStateException();
        }
        
        final Label label;

        if( showLabelInline || showLabelAbove )
        {
            final String labelText = property.getLabel( false, CapitalizationType.FIRST_WORD_ONLY, true ) + ":";
            label = new Label( parent, SWT.NONE );
            label.setLayoutData( gdhindent( gdhspan( gdvalign( gd(), SWT.TOP ), showLabelAbove ? 2 : 1 ), leftMargin + 9 ) );
            label.setText( labelText );
            this.context.adapt( label );
        }
        else
        {
            label = null;
        }
        
        final Composite outerComposite = new EnhancedComposite( parent, SWT.NONE );
        outerComposite.setLayoutData( gdhindent( gdhspan( gdhfill(), showLabelInline ? 1 : 2 ), showLabelInline ? 0 : leftMargin ) );
        outerComposite.setLayout( glayout( ( isReadOnly ? 1 : 2 ), 0, 0, 0, 0 ) );
        this.context.adapt( outerComposite );
        
        if( this.decorator == null )
        {
            this.decorator = createDecorator( outerComposite );
            this.decorator.getControl().setLayoutData( gdvalign( gd(), SWT.TOP ) );
        }
        
        this.innerComposite = new Composite( outerComposite, SWT.NONE );
        this.innerComposite.setLayoutData( gdhfill() );
        this.innerComposite.setLayout( glspacing( glayout( 2, 0, 0 ), 2 ) );

        this.addControls( 1 );

        final List<Control> relatedControls = new ArrayList<Control>();
        
        if( label != null )
        {
            relatedControls.add( label );
        }
        // TOOD
//      this.innerComposite.setData( RELATED_CONTROLS, relatedControls );
        
        this.refreshOperation = new Runnable()
        {
            boolean running = false;
            
            public void run()
            {
                if( CompactListPropertyEditorRenderer.this.innerComposite.isDisposed() )
                {
                    return;
                }
                
                if( this.running == true )
                {
                    return;
                }
                
                this.running = true;
                
                try
                {
                    CompactListPropertyEditorRenderer.this.refreshControls();
                }
                finally
                {
                    this.running = false;
                }
            }
        };
        
        this.binding = new AbstractBinding( element, property, this.context, this.innerComposite )
        {
            @Override
            protected void doUpdateModel()
            {
                // Don't do anything here. Changes to the model are pushed at cell-level.
            }

            @Override
            protected void doUpdateTarget()
            {
                // Changes are mostly synchronized at cell-level. This is only here to
                // catch the case where a full page refresh is performed perhaps because user
                // has visited the source page.
                
                CompactListPropertyEditorRenderer.this.refreshOperation.run();
            }
        };
        
        
        addControl( this.innerComposite );
        
        return this.innerComposite;
    }
    
    private void addControls(int count) {
    	// first remove everything
        this.decorator.removeEditorControl( this.innerComposite );

        if (this.addText != null && this.addTextHyperlinkAdapter != null) {
    		this.addText.removeHyperlinkListener(this.addTextHyperlinkAdapter);
    	}
    	for (int i = 0; i < this.textFields.size(); i++) {
    		this.textFields.get(i).removeModifyListener(this.textFieldListeners.get(i));
    	}
    	for (Control child : this.innerComposite.getChildren()) {
    		child.dispose();
    	}
    	this.textFields.clear();
    	this.textFieldListeners.clear();
    	
    	// add back text controls and add link
    	for (int i = 0; i < count; i++) {
    		Text text = new Text(this.innerComposite, SWT.BORDER);
    		text.setLayoutData( gdwhint( gdhfill(), 150 ) );
    		this.textFields.add(text);
    		
    		CompactListBinding listener = new CompactListBinding(this, text);
    		text.addModifyListener(listener);
    		this.textFieldListeners.add(listener);

    		// add delete decorator
    		new CompactListDeleteDecorator(this, listener, this.innerComposite);
    	}

    	this.addText = new FormText( this.innerComposite, SWT.NONE );
        this.addText.setLayoutData( gdvalign( gdhfill(), SWT.CENTER ) );
        this.context.adapt( this.addText );
        
        final StringBuilder buf = new StringBuilder();
        buf.append( "<form><p vspace=\"false\"><a href=\"action\" nowrap=\"true\">" );
        buf.append( "Add " );
        buf.append( this.memberProperty.getLabel(false, CapitalizationType.NO_CAPS, false/*includeMnemonic*/) );
        buf.append( "</a></p></form>" );
        
        this.addText.setText( buf.toString(), true, false );
        if (this.addTextHyperlinkAdapter == null) {
        	this.addTextHyperlinkAdapter = new HyperlinkAdapter() {
            	@Override
                public void linkActivated( final HyperlinkEvent event )
                {
            		final int size = CompactListPropertyEditorRenderer.this.textFields.size() + 1;
            		final int count = size - CompactListPropertyEditorRenderer.this.getList().size();
            		for (int i = 0; i < count; i++) {
            			CompactListPropertyEditorRenderer.this.getList().addNewElement();
            		}
                	refreshControls();
                	CompactListPropertyEditorRenderer.this.textFields.get(size-1).setFocus();
                }
        	};
        }
        this.addText.addHyperlinkListener(this.addTextHyperlinkAdapter);

        this.innerComposite.setData( SapphirePropertyEditor.DATA_ASSIST_DECORATOR, this.decorator );
        this.decorator.addEditorControl( this.innerComposite );
    }
    
    private void refreshControls() {
        ModelElementList<IModelElement> list = this.getList();

    	final int count = Math.max(1, list.size());
        if (count != this.textFields.size()) {
        	this.addControls(count);
        	this.context.layout();
        }

        for (int i = 0; i < list.size(); i++) {
        	IModelElement elem = list.get(i);
        	String value = elem.read(this.memberProperty).getText();
        	value = value == null ? "" : value;
    		Text text = this.textFields.get(i);
    		this.textFieldListeners.get(i).setModelElement(elem);
    		if (!equals(value, text.getText())) {
    			text.setText(value);
        	}
    	}
        if (list.size() == 0) {
    		this.textFieldListeners.get(0).setModelElement(null);
    		this.textFields.get(0).setText("");
        }
    }

	private static final boolean equals( Object o1, Object o2 ) {
		boolean objectsAreEqual = false;
		if (o1 == o2) {
			objectsAreEqual = true;
		} else if (o1 != null && o2 != null) {
			objectsAreEqual = o1.equals(o2);
		}

		return objectsAreEqual;
	}

    
    public final PropertyEditorAssistDecorator getDecorator()
    {
        return this.decorator;
    }
    
    public final void setDecorator( final PropertyEditorAssistDecorator decorator )
    {
        this.decorator = decorator;
    }
    
    @Override
    protected void handlePropertyChangedEvent()
    {
        super.handlePropertyChangedEvent();
        
        CompactListPropertyEditorRenderer.this.refreshOperation.run();
    }
    
    @Override
    protected void handleListElementChangedEvent( final ModelPropertyChangeEvent event )
    {
        super.handleListElementChangedEvent( event );
        
        CompactListPropertyEditorRenderer.this.refreshOperation.run();
    }
    
    @Override
    protected void handleFocusReceivedEvent()
    {
    	this.textFields.get(0).setFocus();
    }
    
    public static final class Factory
    
        extends PropertyEditorRendererFactory
        
    {
        @Override
        public boolean isApplicableTo( final SapphirePropertyEditor propertyEditorDefinition )
        {
            return ( propertyEditorDefinition.getProperty() instanceof ListProperty );
        }
        
        @Override
        public PropertyEditorRenderer create( final SapphireRenderingContext context,
                                              final SapphirePropertyEditor part )
        {
            return new CompactListPropertyEditorRenderer( context, part );
        }
    }
    
}
