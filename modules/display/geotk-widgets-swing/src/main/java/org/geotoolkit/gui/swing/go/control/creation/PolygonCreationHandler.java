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
 * polygon creation gandler
 * 
 * @author Johann Sorel
 */
public class PolygonCreationHandler extends AbstractEditionHandler {

    @Override
    protected SpecialMouseListener createMouseListener() {
        return new MouseListen();
    }

    @Override
    protected ImageIcon createIcon() {
        return IconBundle.getInstance().getIcon("16_edit_single_polygon");
    }

    @Override
    protected String createTitle() {
        return ResourceBundle.getBundle("org/geotools/gui/swing/map/map2d/handler/Bundle").getString("create_polygon");
    }

    private class MouseListen extends SpecialMouseListener {

        private final List<Coordinate> coords = new ArrayList<Coordinate>();
        private final List<Geometry> geoms = new ArrayList<Geometry>();
        private int nbRightClick = 0;
        private boolean inCreation = false;
        private boolean hasEditionGeometry = false;
        private boolean hasGeometryChanged = false;
        private String editedFeatureID = null;
        private Map<Geometry, Integer[]> editedNodes = new HashMap<Geometry, Integer[]>();

        public boolean isValidLayer() {
            return false;
//            MapLayer editionLayer = map2D.getEditedMapLayer();
//
//            if (editionLayer != null) {
//
//                if (editionLayer.getFeatureSource() instanceof FeatureStore) {
//
//                    Class jtsClass = null;
//                    jtsClass = editionLayer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getBinding();
//
//                    if (jtsClass != null && jtsClass.equals(Polygon.class)) {
//                        return (true);
//                    } else {
//                        return (false);
//                    }
//                } else {
//                    return (false);
//                }
//
//            } else {
//                return (false);
//            }
        }

        public void fireStateChange() {
            coords.clear();
            geoms.clear();
            nbRightClick = 0;
            inCreation = false;
            hasEditionGeometry = false;
            hasGeometryChanged = false;
            editedFeatureID = null;
            editedNodes.clear();
            clearMemoryLayer();
            setMemoryLayerGeometry(geoms);
        }

        private void updateCreationGeoms() {
            int size = coords.size();

            if (inCreation) {
                if (geoms.size() > 0) {
                    geoms.remove(geoms.size() - 1);
                }
            }
            inCreation = true;

            switch (size) {
                case 0:
                    break;
                case 1:
                    geoms.add(createPoint(coords.get(0)));
                    break;
                case 2:
                    geoms.add(createLine(coords));
                    break;
                default:
                    geoms.add(createLine(coords));
                    break;
            }
        }

        private void grabGeometry(int mx, int my) {
//            Geometry geo = mousePositionToGeometry(mx, my);
//            Filter flt = map2D.createFilter(geo, map2D.getSelectionFilter(),map2D.getEditedMapLayer());
//
//            FeatureCollection<SimpleFeatureType, SimpleFeature> editgeoms = null;
//            try {
//                editgeoms = (FeatureCollection<SimpleFeatureType, SimpleFeature>) map2D.getEditedMapLayer().getFeatureSource().getFeatures(flt);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (editgeoms != null) {
//                FeatureIterator<SimpleFeature> fi = editgeoms.features();
//                if (fi.hasNext()) {
//                    SimpleFeature sf = fi.next();
//                    Object obj = sf.getDefaultGeometry();
//
//                    if (obj instanceof Geometry) {
//                        hasEditionGeometry = true;
//                        Geometry geom = (Geometry) obj;
//                        geom = FACILITIES_FACTORY.projectGeometry(geom, map2D.getEditedMapLayer().getFeatureSource().getSchema().getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem());
//                        geoms.add((Geometry) geom.clone());
//                        editedFeatureID = sf.getID();
//                        System.out.println(editedFeatureID);
//                    }
//                }
//                fi.close();
//            }
        }

        private void grabGeometryNode(int mx, int my) {
            editedNodes.clear();

            Geometry geo = geoms.get(0);
            Geometry mouseGeo = mousePositionToGeometry(mx, my);

            for (int i = 0,  n = geo.getNumGeometries(); i < n; i++) {
                Geometry subgeo = geo.getGeometryN(i);

                if (subgeo.intersects(mouseGeo)) {
                    Coordinate[] coos = subgeo.getCoordinates();

                    for (int j = 0,  m = coos.length; j < m; j++) {
                        Coordinate coo = coos[j];
                        Point p = createPoint(coo);
                        if (p.intersects(mouseGeo)) {

                            if ((j == 0 || j == m - 1) && (geo instanceof Polygon || geo instanceof MultiPolygon)) {
                                editedNodes.put(subgeo, new Integer[]{0, m - 1});
                            } else {
                                editedNodes.put(subgeo, new Integer[]{j});
                            }
                        }
                    }
                }

            }


        }

        private void dragGeometryNode(int mx, int my) {
//            Coordinate mouseCoord = map2D.getRenderingStrategy().toMapCoord(mx, my);
//
//            Geometry geo = geoms.get(0);
//
//            Set<Geometry> set = editedNodes.keySet();
//
//
//            for (Iterator<Geometry> ite = set.iterator(); ite.hasNext();) {
//                Geometry subgeo = ite.next();
//                Integer[] nodeIndexes = editedNodes.get(subgeo);
//
//                for (int index : nodeIndexes) {
//                    subgeo.getCoordinates()[index].x = mouseCoord.x;
//                    subgeo.getCoordinates()[index].y = mouseCoord.y;
//                }
//
//                subgeo.geometryChanged();
//            }
//
//            clearMemoryLayer();
//            setMemoryLayerGeometry(geoms);

        }

        private void validateGeometryEdit() {
            if (!geoms.isEmpty() && hasGeometryChanged) {
                validateModifiedGeometry(geoms.get(0), editedFeatureID);
            }
            hasEditionGeometry = false;
            hasGeometryChanged = false;
            editedFeatureID = null;
            editedNodes.clear();
            inCreation = false;
        }

        public void mouseClicked(MouseEvent e) {
//
//            if (isValidLayer()) {
//                int button = e.getButton();
//                Geometry geo = null;
//
//
//                if (button == MouseEvent.BUTTON1) {
//                    coords.add(map2D.getRenderingStrategy().toMapCoord(e.getX(), e.getY()));
//                    updateCreationGeoms();
//                } else if (button == MouseEvent.BUTTON3) {
//                    inCreation = false;
//                    if (coords.size() > 2) {
//                        geo = createPolygon(coords);
//                        editAddGeometry(new Geometry[]{geo});
//                        geoms.clear();
//                    }
//                    coords.clear();
//                }
//                clearMemoryLayer();
//                setMemoryLayerGeometry(geoms);
//            }
        }

        public void mousePressed(MouseEvent e) {

        }

        public void mouseReleased(MouseEvent e) {

        }

        public void mouseEntered(MouseEvent e) {
//            map2D.getComponent().setCursor(CUR_EDIT);

        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {

        }

        public void mouseMoved(MouseEvent e) {

        }
    }
}
