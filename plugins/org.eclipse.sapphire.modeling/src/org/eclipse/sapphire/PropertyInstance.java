/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.Service;

/**
 * Represents an instance of a property within an element.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class PropertyInstance
{
    private final IModelElement element;
    private final ModelProperty property;
    
    public PropertyInstance( final IModelElement element,
                             final ModelProperty property )
    {
        if( element == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.element = element;
        
        if( property == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.property = property;
    }
    
    /**
     * Return the element instance.
     * 
     * @return the element instance
     */
    
    public IModelElement element()
    {
        return this.element;
    }
    
    /**
     * Returns the property.
     * 
     * @return the property
     */
    
    public ModelProperty property()
    {
        return this.property;
    }
    
    /**
     * Reads this property.
     * 
     * @return the property content
     */
    
    @SuppressWarnings( "unchecked" )
    
    public <T> T read()
    {
        return (T) this.element.read( this.property );
    }
    
    /**
     * Writes content to this property. Only defined for value and transient properties.
     * 
     * @param content the property content
     */
    
    public void write( final Object content )
    {
        this.element.write( this.property, content );
    }
    
    /**
     * Clears this property. 
     */
    
    public void clear()
    {
        this.element.clear( this.property );
    }
    
    /**
     * Determines if this property is empty. The empty state is defined as follows:
     * 
     * <ul>
     *   <li><b>Value Property</b> - has null value or has default value</li>
     *   <li><b>Element Property</b> - element does not exist</li>
     *   <li><b>Implied Element Property</b> - none of the child element's properties are non-empty</li>
     *   <li><b>List Property</b> - list size is zero</li>
     *   <li><b>Transient Property</b> - has null content</li>
     * </ul>
     * 
     * @return true if this property is empty, false otherwise
     */
    
    public boolean empty()
    {
        return this.element.empty( this.property );
    }

    /**
     * Determines whether this property is enabled
     * 
     * @return true if this property is enabled and false otherwise
     */
    
    public boolean enabled()
    {
        return this.element.enabled( this.property );
    }
    
    /**
     * Returns the validation result for this property.
     * 
     * @return the validation result for this property
     */
    
    public Status validation()
    {
        return this.element.validation( this.property );
    }
    
    public void refresh()
    {
        this.element.refresh( this.property );
    }
    
    public void refresh( final boolean force )
    {
        this.element.refresh( this.property, force );
    }
    
    public void refresh( final boolean force,
                         final boolean deep )
    {
        this.element.refresh( this.property, force, deep );
    }
    
    /**
     * Returns the service of the specified type from the property instance service context.
     * 
     * <p>Service Context: <b>Sapphire.Property.Instance</b></p>
     * 
     * @param <S> the type of the service
     * @param type the type of the service
     * @return the service or <code>null</code> if not available
     */
    
    public <S extends Service> S service( final Class<S> type )
    {
        return this.element.service( this.property, type );
    }

    /**
     * Returns the service of the specified type from the property instance service context.
     * 
     * <p>Service Context: <b>Sapphire.Property.Instance</b></p>
     * 
     * @param <S> the type of the service
     * @param type the type of the service
     * @return the service or <code>null</code> if not available
     */
    
    public <S extends Service> List<S> services( final Class<S> type )
    {
        return this.element.services( this.property, type );
    }
    
    /**
     * Attaches a listener to this property.
     * 
     * @param listener the listener
     */
    
    public void attach( final Listener listener )
    {
        this.element.attach( listener, this.property );
    }
    
    /**
     * Detaches a listener from this property.
     * 
     * @param listener the listener
     */
    
    public void detach( final Listener listener )
    {
        this.element.detach( listener, this.property );
    }
    
}
