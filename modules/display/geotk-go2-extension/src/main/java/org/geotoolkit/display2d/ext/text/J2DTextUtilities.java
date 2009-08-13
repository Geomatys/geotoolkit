/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.text;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JLabel;
import org.geotoolkit.display2d.ext.BackgroundTemplate;
import org.geotoolkit.display2d.ext.BackgroundUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class J2DTextUtilities {

    private J2DTextUtilities(){}

    public static void paint(Graphics2D g2d, TextTemplate template, int x, int y){

        final Dimension estimation = estimate(g2d, template, false);
        int X = x;
        int Y = y;

        final BackgroundTemplate background = template.getBackground();
        if(background != null){
            final Rectangle area = new Rectangle(estimation);
            area.x = x;
            area.y = y;

            Insets insets = background.getBackgroundInsets();
            area.width += insets.left + insets.right;
            area.height += insets.top + insets.bottom;
            X += insets.left;
            Y += insets.top;

            BackgroundUtilities.paint(g2d, area, background);
        }

        JLabel lbl = new JLabel(template.getText());
        lbl.setSize(lbl.getPreferredSize());
        lbl.setOpaque(false);

        g2d.translate(X, Y);
        lbl.paint(g2d);
        g2d.translate(-X, -Y);
    }

    public static Dimension estimate(Graphics2D g, TextTemplate template, boolean considerBackground){
        final Dimension dim = new Dimension(0, 0);

        final String txt = template.getText();
        final JLabel lbl = new JLabel(txt);
        final Dimension pref = lbl.getPreferredSize();
        dim.width = pref.width;
        dim.height = pref.height;

        if(considerBackground && template.getBackground() != null){
            final Insets insets = template.getBackground().getBackgroundInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.bottom + insets.top;
        }

        return dim;
    }


}
