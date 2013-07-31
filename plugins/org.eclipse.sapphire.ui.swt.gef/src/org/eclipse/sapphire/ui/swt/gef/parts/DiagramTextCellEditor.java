/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.parts;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.sapphire.ui.diagram.editor.TextPart;
import org.eclipse.sapphire.ui.swt.gef.internal.TraverseUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramTextCellEditor extends TextCellEditor 
{
	private TextPart textPart;
	
	public DiagramTextCellEditor(TextPart textPart, Composite parent, int style) 
	{
		super(parent, style);
		this.textPart = textPart;		
	}
	
    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected Control createControl(Composite parent) 
    {
    	Text text = (Text)super.createControl(parent);
    	text.addTraverseListener(new TraverseListener()
    	{
    		@Override
    		public void keyTraversed(TraverseEvent e) 
    		{
    	        if (e.detail == SWT.TRAVERSE_TAB_NEXT) 
    	        {
    	        	TraverseUtil.gotoNextTextPart(textPart);
    	        }
    		}
    		
    	});
    	return text;
    }
}
