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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.swing.JComponent;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.ext.scalebar.DefaultScaleBarTemplate;
import org.geotoolkit.display2d.ext.scalebar.J2DScaleBarUtilities;
import org.geotoolkit.display2d.ext.scalebar.ScaleBarTemplate;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.map.map2d.decoration.MapDecoration;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Decoration displaying a scale bar
 *
 * @author Johann Sorel (Geomatys)
 */
public class JScaleBarDecoration extends JComponent implements MapDecoration{

    private Map2D map = null;

    private ScaleBarTemplate template = new DefaultScaleBarTemplate(10,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 12),true,
                            SI.METER);

    private final Dimension scaleDimension = new Dimension(500, 40);
    private final BufferedImage buffer = new BufferedImage(scaleDimension.width, scaleDimension.height, BufferedImage.TYPE_INT_ARGB);
    private CoordinateReferenceSystem lastObjCRS = null;
    private CoordinateReferenceSystem lastDisplayCRS = null;
    private Point2D lastCenter = null;
    private int margin = 10;
    private int roundSize = 12;
    private int interMargin = 10;
    private boolean mustUpdate = false;

    public JScaleBarDecoration(){
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

    public void setUnit(Unit unit) {
        template = new DefaultScaleBarTemplate(10,
                            false, 5, NumberFormat.getNumberInstance(),
                            Color.BLACK, Color.BLACK, Color.WHITE,
                            3,true,false, new Font("Serial", Font.PLAIN, 12),true,
                            unit);
        mustUpdate = true;
        repaint(0,0,getWidth(),getHeight());
    }

    public Unit getUnit(){
        return template.getUnit();
    }

    @Override
    protected void paintComponent(Graphics g) {

        if(map == null) return;


        final Rectangle all = getBounds();
        final Rectangle scaleArea = new Rectangle(margin,
                                             all.y+all.height-margin-scaleDimension.height,
                                             scaleDimension.width,
                                             scaleDimension.height);

        Rectangle clip = g.getClipBounds();
        if(clip != null && !(clip.intersects(scaleArea))){
            return;
        }

        final Graphics2D g2d = (Graphics2D) g;

        final double[] center = map.getCanvas().getController().getCenter().getCoordinate();
        final Point2D centerPoint = new Point2D.Double(center[0], center[1]);
        final CoordinateReferenceSystem objCRS = map.getCanvas().getObjectiveCRS();
        final CoordinateReferenceSystem dispCRS = map.getCanvas().getDisplayCRS();



        if(mustUpdate ||!centerPoint.equals(lastCenter) || !dispCRS.equals(lastDisplayCRS) || !objCRS.equals(lastObjCRS) ){
            
            Graphics2D bufferG = buffer.createGraphics();
            bufferG.setBackground(new Color(0f,0f,0f,0f));
            bufferG.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());

            bufferG.setRenderingHints(g2d.getRenderingHints());
            bufferG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            bufferG.setStroke(new BasicStroke(1));
            bufferG.setColor(new Color(1f, 1f, 1f, 0.85f));
            bufferG.fillRoundRect(0,0, scaleArea.width-1, scaleArea.height-1, roundSize, roundSize);

            bufferG.setColor(Color.GRAY);
            bufferG.drawRoundRect(0,0, scaleArea.width-1, scaleArea.height-1, roundSize, roundSize);

            scaleArea.x = interMargin;
            scaleArea.y = interMargin;
            scaleArea.width -= 2*interMargin;
            scaleArea.height -= 2*interMargin;

            try {
                J2DScaleBarUtilities.getInstance().paintScaleBar(objCRS, dispCRS, centerPoint, bufferG, scaleArea, template);
            } catch (PortrayalException ex) {
                ex.printStackTrace();
            }

            scaleArea.x = margin;
            scaleArea.y = all.y+all.height-margin-scaleDimension.height;
            lastCenter = centerPoint;
            lastObjCRS = objCRS;
            lastDisplayCRS = dispCRS;
            mustUpdate = false;
        }


        g2d.drawImage(buffer, scaleArea.x, scaleArea.y, null);


        


    }



}
