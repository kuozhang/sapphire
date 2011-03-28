/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.renderers.swt;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

class CompactListProxyResource extends XmlResource {
	
	private IModelElement proxyElement;
	private IModelElement actualElement;
    private ValueProperty actualProperty;

    private CompactListPropertyEditorRenderer renderer;
    
    private String value;

	public CompactListProxyResource(CompactListPropertyEditorRenderer renderer) {
		super((XmlResource)renderer.getPart().getModelElement().resource());
		
		this.renderer = renderer;
	}

	public void init(IModelElement proxyElement, ValueProperty actualProperty) {
		this.proxyElement = proxyElement;
		this.actualProperty = actualProperty;
	}
	

	@Override
	protected BindingImpl createBinding(ModelProperty property) {
		if (property instanceof ValueProperty) {
			return new ProxyBinding();
		}
		return null;
	}
	
	public void write(String value) {
		this.value = value;

		assert this.actualElement != null;
		this.proxyElement.refresh();
		this.actualElement.write( this.actualProperty, value );
	}
	
	public void setModelElement(IModelElement element) {
		this.actualElement = element;
		this.value = element != null ? element.read(this.actualProperty).getText() : null;
		this.proxyElement.refresh();
	}

	public IModelElement getModelElement() {
		return this.actualElement;
	}
	
	public ValueProperty getValueProperty() {
		return this.actualProperty;
	}
	
	public IModelElement getProxyElement() {
		return this.proxyElement;
	}
	
	private IModelElement getActualElement(boolean create) {
		if (create && this.actualElement == null) {
			final IModelElement element = this.renderer.getList().addNewElement();
			setModelElement(element);
		}
		return this.actualElement;
	}

	private class ProxyBinding extends XmlValueBindingImpl {

		@Override
		public String read() {
			return CompactListProxyResource.this.value;
		}

		@Override
		public void write(String value) {
			CompactListProxyResource.this.value = value;
			
			final IModelElement element = getActualElement(true/*create*/);
			final ValueProperty property = getValueProperty();
			element.write(property, value);
		}
	}

	@Override
	public XmlElement getXmlElement(boolean createIfNecessary) {
		final IModelElement element = getActualElement(true/*create*/);
		if (element != null) {
			return ((XmlResource)element.resource()).getXmlElement();
		}
		return null;
	}

}
