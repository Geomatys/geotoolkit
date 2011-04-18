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
package org.geotoolkit.gui.swing.go2.control.navigation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JComponent;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

/**
 * Zoom pan decoration
 *
 * @author Johann Sorel
 * @module pending
 */
public class ZoomDecoration extends JComponent implements MapDecoration{

    private final Color borderColor = Color.GRAY;
    private final Color fillColor = new Color(0.7f,0.7f,0.7f,0.3f);
    
    private Image buffer = null;
    private JMap2D map = null;
    private int startx = 0;
    private int starty = 0;
    private int width = 0;
    private int height = 0;
    private boolean draw = false;
    private boolean fill = false;
    private int lenght = 30;
    private Rectangle lastRect = new Rectangle(0,0,0,0);

    public ZoomDecoration(){
        setOpaque(false);
        setFocusable(false);
    }

    public void setFill(final boolean fill){
        this.fill = fill;
    }

    public void setCoord(final int sx, final int sy, final int ex, final int ey, final boolean draw){
        startx = sx;
        starty = sy;
        width = ex;
        height = ey;
        this.draw = draw;
        lastRect.add(new Rectangle(startx-(2+lenght),starty-(2+lenght),width+(4+2*lenght),height+(4+2*lenght)));
        repaint(lastRect);
    }

    @Override
    public void paintComponent(final Graphics g) {
        if(draw){

            if(fill){
                g.setColor(fillColor);
                g.fillRect(startx, starty, width, height);
            }

            Graphics2D g2 = (Graphics2D) g;

            if(buffer != null){
                g2.drawImage(buffer, startx, starty, null);
            }

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

            g2.drawLine(startx, starty, startx+lenght, starty);
            g2.drawLine(startx, starty, startx, starty+lenght);

            g2.drawLine(startx+width, starty, startx+width-lenght, starty);
            g2.drawLine(startx+width, starty, startx+width, starty+lenght);

            g2.drawLine(startx, starty+height, startx+lenght, starty+height);
            g2.drawLine(startx, starty+height, startx, starty+height-lenght);

            g2.drawLine(startx+width, starty+height, startx+width-lenght, starty+height);
            g2.drawLine(startx+width, starty+height, startx+width, starty+height-lenght);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g2.drawRect(startx, starty, width, height);
            lastRect.setBounds(startx-(2+lenght),starty-(2+lenght),width+(4+2*lenght),height+(4+2*lenght));
            }

    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setMap2D(final JMap2D map) {
        this.map = map;
    }

    @Override
    public JMap2D getMap2D() {
        return map;
    }

    @Override
    public void dispose() {
        map = null;
        setBuffer(null);
    }

    /**
     * @return the buffer
     */
    public Image getBuffer() {
        return buffer;
    }

    /**
     * @param buffer the buffer to set
     */
    public void setBuffer(final Image buffer) {
        this.buffer = buffer;
    }
}
