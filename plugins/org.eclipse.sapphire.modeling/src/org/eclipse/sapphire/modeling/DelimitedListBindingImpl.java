/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class DelimitedListBindingImpl

    extends ListBindingImpl

{
    private ModelElementType listEntryType;
    private ValueProperty listEntryProperty;
    private Entry head;
    
    @Override
    public void init( final IModelElement element,
                      final ModelProperty property,
                      final String[] params )
    {
        super.init( element, property, params );
        
        this.listEntryType = property.getType();
        
        for( ModelProperty prop : this.listEntryType.getProperties() )
        {
            if( this.listEntryProperty != null )
            {
                throw new IllegalStateException();
            }
            
            if( prop instanceof ValueProperty )
            {
                this.listEntryProperty = (ValueProperty) prop;
            }
            else
            {
                throw new IllegalStateException();
            }
        }
        
        if( this.listEntryProperty == null )
        {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public ModelElementType type( final Resource resource )
    {
        return property().getType();
    }

    @Override
    public final List<Resource> read()
    {
        final List<String> strings = split( readListString() );
        final int count = strings.size();
        
        if( count == 0 )
        {
            this.head = null;
        }
        else
        {
            if( this.head == null )
            {
                this.head = new Entry();
            }
            
            int i = 0;
            Entry prev = null;
            Entry entry = this.head;
            
            while( i < count && entry != null )
            {
                entry.value = strings.get( i );
                
                i++;
                prev = entry;
                entry = entry.next;
            }
            
            entry = prev;
            
            while( entry.next != null )
            {
                entry.next.remove();
            }
            
            while( i < count )
            {
                entry = entry.insertAfter();
                entry.value = strings.get( i );
                i++;
            }
        }
        
        final List<Resource> base = new ArrayList<Resource>();
        
        if( this.head != null )
        {
            Entry entry = this.head;
            
            while( entry != null )
            {
                base.add( entry );
                entry = entry.next;
            }
        }
        
        return base;
    }
    
    @Override
    public final Resource add( final ModelElementType type )
    {
        Entry entry = this.head;
        
        if( entry == null )
        {
            entry = new Entry();
            this.head = entry;
        }
        else
        {
            while( entry.next != null )
            {
                entry = entry.next;
            }
            
            entry = entry.insertAfter();
        }
        
        writeListString();
        
        return entry;
    }
    
    @Override
    public void remove( final Resource resource )
    {
        ( (Entry) resource ).remove();
        writeListString();
    }

    @Override
    public final void swap( final Resource x,
                            final Resource y )
    {
        final Entry a = (Entry) x;
        final Entry b = (Entry) y;
        
        if( a.next == b )
        {
            final Entry aPrev = a.prev;
            final Entry bNext = b.next;
            
            a.prev = b;
            a.next = bNext;
            
            b.prev = aPrev;
            b.next = a;
            
            if( aPrev != null )
            {
                aPrev.next = b;
            }
            
            if( bNext != null )
            {
                bNext.prev = a;
            }
            
            if( a == this.head )
            {
                this.head = b;
            }
        }
        else if( b.next == a )
        {
            swap( y, x );
        }
        else
        {
            throw new UnsupportedOperationException();
        }
        
        writeListString();
    }
    
    protected char getDelimiter()
    {
        return ',';
    }
    
    protected abstract String readListString();
    
    protected abstract void writeListString( String str );
    
    private void writeListString()
    {
        final char delimiter = getDelimiter();
        final String str;
        
        if( this.head == null )
        {
            str = null;
        }
        else
        {
            final StringBuilder buf = new StringBuilder();
            
            for( Entry entry = this.head; entry != null; entry = entry.next )
            {
                if( entry != this.head )
                {
                    buf.append( delimiter );
                }
                
                final String val = entry.value;
                
                if( val != null )
                {
                    buf.append( val );
                }
            }
            
            str = buf.toString();
        }
        
        writeListString( str );
    }
    
    private List<String> split( final String str )
    {
        final char delimiter = getDelimiter();
        final List<String> list = new ArrayList<String>();
        
        if( str != null )
        {
            StringBuilder buf = new StringBuilder();
            
            for( int i = 0, n = str.length(); i < n; i++ )
            {
                final char ch = str.charAt( i );
                
                if( ch == delimiter )
                {
                    list.add( buf.toString() );
                    buf = new StringBuilder();
                }
                else
                {
                    buf.append( ch );
                }
            }
            
            list.add( buf.toString() );
        }
        
        return list;
    }
    
    private final class Entry
    
        extends Resource
        
    {
        public Entry prev;
        public Entry next;
        public String value;
        
        public Entry()
        {
            super( DelimitedListBindingImpl.this.element().resource() );
        }
        
        @Override
        protected BindingImpl createBinding( final ModelProperty property )
        {
            if( property == DelimitedListBindingImpl.this.listEntryProperty )
            {
                return new ValueBindingImpl()
                {
                    @Override
                    public String read()
                    {
                        return getValue();
                    }
                    
                    @Override
                    public void write( final String value )
                    {
                        setValue( value );
                    }
                };
            }
            
            return null;
        }

        public String getValue()
        {
            return this.value;
        }

        public void setValue( final String value )
        {
            final List<String> segments = split( value );
            final int count = segments.size();
            
            if( count == 0 )
            {
                this.value = null;
            }
            else
            {
                this.value = segments.get( 0 );
                
                for( int i = 1; i < count; i++ )
                {
                    final Entry entry = insertAfter();
                    entry.binding( DelimitedListBindingImpl.this.listEntryProperty ).write( segments.get( i ) );
                }
            }
            
            writeListString();
        }
        
        public void remove()
        {
            if( this == DelimitedListBindingImpl.this.head )
            {
                DelimitedListBindingImpl.this.head = this.next;
            }
            
            if( this.prev != null )
            {
                this.prev.next = this.next;
            }
            
            if( this.next != null )
            {
                this.next.prev = this.prev;
            }
            
            this.prev = null;
            this.next = null;
        }

        private Entry insertAfter()
        {
            final Entry entry = new Entry();
    
            entry.prev = this;
            entry.next = this.next;
            this.next = entry;
            
            if( entry.next != null )
            {
                entry.next.prev = entry;
            }
            
            return entry;
        }
    }

}
