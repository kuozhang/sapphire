/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.shape.def.FontDef;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramResourceCache {
	
	private List<Color> colors = new ArrayList<Color>();
	private List<Font> fonts = new ArrayList<Font>();
	
	public DiagramResourceCache() {
		FontDescriptor descriptor = JFaceResources.getDefaultFontDescriptor();
		FontData[] fontData = descriptor.getFontData();
		FontData smallerFontData = new FontData(fontData[0].getName(), fontData[0].getHeight()-1, 0);
		Font defaultFont = new Font(null, new FontData[] { smallerFontData });
		fonts.add(defaultFont);
	}
	
    public int getLinkStyle(IDiagramConnectionDef def) {
        int linkStyle = SWT.LINE_SOLID;
        if (def != null) {
            org.eclipse.sapphire.ui.LineStyle style = def.getLineStyle().getContent();
            if (style == org.eclipse.sapphire.ui.LineStyle.DASH ) {
                linkStyle = SWT.LINE_DASH;
            }
            else if (style == org.eclipse.sapphire.ui.LineStyle.DOT) {
                linkStyle = SWT.LINE_DOT;
            }
            else if (style == org.eclipse.sapphire.ui.LineStyle.DASH_DOT) {
                linkStyle = SWT.LINE_DASHDOT;
            }
        }            
        return linkStyle;
    }
	
    public Color getLineColor(DiagramConnectionPart connection) {
    	IDiagramConnectionDef def = connection.getConnectionDef();
    	Color color = ColorConstants.darkBlue;
    	if (def != null) {
        	return getColor(def.getLineColor().getContent());
    	}
    	return color;
    }
    
    public Color getColor(org.eclipse.sapphire.ui.Color sapphireColor) {
    	int red = sapphireColor.getRed();
    	int green = sapphireColor.getGreen();
    	int blue = sapphireColor.getBlue();
    	
		for (Color existingColor : colors) {
			if (existingColor.getRed() == red && existingColor.getGreen() == green && existingColor.getBlue() == blue) {
				return existingColor;
			}
		}
    	
		final Color newColor = new Color(Display.getCurrent(), red, green, blue);
		colors.add(newColor);
		return newColor;
    }
    
    public Font getDefaultFont() {
		return fonts.get(0);
    }
    
    public Font getFont(FontDef fontDef) {

    	if (fontDef != null) {
    		final String name = fontDef.getName().getContent();
    		final int size = fontDef.getSize().getContent();
    		int style = SWT.NORMAL;
    		if (fontDef.isBold().getContent()) {
    			style |= SWT.BOLD;
    		}
    		if (fontDef.isItalic().getContent()) {
    			style |= SWT.ITALIC;
    		}
        	for (Font existingFont : fonts) {
        		FontData data = existingFont.getFontData()[0];
        		if (data.getName().equals(name) && data.getHeight() == size && data.getStyle() == style) {
        			return existingFont;
        		}
        	}
        	final FontData newFontData = new FontData(name, size, style);
        	final Font newFont = new Font(Display.getCurrent(), newFontData);
        	fonts.add(newFont);
        	return newFont;
    	}
    	return fonts.get(0);
    }
    
    public void dispose() {
    	for (Color existingColor : colors) {
    		existingColor.dispose();
    	}
    	
    	for (Font existingFont : fonts) {
    		existingFont.dispose();
    	}
    }
}
