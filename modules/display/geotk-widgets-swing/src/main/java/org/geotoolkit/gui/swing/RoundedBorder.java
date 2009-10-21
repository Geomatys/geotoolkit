/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.gui.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class RoundedBorder extends AbstractBorder {

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.getBackground());
        g.setClip(new Rectangle(x, y, width, height));
        g.fillRoundRect(x, y, width, height, 26, 26);
        g.setColor(c.getForeground());
        g.drawRoundRect(x, y, width-1, height-1, 26, 26);
    }
    
    @Override
    public Insets getBorderInsets(Component comp) {
        return new Insets(0, 4, 0, 4);
    }

    @Override
    public Insets getBorderInsets(Component comp,Insets in) {
        in.set(0, 4, 0, 4);
        return in;
    }

}
