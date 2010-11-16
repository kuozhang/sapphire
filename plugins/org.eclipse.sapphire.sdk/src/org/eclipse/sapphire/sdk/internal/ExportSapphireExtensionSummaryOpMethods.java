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

package org.eclipse.sapphire.sdk.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.extensibility.IModelElementServiceDef;
import org.eclipse.sapphire.modeling.extensibility.IModelPropertyServiceDef;
import org.eclipse.sapphire.modeling.extensibility.IValueSerializationServiceDef;
import org.eclipse.sapphire.modeling.internal.SapphireModelingFrameworkPlugin;
import org.eclipse.sapphire.sdk.IExportSapphireExtensionSummaryOp;
import org.eclipse.sapphire.sdk.ISapphireExtensionDef;
import org.eclipse.sapphire.ui.def.ISapphireActionContext;
import org.eclipse.sapphire.ui.def.ISapphireActionDef;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerFactoryDef;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ExportSapphireExtensionSummaryOpMethods
{
    private static String STYLE;
    
    public static String execute( final IExportSapphireExtensionSummaryOp op,
                                  final List<ISapphireExtensionDef> extensions,
                                  final IProgressMonitor monitor )
    {
        // Gather extensions by type.
        
        final List<IModelElementServiceDef> modelElementServices = new ArrayList<IModelElementServiceDef>();
        final List<IModelPropertyServiceDef> modelPropertyServices = new ArrayList<IModelPropertyServiceDef>();
        final List<IValueSerializationServiceDef> valueSerializationServices = new ArrayList<IValueSerializationServiceDef>();
        
        final List<ISapphireActionDef> actions = new ArrayList<ISapphireActionDef>();
        final List<ISapphireActionHandlerDef> actionHandlers = new ArrayList<ISapphireActionHandlerDef>();
        final List<ISapphireActionHandlerFactoryDef> actionHandlerFactories = new ArrayList<ISapphireActionHandlerFactoryDef>();
        
        for( ISapphireExtensionDef extension : extensions )
        {
            modelElementServices.addAll( extension.getModelElementServices() );
            modelPropertyServices.addAll( extension.getModelPropertyServices() );
            valueSerializationServices.addAll( extension.getValueSerializationServices() );
            
            actions.addAll( extension.getActions() );
            actionHandlers.addAll( extension.getActionHandlers() );
            actionHandlerFactories.addAll( extension.getActionHandlerFactories() );
        }
        
        // Sort extensions.
        
        Collections.sort
        ( 
            actions, 
            new Comparator<ISapphireActionDef>()
            {
                public int compare( final ISapphireActionDef x,
                                    final ISapphireActionDef y )
                {
                    return comp( x.getId().getContent(), y.getId().getContent() );
                }
            }
        );
        
        Collections.sort
        ( 
            actionHandlers, 
            new Comparator<ISapphireActionHandlerDef>()
            {
                public int compare( final ISapphireActionHandlerDef x,
                                    final ISapphireActionHandlerDef y )
                {
                    int res = comp( x.getAction().getContent(), y.getAction().getContent() );
                    
                    if( res == 0 )
                    {
                        res = comp( x.getId().getContent(), y.getId().getContent() );
                    }
                    
                    return res;
                }
            }
        );
        
        Collections.sort
        ( 
            actionHandlerFactories, 
            new Comparator<ISapphireActionHandlerFactoryDef>()
            {
                public int compare( final ISapphireActionHandlerFactoryDef x,
                                    final ISapphireActionHandlerFactoryDef y )
                {
                    int res = comp( x.getAction().getContent(), y.getAction().getContent() );
                    
                    if( res == 0 )
                    {
                        res = comp( x.getImplClass().getContent(), y.getImplClass().getContent() );
                    }
                    
                    return res;
                }
            }
        );
        
        // Write the summary document.
        
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter out = new PrintWriter( stringWriter );
        
        out.println( "<html>" );
        out.println();
        out.println( "<head>" );
        out.println( "  <title>" + op.getDocumentTitle().getContent() + "</title>" );
        
        if( op.getEmbedDefaultStyle().getContent() )
        {
            out.println( style() );
        }
        
        out.println( "</head>" );
        out.println();
        out.println( "<body>" );
        
        final String documentBodyTitle = op.getDocumentBodyTitle().getContent();
        final String extCategoryHeaderLevel; 
        
        if( documentBodyTitle != null )
        {
            out.println( "<h1>" + documentBodyTitle + "</h1>" );
            extCategoryHeaderLevel = "h2";
        }
        else
        {
            extCategoryHeaderLevel = "h1";
        }
        
        if( ! modelElementServices.isEmpty() )
        {
            boolean writeDescriptionColumn = false;
            
            for( IModelElementServiceDef service : modelElementServices )
            {
                if( service.getDescription().getText() != null )
                {
                    writeDescriptionColumn = true;
                    break;
                }
            }
            
            out.println();
            out.println( "<a name=\"actions\"><" + extCategoryHeaderLevel + ">Model Element Services</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            
            if( writeDescriptionColumn )
            {
                out.println( "    <th>Description</th>" );
            }
            
            out.println( "    <th>Type</th>" );
            out.println( "    <th>Factory</th>" );
            out.println( "  </tr>" );
            
            for( IModelElementServiceDef service : modelElementServices )
            {
                out.println( "  <tr>" );
            
                if( writeDescriptionColumn )
                {
                    out.println( td( service.getDescription() ) );
                }
                
                out.println( td( formatClassName( service.getTypeClass() ) ) );
                out.println( td( formatClassName( service.getFactoryClass() ) ) );

                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }
        
        if( ! modelPropertyServices.isEmpty() )
        {
            boolean writeDescriptionColumn = false;
            
            for( IModelPropertyServiceDef service : modelPropertyServices )
            {
                if( service.getDescription().getText() != null )
                {
                    writeDescriptionColumn = true;
                    break;
                }
            }
            
            out.println();
            out.println( "<a name=\"actions\"><" + extCategoryHeaderLevel + ">Model Property Services</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            
            if( writeDescriptionColumn )
            {
                out.println( "    <th>Description</th>" );
            }
            
            out.println( "    <th>Type</th>" );
            out.println( "    <th>Factory</th>" );
            out.println( "  </tr>" );
            
            for( IModelPropertyServiceDef service : modelPropertyServices )
            {
                out.println( "  <tr>" );
                
                if( writeDescriptionColumn )
                {
                    out.println( td( service.getDescription() ) );
                }
                
                out.println( td( formatClassName( service.getTypeClass() ) ) );
                out.println( td( formatClassName( service.getFactoryClass() ) ) );

                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }
        
        if( ! valueSerializationServices.isEmpty() )
        {
            boolean writeDescriptionColumn = false;
            
            for( IValueSerializationServiceDef service : valueSerializationServices )
            {
                if( service.getDescription().getText() != null )
                {
                    writeDescriptionColumn = true;
                    break;
                }
            }
            
            out.println();
            out.println( "<a name=\"actions\"><" + extCategoryHeaderLevel + ">Value Serialization Services</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            
            if( writeDescriptionColumn )
            {
                out.println( "    <th>Description</th>" );
            }
            
            out.println( "    <th>Type</th>" );
            out.println( "    <th>Implementation</th>" );
            out.println( "  </tr>" );
            
            for( IValueSerializationServiceDef service : valueSerializationServices )
            {
                out.println( "  <tr>" );
            
                if( writeDescriptionColumn )
                {
                    out.println( td( service.getDescription() ) );
                }
                
                out.println( td( formatClassName( service.getTypeClass() ) ) );
                out.println( td( formatClassName( service.getImplClass() ) ) );

                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }
        
        if( ! actions.isEmpty() )
        {
            out.println();
            out.println( "<a name=\"actions\"><" + extCategoryHeaderLevel + ">Actions</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            out.println( "    <th>ID</th>" );
            out.println( "    <th>Description</th>" );
            out.println( "    <th>Key Binding</th>" );
            out.println( "    <th>Contexts</th>" );
            out.println( "  </tr>" );
            
            for( ISapphireActionDef action : actions )
            {
                out.println( "  <tr>" );
                
                out.println( td( action.getId() ) );
                out.println( td( action.getDescription() ) );
                out.println( td( action.getKeyBinding() ) );
                
                final StringBuilder contexts = new StringBuilder();
                
                for( ISapphireActionContext context : action.getContexts() )
                {
                    if( contexts.length() > 0 )
                    {
                        contexts.append( "<br/>" );
                    }
                    
                    contexts.append( normalize( context.getContext().getText() ) );
                }
                
                out.println( td( contexts.toString() ) );
                
                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }
        
        if( ! actionHandlers.isEmpty() )
        {
            out.println();
            out.println( "<a name=\"action-handlers\"><" + extCategoryHeaderLevel + ">Action Handlers</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            out.println( "    <th>Action</th>" );
            out.println( "    <th>ID</th>" );
            out.println( "    <th>Description</th>" );
            out.println( "  </tr>" );
            
            for( ISapphireActionHandlerDef handler : actionHandlers )
            {
                out.println( "  <tr>" );
                
                out.println( td( handler.getAction() ) );
                out.println( td( handler.getId() ) );
                out.println( td( handler.getDescription() ) );
                
                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }
        
        if( ! actionHandlerFactories.isEmpty() )
        {
            out.println();
            out.println( "<a name=\"action-handler-factories\"><" + extCategoryHeaderLevel + ">Action Handler Factories</" + extCategoryHeaderLevel + "></a>" );
            out.println();
            out.println( "<table>" );
            out.println( "  <tr>" );
            out.println( "    <th>Action</th>" );
            out.println( "    <th>Description</th>" );
            out.println( "  </tr>" );
            
            for( ISapphireActionHandlerFactoryDef factory : actionHandlerFactories )
            {
                out.println( "  <tr>" );
                
                out.println( td( factory.getAction() ) );
                out.println( td( factory.getDescription() ) );
                
                out.println( "  </tr>" );
            }
            
            out.println( "</table>" );
        }

        out.println( "<br/><br/>" );
        out.println();
        out.println( "</body>" );
        out.println();
        out.println( "</head>" );
        
        out.flush();
        
        return stringWriter.getBuffer().toString();
    }
    
    private static String style()
    {
        if( STYLE == null )
        {
            final StringBuilder buf = new StringBuilder();
            
            buf.append( "<style type=\"text/css\">\n" );
            
            try
            {
                final Bundle bundle = Platform.getBundle( "org.eclipse.sapphire.doc" );
                
                if( bundle != null )
                {
                    final URL url = bundle.getEntry( "html/style.css" );
                    
                    if( url != null )
                    {
                        final InputStream in = url.openStream();
                        
                        try
                        {
                            final Reader reader = new InputStreamReader( in );
                            final char[] chars = new char[ 1024 ];
                            
                            for( int count = reader.read( chars ); count != -1; count = reader.read( chars ) )
                            {
                                buf.append( chars, 0, count );
                            }
                        }
                        finally
                        {
                            try
                            {
                                in.close();
                            }
                            catch( IOException e ) {}
                        }
                    }
                }
            }
            catch( Exception e )
            {
                SapphireModelingFrameworkPlugin.log( e );
            }
            
            buf.append( "\n</style>" );
        
            STYLE = buf.toString();
        }
        
        return STYLE;
    }

    private static String normalize( final String text )
    {
        return normalize( text, "" );
    }
    
    private static String normalize( final String text,
                                     final String textForNull )
    {
        return ( text == null || text.trim().length() == 0 ? textForNull : text );
    }
    
    private static String td( final Value<?> value )
    {
        return td( value.getText() );
    }
    
    private static String td( final String value )
    {
        final StringBuilder buf = new StringBuilder();
        
        buf.append( "    <td>" );
        buf.append( normalize( value, "&nbsp;" ) );
        buf.append( "</td>" );
        
        return buf.toString();
    }

    private static int comp( final String x,
                             final String y )
    {
        if( x == y )
        {
            return 0;
        }
        else if( x == null )
        {
            return -1;
        }
        else if( y == null )
        {
            return 1;
        }
        else
        {
            return x.compareToIgnoreCase( y );
        }
    }

    private static String formatClassName( final Value<?> classNameValue )
    {
        return formatClassName( classNameValue.getText() );
    }

    private static String formatClassName( final String className )
    {
        final StringBuilder buf = new StringBuilder();
        final int lastDot = className.lastIndexOf( '.' );
        
        if( lastDot != -1 )
        {
            buf.append( "<font color=\"#888888\">" );
            buf.append( className.substring( 0, lastDot + 1 ) );
            buf.append( "</font>" );
        }
        
        final int lastDotPlusOne = lastDot + 1;
        
        if( lastDotPlusOne < className.length() )
        {
            buf.append( className.substring( lastDotPlusOne ) );
        }
        
        return buf.toString();
    }
    
}
