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

package org.eclipse.sapphire.modeling;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Status
{
    private static final Status OK_STATUS = new Status( Severity.OK, "", null, Collections.<Status>emptyList() );
    
    public static Status createOkStatus()
    {
        return OK_STATUS;
    }
    
    public static Status createErrorStatus( final String message )
    {
        return createErrorStatus( message, null );
    }
    
    public static Status createErrorStatus( final Throwable exception )
    {
        String message = exception.getMessage();
        
        if( message == null )
        {
            message = "ERROR"; // TODO: externalize
        }
        
        return createErrorStatus( message, exception );
    }
    
    public static Status createErrorStatus( final String message,
                                            final Throwable exception )
    {
        return createStatus( Severity.ERROR, message, exception );
    }
    
    public static Status createStatus( final Severity severity,
                                       final String message,
                                       final Throwable exception )
    {
        return factoryForLeaf().severity( severity ).message( message ).exception( exception ).create();
    }
    
    public static Status createStatus( final Severity severity,
                                       final String message )
    {
        return createStatus( severity, message, null );
    }
    
    public static LeafStatusFactory factoryForLeaf()
    {
        return new LeafStatusFactory();
    }
    
    public static CompositeStatusFactory factoryForComposite()
    {
        return new CompositeStatusFactory();
    }
    
    private final Severity severity;
    private final String message;
    private final Throwable exception;
    private final List<Status> children;
    
    private Status( final Severity severity,
                    final String message,
                    final Throwable exception,
                    final List<Status> children )
    {
        this.severity = severity;
        this.message = message;
        this.exception = exception;
        this.children = children;
    }
    
    public boolean ok()
    {
        return ( this.severity == Severity.OK );
    }

    public Severity severity()
    {
        return this.severity;
    }

    public String message()
    {
        return this.message;
    }

    public Throwable exception()
    {
        return this.exception;
    }

    public List<Status> children()
    {
        return this.children;
    }
    
    @Override
    public boolean equals( final Object obj )
    {
        if( obj == this )
        {
            return true;
        }
        
        if( obj instanceof Status )
        {
            final Status st = (Status) obj;
            
            if( st.severity() == severity() && 
                st.children().size() == children().size() && 
                st.exception() == exception() && 
                st.message().equals( message() ) )
            {
                for( Iterator<Status> itr1 = st.children().iterator(), itr2 = children().iterator(); itr1.hasNext(); )
                {
                    if( ! itr1.next().equals( itr2.next() ) )
                    {
                        return false;
                    }
                }
                
                return true;
            }
        }
        
        return false;
    }

    @Override
    public int hashCode()
    {
        return severity().code() ^ message().hashCode();
    }
    
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        
        buf.append( severity().name() );
        buf.append( " : " );
        buf.append( message() );
        
        final Throwable e = exception();
        
        if( e != null )
        {
            buf.append( System.getProperty( "line.separator" ) );

            final StringWriter sw = new StringWriter();
            e.printStackTrace( new PrintWriter( sw ) );
            buf.append( sw.toString() );
        }
        
        return buf.toString();
    }

    public enum Severity
    {
        OK( 0 ),
        INFO( 1 ),
        WARNING( 2 ),
        ERROR( 4 );
        
        private int code;
        
        private Severity( final int code )
        {
            this.code = code;
        }
        
        public int code()
        {
            return this.code;
        }
    }
    
    public static final class LeafStatusFactory
    {
        private Severity severity;
        private String message;
        private Throwable exception;
        
        private LeafStatusFactory()
        {
            // No direct public instantiation. Use factoryForLeaf() method instead.
        }
        
        public LeafStatusFactory severity( final Severity severity )
        {
            this.severity = severity;
            return this;
        }

        public LeafStatusFactory message( final String message )
        {
            this.message = message;
            return this;
        }

        public LeafStatusFactory exception( final Throwable exception )
        {
            this.exception = exception;
            return this;
        }
        
        public Status create()
        {
            if( this.severity == null )
            {
                throw new IllegalStateException();
            }
            
            if( this.message == null )
            {
                throw new IllegalStateException();
            }
            
            return new Status( this.severity, this.message, this.exception, Collections.<Status>emptyList() );
        }
    }
    
    public static final class CompositeStatusFactory
    {
        private Severity severity = Severity.OK;
        private String message = "";
        private Throwable exception = null;
        private final List<Status> children = new ArrayList<Status>();
        
        private CompositeStatusFactory()
        {
            // No direct public instantiation. Use factoryForComposite() method instead.
        }
        
        public CompositeStatusFactory add( final Status status ) 
        {
            if( status != null )
            {
                final Severity sev = status.severity();
                
                if( sev != Severity.OK )
                {
                    if( sev.code() > this.severity.code() )
                    {
                        this.severity = sev;
                        this.message = status.message();
                        this.exception = status.exception();
                    }
                    
                    this.children.add( status );
                }
            }
            
            return this;
        }
        
        public CompositeStatusFactory merge( final Status status )
        {
            if( status != null )
            {
                if( status.children().isEmpty() )
                {
                    add( status );
                }
                else
                {
                    for( Status st : status.children() )
                    {
                        add( st );
                    }
                }
            }
            
            return this;
        }
        
        public Status create()
        {
            final int count = this.children.size();
            
            if( count == 0 )
            {
                return createOkStatus();
            }
            else if( count == 1 )
            {
                return this.children.get( 0 );
            }
            else
            {
                return new Status( this.severity, this.message, this.exception, Collections.unmodifiableList( this.children ) );
            }
        }
    }
    
}
