/******************************************************************************
 * Copyright (c) 2014 Oracle and Other Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 *    Gregory Amerson - [363258] Allow dynamic model types for SapphireEditorForXml
 *                      [444202] lazy loading of editor pages
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.xml.editor;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.sapphire.Context;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.osgi.BundleBasedContext;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public class SapphireEditorForXml extends SapphireEditor implements IExecutableExtension
{
    @Text( "Source" )
    private static LocalizableText sourcePageTitle;
    
    static 
    {
        LocalizableText.init( SapphireEditorForXml.class );
    }

    private ElementType type;
    private DefinitionLoader.Reference<EditorPageDef> definition;
    private StructuredTextEditor sourcePage;
    private Context context;
    private String sdef;
    private String formPageName;

    public SapphireEditorForXml( final ElementType type,
                                 final DefinitionLoader.Reference<EditorPageDef> definition )
    {
        super();

        if( type == null )
        {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.definition = definition;
    }

    public SapphireEditorForXml( final ElementType type )
    {
        this( type, null );
    }

    public SapphireEditorForXml()
    {
    }
    
    @Override
    public void setInitializationData( final IConfigurationElement config,
                                       final String propertyName,
                                       final Object data )
    {
        super.setInitializationData( config, propertyName, data );
        
        if( this.definition == null )
        {
            final String bundleId = config.getContributor().getName();
            this.context = BundleBasedContext.adapt( bundleId );
            final Map<?,?> properties = (Map<?,?>) data;
            this.sdef = (String) properties.get( "sdef" );
            this.formPageName = (String) properties.get( "formPageName" );
        }
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String pageName )
    {
        if( this.formPageName.equals( pageName ) )
        {
            if( this.definition == null && sdef != null )
            {
                this.definition = DefinitionLoader.context( this.context ).sdef( sdef ).page();

                final JavaType elementJavaType = this.definition.resolve().getElementType().target();
                this.type = ElementType.read( (Class<?>) elementJavaType.artifact(), true );
            }

            return this.definition;
        }

        return null;
    }

    public final StructuredTextEditor getXmlEditor()
    {
        return this.sourcePage;
    }
    
    protected Element createModel()
    {
        final XmlEditorResourceStore store = createResourceStore( this.sourcePage );
        return this.type.instantiate( new RootXmlResource( store ) );
    }
    
    protected XmlEditorResourceStore createResourceStore( final StructuredTextEditor sourceEditor )
    {
        return new XmlEditorResourceStore( this, this.sourcePage );
    }
    
    @Override
    protected final void createSourcePages() throws PartInitException
    {
        this.sourcePage = new StructuredTextEditor();
        this.sourcePage.setEditorPart( this );
        
        final int index = addPage( this.sourcePage, getEditorInput() );
        setPageText( index, sourcePageTitle.text() );
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        addInitialPage( 0, this.formPageName );
    }

    @Override
    public IContentOutlinePage getContentOutline( final Object page )
    {
        if( page == this.sourcePage )
        {
            return (IContentOutlinePage) this.sourcePage.getAdapter( IContentOutlinePage.class );
        }
        
        return super.getContentOutline( page );
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        this.type = null;
        this.definition = null;
        this.sourcePage = null;
    }

}
