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

package org.eclipse.sapphire.samples.map.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.samples.map.IDestination;
import org.eclipse.sapphire.samples.map.IMap;
import org.eclipse.sapphire.ui.DragAndDropService;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class MapDropService extends DragAndDropService 
{

	@Override
	public boolean droppable(DropContext context) 
	{
		return context.object() instanceof IFile;
	}

	@Override
	public Object drop(DropContext context) 
	{
		IFile ifile = (IFile)context.object();
        final List<String> cities = new ArrayList<String>();
        InputStream in = null;
        
        try
        {
            in = ifile.getContents();
            final BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
            
            for( String line = br.readLine(); line != null; line = br.readLine() )
            {
                if( line != null )
                {
                    line = line.trim();
                    
                    if( line.length() > 0 )
                    {
                        cities.add( line );
                    }
                }
            }
        }
        catch( CoreException e )
        {
            LoggingService.log( e );
        }
        catch( IOException e )
        {
            LoggingService.log( e );
        }
        finally
        {
            if( in != null )
            {
                try
                {
                    in.close();
                }
                catch( IOException e ) {}
            }
        }
        
        if( ! cities.isEmpty() )
        {
        	SapphireDiagramEditorPagePart diagramPart = context( SapphireDiagramEditorPagePart.class );
        	final IMap map = (IMap)diagramPart.getLocalModelElement();
            List<DiagramNodePart> cityParts = new ArrayList<DiagramNodePart>();
            int x = context.position().getX();
            int y = context.position().getY();
            for (String cityName : cities)
            {
                final IDestination city = map.getDestinations().insert();
                city.setName( cityName );
                DiagramNodePart cityPart = diagramPart.getDiagramNodePart(city);
                if (cityPart != null)
                {
                    cityPart.setNodeBounds(x, y);
                    cityParts.add(cityPart);
                    x += 50;
                    y += 50;
                }
            }
            return cityParts;
        }
        return null;
	}

}
