/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.IModelParticle;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ModelUtil 
{
    public static ModelProperty resolve(final IModelElement modelElement, 
            String propertyName)
    {
        if (propertyName != null)
        {
            final ModelElementType type = modelElement.type();
            final ModelProperty property = type.property( propertyName );
            if( property == null )
            {
                throw new RuntimeException( "Could not find property " + propertyName + " in " + type.getQualifiedName() );
            }
            return property;
        }    
        return null;
    }

    public static ModelProperty resolve(ModelElementType modelElementType, ModelPath path)
    {
        if (path.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)path.head()).getPropertyName();
            ModelProperty modelProperty = modelElementType.property(propertyName);
            return modelProperty;
        }
        else
        {
            ModelPath.Segment head = path.head();
            if (head instanceof ModelPath.PropertySegment)
            {
                final String propertyName = ((ModelPath.PropertySegment)head).getPropertyName();
                final ModelProperty property = modelElementType.property(propertyName);
                if (property instanceof ListProperty)
                {
                    ModelElementType type = ((ListProperty)property).getType();
                    return resolve(type, path.tail());
                }
                else
                {
                    throw new RuntimeException("Invalid model path <" + path + "> in ModelElementType " + modelElementType.getSimpleName());
                }
            }
            else 
            {
                throw new RuntimeException("Invalid model path <" + path + "> in ModelElementType " + modelElementType.getSimpleName());
            }
        }
    }

    public static ModelProperty resolve(IModelElement modelElement, ModelPath path)
    {
        if (path.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)path.head()).getPropertyName();            
            return resolve(modelElement, propertyName);
        }
        else
        {
            ModelPath.Segment head = path.head();
            if (head instanceof ModelPath.PropertySegment)
            {
                final String propertyName = ((ModelPath.PropertySegment)head).getPropertyName();
                final ModelProperty property = modelElement.type().property(propertyName);
                if (property instanceof ListProperty)
                {
                    ModelElementType type = ((ListProperty)property).getType();
                    return resolve(type, path.tail());
                }
                else
                {
                    throw new RuntimeException("Invalid model path <" + path + "> in model element " + modelElement);
                }
            }
            else if (head instanceof ModelPath.ParentElementSegment)
            {
                IModelParticle parent = modelElement.parent();
                if (parent instanceof ModelElementList<?>)
                {
                    parent = parent.parent();
                }
                return resolve((IModelElement)parent, path.tail());
            }
            else 
            {
                throw new RuntimeException("Invalid model path <" + path + "> in model element " + modelElement);
            }
        }
    }
}
