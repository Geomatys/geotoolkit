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
import java.awt.RenderingHints;
import javax.swing.SwingConstants;
import org.geotoolkit.display.axis.Graduation;
import org.geotoolkit.display.axis.NumberGraduation;
import org.geotoolkit.display.axis.TickIterator;

import static javax.swing.SwingConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DoubleRenderer implements NavigatorRenderer{

    @Override
    public int getGraduationHeight() {
        return 60;
    }

    @Override
    public void render(JNavigator navigator, Graphics2D g, Rectangle area) {
        g.setColor(Color.BLACK);

        final int extent;
        final int orientation = navigator.getOrientation();
        final boolean horizontal = orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH;
        final boolean flipText = orientation == SwingConstants.NORTH || orientation == SwingConstants.WEST;


        if(horizontal){
            extent = area.width;
        }else{
            extent = area.height;
        }

        final NavigatorModel model = navigator.getModel();
        final double start = model.getDimensionValueAt(0);
        final double end = model.getDimensionValueAt(extent);

        final RenderingHints tickHint = new RenderingHints(null);
        tickHint.put(Graduation.VISUAL_AXIS_LENGTH, extent);
        tickHint.put(Graduation.VISUAL_TICK_SPACING, 200);

        final NumberGraduation graduationX = new NumberGraduation(null);
        graduationX.setRange(start, end, null);
        final TickIterator tickIte = graduationX.getTickIterator(tickHint, null);


        while(!tickIte.isDone()){
            tickIte.next();
            final String label = tickIte.currentLabel();
            final double d = tickIte.currentPosition();
            final int p = (int)model.getGraphicValueAt(d);

            switch(orientation){
                case NORTH : break;
                case SOUTH :
                    g.drawLine(p, 0, p,getGraduationHeight());
                    g.drawString(label, p, area.height-2);
                    break;
                case EAST : break;
                case WEST :
                    g.drawLine(0, p, getGraduationHeight(), p);
                    g.drawString(label, 2, p-2);
                    break;
            }

        }

//        if(orientation == SwingConstants.NORTH){
//
//        }else if(orientation == SwingConstants.SOUTH){
//            while(!tickIte.isDone()){
//                tickIte.next();
//                final String label = tickIte.currentLabel();
//                final double d = tickIte.currentPosition();
//                final int p = (int)model.getGraphicValueAt(d);
//
//                g.drawLine(p, 0, p,getGraduationHeight());
//                g.drawString(label, p, area.height-2);
//            }
//
//        }else if(orientation == SwingConstants.WEST){
//            while(!tickIte.isDone()){
//                tickIte.next();
//                final String label = tickIte.currentLabel();
//                final double d = tickIte.currentPosition();
//                final int p = (int)model.getGraphicValueAt(d);
//                g.drawLine(0, p, getGraduationHeight(), p);
//                g.drawString(label, 2, p-2);
//            }
//
//        }else if(orientation == SwingConstants.EAST){
//
//        }

        g.dispose();
    }

}
