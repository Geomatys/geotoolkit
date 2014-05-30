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
package org.geotoolkit.gui.swing.render2d.control.edition;


import com.vividsolutions.jts.geom.Geometry;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.render2d.JMap2D;

/**
 * Geometry moving tool.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class GeometryMoveDelegate extends AbstractFeatureEditionDelegate {

    /**
     * Constant bind to {@link MouseEvent#BUTTON1} to more readable code
     */
    private static final int LEFT   = MouseEvent.BUTTON1;

    /**
     * Constant bind to {@link MouseEvent#BUTTON2} to more readable code
     */
    private static final int MIDDLE = MouseEvent.BUTTON2;

    /**
     * Constant bind to {@link MouseEvent#BUTTON3} to more readable code
     */
    private static final int RIGHT  = MouseEvent.BUTTON3;

    private Feature feature = null;
    private Geometry geometry = null;
    private final List<Geometry> subGeometries =  new ArrayList<Geometry>();
    private boolean draggingAll = false;

    private int pressed = -1;
    private int lastX = 0;
    private int lastY = 0;

    public GeometryMoveDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        geometry = null;
        subGeometries.clear();
        draggingAll = false;
        decoration.setGeometries(null);
        pressed = -1;
        lastX = 0;
        lastY = 0;
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        if(feature != null){
            this.geometry = helper.toObjectiveCRS(feature);
        }else{
            this.geometry = null;
        }
        decoration.setGeometries(Collections.singleton(this.geometry));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();
        lastX = e.getX();
        lastY = e.getY();

        if (pressed == LEFT) {

            //find feature where mouse clicked
            if(geometry == null){
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }

            if(this.geometry != null){
                try {
                    //start dragging mode
                    final Geometry mouseGeo = helper.mousePositionToGeometry(e.getX(), e.getY());
                    if(mouseGeo.intersects(geometry)){
                        draggingAll = true;
                    }
                } catch (NoninvertibleTransformException ex) {
                    Logger.getLogger(MultiPolygonCreationDelegate.class.getName()).log(Level.WARNING, null, ex);
                }
                return;
            }
        }
        super.mousePressed(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if(draggingAll && pressed == LEFT){
            int currentX = e.getX();
            int currentY = e.getY();

            //update geometry/feature position
            helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
            decoration.setGeometries(Collections.singleton(geometry));

            lastX = currentX;
            lastY = currentY;
            return;
        }
        super.mouseDragged(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

        if(draggingAll && pressed == LEFT){
            int currentX = e.getX();
            int currentY = e.getY();

            //last position update
            helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
            decoration.setGeometries(Collections.singleton(geometry));

            //save
            helper.sourceModifyFeature(feature, geometry, true);
            reset();
            return;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
    }

}
