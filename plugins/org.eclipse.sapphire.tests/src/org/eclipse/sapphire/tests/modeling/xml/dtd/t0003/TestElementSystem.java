/******************************************************************************
 *  Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation
 ******************************************************************************/

package org.eclipse.sapphire.tests.modeling.xml.dtd.t0003;

import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlDocumentType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@XmlRootBinding( elementName = "root" )
@XmlDocumentType( systemId = "TestXmlDtd0003.dtd" )
@GenerateImpl

public interface TestElementSystem extends TestElement
{
    ModelElementType TYPE = new ModelElementType( TestElementSystem.class );
    
}
