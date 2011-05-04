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
import com.vividsolutions.jts.geom.MultiLineString;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;


/**
 * Edition tool to create multi line parts.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiLinePartCreationDelegate extends AbstractFeatureEditionDelegate {

    private Feature feature = null;
    private MultiLineString geometry = null;
    private final List<Geometry> subGeometries = new ArrayList<Geometry>();
    //linestring in creation process
    private final List<Coordinate> coords = new ArrayList<Coordinate>();

    public MultiLinePartCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
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
        coords.clear();
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        this.geometry = null;
        this.subGeometries.clear();
        
        if(feature != null){
            this.geometry = (MultiLineString) helper.toObjectiveCRS(feature);
            if(geometry != null){
                geometry.clone();
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
                coords.add(helper.toCoord(e.getX(), e.getY()));
                if(coords.size() == 1){
                    //this is the first point of the geometry we create
                    //add another point that will be used when moving the mouse around
                    coords.add(helper.toCoord(e.getX(), e.getY()));
                    Geometry cdt = EditionHelper.createGeometry(coords);
                    subGeometries.add(cdt);
                }
                Geometry cdt = EditionHelper.createGeometry(coords);
                if (subGeometries.size() > 0) {
                    subGeometries.remove(subGeometries.size() - 1);
                }
                subGeometries.add(cdt);
                geometry = EditionHelper.createMultiLine(subGeometries);
                decoration.setGeometries(Collections.singleton(geometry));
            }
        } else if (button == MouseEvent.BUTTON3 && feature != null) {
            if (!coords.isEmpty()) {
                //finish creation a new subpart
                resetCurrentCreation();
            }else{
                //finish editing the feature
                helper.sourceModifyFeature(feature, geometry);
                reset();
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if(coords.size() > 1){
            coords.remove(coords.size()-1);
            coords.add(helper.toCoord(e.getX(), e.getY()));
            Geometry candidate = EditionHelper.createGeometry(coords);
            if (subGeometries.size() > 0) {
                subGeometries.remove(subGeometries.size() - 1);
            }
            subGeometries.add(candidate);
            geometry = EditionHelper.createMultiLine(subGeometries);
            decoration.setGeometries(Collections.singleton(geometry));
            return;
        }
        super.mouseMoved(e);
    }
    

}
