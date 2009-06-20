/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.control.selection;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.List;
import javax.swing.JComponent;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

/**
 * Selection decoration
 * 
 * @author Johann Sorel
 */
public class DefaultSelectionDecoration extends JComponent implements MapDecoration{

    private static final Color MAIN_COLOR = Color.GREEN;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.5f);
    private static final int SHADOW_STEP = 2;
    
    List<Point> points = null;
    
    
    public DefaultSelectionDecoration(){}
    
    
    
    public void setPoints(List<Point> points){
        this.points = points;
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        if(points != null && points.size() > 1){

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(points.get(0).x, points.get(0).y);

            
            for(int i=1;i<points.size();i++){
                Point p = points.get(i);
                path.lineTo(p.x, p.y);
            }


            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            //draw a shadow
            g2.translate(SHADOW_STEP, SHADOW_STEP);
            g2.setColor(SHADOW_COLOR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.fill(path);

            //draw the lines
            g2.translate(-SHADOW_STEP, -SHADOW_STEP);
            g2.setColor(MAIN_COLOR);
            g2.fill(path);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.draw(path);

            //draw start cross
            paintCross(g2, points.get(0));

            //draw end cross
            paintCross(g2, points.get(points.size()-1));

            
        }
    }


    private void paintCross(Graphics2D g2, Point p){
        g2.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER));
        //draw a shadow
        p.x +=SHADOW_STEP;
        p.y +=SHADOW_STEP;
        g2.setColor(SHADOW_COLOR);
        g2.drawLine((int)p.x, (int)p.y-6, (int)p.x, (int)p.y+6);
        g2.drawLine((int)p.x-6, (int)p.y, (int)p.x+6, (int)p.y);
        ///draw the start cross
        p.x -=SHADOW_STEP;
        p.y -=SHADOW_STEP;
        g2.setColor(MAIN_COLOR);
        g2.drawLine((int)p.x, (int)p.y-6, (int)p.x, (int)p.y+6);
        g2.drawLine((int)p.x-6, (int)p.y, (int)p.x+6, (int)p.y);
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public JComponent geComponent() {
        return this;
    }
    
    @Override
    public void setMap2D(Map2D map) {
        
    }

    @Override
    public Map2D getMap2D() {
        return null;
    }

    @Override
    public void dispose() {
    }
}
