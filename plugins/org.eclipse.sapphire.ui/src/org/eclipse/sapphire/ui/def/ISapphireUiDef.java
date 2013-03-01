/******************************************************************************
 * Copyright (c) 2013 Oracle and Accenture
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 *    Ling Hao - [329114] rewrite context help binding feature
 *    Shenxue Zhou - [330482] support diagram editing in Sapphire UI
 *    Kamesh Sampath - [355751] General improvement of XML root binding API
 ******************************************************************************/

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.internal.SapphireUiDefMethods;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEditorPageDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsContentNodeFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.def.MasterDetailsEditorPageDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a> 
 */

@XmlBinding( path = "definition" )

public interface ISapphireUiDef extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( ISapphireUiDef.class );
    
    // *** ImportedPackages ***
    
    @Type( base = IPackageReference.class )
    @Label( standard = "imported packages" )
    @XmlListBinding( path = "import", mappings = @XmlListBinding.Mapping( element = "package", type = IPackageReference.class ) )
    
    ListProperty PROP_IMPORTED_PACKAGES = new ListProperty( TYPE, "ImportedPackages" );
    
    ModelElementList<IPackageReference> getImportedPackages();
    
    // *** ImportedDefinitions ***
    
    @Type( base = IDefinitionReference.class )
    @Label( standard = "imported definitions" )
    @XmlListBinding( path = "import", mappings = @XmlListBinding.Mapping( element = "definition", type = IDefinitionReference.class ) )
    
    ListProperty PROP_IMPORTED_DEFINITIONS = new ListProperty( TYPE, "ImportedDefinitions" );
    
    ModelElementList<IDefinitionReference> getImportedDefinitions();
    
    // *** DocumentationDefs ***
    
    @Type( base = ISapphireDocumentationDef.class )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "documentation", type = ISapphireDocumentationDef.class ) )
                             
    ListProperty PROP_DOCUMENTATION_DEFS = new ListProperty( TYPE, "DocumentationDefs" );
    
    ModelElementList<ISapphireDocumentationDef> getDocumentationDefs();
    
    // *** Method : getDocumentationDef ***
    
    @DelegateImplementation( SapphireUiDefMethods.class )
    
    ISapphireDocumentationDef getDocumentationDef( String id,
                                                   boolean searchImportedDefinitions );

    // *** PartDefs ***
    
    @Type
    ( 
        base = PartDef.class,
        possible = 
        { 
            PropertyEditorDef.class, 
            LineSeparatorDef.class,
            WhitespaceSeparatorDef.class,
            ISapphireLabelDef.class,
            ISapphireGroupDef.class,
            WithDef.class,
            ConditionalDef.class,
            CompositeDef.class,
            ActuatorDef.class,
            ISapphireCustomPartDef.class,
            ISapphireStaticTextFieldDef.class,
            PageBookExtDef.class,
            TabGroupDef.class,
            HtmlPanelDef.class,
            FormDef.class,
            SplitFormDef.class,
            MasterDetailsContentNodeDef.class,
            MasterDetailsContentNodeFactoryDef.class,
            MasterDetailsEditorPageDef.class,
            IDiagramEditorPageDef.class,
            DialogDef.class,
            WizardDef.class,
            FormEditorPageDef.class,
            SectionDef.class
        }
    )
                      
    @XmlListBinding( path = "" )
                             
    ListProperty PROP_PART_DEFS = new ListProperty( TYPE, "PartDefs" );
    
    ModelElementList<PartDef> getPartDefs();
    
    // *** Method : getPartDef ***
    
    @DelegateImplementation( SapphireUiDefMethods.class )
    
    PartDef getPartDef( String id,
                                 boolean searchImportedDefinitions,
                                 Class<?> expectedType );
    
    // *** Method : resolveClass ***
    
    @DelegateImplementation( SapphireUiDefMethods.class )
    
    Class<?> resolveClass( String className );
    
    // *** Method : resolveProperty ***
    
    @DelegateImplementation( SapphireUiDefMethods.class )
    
    ModelProperty resolveProperty( String qualifiedPropertyName );
    
}
