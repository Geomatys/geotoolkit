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


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opengis.feature.Feature;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.gui.swing.go2.JMap2D;


/**
 * Edition tool to create multi point parts.
 * 
 * @author Johann Sorel
 * @module pending
 */
public class MultiPointPartCreationDelegate extends AbstractFeatureEditionDelegate {

    private Feature feature = null;
    private MultiPoint geometry = null;
    private final List<Geometry> subGeometries = new ArrayList<Geometry>();

    public MultiPointPartCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map,candidate);
    }

    private void reset(){
        feature = null;
        geometry = null;
        subGeometries.clear();
        decoration.setGeometries(null);
    }
    
    private void setCurrentFeature(final Feature feature){
        this.feature = feature;
        this.geometry = null;
        this.subGeometries.clear();
        
        if(feature != null){
            this.geometry = (MultiPoint) helper.toObjectiveCRS(feature);
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
                final Point cdt2 = helper.toJTS(e.getX(), e.getY());
                subGeometries.add(cdt2);
                geometry = EditionHelper.createMultiPoint(subGeometries);
                decoration.setGeometries(Collections.singleton(geometry));
            }
        } else if (button == MouseEvent.BUTTON3 && feature != null) {
            helper.sourceModifyFeature(feature, geometry);
            reset();
        }
    }

}
