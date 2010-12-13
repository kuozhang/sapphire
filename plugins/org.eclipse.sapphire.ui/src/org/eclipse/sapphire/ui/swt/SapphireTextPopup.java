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

package org.eclipse.sapphire.ui.swt;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormText;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */
public class SapphireTextPopup extends SapphirePopup {

    private static final int MAX_WIDTH = 400;

    private String text;

    public SapphireTextPopup(Display display, Point position) {
        super(display, position);
    }
    
    public String getText()
    {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected Control createContentArea(Composite parent) {
        if (this.text != null && !this.text.trim().equals("")) { //$NON-NLS-1$
            GridLayout layout = (GridLayout)parent.getLayout();
            layout.marginTop = 5;
            layout.marginBottom = 5;
            layout.marginRight = 10;
            layout.marginLeft = 10;
            parent.setLayout(layout);
            
            FormText text = new FormText(parent, SWT.NO_FOCUS | SWT.WRAP);
            
            final StringBuffer buffer = new StringBuffer();
            buffer.append( "<form>" ); //$NON-NLS-1$
            buffer.append( "<p>" ); //$NON-NLS-1$
            buffer.append( toLabel(this.text) );
            // Add an invisible link here so the FormText does not draw a focus ring around it.
            buffer.append( "<a href=\"action\"></a>" ); //$NON-NLS-1$
            buffer.append( "</p>" ); //$NON-NLS-1$
            buffer.append( "</form>" ); //$NON-NLS-1$
            text.setText( buffer.toString(), true, false );
            
            GridDataFactory.fillDefaults()
                    .span(2, SWT.DEFAULT)
                    .grab(true, false)
                    .align(SWT.FILL, SWT.TOP)
                    .hint(MAX_WIDTH, SWT.DEFAULT)
                    .applyTo(text);

            return text;
        }
        return parent;
    }

    /**
     * Returns text masking the &amp;-character from decoration as an accelerator in SWT labels.
     */
    public static String toLabel(String text) {
        return (text != null) ? text.replaceAll("&", "&&") : null; // mask & from SWT //$NON-NLS-1$ //$NON-NLS-2$
    }

}
