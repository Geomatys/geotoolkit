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
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.JComponent;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.northarrow.DefaultNorthArrowTemplate;
import org.geotoolkit.display2d.ext.northarrow.J2DNorthArrowUtilities;
import org.geotoolkit.display2d.ext.northarrow.NorthArrowTemplate;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.decoration.MapDecoration;

/**
 * Decoration displaying a north arrow.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JNorthArrowDecoration extends JComponent implements MapDecoration{

    private Map2D map = null;

    private final NorthArrowTemplate arrowTemplate;

    private final Dimension arrowDimension = new Dimension(100,100);
    private final BufferedImage buffer = new BufferedImage(arrowDimension.width, arrowDimension.height, BufferedImage.TYPE_INT_ARGB);
    private float lastRotation = Float.NEGATIVE_INFINITY;
    private int margin = 10;
    private int interMargin = 3;

    public JNorthArrowDecoration(){
        this(JNorthArrowDecoration.class.getResource("/org/geotoolkit/gui/swing/resource/icon/boussole.svg"));
    }

    public JNorthArrowDecoration(URL svgFile){
        arrowTemplate = new DefaultNorthArrowTemplate(null,svgFile, new Dimension(100,100));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refresh() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMap2D(Map2D map) {
        this.map = map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map2D getMap2D() {
        return map;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JComponent geComponent() {
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {

        if(map == null) return;

        final Rectangle all = getBounds();
        final Rectangle arrowArea = new Rectangle(all.x+all.width-margin-arrowDimension.width,
                                             all.y+all.height-margin-arrowDimension.height,
                                             arrowDimension.width,
                                             arrowDimension.height);

        Rectangle clip = g.getClipBounds();
        if(clip != null && !(clip.intersects(arrowArea))){
            return;
        }

         final Graphics2D g2d = (Graphics2D) g;

        float rotate = (float)map.getCanvas().getController().getRotation();
        if(rotate != lastRotation){
            Graphics2D bufferG = buffer.createGraphics();
            bufferG.setBackground(new Color(0f,0f,0f,0f));
            bufferG.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            bufferG.setRenderingHints(g2d.getRenderingHints());
            bufferG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // paint the north arrow -----------------------------------------------

            bufferG.setStroke(new BasicStroke(1));
            bufferG.setColor(new Color(1f, 1f, 1f, 0.85f));
            bufferG.fillOval(0,0, arrowArea.width-1, arrowArea.height-1);
    //        g2d.fillRoundRect(arrowArea.x, arrowArea.y, arrowArea.width, arrowArea.height, roundSize, roundSize);

            bufferG.setColor(Color.GRAY);
            bufferG.drawOval(0,0, arrowArea.width-1, arrowArea.height-1);
    //        g2d.drawRoundRect(arrowArea.x, arrowArea.y, arrowArea.width, arrowArea.height, roundSize, roundSize);

            arrowArea.x = interMargin;
            arrowArea.y = interMargin;
            arrowArea.width -= 2*interMargin;
            arrowArea.height -= 2*interMargin;

            try {
                J2DNorthArrowUtilities.paint(rotate,bufferG, arrowArea.x,arrowArea.y, arrowTemplate);
            } catch (PortrayalException ex) {
                ex.printStackTrace();
            }
            lastRotation = rotate;
            arrowArea.x = all.x+all.width-margin-arrowDimension.width;
            arrowArea.y = all.y+all.height-margin-arrowDimension.height;
        }

       
        g2d.drawImage(buffer, arrowArea.x, arrowArea.y, null);

    }


}
