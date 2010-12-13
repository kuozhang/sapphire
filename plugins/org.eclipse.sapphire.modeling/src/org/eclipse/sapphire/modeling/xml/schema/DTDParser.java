/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.xml.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.dtd.core.internal.contentmodel.CMNodeImpl;
import org.eclipse.wst.dtd.core.internal.emf.DTDElement;
import org.eclipse.wst.dtd.core.internal.emf.DTDElementReferenceContent;
import org.eclipse.wst.dtd.core.internal.emf.DTDOccurrenceType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.ContentModelManager;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DTDParser {
	
	public final static void parse(final String schemaLocation, final XmlDocumentSchema schema, 
			Map<String,XmlContentModel> contentModels, Map<String,XmlElementDefinition> topLevelElements) {
        CMDocument doc = ContentModelManager.getInstance().createCMDocument( schemaLocation, "dtd");
        CMNamedNodeMap elements = doc.getElements();
        Iterator iter = elements.iterator();
        while (iter.hasNext()) {
        	Object obj = iter.next();
        	if (obj instanceof CMNodeImpl) {
        		Object key = ((CMNodeImpl)obj).getKey();
        		if (key instanceof DTDElement) {
        			DTDElement element = (DTDElement)key;
                    final XmlContentModel contentModel = parseElement(schema, element);
                    if (contentModel != null) {
                        contentModels.put(element.getName(), contentModel );
                    }
                    // No info about top level element, so add all
            		QName qname = new QName(element.getName());
                    final XmlElementDefinition elem = new XmlElementDefinition(schema, qname, qname, 1, 1);
                    topLevelElements.put(element.getName(), elem);
        		}
        	}
        }

	}
	
	private final static XmlContentModel parseElement(final XmlDocumentSchema schema, final DTDElement element) {
        final List<XmlContentModel> nestedContentModels = new ArrayList<XmlContentModel>();
        if (element.getContent() instanceof DTDElementReferenceContent) {
			XmlElementDefinition def = createXmlElementDefinition(schema, (DTDElementReferenceContent)element.getContent());
			if (def != null) {
				nestedContentModels.add(def);
			}
        } else {
	        for (Iterator<EObject> i = element.getContent().eAllContents(); i.hasNext();) {
				EObject childEObject = i.next();
				if (childEObject instanceof DTDElementReferenceContent) {
					DTDElementReferenceContent referenceContent = ((DTDElementReferenceContent) childEObject);
					XmlElementDefinition def = createXmlElementDefinition(schema, referenceContent);
					if (def != null) {
						nestedContentModels.add(def);
					}
				}
			}
        }
        
        if (nestedContentModels.size() > 0) {
            return new XmlSequenceGroup(schema, 1, 1, nestedContentModels);
        }
        
        return null;
	}
	
	private final static XmlElementDefinition createXmlElementDefinition(final XmlDocumentSchema schema, final DTDElementReferenceContent referenceContent) {
		final String name = referenceContent.getContentName();
		DTDOccurrenceType occurrence = referenceContent.getOccurrence();
		int minOccur = 1;
		int maxOccur = 1;
		switch (occurrence.getValue()) {
		case DTDOccurrenceType.ONE:
			minOccur = 1;
			maxOccur = 1;
			break;
		case DTDOccurrenceType.OPTIONAL:
			minOccur = 0;
			maxOccur = 1;
			break;
		case DTDOccurrenceType.ONE_OR_MORE:
			minOccur = 1;
			maxOccur = Integer.MAX_VALUE;
			break;
		case DTDOccurrenceType.ZERO_OR_MORE:
			minOccur = 0;
			maxOccur = Integer.MAX_VALUE;
			break;
		}
		QName qname = new QName(name);
		return new XmlElementDefinition(schema, qname, qname, minOccur, maxOccur);
	}
}