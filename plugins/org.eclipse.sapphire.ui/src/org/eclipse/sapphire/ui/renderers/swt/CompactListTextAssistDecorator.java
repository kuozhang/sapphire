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

package org.eclipse.sapphire.ui.renderers.swt;

import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.internal.PropertyEditorAssistDecorator;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

class CompactListTextAssistDecorator extends PropertyEditorAssistDecorator
{
    
    private CompactTextBinding binding;
    
    public CompactListTextAssistDecorator( final SapphirePropertyEditor propertyEditor,
                                           final SapphireRenderingContext context,
                                           final Composite parent )
    {
    	super(propertyEditor, context, parent);
    }
    
    public CompactTextBinding getBinding() {
		return this.binding;
	}

	public void setBinding(CompactTextBinding binding) {
		this.binding = binding;
	}
    
    public void refresh() {
    	if (this.binding != null && this.binding.getModelElement() == null) {
            this.assistContext = null;
            this.problem = null;
            refreshImageAndCursor();
    	} else {
    		super.refresh();
    	}
    }

}
