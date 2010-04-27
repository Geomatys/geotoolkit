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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

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

    private final Color Cbase = Color.GRAY;
    private final Color Ctop = Cbase.brighter();
    private final Color Clast = Cbase.darker();

    public DoubleRenderer(){

    }

    @Override
    public int getGraduationHeight() {
        return 20;
    }

    @Override
    public void render(JNavigator navigator, Graphics2D g, Rectangle area) {

        final int extent;
        final int orientation = navigator.getOrientation();
        final boolean horizontal = orientation == NORTH || orientation == SOUTH;
        final boolean flipText = orientation == NORTH || orientation == WEST;

        //draw the background gradient -----------------------------------------

        int sx = 0;
        int sy = 0;
        int ex = 0;
        int ey = 0;
        switch(orientation){
            case NORTH : sx=0;sy=0;ex=0;ey=area.height;break;
            case SOUTH : sx=0;sy=area.height;ex=0;ey=0;break;
            case EAST : sx=area.width;sy=0;ex=0;ey=0;break;
            case WEST : sx=0;sy=0;ex=area.width;ey=0;break;
        }

        final GradientPaint mask = new GradientPaint(
                sx, sy, Cbase,
                ex, ey, Ctop);
        g.setPaint(mask);
        g.fill(area);

        if(horizontal){
            extent = area.width;
        }else{
            extent = area.height;
        }

        //draw the graduations -------------------------------------------------
        final int spacing = 200;

        final NavigatorModel model = navigator.getModel();
        final double start = model.getDimensionValueAt(-spacing);
        final double end = model.getDimensionValueAt(extent+spacing);

        final RenderingHints tickHint = new RenderingHints(null);
        tickHint.put(Graduation.VISUAL_AXIS_LENGTH, extent+spacing);
        tickHint.put(Graduation.VISUAL_TICK_SPACING, spacing/2);

        final NumberGraduation graduationX = new NumberGraduation(null);
        graduationX.setRange(start, end, null);
        final TickIterator tickIte = graduationX.getTickIterator(tickHint, null);


        while(!tickIte.isDone()){
            tickIte.next();
            final String label = tickIte.currentLabel();
            final double d = tickIte.currentPosition();
            int p = (int)model.getGraphicValueAt(d);


            if(tickIte.isMajorTick()){
                g.setStroke(new BasicStroke(2.5f));
                g.setColor(Ctop);
                g.setFont(new Font("Serif", Font.BOLD, 12));
            }else{
                g.setStroke(new BasicStroke(1f));
                g.setColor(Ctop);
                g.setFont(new Font("Serif", Font.PLAIN, 10));
            }

            switch(orientation){
                case NORTH : break;
                case SOUTH :
                    g.drawLine(p, 0, p,area.height);
                    g.drawString(label, p, area.height-2);
                    break;
                case EAST : break;
                case WEST :
                    g.drawLine(0, p, area.width, p);
                    g.drawString(label, 2, p-2);
                    break;
            }

        }

    }

}
