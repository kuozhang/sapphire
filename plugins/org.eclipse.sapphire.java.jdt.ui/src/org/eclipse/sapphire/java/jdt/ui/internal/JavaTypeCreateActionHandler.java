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

package org.eclipse.sapphire.java.jdt.ui.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeConstraintBehavior;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class JavaTypeCreateActionHandler extends SapphirePropertyEditorActionHandler
{
    private final JavaTypeKind kind;
    private JavaTypeName base;
    private final List<JavaTypeName> interfaces = new ArrayList<JavaTypeName>();
    
    public JavaTypeCreateActionHandler( final JavaTypeKind kind )
    {
        this.kind = kind;
    }
    
    @Override
    public void init( final SapphireAction action,
                      final ISapphireActionHandlerDef def )
    {
        super.init( action, def );

        final IModelElement element = getModelElement();
        final ValueProperty property = (ValueProperty) getProperty();
        
        final IProject proj = element.adapt( IProject.class );
        final IJavaProject jproj = JavaCore.create( proj );
        
        final JavaTypeConstraint javaTypeConstraintAnnotation = property.getAnnotation( JavaTypeConstraint.class );
        
        if( javaTypeConstraintAnnotation != null )
        {
            for( String typeName : javaTypeConstraintAnnotation.type() )
            {
                try
                {
                    final IType type = jproj.findType( typeName );
                    
                    if( type != null && type.exists() )
                    {
                        if( type.isClass() )
                        {
                            this.base = new JavaTypeName( typeName );
                        }
                        else if( type.isInterface() )
                        {
                            this.interfaces.add( new JavaTypeName( typeName ) );
                        }
                    }
                }
                catch( Exception e )
                {
                    LoggingService.log( e );
                }
            }
        }
        
        final ModelPropertyListener listener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                refreshEnablementState();
            }
        };
        
        element.addListener( listener, property.getName() );
        
        attach
        (
            new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        element.removeListener( listener, property.getName() );
                    }
                }
            }
        );
    }
    
    @Override
    protected Object run( final SapphireRenderingContext context )
    {
        final IModelElement element = getModelElement();
        final ModelProperty property = getProperty();
        
        final Value<JavaTypeName> javaTypeNameValue = element.read( (ValueProperty) property );
        
        if( javaTypeNameValue.isMalformed() )
        {
            return null;
        }
        
        final IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
            {
                monitor.beginTask( "", 6 );
                
                final JavaTypeKind kind = JavaTypeCreateActionHandler.this.kind;
                final JavaTypeName javaTypeName = javaTypeNameValue.getContent();
                
                final StringBuilder buf = new StringBuilder();
                
                if( javaTypeName.pkg() != null )
                {
                    buf.append( "package " );
                    buf.append( javaTypeName.pkg() );
                    buf.append( ";\n\n" );
                }
                
                final List<JavaTypeName> imports = new ArrayList<JavaTypeName>();
                imports.add( javaTypeName );
                
                String base = null;
                
                if( kind == JavaTypeKind.CLASS && JavaTypeCreateActionHandler.this.base != null )
                {
                    base = deriveSafeLocalName( JavaTypeCreateActionHandler.this.base, imports );
                }
                
                final List<String> interfaces = new ArrayList<String>();
                
                if( kind == JavaTypeKind.CLASS || kind == JavaTypeKind.INTERFACE )
                {
                    for( JavaTypeName t : JavaTypeCreateActionHandler.this.interfaces )
                    {
                        interfaces.add( deriveSafeLocalName( t, imports ) );
                    }
                }
                
                imports.remove( javaTypeName );
                
                if( ! imports.isEmpty() )
                {
                    for( JavaTypeName t : imports )
                    {
                        buf.append( "import " );
                        buf.append( t.qualified() );
                        buf.append( ";\n" );
                    }
                    
                    buf.append( '\n' );
                }
                
                buf.append( "public " );
                
                switch( kind )
                {
                    case ANNOTATION:  buf.append( "@interface" ); break;
                    case ENUM:        buf.append( "enum" ); break;
                    case INTERFACE:   buf.append( "interface" ); break;
                    default:          buf.append( "class" );
                }
                
                buf.append( ' ' );
                buf.append( javaTypeName.local() );
                
                if( base != null )
                {
                    buf.append( " extends " );
                    buf.append( base );
                }
                
                if( ! interfaces.isEmpty() )
                {
                    buf.append( kind == JavaTypeKind.INTERFACE ? " extends " : " implements " );
                    
                    boolean first = true;
                    
                    for( String implementsType : interfaces )
                    {
                        if( first )
                        {
                            first = false;
                        }
                        else
                        {
                            buf.append( ", " );
                        }
                        
                        buf.append( implementsType );
                    }
                }
                
                buf.append( "\n{\n}\n" );
                
                monitor.worked( 1 );
                
                final IProject proj = element.adapt( IProject.class );
                final IJavaProject jproj = JavaCore.create( proj );
                
                try
                {
                    final IPackageFragmentRoot src = src( jproj );
                    final IPackageFragment pkg = src.createPackageFragment( ( javaTypeName.pkg() == null ? "" : javaTypeName.pkg() ), true, null );
                    final ICompilationUnit cu = pkg.createCompilationUnit( javaTypeName.local() + ".java", buf.toString(), true, null );
                    
                    cu.save( null, true );
                    
                    monitor.worked( 1 );
                    
                    final IEditorPart editor = JavaUI.openInEditor( cu );
                    final IWorkbenchPartSite site = editor.getSite();
                    
                    monitor.worked( 1 );
                    
                    final OrganizeImportsAction organizeImportsAction = new OrganizeImportsAction( site );
                    organizeImportsAction.run( cu );
                    
                    monitor.worked( 1 );
                    
                    final FormatAllAction formatAllAction = new FormatAllAction( site );
                    formatAllAction.runOnMultiple( new ICompilationUnit[] { cu } );
                    
                    monitor.worked( 1 );
                    
                    editor.doSave( null );
                    
                    monitor.worked( 1 );                    
                }
                catch( CoreException e )
                {
                    LoggingService.log( e );
                }
                
                monitor.done();
            }
        };
        
        try
        {
            ( new ProgressMonitorDialog( context.getShell() ) ).run( false, false, op );
        }
        catch( InvocationTargetException e )
        {
            LoggingService.log( e );
        }
        catch( InterruptedException e )
        {
            // Should not happen.

            LoggingService.log( e );
        }
        
        return null;
    }
    
    @Override
    protected boolean computeEnablementState()
    {
        boolean enabled = super.computeEnablementState();
        
        if( enabled )
        {
            final ReferenceValue<?,?> ref = (ReferenceValue<?,?>) getModelElement().read( getProperty() );
            
            enabled = false;
            
            if( ! ref.isMalformed() )
            {
                final String typeName = ref.getText();
                
                if( typeName != null && typeName.indexOf( '$' ) == -1 && ref.resolve() == null )
                {
                    enabled = true;
                }
            }
        }
        
        return enabled;
    }

    private final IPackageFragmentRoot src( final IJavaProject project ) throws JavaModelException
    {
        for( IPackageFragmentRoot root : project.getPackageFragmentRoots() )
        {
            if( root.getKind() == IPackageFragmentRoot.K_SOURCE )
            {
                return root;
            }
        }
        
        return null;
    }
    
    private static String deriveSafeLocalName( final JavaTypeName type,
                                               final Collection<JavaTypeName> imports )
    {
        boolean collision = false;
        
        for( JavaTypeName n : imports )
        {
            if( n.local().equals( type.local() ) )
            {
                collision = true;
                break;
            }
        }
        
        if( collision )
        {
            return type.qualified();
        }
        else
        {
            imports.add( type );
            return type.local();
        }
    }
    
    protected static abstract class Condition extends SapphirePropertyEditorCondition
    {
        @Override
        protected final boolean evaluate( final SapphirePropertyEditor part )
        {
            final ModelProperty property = part.getProperty();
            
            if( property instanceof ValueProperty && property.isOfType( JavaTypeName.class ) )
            {
                final Reference referenceAnnotation = property.getAnnotation( Reference.class );
                
                if( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class )
                {
                    final JavaTypeConstraint javaTypeConstraint = property.getAnnotation( JavaTypeConstraint.class );
                    
                    if( javaTypeConstraint != null && javaTypeConstraint.behavior() == JavaTypeConstraintBehavior.ALL )
                    {
                        return evaluate( javaTypeConstraint );
                    }
                }
            }
            
            return false;
        }
        
        protected abstract boolean evaluate( JavaTypeConstraint javaTypeConstraint );
    }    

}