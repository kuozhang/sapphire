/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire;

import java.util.ArrayList;
import java.util.List;

/**
 * A version constraint is a boolean expression that can check versions for applicability. In string 
 * format, it is represented as a comma-separated list of specific versions, closed 
 * ranges (expressed using "[1.2.3-4.5)" syntax and open ranges (expressed using "[1.2.3" or "4.5)" 
 * syntax). The square brackets indicate that the range includes the specified version. The parenthesis 
 * indicate that the range goes up to, but does not actually include the specified version.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class VersionConstraint
{
    private static final int SM_RANGE_STARTING = 0;
    private static final int SM_RANGE_ENDED = 1;
    private static final int SM_VERSION_STARTING = 2;
    private static final int SM_VERSION_SEGMENT_STARTING = 3;
    private static final int SM_VERSION_SEGMENT_CONTINUING = 4;
    private static final int SM_VERSION_ENDED = 5;
    
    private final List<Range> ranges;
    
    public VersionConstraint( final String expr )
    {
        if( expr == null )
        {
            throw new IllegalArgumentException();
        }
        
        this.ranges = new ArrayList<Range>();
        
        int state = SM_RANGE_STARTING;
        Range range = null;
        StringBuilder buf = null;
        
        for( int position = 0, n = expr.length(); position < n; position++ )
        {
            final char ch = expr.charAt( position );
            
            switch( state )
            {
                case SM_RANGE_STARTING:
                {
                    if( ch == ' ' )
                    {
                        // ignore
                    }
                    else if( ch == '[' )
                    {
                        range = new Range();
                        range.includesStartVersion = true;
                        buf = new StringBuilder();
                        state = SM_VERSION_STARTING;
                    }
                    else if( ch == '(' )
                    {
                        range = new Range();
                        range.includesStartVersion = false;
                        buf = new StringBuilder();
                        state = SM_VERSION_STARTING;
                    }
                    else if( ch >= '0' && ch <= '9' )
                    {
                        buf = new StringBuilder();
                        buf.append( ch );
                        state = SM_VERSION_SEGMENT_CONTINUING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                case SM_RANGE_ENDED:
                {
                    if( ch == ' ' )
                    {
                        // ignore
                    }
                    else if( ch == ',' )
                    {
                        state = SM_RANGE_STARTING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                case SM_VERSION_STARTING:
                {
                    if( ch == ' ' )
                    {
                        // ignore
                    }
                    else if( ch >= '0' && ch <= '9' )
                    {
                        buf.append( ch );
                        state = SM_VERSION_SEGMENT_CONTINUING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                case SM_VERSION_SEGMENT_STARTING:
                {
                    if( ch >= '0' && ch <= '9' )
                    {
                        buf.append( ch );
                        state = SM_VERSION_SEGMENT_CONTINUING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                case SM_VERSION_SEGMENT_CONTINUING:
                {
                    if( ch >= '0' && ch <= '9' )
                    {
                        buf.append( ch );
                    }
                    else if( ch == '.' )
                    {
                        buf.append( ch );
                        state = SM_VERSION_SEGMENT_STARTING;
                    }
                    else if( ch == ' ' )
                    {
                        state = SM_VERSION_ENDED;
                    }
                    else if( ch == ']' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                        }
                        
                        range.endVersion = new Version( buf.toString() );
                        range.includesEndVersion = true;
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_ENDED;
                    }
                    else if( ch == ')' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                        }
                        
                        range.endVersion = new Version( buf.toString() );
                        range.includesEndVersion = false;
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_ENDED;
                    }
                    else if( ch == '-' )
                    {
                        if( range == null )
                        {
                            throw new IllegalArgumentException();
                        }
                        
                        range.startVersion = new Version( buf.toString() );
                        
                        buf = new StringBuilder();
                        
                        state = SM_VERSION_STARTING;
                    }
                    else if( ch == ',' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                            range.startVersion = new Version( buf.toString() );
                            range.endVersion = range.startVersion;
                            range.includesStartVersion = true;
                            range.includesEndVersion = true;
                        }
                        else
                        {
                            range.startVersion = new Version( buf.toString() );
                        }
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_STARTING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                case SM_VERSION_ENDED:
                {
                    if( ch == ' ' )
                    {
                        // ignore
                    }
                    else if( ch == ']' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                        }
                        
                        range.endVersion = new Version( buf.toString() );
                        range.includesEndVersion = true;
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_ENDED;
                    }
                    else if( ch == ')' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                        }
                        
                        range.endVersion = new Version( buf.toString() );
                        range.includesEndVersion = false;
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_ENDED;
                    }
                    else if( ch == '-' )
                    {
                        if( range == null )
                        {
                            throw new IllegalArgumentException();
                        }
                        
                        range.startVersion = new Version( buf.toString() );
                        
                        buf = new StringBuilder();
                        
                        state = SM_VERSION_STARTING;
                    }
                    else if( ch == ',' )
                    {
                        if( range == null )
                        {
                            range = new Range();
                            range.startVersion = new Version( buf.toString() );
                            range.endVersion = range.startVersion;
                            range.includesStartVersion = true;
                            range.includesEndVersion = true;
                        }
                        else
                        {
                            range.startVersion = new Version( buf.toString() );
                        }
                        
                        this.ranges.add( range );
                        
                        range = null;
                        buf = null;
                        
                        state = SM_RANGE_STARTING;
                    }
                    else
                    {
                        throw new IllegalArgumentException();
                    }
                    
                    break;
                }
                default:
                {
                    throw new IllegalStateException();
                }
            }
        }
        
        if( state == SM_VERSION_SEGMENT_CONTINUING || state == SM_VERSION_ENDED )
        {
            if( range == null )
            {
                range = new Range();
                range.startVersion = new Version( buf.toString() );
                range.endVersion = range.startVersion;
                range.includesStartVersion = true;
                range.includesEndVersion = true;
            }
            else
            {
                range.startVersion = new Version( buf.toString() );
            }
            
            this.ranges.add( range );
            
            range = null;
            buf = null;
            
            state = SM_RANGE_ENDED;
        }
        
        if( state != SM_RANGE_ENDED )
        {
            throw new IllegalArgumentException();
        }
    }
    
    public boolean check( final Version version )
    {
        for( Range subexpr : this.ranges )
        {
            if( subexpr.check( version ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean check( final String version )
    {
        return check( new Version( version ) );
    }
    
    @Override
    public String toString()
    {
        final StringBuffer buf = new StringBuffer();
        
        for( Range subexpr : this.ranges )
        {
            if( buf.length() > 0 ) buf.append( ',' );
            buf.append( subexpr.toString() );
        }
        
        return buf.toString();
    }
    
    private static final class Range
    {
        public Version startVersion = null;
        public boolean includesStartVersion = false;
        public Version endVersion = null;
        public boolean includesEndVersion = false;
        
        public boolean check( final Version version )
        {
            if( this.startVersion != null )
            {
                final int res = version.compareTo( this.startVersion );
                
                if( ! ( res > 0 || ( res == 0 && this.includesStartVersion ) ) )
                {
                    return false;
                }
            }
            
            if( this.endVersion != null )
            {
                final int res = version.compareTo( this.endVersion );
                
                if( ! ( res < 0 || ( res == 0 && this.includesEndVersion ) ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        
        @Override
        public String toString()
        {
            if( this.startVersion.equals( this.endVersion ) &&
                this.includesStartVersion == this.includesEndVersion == true )
            {
                return this.startVersion.toString();
            }
            else
            {
                final StringBuffer buf = new StringBuffer();
                
                if( this.startVersion != null )
                {
                    buf.append( this.includesStartVersion ? '[' : '(' );
                    buf.append( this.startVersion.toString() );
                }
                
                if( this.endVersion != null )
                {
                    if( buf.length() != 0 )
                    {
                        buf.append( '-' );
                    }
                    
                    buf.append( this.endVersion.toString() );
                    buf.append( this.includesEndVersion ? ']' : ')' );
                }
                
                return buf.toString();
            }
        }
    }

}
