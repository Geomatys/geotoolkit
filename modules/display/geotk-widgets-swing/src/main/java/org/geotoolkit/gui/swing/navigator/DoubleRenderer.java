/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DoubleRenderer implements NavigatorRenderer<Double>{

    @Override
    public int getGraduationHeight() {
        return 20;
    }

    @Override
    public void render(NavigatorModel<Double> model, Graphics2D g, Rectangle area) {
        g.setClip(area);
        g.setColor(Color.LIGHT_GRAY);
        g.fill(area);

        g.setColor(Color.BLACK);

        for(int i=0; i<area.width; i+=10){
            final int d = model.getValueAt(i).intValue();

            g.drawLine(d, 0, d, getGraduationHeight());
            g.drawString(String.valueOf(d), d+2, getGraduationHeight());

        }

    }

}
