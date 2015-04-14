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

import org.junit.Test;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureBuilder;

import org.geotoolkit.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.spatial.BBOX;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.PropertyDescriptor;

import static org.junit.Assert.*;
import org.junit.Ignore;

public class FidQueryTest extends FIDTestCase {
    

    private IndexedShapefileFeatureStore ds;

    private static final FilterFactory2 fac = (FilterFactory2) FactoryFinder.getFilterFactory(null);
    private Map<String, Feature> fids = new HashMap<>();

    private Name name;
    private Session session;

    private int numFeatures;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final URL url = backshp.toURI().toURL();
        ds = new IndexedShapefileFeatureStore(url, null, false, true, IndexType.QIX,null);
        numFeatures = 0;
        name = ds.getNames().iterator().next();
        session = ds.createSession(true);

        final FeatureIterator features = ds.getFeatureReader(QueryBuilder.all(name));
        try {
            while (features.hasNext()) {
                numFeatures++;
                final Feature feature = features.next();
                fids.put(feature.getIdentifier().getID(), feature);
            }
        } finally {
            if (features != null)
                features.close();
        }
        assertEquals(numFeatures, fids.size());

    }

    @Test
    public void testGetByFID() throws Exception {
        assertFidsMatch();
    }

    @Test
    public void testAddFeature() throws Exception {
        Feature feature = fids.values().iterator().next();
        final FeatureType schema = ds.getFeatureType(ds.getTypeNames()[0]);

        final FeatureBuilder build = new FeatureBuilder(schema);
        final GeometryFactory gf = new GeometryFactory();
        build.add(gf.createPoint((new Coordinate(0, 0))));
        build.add(new Long(0));
        build.add(new Long(0));
        build.add("Hey");
        final Feature newFeature = build.buildFeature(null);
        final Collection<Feature> collection = new ArrayList<>();
        collection.add(newFeature);

        final List<FeatureId> newFids = FeatureStoreUtilities.write(ds.getFeatureWriterAppend(name), collection);
        assertEquals(1, newFids.size());
        // this.assertFidsMatch();


        final FeatureId id = newFids.iterator().next();
        final Filter filter = fac.id(Collections.singleton(id));

        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(schema.getName());
        builder.setFilter(filter);
        final Query query = builder.buildQuery();


        final FeatureIterator features = ds.getFeatureReader(query);
        try {
            feature = features.next();
            for(PropertyDescriptor desc : schema.getDescriptors()){
                final Object value = feature.getPropertyValue(desc.getName().getLocalPart());
                final Object newValue = newFeature.getPropertyValue(desc.getName().getLocalPart());

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

    @Test
    public void testModifyFeature() throws Exception {
        final Feature feature = this.fids.values().iterator().next();
        final int newId = 237594123;

        final Id createFidFilter = fac.id(Collections.singleton(feature.getIdentifier()));

        final FeatureType schema = feature.getType();
        session.updateFeatures(schema.getName(),createFidFilter, (AttributeDescriptor) schema.getDescriptor("ID"), new Integer(newId));
        session.commit();

        final FeatureIterator features = ds.getFeatureReader(QueryBuilder.filtered(name, createFidFilter));

        try {
            assertFalse(feature.equals(features.next()));
        } finally {
            if (features != null) {
                features.close();
            }
        }
        feature.setPropertyValue("ID", new Long(newId));
        this.assertFidsMatch();
    }

    @Test
    public void testDeleteFeature() throws Exception {
        FeatureIterator features = ds.getFeatureReader(QueryBuilder.all(name));
        Feature feature;
        try {
            feature = features.next();
        } finally {
            if (features != null)
                features.close();
        }

        final Id createFidFilter = fac.id(Collections.singleton(feature.getIdentifier()));

        session.removeFeatures(name,createFidFilter);
        session.commit();
        fids.remove(feature.getIdentifier().getID());

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

    @Test
    public void testFIDBBoxQuery() throws Exception {
        FeatureIterator features = ds.getFeatureReader(QueryBuilder.all(name));
        Feature feature;
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
                Feature newFeature = features.next();
                assertEquals(newFeature, fids.get(newFeature.getIdentifier().getID()));
            }
        } finally {
            if (features != null)
                features.close();
        }
    }

    private void assertFidsMatch() throws IOException, DataStoreException {
        int i = 0;

        for (Iterator<Entry<String,Feature>> iter = fids.entrySet().iterator(); iter.hasNext();) {
            i++;
            final Entry<String,Feature> entry = iter.next();
            final String fid = (String) entry.getKey();
            final FeatureId id = fac.featureId(fid);
            final Filter filter = fac.id(Collections.singleton(id));
            final Query query = QueryBuilder.filtered(name, filter);
            final FeatureIterator features = ds.getFeatureReader(query);
            try {
                final Feature feature = features.next();
                assertFalse(features.hasNext());
                assertEquals(i + "th feature", entry.getValue(), feature);
            } finally {
                if (features != null)
                    features.close();
            }
        }
    }

}
