/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.gui.swing.debug;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 *
 * @author sorel
 */
public class WriteTest {

    public static void main(String[] args) throws MalformedURLException, IOException {

        File shape;
        DataStore dataStore;
        FeatureSource<SimpleFeatureType, SimpleFeature> fs;

        Map params = new HashMap<String, Object>();
        shape = new File("/home/sorel/temp/shapes/mer.shp");
        params.put("url", shape.toURI().toURL());
        dataStore = DataStoreFinder.getDataStore(params);
        fs = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);

        // (1)
        GeometryFactory geoFactory = new GeometryFactory();

        // (2)
        Coordinate c1 = new Coordinate( 20000000, -20000000);
        Coordinate c2 = new Coordinate(-20000000, -20000000);
        Coordinate c3 = new Coordinate(-20000000,  20000000);
        Coordinate c4 = new Coordinate( 20000000,  20000000);
        Coordinate c5 = new Coordinate( 20000000, -20000000);
        LinearRing ring = geoFactory.createLinearRing(new Coordinate[]{c1,c2,c3,c4,c5});
        Polygon poly = geoFactory.createPolygon(ring, new LinearRing[0]);

        SimpleFeatureType featureType = fs.getSchema();

        // (3)
        Object[] values = new Object[featureType.getAttributeCount()];

        AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
        List<AttributeDescriptor> attributes = featureType.getAttributeDescriptors();

        // (4)
        for (int i = 0, max = attributes.size(); i < max; i++) {
            AttributeDescriptor oneAttribut = attributes.get(i);

            // (5)
            if (oneAttribut.equals(geomAttribut)) {
                values[i] = poly;
            } else {
                values[i] = oneAttribut.getDefaultValue();
            }
        }

        // (6)
        SimpleFeature myFeature = SimpleFeatureBuilder.build(featureType, values, null);

        // (7)
        FeatureCollection lstFeatures = FeatureCollections.newCollection();
        lstFeatures.add(myFeature);

        // (8)
        if (fs instanceof FeatureStore) {
            FeatureStore store = (FeatureStore) fs;

            DefaultTransaction transaction = new DefaultTransaction();
            store.setTransaction(transaction);

            // (9)
            try {
                store.addFeatures(lstFeatures);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    store.getTransaction().rollback();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                transaction.close();
            }

        }




    }
}
