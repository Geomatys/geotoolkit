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
package org.geotoolkit.gui.swing.go2.control.creation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.MouseInputListener;

import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.go2.control.navigation.MouseNavigatonListener;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;

import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.operation.MathTransform;

/**
 * special mouse listener
 * 
 * @author Johann Sorel
 * @module pending
 */
public abstract class AbstractMouseDelegate extends MouseNavigatonListener implements MouseInputListener,KeyListener {

    protected final DefaultEditionDecoration editionDecoration;
    
    protected final List<Coordinate> coords = new ArrayList<Coordinate>();
    protected final List<Geometry> geoms = new ArrayList<Geometry>();
    protected int nbRightClick = 0;
    protected boolean inCreation = false;
    protected boolean hasEditionGeometry = false;
    protected boolean hasGeometryChanged = false;
    protected String editedFeatureID = null;
    protected Map<Geometry, Integer[]> editedNodes = new HashMap<Geometry, Integer[]>();

    public AbstractMouseDelegate(DefaultEditionDecoration handler) {
        super(null);
        this.editionDecoration = handler;
    }

//    protected void updateCreationGeoms() {
//        int size = coords.size();
//
//        if (inCreation) {
//            if (geoms.size() > 0) {
//                geoms.remove(geoms.size() - 1);
//            }
//        }
//        inCreation = true;
//
//        switch (size) {
//            case 0:
//                break;
//            case 1:
//                geoms.add(EditionHelper.createPoint(coords.get(0)));
//                break;
//            case 2:
//                geoms.add(EditionHelper.createLine(coords));
//                break;
//            default:
//                geoms.add(EditionHelper.createLine(coords));
//                break;
//        }
//    }
//
//    protected void grabGeometry(int mx, int my) {
//        final FeatureMapLayer layer = editionDecoration.getEditedLayer();
//
//        if(layer == null) return;
//
//        try {
//            final Polygon geo = editionDecoration.mousePositionToGeometry(mx, my);
//            final Filter flt = editionDecoration.toFilter(geo, layer);
//            final FeatureCollection<SimpleFeatureType, SimpleFeature> editgeoms = layer.getFeatureSource().getFeatures(flt);
//
//            if (editgeoms != null) {
//                FeatureIterator<SimpleFeature> fi = editgeoms.features();
//                if (fi.hasNext()) {
//                    SimpleFeature sf = fi.next();
//                    Object obj = sf.getDefaultGeometry();
//
//                    if (obj instanceof Geometry) {
//                        try{
//                            hasEditionGeometry = true;
//                            Geometry geom = (Geometry) obj;
//
//                            MathTransform trs = CRS.findMathTransform(
//                                    layer.getFeatureSource().getSchema().getCoordinateReferenceSystem(),
//                                    editionDecoration.getMap2D().getCanvas().getObjectiveCRS(),
//                                    true);
//
//                            geom = JTS.transform(geom, trs);
//                            geoms.add((Geometry) geom.clone());
//                            editedFeatureID = sf.getID();
//                        }catch(Exception ex){
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//                fi.close();
//            }
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//    }
//
//    protected void grabGeometryNode(int mx, int my) {
//        editedNodes.clear();
//
//        final Geometry geo = geoms.get(0);
//
//        try{
//            //transform our mouse in a geometry
//            final Geometry mouseGeo = editionDecoration.mousePositionToGeometry(mx, my);
//
//            for (int i=0,n=geo.getNumGeometries(); i<n; i++) {
//                final Geometry subgeo = geo.getGeometryN(i);
//
//                if (subgeo.intersects(mouseGeo)) {
//                    //this geometry intersect the mouse
//                    final Coordinate[] coos = subgeo.getCoordinates();
//
//                    for (int j=0,m=coos.length; j<m; j++) {
//                        final Coordinate coo = coos[j];
//                        final Point p = EditionHelper.createPoint(coo);
//                        if (p.intersects(mouseGeo)) {
//
//                            if ((j==0 || j==m-1) && (geo instanceof Polygon || geo instanceof MultiPolygon)) {
//                                //first and last coordinate index are the same point
//                                editedNodes.put(subgeo, new Integer[]{0, m - 1});
//                            } else {
//                                //coordinate is in the middle of the geometry
//                                editedNodes.put(subgeo, new Integer[]{j});
//                            }
//                        }
//                    }
//                }
//
//            }
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
//
//    }
//
//    protected void dragGeometryNode(int mx, int my) {
//        final Coordinate mouseCoord = editionDecoration.toCoord(mx, my);
//
//        for (final Geometry subgeo : editedNodes.keySet()) {
//            final Integer[] nodeIndexes = editedNodes.get(subgeo);
//
//            for (int index : nodeIndexes) {
//                subgeo.getCoordinates()[index].x = mouseCoord.x;
//                subgeo.getCoordinates()[index].y = mouseCoord.y;
//            }
//
//            subgeo.geometryChanged();
//        }
//
//        editionDecoration.clearMemoryLayer();
//        editionDecoration.setMemoryLayerGeometry(geoms);
//    }
//
//    protected void removeGemetryNode(){
//
//        for (final Geometry subgeo : editedNodes.keySet()) {
//            final Integer[] nodeIndexes = editedNodes.get(subgeo);
//
//            if(subgeo instanceof Point){
//
//            }else if(subgeo instanceof LineString){
//
//            }else if(subgeo instanceof Polygon){
//
//            }
//
////            for (int index : nodeIndexes) {
////                subgeo.getCoordinates()[index].x = mouseCoord.x;
////                subgeo.getCoordinates()[index].y = mouseCoord.y;
////            }
//
//            subgeo.geometryChanged();
//        }
//
//        editionDecoration.clearMemoryLayer();
//        editionDecoration.setMemoryLayerGeometry(geoms);
//
//
//    }
//
//    protected void validateGeometryEdit() {
//        if (!geoms.isEmpty() && hasGeometryChanged) {
//            editionDecoration.validateModifiedGeometry(geoms.get(0), editedFeatureID);
//        }
//        hasEditionGeometry = false;
//        hasGeometryChanged = false;
//        editedFeatureID = null;
//        editedNodes.clear();
//        inCreation = false;
//    }
//
//    protected void removeGeometryEdit() {
//        if (!geoms.isEmpty() ) {
//            editionDecoration.removeSelectedGeometry(editedFeatureID);
//        }
//        hasEditionGeometry = false;
//        hasGeometryChanged = false;
//        editedFeatureID = null;
//        editedNodes.clear();
//        inCreation = false;
//    }
//
//    public abstract void fireStateChange();

    @Override
    public void mouseEntered(MouseEvent e) {
        getMap().getComponent().requestFocus();
        super.mouseEntered(e);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

}
