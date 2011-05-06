/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011, Geomatys
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


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.util.collection.UnmodifiableArrayList;


/**
 * Edition tool to create holes in polygons.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class PolygonHoleCreationDelegate extends AbstractFeatureEditionDelegate {

    private boolean multipolygon = false;
    
    private Feature feature = null;
    private Geometry geometry = null;
    private final List<Geometry> subGeometries = new ArrayList<Geometry>();
    //polygon in creation process, used for exclusion
    private final List<Coordinate> coords = new ArrayList<Coordinate>();
    private Polygon erase = null;
    private boolean justCreated = false;

    public PolygonHoleCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        resetCurrentCreation();
        feature = null;
        geometry = null;
        subGeometries.clear();
        decoration.setGeometries(null);
    }
    
    private void resetCurrentCreation(){
        justCreated = false;
        coords.clear();
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        this.geometry = null;
        this.subGeometries.clear();
        
        if(feature != null){
            this.geometry = helper.toObjectiveCRS(feature);
            if(geometry != null){
                geometry.clone();
                multipolygon = (geometry instanceof MultiPolygon);
                for(int i=0; i<geometry.getNumGeometries();i++){
                    subGeometries.add(geometry.getGeometryN(i));
                }
            }
        }
        decoration.setGeometries(Collections.singleton(this.geometry));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            if (geometry == null) {
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            } else {
                if(justCreated){
                    justCreated = false;
                    //we must modify the second point since two point where added at the start
                    coords.remove(2);
                    coords.remove(1);
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                    coords.add(helper.toCoord(e.getX(), e.getY()));

                }else if(coords.isEmpty()){
                    justCreated = true;
                    //this is the first point of the geometry we create
                    //add 3 points that will be used when moving the mouse around
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                }else{
                    justCreated = false;
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                }

                erase = EditionHelper.createPolygon(coords);
                final Geometry line = EditionHelper.createLinearRing(coords);
                geometry = (multipolygon) ? EditionHelper.createMultiPolygon(subGeometries) : subGeometries.get(0);
                try{
                    geometry = geometry.difference(erase);
                }catch(TopologyException ex){
                    LOGGER.log(Level.INFO, ex.getLocalizedMessage());
                }
                decoration.setGeometries(UnmodifiableArrayList.wrap(geometry,line));
            }
        } else if (button == MouseEvent.BUTTON3 && feature != null) {
            //finish editing the feature
            helper.sourceModifyFeature(feature, geometry);
            reset();
        }
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
            
            erase = EditionHelper.createPolygon(coords);
            final Geometry line = EditionHelper.createLinearRing(coords);
            geometry = (multipolygon) ? EditionHelper.createMultiPolygon(subGeometries) : subGeometries.get(0);
            try{
                geometry = geometry.difference(erase);
            }catch(TopologyException ex){
                LOGGER.log(Level.INFO, ex.getLocalizedMessage());
            }
            decoration.setGeometries(UnmodifiableArrayList.wrap(geometry,line));
            return;
        }
        super.mouseMoved(e);
    }
    
}
