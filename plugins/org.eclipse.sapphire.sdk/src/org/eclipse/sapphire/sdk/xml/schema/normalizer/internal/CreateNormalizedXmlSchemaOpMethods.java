/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.sdk.xml.schema.normalizer.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.eclipse.sapphire.modeling.FileResourceStore;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.UrlResourceStore;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlComment;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.Exclusion;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.Exclusion.ExclusionType;
import org.eclipse.sapphire.sdk.xml.schema.normalizer.CreateNormalizedXmlSchemaOp.TypeSubstitution;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class CreateNormalizedXmlSchemaOpMethods
{
    private static final String NS_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    public static Status execute( final CreateNormalizedXmlSchemaOp operation,
                                  ProgressMonitor monitor )
    {
        if( monitor == null )
        {
            monitor = new ProgressMonitor();
        }
        
        monitor.beginTask( Resources.executeTaskName, 2 );
        
        try
        {
            final Path sourceSchemaFilePath = operation.getSourceFile().getContent();
            
            final PersistedState state = PersistedStateManager.load( sourceSchemaFilePath );
            
            if( state != null )
            {
                try
                {
                    state.copy( operation, CreateNormalizedXmlSchemaOp.PROP_ROOT_ELEMENTS );
                    state.copy( operation, CreateNormalizedXmlSchemaOp.PROP_EXCLUSIONS );
                    state.copy( operation, CreateNormalizedXmlSchemaOp.PROP_TYPE_SUBSTITUTIONS );
                    state.copy( operation, CreateNormalizedXmlSchemaOp.PROP_SORT_SEQUENCE_CONTENT );
                    state.resource().save();
                }
                catch( ResourceStoreException e )
                {
                    // Ignore, since persisted state isn't a critical aspect.
                }
                finally
                {
                    state.dispose();
                }
            }
            
            final IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
            final IFile sourceSchemaFile = wsroot.getFile( PathBridge.create( sourceSchemaFilePath ) );
            final IFile targetSchemaFile = operation.getFileHandle();
            
            try
            {
                final InputStream sourceSchemaFileInputStream = sourceSchemaFile.getContents();
                
                try
                {
                    targetSchemaFile.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
                    
                    create( targetSchemaFile.getParent() );
                    
                    if( targetSchemaFile.exists() )
                    {
                        targetSchemaFile.setContents( sourceSchemaFileInputStream, IFile.FORCE, null );
                    }
                    else
                    {
                        targetSchemaFile.create( sourceSchemaFileInputStream, IFile.FORCE, null );
                    }
                }
                finally
                {
                    try
                    {
                        sourceSchemaFileInputStream.close();
                    }
                    catch( IOException e ) {}
                }
            }
            catch( CoreException e )
            {
                return StatusBridge.create( e.getStatus() );
            }
            
            monitor.worked( 1 );
            
            try
            {
                final WorkspaceFileResourceStore fileResourceStore = new WorkspaceFileResourceStore( targetSchemaFile );
                final XmlResourceStore xmlResourceStore = new XmlResourceStore( fileResourceStore );
                final XmlElement root = new XmlElement( xmlResourceStore, xmlResourceStore.getDomDocument().getDocumentElement() );
                
                final String targetNamespace = root.getAttributeText( "targetNamespace" );
                String targetNamespacePrefix = null;
                
                for( XmlAttribute attribute : root.getAttributes() )
                {
                    final String name = attribute.getDomNode().getName();
                    final String value = attribute.getText();
                    
                    if( name.startsWith( "xmlns:" ) && value.equals( targetNamespace ) )
                    {
                        targetNamespacePrefix = name.substring( 6 );
                    }
                }
    
                final Set<String> included = new HashSet<String>();
                included.add( sourceSchemaFile.getLocation().toOSString() );
                while( inlineIncludes( root, sourceSchemaFile.getParent().getLocation().toOSString(), included ) );
                
                removeComments( root );
                removeAnnotations( root );
                removeDefaultMinMaxOccurs( root );
                
                exclude( root, operation.getExclusions() );
                
                final Map<String,XmlElement> types = new HashMap<String,XmlElement>();
                final Map<String,XmlElement> elements = new HashMap<String,XmlElement>();
                final Map<String,XmlElement> groups = new HashMap<String,XmlElement>();
                
                for( XmlElement element : root.getChildElements() )
                {
                    final String elname = element.getLocalName();
                    
                    if( elname.equals( "simpleType" ) || elname.equals( "complexType" ) )
                    {
                        String tname = element.getAttributeText( "name" );
                        
                        if( tname.length() > 0 )
                        {
                            if( targetNamespacePrefix != null )
                            {
                                tname = targetNamespacePrefix + ":" + tname;
                            }
                            
                            if( types.containsKey( tname ) )
                            {
                                element.remove();
                            }
                            else
                            {
                                types.put( tname, element );
                            }
                        }
                    }
                    else if( elname.equals( "element" ) )
                    {
                        String ename = element.getAttributeText( "name" );
                        
                        if( ename.length() > 0 )
                        {
                            if( targetNamespacePrefix != null )
                            {
                                ename = targetNamespacePrefix + ":" + ename;
                            }
                            
                            if( elements.containsKey( ename ) )
                            {
                                element.remove();
                            }
                            else
                            {
                                elements.put( ename, element );
                            }
                        }
                    }
                    else if( elname.equals( "group" ) )
                    {
                        String gname = element.getAttributeText( "name" );
                        
                        if( gname.length() > 0 )
                        {
                            if( targetNamespacePrefix != null )
                            {
                                gname = targetNamespacePrefix + ":" + gname;
                            }
                            
                            if( groups.containsKey( gname ) )
                            {
                                element.remove();
                            }
                            else
                            {
                                groups.put( gname, element );
                            }
                        }
                    }
                }
                
                changeSchemaNamespacePrefix( root.getDomNode().getOwnerDocument(), "xsd" );
                
                final Map<String,String> typeSubstitutions = new HashMap<String,String>();
                
                for( TypeSubstitution sub : operation.getTypeSubstitutions() )
                {
                    typeSubstitutions.put( sub.getBefore().getContent(), sub.getAfter().getContent() );
                }
                
                applyTypeSubstitutions( root, typeSubstitutions );
                
                boolean keepInlining = true;
                
                while( keepInlining )
                {
                    keepInlining =
                        inlineRestriction( root )
                        || inlineExtension( root )
                        || inlineSequenceInSequence( root )
                        || inlineSequenceInChoice( root )
                        || inlineTypes( root, types )
                        || inlineElements( root, elements )
                        || inlineGroups( root, groups )
                        || removeRedundantMinMaxOccursInChoice( root );
                }
                
                for( XmlElement element : root.getChildElements() )
                {
                    final String elname = element.getLocalName();
                    
                    if( elname.equals( "simpleType" ) || elname.equals( "complexType" ) || elname.equals( "group" ) )
                    {
                        element.remove();
                    }
                }
                
                // If root elements are specified, remove all other top-level elements.
                
                final Set<String> roots = new HashSet<String>();
                operation.read( new ModelPath( "RootElements/Name" ), roots );

                if( ! roots.isEmpty() )
                {
                    for( XmlElement element : root.getChildElements() )
                    {
                        final String elname = element.getLocalName();
                        
                        if( elname.equals( "element" ) && ! roots.contains( element.getAttributeText( "name" ) ) )
                        {
                            element.remove();
                        }
                    }
                }
                
                sortChoiceContent( root );
                sortElementContent( root );
                
                if( operation.getSortSequenceContent().getContent() )
                {
                    sortSequenceContent( root );
                }
                
                sort( root );
                
                root.format();
                
                xmlResourceStore.save();
            }
            catch( ResourceStoreException e )
            {
                return Status.createErrorStatus( e );
            }
        }
        finally
        {
            monitor.done();
        }
        
        return Status.createOkStatus();
    }
    
    private static void create( final IContainer container ) throws CoreException
    {
        if( ! container.exists() )
        {
            create( container.getParent() );
            
            final IFolder iFolder = (IFolder) container;
            iFolder.create( true, true, null );
        }
    }
    
    private static boolean inlineIncludes( final XmlElement root,
                                           final String context,
                                           final Set<String> included )
    {
        boolean changed = false;
        
        final Element rootDomNode = root.getDomNode();
        final Document document = rootDomNode.getOwnerDocument();
        
        for( XmlElement includeElement : root.getChildElements( "include" ) )
        {
            final Element includeElementDomNode = includeElement.getDomNode();
            String includedSchemaLocation = includeElement.getAttributeText( "schemaLocation" );
            
            if( ! includedSchemaLocation.startsWith( "http://" ) )
            {
                includedSchemaLocation = context + "/" + includedSchemaLocation;
            }

            try
            {
                ByteArrayResourceStore includedSchemaResourceStore;
                
                if( includedSchemaLocation.startsWith( "http://" ) )
                {
                    includedSchemaResourceStore = new UrlResourceStore( new URL( includedSchemaLocation ) );
                }
                else
                {
                    includedSchemaResourceStore = new FileResourceStore( new File( includedSchemaLocation ) );
                }
            
                final XmlResourceStore xmlResourceStore = new XmlResourceStore( includedSchemaResourceStore );
                final XmlElement includedSchemaRoot = new XmlElement( xmlResourceStore, xmlResourceStore.getDomDocument().getDocumentElement() );
    
                for( XmlElement includedSchemaElement : includedSchemaRoot.getChildElements() )
                {
                    final Element inlinedDomNode = (Element) document.importNode( includedSchemaElement.getDomNode(), true );
                    rootDomNode.insertBefore( inlinedDomNode, includeElementDomNode );
                }
                
                includeElement.remove();
                
                changed = true;
            }
            catch( MalformedURLException e )
            {
                e.printStackTrace();
            }
            catch( ResourceStoreException e )
            {
                e.printStackTrace();
            }
        }
        
        return changed;
    }
    
    private static void removeComments( final XmlElement element )
    {
        for( XmlComment comment : element.getComments() )
        {
            comment.remove();
        }
    }
    
    private static void removeAnnotations( final XmlElement element )
    {
        for( XmlElement x : element.getChildElements( "annotation" ) )
        {
            x.remove();
        }
        
        for( XmlElement x : element.getChildElements() )
        {
            removeAnnotations( x );
        }
    }
    
    private static void removeDefaultMinMaxOccurs( final XmlElement element )
    {
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getAttributeText( "minOccurs" ).equals( "1" ) )
            {
                x.setAttributeText( "minOccurs", null, true );
            }
            
            if( x.getAttributeText( "maxOccurs" ).equals( "1" ) )
            {
                x.setAttributeText( "maxOccurs", null, true );
            }
            
            removeDefaultMinMaxOccurs( x );
        }
    }
    
    private static void exclude( final XmlElement element,
                                 final List<Exclusion> exclusions )
    {
        for( Exclusion exclusion : exclusions )
        {
            exclude( element, exclusion );
        }
    }

    private static void exclude( final XmlElement element,
                                 final Exclusion exclusion )
    {
        if( exclusion.getType().getContent() == ExclusionType.ATTRIBUTE )
        {
            excludeAttributes( element, exclusion.getName().getContent() );
        }
        else
        {
            excludeElements( element, exclusion.getName().getContent() );
        }
    }

    private static void excludeAttributes( final XmlElement element,
                                           final String name )
    {
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "attribute" ) && ( name.equals( x.getAttributeText( "name" ) ) || name.equals( x.getAttributeText( "ref" ) ) ) )
            {
                x.remove();
            }
            else
            {
                excludeAttributes( x, name );
            }
        }
    }
    
    private static void excludeElements( final XmlElement element,
                                         final String name )
    {
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "element" ) && ( name.equals( x.getAttributeText( "name" ) ) || name.equals( x.getAttributeText( "ref" ) ) ) )
            {
                x.remove();
            }
            else
            {
                excludeElements( x, name );
            }
        }
    }
    
    /**
     * Before:
     * 
     * <xsd:element name="servlet-class">
     *     <xsd:complexType>
     *         <xsd:simpleContent>
     *             <xsd:restriction base="xsd:string"/>
     *         </xsd:simpleContent>
     *     </xsd:complexType>
     * </xsd:element>
     * 
     * After:
     * 
     * <xsd:element name="servlet-class" type="xsd:string"/>
     */
    
    private static boolean inlineRestriction( final XmlElement element )
    {
        boolean changed = false;
        
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "element" ) )
            {
                final XmlElement complexTypeElement = getSoleChildElement( x, "complexType" );
                final XmlElement simpleContentElement = getSoleChildElement( complexTypeElement, "simpleContent" );
                final XmlElement restrictionElement = getSoleChildElement( simpleContentElement, "restriction" );
                
                if( restrictionElement != null && restrictionElement.getChildElements().isEmpty() )
                {
                    final String base = restrictionElement.getAttributeText( "base" );
                    
                    if( base.length() > 0 )
                    {
                        x.setAttributeText( "type", base, false );
                        complexTypeElement.remove();
                        
                        changed = true;
                    }
                }
            }
            
            if( inlineRestriction( x ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    /**
     * Before:
     * 
     * <xsd:element name="servlet-class">
     *     <xsd:complexType>
     *         <xsd:simpleContent>
     *             <xsd:extension base="xsd:string"/>
     *         </xsd:simpleContent>
     *     </xsd:complexType>
     * </xsd:element>
     * 
     * After:
     * 
     * <xsd:element name="servlet-class" type="xsd:string"/>
     */
    
    private static boolean inlineExtension( final XmlElement element )
    {
        boolean changed = false;
        
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "element" ) )
            {
                final XmlElement complexTypeElement = getSoleChildElement( x, "complexType" );
                final XmlElement simpleContentElement = getSoleChildElement( complexTypeElement, "simpleContent" );
                final XmlElement extensionElement = getSoleChildElement( simpleContentElement, "extension" );
                
                if( extensionElement != null && extensionElement.getChildElements().isEmpty() )
                {
                    final String base = extensionElement.getAttributeText( "base" );
                    
                    if( base.length() > 0 )
                    {
                        x.setAttributeText( "type", base, false );
                        complexTypeElement.remove();
                        
                        changed = true;
                    }
                }
            }
            
            if( inlineExtension( x ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }

    /**
     * Before:
     * 
     * <xsd:sequence ... >
     *     <xsd:sequence>
     *         ...
     *     </xsd:sequence>
     * </xsd:sequence>
     * 
     * After:
     * 
     * <xsd:sequence ... >
     *     ...
     * </xsd:sequence>
     */
    
    private static boolean inlineSequenceInSequence( final XmlElement element )
    {
        boolean changed = false;
        
        if( element.getLocalName().equals( "sequence" ) )
        {
            final Element elementDomNode = element.getDomNode();
            
            for( XmlElement child : element.getChildElements() )
            {
                final Element childDomNode = child.getDomNode();
                
                if( child.getLocalName().equals( "sequence" ) && child.getAttributes().size() == 0 )
                {
                    for( XmlElement grandchild : child.getChildElements() )
                    {
                        elementDomNode.insertBefore( grandchild.getDomNode(), childDomNode );
                    }
                    
                    child.remove();
                    
                    changed = true;
                }
            }
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            if( inlineSequenceInSequence( child ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }

    /**
     * Before:
     * 
     * <xsd:choice maxOccurs="unbounded" minOccurs="0">
     *     <xsd:sequence>
     *         <xsd:element maxOccurs="unbounded" minOccurs="0" name="a"/>
     *         <xsd:element maxOccurs="unbounded" minOccurs="0" name="b"/>
     *         <xsd:element maxOccurs="unbounded" minOccurs="0" name="c"/>
     *     </xsd:sequence>
     * </xsd:choice>
     * 
     * After:
     * 
     * <xsd:choice maxOccurs="unbounded" minOccurs="0">
     *     <xsd:element name="a"/>
     *     <xsd:element name="b"/>
     *     <xsd:element name="c"/>
     * </xsd:choice>
     */
    
    private static boolean inlineSequenceInChoice( final XmlElement element )
    {
        boolean changed = false;
        
        if( element.getLocalName().equals( "choice" ) && element.getAttributeText( "maxOccurs" ).equals( "unbounded" ) && element.getAttributeText( "minOccurs" ).equals( "0" ) )
        {
            final Element elementDomNode = element.getDomNode();
            
            for( XmlElement child : element.getChildElements() )
            {
                final Element childDomNode = child.getDomNode();
                
                if( child.getLocalName().equals( "sequence" ) && child.getAttributes().size() == 0 )
                {
                    boolean doit = false;
                    
                    for( XmlElement grandchild : child.getChildElements() )
                    {
                        if( grandchild.getAttributeText( "maxOccurs" ).equals( "unbounded" ) && grandchild.getAttributeText( "minOccurs" ).equals( "0" ) )
                        {
                            doit = true;
                            break;
                        }
                    }
                    
                    if( doit )
                    {
                        for( XmlElement grandchild : child.getChildElements() )
                        {
                            grandchild.setAttributeText( "maxOccurs", null, true );
                            grandchild.setAttributeText( "minOccurs", null, true );
                            elementDomNode.insertBefore( grandchild.getDomNode(), childDomNode );
                        }
                        
                        child.remove();
                        
                        changed = true;
                    }
                }
            }
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            if( inlineSequenceInChoice( child ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    private static boolean inlineTypes( final XmlElement element,
                                        final Map<String,XmlElement> types )
    {
        boolean changed = false;
        
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "element" ) )
            {
                final String tname = x.getAttributeText( "type" );
                
                if( tname.length() > 0 )
                {
                    final XmlElement type = types.get( tname );
                    
                    if( type != null )
                    {
                        final Element xdom = x.getDomNode();
                        final Element tdom = (Element) xdom.getOwnerDocument().importNode( type.getDomNode(), true );
                        
                        x.setAttributeText( "type", null, true );
                        tdom.removeAttribute( "name" );
                        xdom.insertBefore( tdom, null );
                        
                        changed = true;
                    }
                }
            }
            
            if( inlineTypes( x, types ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    private static boolean inlineElements( final XmlElement element,
                                           final Map<String,XmlElement> elements )
    {
        boolean changed = false;

        final Element elementDomNode = element.getDomNode();
        final Document document = elementDomNode.getOwnerDocument();
        
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "element" ) )
            {
                final String ename = x.getAttributeText( "ref" );
                
                if( ename.length() > 0 )
                {
                    final XmlElement reftarget = elements.get( ename );
                    
                    if( reftarget != null )
                    {
                        final Element xdom = x.getDomNode();
                        
                        for( XmlElement refTargetContentElement : reftarget.getChildElements() )
                        {
                            final Element idom = (Element) document.importNode( refTargetContentElement.getDomNode(), true );
                            xdom.insertBefore( idom, null );
                        }
                        
                        x.setAttributeText( "ref", null, true );
                        x.setAttributeText( "name", reftarget.getAttributeText( "name" ), false );
                        x.setAttributeText( "type", reftarget.getAttributeText( "type" ), false );
                        
                        changed = true;
                    }
                }
            }
            
            if( inlineElements( x, elements ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }

    private static boolean inlineGroups( final XmlElement element,
                                         final Map<String,XmlElement> groups )
    {
        boolean changed = false;

        final Element elementDomNode = element.getDomNode();
        final Document document = elementDomNode.getOwnerDocument();
        
        for( XmlElement x : element.getChildElements() )
        {
            if( x.getLocalName().equals( "group" ) )
            {
                final String gname = x.getAttributeText( "ref" );
                
                if( gname.length() > 0 )
                {
                    final XmlElement group = groups.get( gname );
                    
                    if( group != null )
                    {
                        final Element xdom = x.getDomNode();
                        
                        for( XmlElement groupContentElement : group.getChildElements() )
                        {
                            final Element gdom = (Element) document.importNode( groupContentElement.getDomNode(), true );
                            elementDomNode.insertBefore( gdom, xdom );
                        }
                        
                        elementDomNode.removeChild( xdom );
                        
                        changed = true;
                    }
                }
            }
            
            if( inlineGroups( x, groups ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    /**
     * Before:
     * 
     * <xsd:choice maxOccurs="unbounded" minOccurs="0">
     *     <xsd:element maxOccurs="unbounded" minOccurs="0" name="a"/>
     *     <xsd:element minOccurs="0" name="b"/>
     * </xsd:choice>
     * 
     * After:
     * 
     * <xsd:choice maxOccurs="unbounded" minOccurs="0">
     *     <xsd:element name="a"/>
     *     <xsd:element name="b"/>
     * </xsd:choice>
     */
    
    private static boolean removeRedundantMinMaxOccursInChoice( final XmlElement element )
    {
        boolean changed = false;
        
        if( element.getLocalName().equals( "choice" ) && element.getAttributeText( "maxOccurs" ).equals( "unbounded" ) && element.getAttributeText( "minOccurs" ).equals( "0" ) )
        {
            for( XmlElement child : element.getChildElements() )
            {
                if( child.getAttributeText( "maxOccurs" ).equals( "unbounded" ) )
                {
                    child.setAttributeText( "maxOccurs", null, true );
                    changed = true;
                }
                
                if( child.getAttributeText( "minOccurs" ).equals( "0" ) )
                {
                    child.setAttributeText( "minOccurs", null, true );
                    changed = true;
                }
            }
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            if( removeRedundantMinMaxOccursInChoice( child ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    private static boolean applyTypeSubstitutions( final XmlElement element,
                                                   final Map<String,String> typeSubstitutions )
    {
        boolean changed = false;
        
        String before = element.getAttributeText( "type" );
        
        if( before.length() > 0 )
        {
            final String after = typeSubstitutions.get( before );
            
            if( after != null )
            {
                element.setAttributeText( "type", after, false );
                changed = true;
            }
        }
        
        before = element.getAttributeText( "base" );
        
        if( before.length() > 0 )
        {
            final String after = typeSubstitutions.get( before );
            
            if( after != null )
            {
                element.setAttributeText( "base", after, false );
                changed = true;
            }
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            if( applyTypeSubstitutions( child, typeSubstitutions ) )
            {
                changed = true;
            }
        }
        
        return changed;
    }
    
    private static void sortChoiceContent( final XmlElement element )
    {
        if( element.getLocalName().equals( "choice" ) )
        {
            sort( element, new SchemaContentComparator() );
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            sortChoiceContent( child );
        }
    }

    private static void sortSequenceContent( final XmlElement element )
    {
        if( element.getLocalName().equals( "sequence" ) )
        {
            sort( element, new SchemaContentComparator() );
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            sortSequenceContent( child );
        }
    }
    
    private static void sortElementContent( final XmlElement element )
    {
        if( element.getLocalName().equals( "element" ) )
        {
            sort( element, new ElementChildrenComparator() );
        }
        
        for( XmlElement child : element.getChildElements() )
        {
            sortElementContent( child );
        }
    }
    
    private static void sort( final XmlElement element,
                              final Comparator<XmlElement> comparator )
    {
        final List<XmlElement> children = new ArrayList<XmlElement>();
        
        for( XmlElement child : element.getChildElements() )
        {
            children.add( child );
            child.remove();
        }
        
        Collections.sort( children, comparator );
        
        final Element rootDomElement = element.getDomNode();
        
        for( XmlElement child : children )
        {
            rootDomElement.insertBefore( child.getDomNode(), null );
        }
    }
    
    private static void sort( final XmlElement root )
    {
        final List<XmlElement> elements = new ArrayList<XmlElement>();
        
        for( XmlElement x : root.getChildElements() )
        {
            elements.add( x );
            x.remove();
        }
        
        Collections.sort( elements, new SchemaContentComparator() );
        
        final Element rootDomElement = root.getDomNode();
        
        for( XmlElement x : elements )
        {
            rootDomElement.insertBefore( x.getDomNode(), null );
        }
    }
    
    private static void changeSchemaNamespacePrefix( final Document document,
                                                     final String prefix )
    {
        final Element root = document.getDocumentElement();
        
        if( root != null )
        {
            final String newPrefix = ( prefix.equals( "" ) ? null : prefix );
            final NamedNodeMap attributes = root.getAttributes();
            boolean doit = false;
            String oldPrefix = null;
            
            for( int i = 0, count = attributes.getLength(); i < count; i++ )
            {
                final Attr attribute = (Attr) attributes.item( i );
                final String name = attribute.getName();
                final String value = attribute.getValue();
                
                if( name.equals( "xmlns" ) && value.equals( NS_SCHEMA ) )
                {
                    if( newPrefix != null )
                    {
                        root.removeAttributeNode( attribute );
                        doit = true;
                    }
                    
                    break;
                }
                else if( name.startsWith( "xmlns:" ) && value.equals( NS_SCHEMA ) )
                {
                    oldPrefix = name.substring( 6 );
                    
                    if( ! oldPrefix.equals( newPrefix ) )
                    {
                        root.removeAttributeNode( attribute );
                        doit = true;
                    }
                    
                    break;
                }
            }
            
            if( doit )
            {
                if( newPrefix == null )
                {
                    root.setAttribute( "xmlns", NS_SCHEMA );
                }
                else
                {
                    root.setAttribute( "xmlns:" + newPrefix, NS_SCHEMA );
                }
            }
            
            changeSchemaNamespacePrefix( root, oldPrefix, newPrefix );
        }
    }
    
    private static void changeSchemaNamespacePrefix( final Node node,
                                                     final String oldPrefix,
                                                     final String newPrefix )
    {
        if( node instanceof Element )
        {
            final Element element = (Element) node;
            
            if( NS_SCHEMA.equals( element.getNamespaceURI() ) )
            {
                final NodeList children = element.getChildNodes();
                
                for( int i = 0, n = children.getLength(); i < n; i++ )
                {
                    changeSchemaNamespacePrefix( children.item( i ), oldPrefix, newPrefix );
                }
                
                element.getOwnerDocument().renameNode( element, NS_SCHEMA, ( newPrefix == null ? "" : newPrefix + ":" ) + element.getLocalName() );
                
                String type = element.getAttribute( "type" );
                
                if( type.length() > 0 )
                {
                    final int colon = type.indexOf( ':' );
                    final String typePrefix = ( colon == -1 ? null : type.substring( 0, colon ) );
                    
                    if( equal( typePrefix, oldPrefix ) )
                    {
                        final String typeWithoutPrefix = ( colon == -1 ? type : type.substring( colon + 1 ) );
                        element.setAttribute( "type", ( newPrefix == null ? "" : newPrefix + ":" ) + typeWithoutPrefix );
                    }
                }
            }
        }
    }
    
    private static XmlElement getSoleChildElement( final XmlElement element,
                                                   final String name )
    {
        if( element != null )
        {
            final List<XmlElement> children = element.getChildElements();
            
            if( children.size() == 1 )
            {
                final XmlElement child = children.get( 0 );
                
                if( child.getLocalName().equals( name ) )
                {
                    return child;
                }
            }
        }
        
        return null;
    }

    private static final class SchemaContentComparator implements Comparator<XmlElement>
    {
        public int compare( final XmlElement x, final XmlElement y )
        {
            int result = x.getLocalName().compareTo( y.getLocalName() );
            
            if( result == 0 )
            {
                result = x.getAttributeText( "name" ).compareTo( y.getAttributeText( "name" ) );
            }
            
            return result;
        }
    }
    
    private static final class ElementChildrenComparator implements Comparator<XmlElement>
    {
        public int compare( final XmlElement x, final XmlElement y )
        {
            int result = bucket( x ) - bucket( y );
            
            if( result == 0 )
            {
                result = x.getLocalName().compareTo( y.getLocalName() );
                
                if( result == 0 )
                {
                    result = x.getAttributeText( "name" ).compareTo( y.getAttributeText( "name" ) );
                }
            }
            
            return result;
        }
        
        private int bucket( final XmlElement element )
        {
            final String name = element.getLocalName();
            
            if( name.equals( "simpleType" ) || name.equals( "complexType" ) )
            {
                return 1;
            }
            else if( name.equals( "unique" ) || name.equals( "key" ) || name.equals( "keyref" ) )
            {
                return 2;
            }
            else
            {
                return 3;
            }
        }
    }
    
    private static final class Resources extends NLS
    {
        public static String executeTaskName;
        
        static
        {
            initializeMessages( CreateNormalizedXmlSchemaOpMethods.class.getName(), Resources.class );
        }
    }
    
}
