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
package org.geotoolkit.gui.swing.go.control.creation;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ImageIcon;

import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Default edition handler
 * 
 * @author Johann Sorel
 */
public class DefaultEditionHandler extends AbstractEditionHandler {

    @Override
    protected SpecialMouseListener createMouseListener() {
        return null; //new MouseListen();
    }

    @Override
    protected ImageIcon createIcon() {
        return IconBundle.getInstance().getIcon("16_edit_geom");
    }

    @Override
    protected String createTitle() {
        return ResourceBundle.getBundle("org/geotools/gui/swing/map/map2d/handler/Bundle").getString("edit_geom");
    }

//    private class MouseListen extends SpecialMouseListener {
//
//        public boolean isValidLayer() {
//            return false;
////
////            MapLayer editionLayer = map2D.getEditedMapLayer();
////
////            if (editionLayer != null) {
////
////                if (editionLayer.getFeatureSource() instanceof FeatureStore) {
////
////                    return (true);
////
////                } else {
////                    return (false);
////                }
////
////            } else {
////                return (false);
////            }
//        }
//
//        public void fireStateChange() {
//            coords.clear();
//            geoms.clear();
//            nbRightClick = 0;
//            inCreation = false;
//            hasEditionGeometry = false;
//            hasGeometryChanged = false;
//            editedFeatureID = null;
//            editedNodes.clear();
//            clearMemoryLayer();
//            setMemoryLayerGeometry(geoms);
//        }
//
//        public void mouseClicked(MouseEvent e) {
//
//            if (isValidLayer()) {
//                int button = e.getButton();
//                Geometry geo = null;
//
//
//                if (button == MouseEvent.BUTTON1) {
//                    if (!hasEditionGeometry) {
//                        grabGeometry(e.getX(), e.getY());
//                    }
//                } else if (button == MouseEvent.BUTTON3) {
//                    validateGeometryEdit();
//                    geoms.clear();
//                    coords.clear();
//                }
//                clearMemoryLayer();
//                setMemoryLayerGeometry(geoms);
//            }
//
//        }
//
//        public void mousePressed(MouseEvent e) {
//            if (isValidLayer()) {
//                int button = e.getButton();
//
//
//                if (button == MouseEvent.BUTTON1) {
//
//                    if (hasEditionGeometry) {
//                        grabGeometryNode(e.getX(), e.getY());
//                    }
//
//                }
//            }
//
//        }
//
//        public void mouseReleased(MouseEvent e) {
//
//
//            if (isValidLayer()) {
//
//                if (hasEditionGeometry && !editedNodes.isEmpty()) {
//                    hasGeometryChanged = true;
//                    dragGeometryNode(e.getX(), e.getY());
//                }
//
//                editedNodes.clear();
//            }
//
//        }
//
//        public void mouseEntered(MouseEvent e) {
////            map2D.getComponent().setCursor(CUR_EDIT);
//
//        }
//
//        public void mouseExited(MouseEvent e) {
//        }
//
//        public void mouseDragged(MouseEvent e) {
//
//            if (isValidLayer()) {
//
//                if (hasEditionGeometry && !editedNodes.isEmpty()) {
//                    hasGeometryChanged = true;
//                    dragGeometryNode(e.getX(), e.getY());
//                }
//            }
//
//        }
//
//        public void mouseMoved(MouseEvent e) {
//
//
//        }
//    }
}
