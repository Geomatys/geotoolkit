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
package org.geotoolkit.gui.swing.go2.control.edition;


import com.vividsolutions.jts.geom.Geometry;

import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;


import static org.geotoolkit.gui.swing.go2.control.creation.DefaultEditionDecoration.*;
import static java.awt.event.MouseEvent.*;

/**
 * Geometry moving tool.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class GeometryMoveDelegate extends AbstractFeatureEditionDelegate {

    private Feature feature = null;
    private Geometry geometry = null;
    private final List<Geometry> subGeometries =  new ArrayList<Geometry>();
    private boolean draggingAll = false;


    public GeometryMoveDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        geometry = null;
        subGeometries.clear();
        draggingAll = false;
        decoration.setGeometries(null);
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

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            if(geometry == null){
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            }
        }else if(button == MouseEvent.BUTTON3){
            if(draggingAll){
                helper.sourceModifyFeature(feature, geometry);
            }
            reset();
        }
    }

    int pressed = -1;
    int lastX = 0;
    int lastY = 0;

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();
        lastX = e.getX();
        lastY = e.getY();
        
        if(this.geometry != null && e.getButton()==BUTTON1){
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
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {

        if(draggingAll && pressed==BUTTON1){
            int currentX = e.getX();
            int currentY = e.getY();

            helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
            decoration.setGeometries(Collections.singleton(geometry));

            lastX = currentX;
            lastY = currentY;
            return;
        }
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if(draggingAll && pressed==BUTTON1){
            int currentX = e.getX();
            int currentY = e.getY();

            helper.moveGeometry(geometry, currentX-lastX, currentY-lastY);
            decoration.setGeometries(Collections.singleton(geometry));

            lastX = currentX;
            lastY = currentY;
            return;
        }
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
    }

}
