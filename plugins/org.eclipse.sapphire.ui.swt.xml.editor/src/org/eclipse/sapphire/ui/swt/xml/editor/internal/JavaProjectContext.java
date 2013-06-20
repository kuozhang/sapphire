package org.eclipse.sapphire.ui.swt.xml.editor.internal;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.sapphire.Context;

public final class JavaProjectContext extends Context
{
    private final IJavaProject project;
    
    public JavaProjectContext( final IJavaProject project )
    {
        this.project = project;
    }
    
    @Override
    public InputStream findResource( final String name )
    {
        if( name == null )
        {
            throw new IllegalArgumentException();
        }
        
        final int lastSlash = name.lastIndexOf( '/' );
        final String packageName;
        final String localName;
        
        if( lastSlash == -1 )
        {
            packageName = "";
            localName = name;
        }
        else
        {
            packageName = name.substring( 0, lastSlash );
            localName = name.substring( lastSlash + 1 );
        }
        
        try
        {
            final IJavaElement element = this.project.findElement( new Path( packageName ) );
            
            if( element instanceof IPackageFragment )
            {
                for( Object resource : ( (IPackageFragment) element ).getNonJavaResources() )
                {
                    if( resource instanceof IStorage )
                    {
                        final IStorage storage = (IStorage) resource;
                        
                        if( storage.getName().equals( localName ) )
                        {
                            return storage.getContents();
                        }
                    }
                }
            }
        }
        catch( CoreException e )
        {
            // Failure to open is equated with not found by returning null.
        }
        
        return null;
    }
    
    @Override
    public <T> Class<T> findClass( String name )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
