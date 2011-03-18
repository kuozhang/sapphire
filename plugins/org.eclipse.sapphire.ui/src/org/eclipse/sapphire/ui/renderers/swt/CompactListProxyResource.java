package org.eclipse.sapphire.ui.renderers.swt;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;
import org.eclipse.sapphire.modeling.ValueProperty;

public class CompactListProxyResource extends Resource {
	
	private IModelElement proxyElement;
	private IModelElement actualElement;
    private ValueProperty actualProperty;
    
    private String value;

	public CompactListProxyResource() {
		super(null);
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

	private class ProxyBinding extends ValueBindingImpl {

		@Override
		public String read() {
			return CompactListProxyResource.this.value;
		}

		@Override
		public void write(String value) {
			CompactListProxyResource.this.value = value;
			
			final IModelElement element = CompactListProxyResource.this.actualElement;
			final ValueProperty property = CompactListProxyResource.this.actualProperty;
			if (element != null) {
				element.write(property, value);
			}
		}
	}

}
