/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.go.control.creation;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
//import org.geotools.gui.swing.icon.IconBundle;
//import org.geotools.gui.swing.map.map2d.stream.EditableMap2D;
//import org.geotools.gui.swing.map.map2d.stream.TempMemoryDataStore;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
//import org.geotools.gui.swing.map.map2d.stream.strategy.StreamingStrategy;
import org.geotools.gui.swing.go.GoMap2D;
import org.geotools.gui.swing.misc.FacilitiesFactory;
import org.geotools.gui.swing.misc.GeometryClassFilter;
//import org.geotools.styling.Rule;
//import org.geotools.styling.Style;
//import org.geotools.styling.StyleBuilder;
//import org.geotools.styling.Symbolizer;

/**
 * Abstract edition handler
 * 
 * @author Johann Sorel
 */
abstract class AbstractEditionHandler implements EditionHandler {

//    protected final StyleBuilder STYLE_BUILDER = new StyleBuilder();
    protected final FacilitiesFactory FACILITIES_FACTORY = new FacilitiesFactory();
    protected final ImageIcon ICON;
    protected final String title;
    protected static final Coordinate[] EMPTY_COORDINATE_ARRAY = new Coordinate[0];
    protected final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    protected final SpecialMouseListener mouseInputListener;
//    protected EditableMap2D map2D = null;
    protected boolean installed = false;
    protected Cursor CUR_EDIT;
    protected MapLayer memoryLayer;
    protected MapLayer edgesLayer;

    public AbstractEditionHandler() {
        buildCursors();
        mouseInputListener = createMouseListener();
        ICON = createIcon();
        title = createTitle();
    }

    protected abstract SpecialMouseListener createMouseListener();

    protected abstract ImageIcon createIcon();

    protected abstract String createTitle();

    protected void buildCursors() {
//        Toolkit tk = Toolkit.getDefaultToolkit();
//        ImageIcon eci_edit = IconBundle.getResource().getIcon("16_edit");
//
//        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
//        img.getGraphics().drawImage(eci_edit.getImage(), 0, 0, null);
//        CUR_EDIT = tk.createCustomCursor(img, new java.awt.Point(7, 1), "edit");
    }

//    private Style createPointStyle() {
//        Style pointSelectionStyle = STYLE_BUILDER.createStyle();
//        pointSelectionStyle.addFeatureTypeStyle(STYLE_BUILDER.createFeatureTypeStyle(map2D.getPointSymbolizer()));
//        return pointSelectionStyle;
//    }
//
//    private Style createStyle() {
//        Rule r2 = STYLE_BUILDER.createRule(new Symbolizer[]{map2D.getLineSymbolizer()});
//        r2.setFilter(new GeometryClassFilter(LineString.class, MultiLineString.class));
//        Rule r3 = STYLE_BUILDER.createRule(new Symbolizer[]{map2D.getPolygonSymbolizer()});
//        r3.setFilter(new GeometryClassFilter(Polygon.class, MultiPolygon.class));
//
//        Style editionStyle = STYLE_BUILDER.createStyle();
//        editionStyle.addFeatureTypeStyle(STYLE_BUILDER.createFeatureTypeStyle(null, new Rule[]{r2, r3}));
//
//        return editionStyle;
//    }

    public void install(GoMap2D map) {
//        installed = true;
//        map2D = map;
//
//        //create the memory layers----------------------------------------------
//
//        TempMemoryDataStore mds = new TempMemoryDataStore();
//        SimpleFeatureType featureType = null;
//        MapLayer layer = null;
//        try {
//            featureType = DataUtilities.createType("memory", "geom:Geometry");
//        } catch (SchemaException se) {
//            se.printStackTrace();
//        }
//
//        if (featureType != null) {
//            try {
//                mds.createSchema(featureType);
//                FeatureSource<SimpleFeatureType, SimpleFeature> fs = ((DataStore) mds).getFeatureSource(((DataStore) mds).getTypeNames()[0]);
//                layer = new DefaultMapLayer(fs, createStyle());
//            } catch (IOException se) {
//                se.printStackTrace();
//            }
//        }
//        memoryLayer = layer;
//
//
//        mds = new TempMemoryDataStore();
//        featureType = null;
//        layer = null;
//        try {
//            featureType = DataUtilities.createType("memory", "geom:Point");
//        } catch (SchemaException se) {
//            se.printStackTrace();
//        }
//
//        if (featureType != null) {
//            try {
//                mds.createSchema(featureType);
//                FeatureSource<SimpleFeatureType, SimpleFeature> fs = ((DataStore) mds).getFeatureSource(((DataStore) mds).getTypeNames()[0]);
//                layer = new DefaultMapLayer(fs, createPointStyle());
//            } catch (IOException se) {
//                se.printStackTrace();
//            }
//        }
//        edgesLayer = layer;
//
//        map2D.setMemoryLayers(new MapLayer[]{memoryLayer, edgesLayer});
    }

    public void installListeners(GoMap2D map2d) {
//        map2D = map2d;
//        map2D.getComponent().addMouseListener(mouseInputListener);
//        map2D.getComponent().addMouseMotionListener(mouseInputListener);
    }

