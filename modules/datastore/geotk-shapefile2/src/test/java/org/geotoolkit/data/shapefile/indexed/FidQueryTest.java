/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.data.DefaultFeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Map.Entry;
import org.geotoolkit.data.DataUtilities;

public class FidQueryTest extends FIDTestCase {
    public  FidQueryTest(  ) throws IOException {
        super("FidQueryTest");
    }

    private IndexedShapefileDataStore ds;

    private static final FilterFactory2 fac = (FilterFactory2) FactoryFinder.getFilterFactory(null);
    Map<String, SimpleFeature> fids = new HashMap<String, SimpleFeature>();

    Name name;
    Session session;

    private int numFeatures;

    protected void setUp() throws Exception {

        super.setUp();

        URL url = backshp.toURI().toURL();
        ds = new IndexedShapefileDataStore(url, null, false, true, IndexType.QIX);
        numFeatures = 0;
        name = ds.getNames().iterator().next();
        session = ds.createSession(true);
        {
            FeatureIterator<SimpleFeature> features = ds.getFeatureReader(QueryBuilder.all(name));
            try {
                while (features.hasNext()) {
                    numFeatures++;
                    SimpleFeature feature = features.next();
                    fids.put(feature.getID(), feature);
                }
            } finally {
                if (features != null)
                    features.close();
            }
            assertEquals(numFeatures, fids.size());
        }

    }

    public void testGetByFID() throws Exception {
        assertFidsMatch();
    }

    public void testAddFeature() throws Exception {
        SimpleFeature feature = fids.values().iterator().next();
        SimpleFeatureType schema = (SimpleFeatureType) ds.getSchema(ds.getTypeNames()[0]);

        SimpleFeatureBuilder build = new SimpleFeatureBuilder(schema);
        GeometryFactory gf = new GeometryFactory();
        build.add(gf.createPoint((new Coordinate(0, 0))));
        build.add(new Long(0));
        build.add(new Long(0));
        build.add("Hey");
        SimpleFeature newFeature = build.buildFeature(null);
        FeatureCollection<SimpleFeature> collection = new DefaultFeatureCollection<SimpleFeature>("", null, SimpleFeature.class);
        collection.add(newFeature);

        List<FeatureId> newFids = DataUtilities.write(ds.getFeatureWriterAppend(name), collection);
        assertEquals(1, newFids.size());
        // this.assertFidsMatch();


        FeatureId id = newFids.iterator().next();
        Filter filter = fac.id(Collections.singleton(id));

        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(schema.getName());
        builder.setFilter(filter);
        Query query = builder.buildQuery();


        FeatureIterator<SimpleFeature> features = ds.getFeatureReader(query);
        try {
            feature = features.next();
            for (int i = 0; i < schema.getAttributeCount(); i++) {
                Object value = feature.getAttribute(i);
                Object newValue = newFeature.getAttribute(i);

                if (value instanceof Geometry) {
                    assertTrue(((Geometry) newValue).equals((Geometry) value));
                } else {
                    assertEquals(newValue, value);
                }
            }
            assertFalse(features.hasNext());
        } finally {
            if (features != null)
                features.close();
        }
    }

    public void testModifyFeature() throws Exception {
        final SimpleFeature feature = this.fids.values().iterator().next();
        final int newId = 237594123;

        final Id createFidFilter = fac.id(Collections.singleton(feature.getIdentifier()));

        final SimpleFeatureType schema = feature.getFeatureType();
        session.update(schema.getName(),createFidFilter,schema.getDescriptor("ID"), new Integer(newId));
        session.commit();

        FeatureIterator<SimpleFeature> features = ds.getFeatureReader(QueryBuilder.filtered(name, createFidFilter));

        try {
            assertFalse(feature.equals(features.next()));
        } finally {
            if (features != null) {
                features.close();
            }
        }
        feature.setAttribute("ID", new Integer(newId));
        this.assertFidsMatch();
    }

    public void testDeleteFeature() throws Exception {
        FeatureIterator<SimpleFeature> features = ds.getFeatureReader(QueryBuilder.all(name));
        SimpleFeature feature;
        try {
            feature = features.next();
        } finally {
            if (features != null)
                features.close();
        }

        Id createFidFilter = fac.id(Collections.singleton(feature.getIdentifier()));

        session.remove(name,createFidFilter);
        session.commit();
        fids.remove(feature.getID());

        assertEquals(fids.size(), ds.getCount(QueryBuilder.all(name)));

        features = ds.getFeatureReader(QueryBuilder.filtered(name, createFidFilter));
        try {
            assertFalse(features.hasNext());
        } finally {
            if (features != null)
                features.close();
        }

        this.assertFidsMatch();

    }

    public void testFIDBBoxQuery() throws Exception {
        FeatureIterator<SimpleFeature> features = ds.getFeatureReader(QueryBuilder.all(name));
        SimpleFeature feature;
        try {
            feature = features.next();
            feature = features.next();
            feature = features.next();
        } finally {
            if (features != null)
                features.close();
        }
        // FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        // BBoxExpression bb =
        // factory.createBBoxExpression(feature.getBounds());
        //
        // GeometryFilter bboxFilter =
        // factory.createGeometryFilter(FilterType.GEOMETRY_INTERSECTS);
        // bboxFilter.addRightGeometry(bb);
        //
        // String geom = ds.getSchema().getDefaultGeometry().getLocalName();
        //
        // bboxFilter.addLeftGeometry(factory.createAttributeExpression(geom));

        FilterFactory2 ff = (FilterFactory2) FactoryFinder.getFilterFactory(null);
        BBOX bbox = ff.bbox(ff.property(""), feature.getBounds());

        features = ds.getFeatureReader(QueryBuilder.filtered(name, bbox));

        try {
            while (features.hasNext()) {
                SimpleFeature newFeature = features.next();
                assertEquals(newFeature, fids.get(newFeature.getID()));
            }
        } finally {
            if (features != null)
                features.close();
        }
    }

    private void assertFidsMatch() throws IOException, DataStoreException {
        // long start = System.currentTimeMillis();
        Query query = QueryBuilder.all(name);

        int i = 0;

        for (Iterator<Entry<String,SimpleFeature>> iter = fids.entrySet().iterator(); iter.hasNext();) {
            i++;
            Entry<String,SimpleFeature> entry = iter.next();
            String fid = (String) entry.getKey();
            FeatureId id = fac.featureId(fid);
            Filter filter = fac.id(Collections.singleton(id));
            final QueryBuilder builder = new QueryBuilder(query);
            builder.setFilter(filter);
            query = builder.buildQuery();
            FeatureIterator<SimpleFeature> features = ds.getFeatureReader(query);
            try {
                SimpleFeature feature = features.next();
                assertFalse(features.hasNext());
                assertEquals(i + "th feature", entry.getValue(), feature);
            } finally {
                if (features != null)
                    features.close();
            }

        }
    }

}
