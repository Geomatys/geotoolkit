/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.debug;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 * This class is a represent a "dynamic" graphic. it should look like a bounding
 * ball with a random speed. Thoses objects are maid to test canvas clip area repaint.
 * 
 * 
 * @author johann sorel (Geomatys)
 */
public class DynamicGraphic extends GraphicJ2D{

    private final int DELAY = 50;
    private int STEP = 5; //(int) (6 * Math.random());
    
    private Rectangle bounds = new Rectangle((int) (200 * Math.random()),(int) (200 * Math.random()), 20, 20);
    private int extendX = 100;
    private int extendY = 100;
    private boolean upflag = false;
    private boolean rightflag = false;

    private boolean waitpaint = false;
    
    public DynamicGraphic(ReferencedCanvas2D canvas){
        super(canvas,DefaultGeographicCRS.WGS84);
          
        if(STEP<1) STEP =1;
        
        if (Math.random() <0.5){
            upflag = true;
        }    
        
        if (Math.random() <0.5){
            rightflag = true;
        }
        
        
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if(!waitpaint){
                    Rectangle concat = new Rectangle(bounds);

                    bounds.x += (rightflag) ? +STEP : -STEP ;
                    if(bounds.getMinX() < 0 || bounds.getMaxX() > extendX) rightflag = !rightflag;


                    bounds.y += (upflag) ? +STEP : -STEP ;
                    if(bounds.getMinY() < 0 || bounds.getMaxY() > extendY) upflag = !upflag;


                    concat.add(bounds);
                    waitpaint = true;
                    setDisplayBounds(concat);
                }
            }
        }, DELAY, DELAY);
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        
        if(event.getPropertyName().equals(ReferencedCanvas2D.DISPLAY_BOUNDS_PROPERTY)){
            Shape shp = (Shape) event.getNewValue();
            extendY = shp.getBounds().height;
            extendX = shp.getBounds().width;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void paint(RenderingContext2D context){
        final Graphics2D g2 = context.getGraphics();

        if(!bounds.intersects(g2.getClipBounds())){
            return;
        }

        Shape shp = context.getCanvasDisplayShape();
        extendY = shp.getBounds().height;
        extendX = shp.getBounds().width;

        context.switchToDisplayCRS();
        
        float Red = (float)bounds.getCenterX()/(float)extendX;
        if(Red < 0 || Red > 1) Red = 0;
        
        float Blue = (float)bounds.getCenterY()/(float)extendY;
        if(Blue < 0 || Blue > 1) Blue = 0;
        
        final Color c = new Color( Red, 0 , Blue );
        
        g2.setColor( c );
        g2.fillOval(bounds.x, bounds.y, bounds.width-1, bounds.height-1);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(bounds.x, bounds.y, bounds.width-1, bounds.height-1);
        waitpaint = false;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isVisible() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() {
        return super.getEnvelope();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {
        //not selectable graphic
        return graphics;
    }
        
}
