/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.DefaultTransaction;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureCollectionUtilities;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.AffineMatrix3;
import org.geotoolkit.util.logging.Logging;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public class EditionHelper {

    private static final Logger LOGGER = Logging.getLogger(DefaultEditionDecoration.class);
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
                                                new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final Coordinate[] EMPTY_COORDINATE_ARRAY = new Coordinate[0];

    private final DefaultEditionHandler handler;

    EditionHelper(DefaultEditionHandler handler) {
        this.handler = handler;
    }

    /**
     * transform a mouse coordinate in JTS Geometry using the CRS of the mapcontext
     * @param mx : x coordinate of the mouse on the map (in pixel)
     * @param my : y coordinate of the mouse on the map (in pixel)
     * @return JTS geometry (corresponding to a square of 6x6 pixel around mouse coordinate)
     */
    public Polygon mousePositionToGeometry(int mx, int my) throws NoninvertibleTransformException {
        Coordinate[] coord = new Coordinate[5];
        int taille = 4;

        coord[0] = toCoord(mx - taille, my - taille);
        coord[1] = toCoord(mx - taille, my + taille);
        coord[2] = toCoord(mx + taille, my + taille);
        coord[3] = toCoord(mx + taille, my - taille);
        coord[4] = coord[0];

        LinearRing lr1 = GEOMETRY_FACTORY.createLinearRing(coord);
        return GEOMETRY_FACTORY.createPolygon(lr1, null);
    }

    public Point toJTS(int x, int y){
        Coordinate coord = toCoord(x, y);
        Point geom = GEOMETRY_FACTORY.createPoint(coord);
        return geom;
    }

    public Coordinate toCoord(int x, int y){
        AffineMatrix3 trs = handler.getMap().getCanvas().getController().getTransform();
        AffineTransform dispToObj;
        try {
            dispToObj = trs.createInverse();
        } catch (NoninvertibleTransformException ex) {
            dispToObj = new AffineTransform();
            LOGGER.log(Level.SEVERE, null, ex);
        }
        double[] crds = new double[]{x,y};
        dispToObj.transform(crds, 0, crds, 0, 1);
        return new Coordinate(crds[0], crds[1]);
    }

    public SimpleFeature grabFeature(int mx, int my) {

        final FeatureMapLayer layer = handler.getEditedLayer();

        if(layer == null) return null;

        try {
            final Polygon geo = mousePositionToGeometry(mx, my);
            final Filter flt = toFilter(geo, layer);
            final FeatureCollection<SimpleFeatureType, SimpleFeature> editgeoms = layer.getFeatureSource().getFeatures(flt);

            if (editgeoms != null) {
                FeatureIterator<SimpleFeature> fi = editgeoms.features();
                if (fi.hasNext()) {
                    SimpleFeature sf = fi.next();
                    return sf;
                }
                fi.close();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return null;
    }


    public Geometry toObjectiveCRS(SimpleFeature sf){
        final FeatureMapLayer layer = handler.getEditedLayer();
        final Object obj = sf.getDefaultGeometry();

        if (obj instanceof Geometry) {
            try{
                Geometry geom = (Geometry) obj;

                MathTransform trs = CRS.findMathTransform(
                        layer.getFeatureSource().getSchema().getCoordinateReferenceSystem(),
                        handler.getMap().getCanvas().getObjectiveCRS(),
                        true);

                geom = JTS.transform(geom, trs);
                return geom;
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     * @param geom : in canvas objective CRS
     * @param layer : target layer filter
     * @return geometry filter
     */
    public Filter toFilter(Geometry poly, FeatureMapLayer fl) throws FactoryException, MismatchedDimensionException, TransformException{

        final String geoStr = fl.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
        final Expression geomField = FF.property(geoStr);

        final CoordinateReferenceSystem dataCrs = fl.getFeatureSource().getSchema().getCoordinateReferenceSystem();

        final Geometry dataPoly = JTS.transform(poly, CRS.findMathTransform(handler.getMap().getCanvas().getObjectiveCRS(), dataCrs,true));

        final Expression geomData = FF.literal(dataPoly);
        final Filter f = FF.intersects(geomField, geomData);

        return f;
    }

    
    //manipulating the feature source, transaction -----------------------------

    public void sourceAddGeometry(Geometry ... geoms) {

        final FeatureMapLayer editionLayer = handler.getEditedLayer();
        final Map2D map = handler.getMap();

        if (editionLayer != null) {

            for (Geometry geom : geoms) {
                SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
                FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollectionUtilities.createCollection();
                Object[] values = new Object[featureType.getAttributeCount()];

                AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();

                final CoordinateReferenceSystem dataCrs = editionLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();

                try {
                    geom = JTS.transform(geom, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs, true));
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }

                List<AttributeDescriptor> lst = featureType.getAttributeDescriptors();
                for (int i = 0,  n = lst.size(); i < n; i++) {
                    AttributeDescriptor desc = lst.get(i);

                    if (desc.equals(geomAttribut)) {
                        values[i] = geom;
                    } else {
                        values[i] = desc.getDefaultValue();
                    }
                }

                SimpleFeature sf = SimpleFeatureBuilder.build(featureType, values, null);
                collection.add(sf);

                DefaultTransaction transaction = null;
                Transaction oldTransaction = null;
                FeatureStore<SimpleFeatureType, SimpleFeature> store = null;
                try {
//                    String featureName = data.getTypeNames()[0]; // there is only one in a shapefile

                    // Create the DefaultTransaction Object
                    transaction = new DefaultTransaction();

//                    String name = editionLayer.getFeatureSource().getName().getLocalPart();
//                    try {
//                        //GR: question: why not just editionLayer.getFeatureSource()?
//                        FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
//                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
//                    } catch (IOException e) {
//                        // Tell it the name of the shapefile it should look for in our DataStore
//                        store = (FeatureStore<SimpleFeatureType, SimpleFeature>) data.getFeatureSource(featureName);
//                    }

                    store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();

                    // Then set the transaction for that FeatureStore
                    oldTransaction = store.getTransaction();
                    store.setTransaction(transaction);

                    store.addFeatures(collection);
                    transaction.commit();
                } catch (Exception eek) {
                    eek.printStackTrace();
                    try {
                        store.getTransaction().rollback();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } finally {
                    transaction.close();
                    store.setTransaction(oldTransaction);
                }


            }

            map.getCanvas().getController().repaint();
        }

    }

    public void sourceModifyFeature(SimpleFeature feature, Geometry geo){

        final String ID = feature.getID();

        if (geo == null || ID == null) {
            throw new NullPointerException();
        }

        final FeatureMapLayer editionLayer = handler.getEditedLayer();
        final Map2D map = handler.getMap();

        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {

//            String name = editionLayer.getFeatureSource().getName().getLocalPart();
//            try {
//                //GR question: why not just editionLayer.getFeatureSource()?
//                FeatureSource<SimpleFeatureType, SimpleFeature> source = ((DataStore) editionLayer.getFeatureSource().getDataStore()).getFeatureSource(name);
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) source;
//            } catch (IOException e) {
//                store = (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//            }

            final FeatureStore<SimpleFeatureType, SimpleFeature> store =
                    (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();
//                    store.getDataStore().dispose();

            DefaultTransaction transaction = new DefaultTransaction("trans_maj");
            Transaction previoustransaction = store.getTransaction();

            store.setTransaction(transaction);
            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));

            SimpleFeatureType featureType = (SimpleFeatureType) editionLayer.getFeatureSource().getSchema();
            AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();

            final CoordinateReferenceSystem dataCrs = store.getSchema().getCoordinateReferenceSystem();

            try {
                final Geometry geom = JTS.transform(geo, CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs,true));
                store.updateFeatures(geomAttribut, geom, filter);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                transaction.close();
                store.setTransaction(Transaction.AUTO_COMMIT);
            }

            map.getCanvas().getController().repaint();
        }

    }

    public void sourceRemoveFeature(SimpleFeature feature){
        sourceRemoveFeature(feature.getID());
    }

    public void sourceRemoveFeature(final String ID) {

        if (ID == null) {
            throw new NullPointerException();
        }

        final FeatureMapLayer editionLayer = handler.getEditedLayer();
        final Map2D map = handler.getMap();

        if (editionLayer != null && editionLayer.getFeatureSource() instanceof FeatureStore) {
            final FeatureStore<SimpleFeatureType, SimpleFeature> store =
                    (FeatureStore<SimpleFeatureType, SimpleFeature>) editionLayer.getFeatureSource();

            DefaultTransaction transaction = new DefaultTransaction("trans_maj");

            store.setTransaction(transaction);
            Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));

            try {
                store.removeFeatures(filter);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    transaction.rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                transaction.close();
                store.setTransaction(Transaction.AUTO_COMMIT);
            }

            map.getCanvas().getController().repaint();
        }

    }


    //staic helper methods -----------------------------------------------------

    public static Point createPoint(Coordinate coord) {
        return GEOMETRY_FACTORY.createPoint(coord);
    }

    public static MultiPoint createMultiPoint(List<Geometry> geoms) {
        List<Point> lst = new ArrayList<Point>();
        for (Geometry go : geoms) {
            if (go instanceof Point) {
                lst.add((Point) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiPoint(lst.toArray(new Point[lst.size()]));
    }

    public static LineString createLine(List<Coordinate> coords) {
        return GEOMETRY_FACTORY.createLineString(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    public static LinearRing createLinearRing(List<Coordinate> coords) {
        if (!(coords.get(0).equals2D(coords.get(coords.size() - 1)))) {
            Coordinate coo = new Coordinate(coords.get(0));
            coords.add(coo);
        }

        return GEOMETRY_FACTORY.createLinearRing(coords.toArray(EMPTY_COORDINATE_ARRAY));
    }

    public static Polygon createPolygon(List<Coordinate> coords) {
        LinearRing ring = createLinearRing(coords);
        return GEOMETRY_FACTORY.createPolygon(ring, null);
    }

    public static MultiPolygon createMultiPolygon(List<Geometry> geoms) {
        List<Polygon> lst = new ArrayList<Polygon>();
        for (Geometry go : geoms) {
            if (go instanceof Polygon) {
                lst.add((Polygon) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiPolygon(lst.toArray(new Polygon[lst.size()]));
    }

    public static MultiLineString createMultiLine(List<Geometry> geoms) {
        List<LineString> lst = new ArrayList<LineString>();
        for (Geometry go : geoms) {
            if (go instanceof LineString) {
                lst.add((LineString) go);
            }
        }
        return GEOMETRY_FACTORY.createMultiLineString(lst.toArray(new LineString[lst.size()]));
    }


}
