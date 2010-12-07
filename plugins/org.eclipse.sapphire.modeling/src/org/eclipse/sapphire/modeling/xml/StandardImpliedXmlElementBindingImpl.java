package org.eclipse.sapphire.modeling.xml;

import org.eclipse.sapphire.modeling.ElementBindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

public final class StandardImpliedXmlElementBindingImpl

    extends ElementBindingImpl
    
{
    private XmlPath path;
    private Resource resource;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        if( property.getAllPossibleTypes().size() > 1 )
        {
            throw new IllegalStateException();
        }
        
        String pathString = null;
        
        final XmlElementBinding xmlElementBindingAnnotation = property.getAnnotation( XmlElementBinding.class );
        
        if( xmlElementBindingAnnotation != null )
        {
            if( xmlElementBindingAnnotation.mappings().length > 0 )
            {
                throw new IllegalStateException();
            }
            
            pathString = xmlElementBindingAnnotation.path();
        }
        else
        {
            final XmlBinding xmlBindingAnnotation = property.getAnnotation( XmlBinding.class );
            
            if( xmlBindingAnnotation != null )
            {
                pathString = xmlBindingAnnotation.path();
            }
        }
        
        if( pathString != null && pathString.length() > 0 )
        {
            this.path = new XmlPath( pathString, ( (XmlResource) element.resource() ).getXmlNamespaceResolver() );
        }
    }
    
    @Override
    public ModelElementType type( final Resource resource )
    {
        return property().getType();
    }

    @Override
    public Resource read()
    {
        if( this.resource == null )
        {
            final XmlResource parentXmlResource = (XmlResource) element().resource();
            this.resource = new VirtualChildXmlResource( parentXmlResource, this.path );
        }
        
        return this.resource;
    }
    
}