    public void uninstallListeners() {
//        map2D.getComponent().removeMouseListener(mouseInputListener);
//        map2D.getComponent().removeMouseMotionListener(mouseInputListener);
    }

    public void uninstall() {
//        map2D.setMemoryLayers(new MapLayer[0]);
//        map2D = null;
//        installed = false;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void cancelEdition() {
//        clearMemoryLayer();
//        mouseInputListener.fireStateChange();
//        map2D.repaintMemoryDecoration();
    }

    public String getTitle() {
        return title;
    }

    public ImageIcon getIcon() {
        return ICON;
    }

    //--------------------Geometry Edition--------------------------------------

    /**
     *  transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    protected Geometry mousePositionToGeometry(int mx, int my) {
        return null;
//        Coordinate[] coord = new Coordinate[5];
//        int taille = 4;
//
//        StreamingStrategy strategy = map2D.getRenderingStrategy();
//        coord[0] = strategy.toMapCoord(mx - taille, my - taille);
//        coord[1] = strategy.toMapCoord(mx - taille, my + taille);
//        coord[2] = strategy.toMapCoord(mx + taille, my + taille);
//        coord[3] = strategy.toMapCoord(mx + taille, my - taille);
//        coord[4] = coord[0];
//
//        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
//        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    protected Point createPoint(Coordinate coord) {
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    protected MultiPoint createMultiPoint(List<Geometry> geoms) {
        List<Point> lst = new ArrayList<Point>();
        for (Geometry go : geoms) {
            if (go instanceof Point) {
                lst.add((Point) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiPoint(lst.toArray(new Point[lst.size()]));
    }

    protected LineString createLine(List<Coordinate> coords) {
        return GEOMETRY_FACTORY.createLineString(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    protected LinearRing createLinearRing(List<Coordinate> coords) {
        if (!(coords.get(0).equals2D(coords.get(coords.size() - 1)))) {
            Coordinate coo = new Coordinate(coords.get(0));
            coords.add(coo);
        }

        return GEOMETRY_FACTORY.createLinearRing(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    protected Polygon createPolygon(List<Coordinate> coords) {
        LinearRing ring = createLinearRing(coords);
        return GEOMETRY_FACTORY.createPolygon(ring, null);
    }

    protected MultiPolygon createMultiPolygon(List<Geometry> geoms) {
        List<Polygon> lst = new ArrayList<Polygon>();
        for (Geometry go : geoms) {
            if (go instanceof Polygon) {
                lst.add((Polygon) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiPolygon(lst.toArray(new Polygon[lst.size()]));
    }

    protected MultiLineString createMultiLine(List<Geometry> geoms) {
        List<LineString> lst = new ArrayList<LineString>();
        for (Geometry go : geoms) {
            if (go instanceof LineString) {
                lst.add((LineString) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiLineString(lst.toArray(new LineString[lst.size()]));
    }

    protected synchronized void editAddGeometry(Geometry[] geoms) {
//
//        MapLayer editionLayer = map2D.getEditedMapLayer();
//
//        if (editionLayer != null) {
//
//            for (Geometry geom : geoms) {
//
//                SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
//                Object[] values = new Object[featureType.getAttributeCount()];
//
//                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//                geom = FACILITIES_FACTORY.projectGeometry(geom, map2D.getRenderingStrategy().getContext(), editionLayer);
//
//                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//                for (int i = 0,  n = lst.size(); i < n; i++) {
//                    AttributeDescriptor desc = lst.get(i);
//
//                    if (desc.equals(geomAttribut)) {
//                        values[i] = geom;
//                    } else {
//                        values[i] = desc.getDefaultValue();
//                    }
//                }
//
//                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                collection.add(sf);
//
//                //commit in shape
//                DataStore data = (DataStore) editionLayer.getFeatureSource().getDataStore();
//
//                DefaultTransaction transaction = null;
//                FeatureStore<SimpleFeatureType, SimpleFeature> store = null;
//                try {
////                    String featureName = data.getTypeNames()[0]; // there is only one in a shapefile
//
//                    // Create the DefaultTransaction Object
//                    transaction = new DefaultTransaction();
//
////                    String name = editionLayer.getFeatureSource().getName().getLocalPart();
////                    try {
////                        //GR: question: why not just editionLayer.getFeatureSource()?
////                        FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
////                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
////                    } catch (IOException e) {
////                        // Tell it the name of the shapefile it should look for in our DataStore
////                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) data.getFeatureSource(featureName);
////                    }
//
//                    store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//
//                    // Then set the transaction for that FeatureStore
//                    store.setTransaction(transaction);
//
//                    store.addFeatures(collection);
//                    transaction.commit();
//                } catch (Exception eek) {
//                    eek.printStackTrace();
//                    try {
//                        store.getTransaction().rollback();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } finally {
//                    transaction.close();
//                }
//
//
//            }
//
//        }

    }

    protected synchronized void validateModifiedGeometry(final Geometry geo, final String ID) {
//
//        if (geo == null || ID == null) {
//            throw new NullPointerException();
//        }
//
//
//        MapLayer editionLayer = map2D.getEditedMapLayer();
//
//        FeatureStore<SimpleFeatureType, SimpleFeature> store;
//        if (editionLayer.getFeatureSource() instanceof FeatureStore) {
//
////            String name = editionLayer.getFeatureSource().getName().getLocalPart();
////            try {
////                //GR question: why not just editionLayer.getFeatureSource()?
////                FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
////                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
////            } catch (IOException e) {
////                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
////            }
//
//            store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
////                    store.getDataStore().dispose();
//
//            DefaultTransaction transaction = new DefaultTransaction("trans_maj");
////                    Transaction previoustransaction = store.getTransaction();
//
//            store.setTransaction(transaction);
//            FilterFactory ff = FactoryFinder.getFilterFactory(null);
//            Filter filter = ff.id(Collections.singleton(ff.featureId(ID)));
//
//
//            SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
//            AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//            Geometry geom = FACILITIES_FACTORY.projectGeometry(geo, map2D.getRenderingStrategy().getContext(), editionLayer);
//
//            try {
//                store.modifyFeatures(geomAttribut, geom, filter);
//                transaction.commit();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                try {
//                    transaction.rollback();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } finally {
//                transaction.close();
////                store.setTransaction(Transaction.AUTO_COMMIT);
//            }
//
//        }

    }


    //---------------------Memory Layer-----------------------------------------

    protected synchronized void setMemoryLayerGeometry(List<Geometry> geoms) {
//
//        if (memoryLayer != null) {
//
//            //memory layer--------------------------
//            FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
//
//            for (Geometry geom : geoms) {
//
//                //geom = projectGeometry(geom, memoryLayer);
//                SimpleFeatureType featureType = (SimpleFeatureType) memoryLayer.getFeatureSource().getSchema();
//                Object[] values = new Object[featureType.getAttributeCount()];
//                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//
//                for (int i = 0,  n = lst.size(); i < n; i++) {
//                    AttributeDescriptor desc = lst.get(i);
//                    values[i] = (desc.equals(geomAttribut)) ? geom : desc.getDefaultValue();
//                }
//
//                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                collection.add(sf);
//            }
//
//
//            //commit
//            FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource();
//            try {
//                store.addFeatures(collection);
//            } catch (Exception eek) {
//                eek.printStackTrace();
//            }
//
//
//            //edges layer --------------------------------
//            collection = FeatureCollections.newCollection();
//            for (Geometry geom : geoms) {
//
//                Coordinate[] coords = geom.getCoordinates();
//                for (Coordinate coord : coords) {
//
//                    //geom = projectGeometry(geom, memoryLayer);
//                    SimpleFeatureType featureType = (SimpleFeatureType) edgesLayer.getFeatureSource().getSchema();
//                    Object[] values = new Object[featureType.getAttributeCount()];
//                    AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
//
//                    List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
//                    for (int i = 0,  n = lst.size(); i < n; i++) {
//                        AttributeDescriptor desc = lst.get(i);
//
//                        if (desc.equals(geomAttribut)) {
//                            values[i] = GEOMETRY_FACTORY.createPoint(coord);
//                        } else {
//                            values[i] = desc.getDefaultValue();
//                        }
//                    }
//
//                    //featureType.
//                    SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
//                    collection.add(sf);
//
//                }
//
//                //commit
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) edgesLayer.getFeatureSource();
//                try {
//                    store.addFeatures(collection);
//                } catch (Exception eek) {
//                    eek.printStackTrace();
//                }
//
//            }
//        }
//
//        map2D.repaintMemoryDecoration();
    }

    protected synchronized void clearMemoryLayer() {
//
//        try {
//            FeatureStore<SimpleFeatureType, SimpleFeature> fst = (FeatureStore<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource();
//            fst.removeFeatures(Filter.INCLUDE);
//            fst = (FeatureStore<SimpleFeatureType, SimpleFeature>) edgesLayer.getFeatureSource();
//            fst.removeFeatures(Filter.INCLUDE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        map2D.repaintMemoryDecoration();
    }

    protected synchronized void reprojectEditionLayer() {
//
//        List<Geometry> geoms = new ArrayList<Geometry>();
//        List<Geometry> geomsOut = new ArrayList<Geometry>();
//
//        try {
//            FeatureCollection<SimpleFeatureType, SimpleFeature> col = (FeatureCollection<SimpleFeatureType, SimpleFeature>) memoryLayer.getFeatureSource().getFeatures();
//            FeatureIterator<SimpleFeature> ite = col.features();
//
//            while (ite.hasNext()) {
//                SimpleFeature sf = ite.next();
//                geoms.add((Geometry) sf.getDefaultGeometry());
//            }
//            ite.close();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//        for (Geometry geo : geoms) {
//            geomsOut.add(FACILITIES_FACTORY.projectGeometry(geo, map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
//        //geomsOut.add(map2D.projectGeometry(geo, memoryMapContext.getCoordinateReferenceSystem(), map2D.getRenderingStrategy().getContext().getCoordinateReferenceSystem()));
//        }
//
//        clearMemoryLayer();
//        setMemoryLayerGeometry(geomsOut);

    }
}
