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

package org.eclipse.sapphire.ui.def;

import org.eclipse.sapphire.modeling.BundleResourceStore;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.SharedModelsCache;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.diagram.def.IDiagramPageDef;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class SapphireDiagramDefFactory 
{
	public static IDiagramPageDef load(final String bundleId, final String path)
	{
        try
        {
            return load( new XmlResourceStore( new BundleResourceStore( bundleId, path ) ), false );
        }
        catch( ResourceStoreException e )
        {
            SapphireUiFrameworkPlugin.log( e );
            return null;
        }		
	}
	
	public static IDiagramPageDef load( final XmlResourceStore resourceStore,
            							final boolean writable )
	{
		IDiagramPageDef model;

		if( writable )
		{
			model = IDiagramPageDef.TYPE.instantiate( new RootXmlResource( resourceStore ) );
		}
		else
		{
			final SharedModelsCache.StandardKey key = new SharedModelsCache.StandardKey( resourceStore, IDiagramPageDef.TYPE );
			
			model = (IDiagramPageDef) SharedModelsCache.retrieve( key );
			
			if( model == null )
			{
				model = IDiagramPageDef.TYPE.instantiate( new RootXmlResource( resourceStore ) );
				SharedModelsCache.store( key, model );			
			}	
		}
		return model;
	}

}

