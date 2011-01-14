/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.geometry.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class GeometryAttributeBinding extends XmlValueBindingImpl
{
	private String modelPath = null;
	private String attrName = null;
	
	@Override
    public void init( final IModelElement element,
            final ModelProperty property,
            final String[] params )
	{
		super.init(element, property, params);
		if (params != null)
		{
			if (params.length > 1)
			{
				this.modelPath = params[0];
				this.attrName = params[1];
			}
			else if (params.length > 0) 
			{
				this.attrName = params[0];
			}
		}
	}

	@Override
	public String read() 
	{
		String attrVal = null;
		if (this.attrName != null)
		{
			XmlElement geometryEl = xml(false);
			if (geometryEl != null)
			{
				if (this.modelPath != null)
				{
					geometryEl = geometryEl.getChildElement(this.modelPath, false);
				}
				if (geometryEl != null)
				{
					attrVal = ( geometryEl.getAttributeText(this.attrName) );
				}
			}
		}
		return attrVal;
	}

	@Override
	public void write(String value) 
	{
		if (this.attrName != null)
		{
			XmlElement geometryEl = xml(true);
			if (this.modelPath != null)
			{
				geometryEl = geometryEl.getChildElement(this.modelPath, true);
			}
			geometryEl.setAttributeText( this.attrName, value, true );
		}		
	}
}
