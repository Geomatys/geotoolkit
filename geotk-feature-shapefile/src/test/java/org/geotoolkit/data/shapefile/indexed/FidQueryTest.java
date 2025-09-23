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
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.FeatureWriter;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.session.Session;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.ResourceId;
import org.opengis.filter.SpatialOperator;
import org.opengis.geometry.Envelope;
import org.opengis.util.GenericName;

public class FidQueryTest extends FIDTestCase {


    private IndexedShapefileFeatureStore ds;

    private static final FilterFactory fac = FilterUtilities.FF;
    private Map<String, Feature> fids = new HashMap<>();

    private GenericName name;
    private Session session;

    private int numFeatures;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        final URI uri = backshp.toURI();
        ds = new IndexedShapefileFeatureStore(uri, false, true, IndexType.QIX,null);
        numFeatures = 0;
        name = ds.getNames().iterator().next();
        session = ds.createSession(true);

        final FeatureIterator features = ds.getFeatureReader(new Query(name));
        try {
            while (features.hasNext()) {
                numFeatures++;
                final Feature feature = features.next();
                fids.put(FeatureExt.getId(feature).getIdentifier(), feature);
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
    @Ignore
    public void testAddFeature() throws Exception {
        final FeatureType schema = ds.getFeatureType(ds.getName().toString());

        final Feature newFeature = schema.newInstance();
        final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();
        newFeature.setPropertyValue("a",gf.createPoint((new Coordinate(0, 0))));
        newFeature.setPropertyValue("b",new Long(0));
        newFeature.setPropertyValue("c",new Long(0));
        newFeature.setPropertyValue("d","Hey");

        final Collection<Feature> collection = new ArrayList<>();
        collection.add(newFeature);

        final List<ResourceId> newFids;
        try(FeatureWriter writer = ds.getFeatureWriter(Query.filtered(name.toString(), Filter.exclude()))){
            newFids = FeatureStoreUtilities.write(writer, collection);
        }
        assertEquals(1, newFids.size());
        // this.assertFidsMatch();


        final Filter filter = newFids.iterator().next();

        final Query query = new Query();
        query.setTypeName(schema.getName());
        query.setSelection(filter);

        final FeatureIterator features = ds.getFeatureReader(query);
        try {
            final Feature feature = features.next();
            for(PropertyType desc : schema.getProperties(true)){
                final Object value = feature.getPropertyValue(desc.getName().toString());
                final Object newValue = newFeature.getPropertyValue(desc.getName().toString());

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
        final long newId = 237594123;

        final ResourceId createFidFilter = FeatureExt.getId(feature);

        final FeatureType schema = feature.getType();
        session.updateFeatures(schema.getName().toString(),createFidFilter, Collections.singletonMap("ID", newId));
        session.commit();

        final FeatureIterator features = ds.getFeatureReader(Query.filtered(name.toString(), createFidFilter));

        try {
            assertFalse(feature.equals(features.next()));
        } finally {
            if (features != null) {
                features.close();
            }
        }
        feature.setPropertyValue("ID", newId);
        this.assertFidsMatch();
    }

    @Test
    public void testDeleteFeature() throws Exception {
        FeatureIterator features = ds.getFeatureReader(new Query(name));
        Feature feature;
        try {
            feature = features.next();
        } finally {
            if (features != null)
                features.close();
        }

        final ResourceId createFidFilter = FeatureExt.getId(feature);

        session.removeFeatures(name.toString(),createFidFilter);
        session.commit();
        fids.remove(FeatureExt.getId(feature).getIdentifier());

        assertEquals(fids.size(), ds.getCount(new Query(name)));

        features = ds.getFeatureReader(Query.filtered(name.toString(), createFidFilter));
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
        FeatureIterator features = ds.getFeatureReader(new Query(name));
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
        // String geom = ds.getSchema().getDefaultGeometryValue().getLocalName();
        //
        // bboxFilter.addLeftGeometry(factory.createAttributeExpression(geom));
        GeneralEnvelope envelope = new GeneralEnvelope(FeatureExt.getEnvelope(feature));
        envelope.setRange(0, envelope.getMinimum(0)-1, envelope.getMinimum(0)+1);
        envelope.setRange(1, envelope.getMinimum(1)-1, envelope.getMinimum(1)+1);

        SpatialOperator bbox = fac.bbox(fac.property(AttributeConvention.GEOMETRY), envelope);
        features = ds.getFeatureReader(Query.filtered(name.toString(), bbox));
        try {
            while (features.hasNext()) {
                Feature newFeature = features.next();
                assertEquals(newFeature, fids.get(FeatureExt.getId(newFeature).getIdentifier()));
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
            final Filter filter = fac.resourceId(fid);
            final Query query = Query.filtered(name.toString(), filter);
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
