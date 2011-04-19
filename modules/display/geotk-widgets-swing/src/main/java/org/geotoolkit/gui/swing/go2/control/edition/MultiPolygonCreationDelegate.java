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

import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * multipolygon creation handler
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiPolygonCreationDelegate extends AbstractFeatureEditionDelegate {

    private int nbRighClick = 0;
    private MultiPolygon geometry = null;
    private final List<Geometry> subGeometries =  new ArrayList<Geometry>();
    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private boolean justCreated = false;


    public MultiPolygonCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        geometry = null;
        subGeometries.clear();
        coords.clear();
        justCreated = false;
        nbRighClick = 0;
        decoration.setGeometries(null);
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if(button == MouseEvent.BUTTON1){
            nbRighClick = 0;

            if(justCreated){
                justCreated = false;
                //we must modify the second point since two point where added at the start
                coords.remove(2);
                coords.remove(1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));

            }else if(coords.size() == 0){
                justCreated = true;
                //this is the first point of the geometry we create
                //add 3 points that will be used when moving the mouse around
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
                Geometry candidate = EditionHelper.createPolygon(coords);
                subGeometries.add(candidate);
            }else{
                justCreated = false;
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }

            Geometry candidate = EditionHelper.createPolygon(coords);
            if (subGeometries.size() > 0) {
                subGeometries.remove(subGeometries.size() - 1);
            }
            subGeometries.add(candidate);
            geometry = EditionHelper.createMultiPolygon(subGeometries);
            decoration.setGeometries(Collections.singleton(geometry));
        }else if(button == MouseEvent.BUTTON3){            
            justCreated = false;
            nbRighClick++;
            if (nbRighClick == 1) {
                if (coords.size() > 2) {
                    if (subGeometries.size() > 0) {
                        subGeometries.remove(subGeometries.size() - 1);
                    }
                    Geometry geo = EditionHelper.createPolygon(coords);
                    subGeometries.add(geo);
                } else if (coords.size() > 0) {
                    if (subGeometries.size() > 0) {
                        subGeometries.remove(subGeometries.size() - 1);
                    }
                }
            } else {
                if (subGeometries.size() > 0) {
                    Geometry geo = EditionHelper.createMultiPolygon(subGeometries);
                    helper.sourceAddGeometry(geo);
                    nbRighClick = 0;
                    reset();
                }
                decoration.setGeometries(null);
            }
            coords.clear();
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
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if(coords.size() > 2){
            if(justCreated){
                coords.remove(coords.size()-1);
                coords.remove(coords.size()-1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }else{
                coords.remove(coords.size()-1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }
            Geometry candidate = EditionHelper.createPolygon(coords);
            if (subGeometries.size() > 0) {
                subGeometries.remove(subGeometries.size() - 1);
            }
            subGeometries.add(candidate);
            geometry = EditionHelper.createMultiPolygon(subGeometries);
            decoration.setGeometries(Collections.singleton(geometry));
            return;
        }
        super.mouseMoved(e);
    }

}
