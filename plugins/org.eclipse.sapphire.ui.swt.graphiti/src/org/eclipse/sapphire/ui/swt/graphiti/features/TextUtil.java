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

package org.eclipse.sapphire.ui.swt.graphiti.features;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class TextUtil 
{
    private static final Class[] PARAM_M5 = new Class[] {GraphicsAlgorithmContainer.class, String.class};
    private static final Class[] PARAM_M6 = new Class[] {Diagram.class, GraphicsAlgorithmContainer.class, String.class};

    /**
     * A temporary util method to support both Graphiti Indigo M5 and Graphiti Indigo M6 build
     * @param diagram
     * @param gaContainer
     * @param value
     * @return
     */
    public static Text createDefaultText(Diagram diagram, GraphicsAlgorithmContainer gaContainer, String value)
    {
    	IGaService gaService = Graphiti.getGaService();
        Text text = null;
        Method createTextMethod = null;
    	try
    	{
    		createTextMethod = gaService.getClass().getMethod("createDefaultText", PARAM_M5);
    		Object[] args = new Object[]{gaContainer, value};
    		text = (Text)createTextMethod.invoke(gaService, args);            		
    	}
    	catch (NoSuchMethodException e)
    	{
    		try
    		{
        		createTextMethod = gaService.getClass().getMethod("createDefaultText", PARAM_M6);
        		Object[] args = new Object[]{diagram, gaContainer, value};
        		text = (Text)createTextMethod.invoke(gaService, args);
    		}
            catch (NoSuchMethodException e2) {}        		
            catch (IllegalAccessException ia) {}
            catch (InvocationTargetException ie) {}
    	}
        catch (IllegalAccessException ia) {}
        catch (InvocationTargetException ie) {}
    	
        return text;
    }
}
