/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.resource;

import org.geotoolkit.font.IconBuilder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 * Extend swing JButton with FontAwesome icon.
 * 
 * These button have by default : 
 * <ul>
 *  <li>No border</li>
 *  <li>No margin</li>
 *  <li>No inset</li>
 *  <li>borderPainted to false</li>
 *  <li>contentAreaFilled to false</li>
 *  <li>cursor Hand</li>
 *  <li>focusable to false</li>
 *  <li>size/preferredSize as font size</li>
 * </ul>
 * 
 * @author Quentin Boileau (Geomatys)
 */
public class FontIconJButton extends JButton {


    public FontIconJButton(final String charStr, final int fontSize, final Color fontColor) {
        super(IconBuilder.createIcon(charStr, fontSize, fontColor));
        
        getInsets().set(0, 0, 0, 0);
        getMargin().set(0, 0, 0, 0);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFocusable(false);
        
        setPreferredSize(new Dimension(fontSize, fontSize));
        setSize(new Dimension(fontSize, fontSize));
    }
    
}
