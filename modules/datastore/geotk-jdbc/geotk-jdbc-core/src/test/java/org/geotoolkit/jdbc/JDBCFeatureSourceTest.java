/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
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
package org.geotoolkit.jdbc;

import java.util.Iterator;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.store.ContentFeatureSource;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.And;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.spatial.BBOX;



public abstract class JDBCFeatureSourceTest extends JDBCTestSupport {
    ContentFeatureSource featureSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        featureSource = (JDBCFeatureStore) dataStore.getFeatureSource(tname("ft1"));
    }

    public void testSchema() throws Exception {
        SimpleFeatureType schema = featureSource.getSchema();
        assertEquals(tname("ft1"), schema.getTypeName());
        assertEquals(dataStore.getNamespaceURI(), schema.getName().getNamespaceURI());
        assertTrue(areCRSEqual(CRS.decode("EPSG:4326"), schema.getCoordinateReferenceSystem()));

        assertEquals(4, schema.getAttributeCount());
        assertNotNull(schema.getDescriptor(aname("geometry")));
        assertNotNull(schema.getDescriptor(aname("intProperty")));
        assertNotNull(schema.getDescriptor(aname("stringProperty")));
        assertNotNull(schema.getDescriptor(aname("doubleProperty")));
    }

    public void testBounds() throws Exception {
        JTSEnvelope2D bounds = featureSource.getBounds();
        assertEquals(0l, Math.round(bounds.getMinX()));
        assertEquals(0l, Math.round(bounds.getMinY()));
        assertEquals(2l, Math.round(bounds.getMaxX()));
        assertEquals(2l, Math.round(bounds.getMaxY()));

        assertTrue(areCRSEqual(CRS.decode("EPSG:4326"), bounds.getCoordinateReferenceSystem()));
    }

    public void testBoundsWithQuery() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo filter = ff.equals(ff.property(aname("stringProperty")), ff.literal("one"));

        Query query = QueryBuilder.filtered(featureSource.getSchema().getName(), filter);

        JTSEnvelope2D bounds = featureSource.getBounds(query);
        assertEquals(1l, Math.round(bounds.getMinX()));
        assertEquals(1l, Math.round(bounds.getMinY()));
        assertEquals(1l, Math.round(bounds.getMaxX()));
        assertEquals(1l, Math.round(bounds.getMaxY()));

        assertTrue(areCRSEqual(CRS.decode("EPSG:4326"), bounds.getCoordinateReferenceSystem()));
    }

    public void testCount() throws Exception {
        assertEquals(3, featureSource.getCount(QueryBuilder.all(featureSource.getName())));
    }

    public void testCountWithFilter() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo filter = ff.equals(ff.property(aname("stringProperty")), ff.literal("one"));

        Query query = QueryBuilder.filtered(featureSource.getSchema().getName(), filter);
        assertEquals(1, featureSource.getCount(query));
    }

    public void testGetFeatures() throws Exception {
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures();
        assertEquals(3, features.size());
    }

    public void testGetFeaturesWithFilter() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo filter = ff.equals(ff.property(aname("stringProperty")), ff.literal("one"));

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(filter);
        assertEquals(1, features.size());

        Iterator<SimpleFeature> iterator = features.iterator();
        assertTrue(iterator.hasNext());

        SimpleFeature feature = (SimpleFeature) iterator.next();
        assertEquals("one", feature.getAttribute(aname("stringProperty")));
        assertEquals( new Double(1.1), feature.getAttribute( aname("doubleProperty")) );
        features.close(iterator);
    }
    
    public void testGetFeaturesWithInvalidFilter() throws Exception {
        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        PropertyIsEqualTo f = ff.equals(ff.property("invalidAttribute"), ff.literal(5));

        // make sure a complaint related to the invalid filter is thrown here
        try { 
            FeatureIterator<SimpleFeature> fi = featureSource.getFeatures(f).features();
            fi.close();
            fail("This query should have failed, it contains an invalid filter");
        } catch(Exception e) {
            e.printStackTrace();
            // fine
        }
    }
    
    public void testGetFeaturesWithLogicFilter() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo property = ff.equals(ff.property(aname("stringProperty")), ff.literal("one"));
        BBOX bbox = ff.bbox(aname("geometry"), -20, -20, 20, 20, "EPSG:4326");
        And filter = ff.and(property, bbox);

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(filter);
        assertEquals(1, features.size());

        Iterator<SimpleFeature> iterator = features.iterator();
        assertTrue(iterator.hasNext());

        SimpleFeature feature = (SimpleFeature) iterator.next();
        assertEquals("one", feature.getAttribute(aname("stringProperty")));
        assertEquals( new Double(1.1), feature.getAttribute( aname("doubleProperty")) );
        features.close(iterator);
        
    }
    
    public void testCaseInsensitiveFilter() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo sensitive = ff.equal(ff.property(aname("stringProperty")), ff.literal("OnE"), true);
        PropertyIsEqualTo insensitive = ff.equal(ff.property(aname("stringProperty")), ff.literal("OnE"), false);
        assertEquals(0, featureSource.getCount(QueryBuilder.filtered(featureSource.getSchema().getName(), sensitive)));
        assertEquals(1, featureSource.getCount(QueryBuilder.filtered(featureSource.getSchema().getName(), insensitive)));
    }

    public void testGetFeaturesWithQuery() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        PropertyIsEqualTo filter = ff.equals(ff.property(aname("stringProperty")), ff.literal("one"));

        final QueryBuilder builder = new QueryBuilder();
        builder.setProperties(new String[] { aname("doubleProperty"), aname("intProperty") });
        builder.setFilter(filter);
        final Query query = builder.buildQuery();

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(query);
        assertEquals(1, features.size());

        Iterator<SimpleFeature> iterator = features.iterator();
        assertTrue(iterator.hasNext());

        SimpleFeature feature = (SimpleFeature) iterator.next();
        assertEquals(2, feature.getAttributeCount());

        assertEquals(new Double(1.1), feature.getAttribute(0));
        assertNotNull( feature.getAttribute(1));
        features.close(iterator);
    }
    
    public void testGetFeaturesWithInvalidQuery() {
        FilterFactory ff = FactoryFinder.getFilterFactory(null);
        PropertyIsEqualTo f = ff.equals(ff.property("invalidAttribute"), ff.literal(5));

        // make sure a complaint related to the invalid filter is thrown here
        try { 
            FeatureIterator<SimpleFeature> fi = featureSource.getFeatures(
                    QueryBuilder.filtered(new DefaultName("ft1"), f)).features();
            fi.close();
            fail("This query should have failed, it contains an invalid filter");
        } catch(Exception e) {
            e.printStackTrace();
            // fine
        }
    }

    public void testGetFeaturesWithSort() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        SortBy sort = ff.sort(aname("stringProperty"), SortOrder.ASCENDING);
        Query query = QueryBuilder.sorted(featureSource.getSchema().getName(), new SortBy[] { sort });

        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(query);
        assertEquals(3, features.size());

        Iterator<SimpleFeature> iterator = features.iterator();
        assertTrue(iterator.hasNext());

        SimpleFeature f = (SimpleFeature) iterator.next();
        assertEquals("one", f.getAttribute(aname("stringProperty")));

        assertTrue(iterator.hasNext());
        f = (SimpleFeature) iterator.next();
        assertEquals("two", f.getAttribute(aname("stringProperty")));

        assertTrue(iterator.hasNext());
        f = (SimpleFeature) iterator.next();
        assertEquals("zero", f.getAttribute(aname("stringProperty")));

        features.close(iterator);

        sort = ff.sort(aname("stringProperty"), SortOrder.DESCENDING);
        final QueryBuilder builder = new QueryBuilder(query);
        builder.setSortBy(new SortBy[] { sort });
        query = builder.buildQuery();
        features = featureSource.getFeatures(query);

        iterator = features.iterator();
        assertTrue(iterator.hasNext());

        f = (SimpleFeature) iterator.next();
        assertEquals("zero", f.getAttribute(aname("stringProperty")));

        assertTrue(iterator.hasNext());
        f = (SimpleFeature) iterator.next();
        assertEquals("two", f.getAttribute(aname("stringProperty")));

        assertTrue(iterator.hasNext());
        f = (SimpleFeature) iterator.next();
        assertEquals("one", f.getAttribute(aname("stringProperty")));
        features.close(iterator);
    }
    
    public void testGetFeaturesWithMax() throws Exception {
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(featureSource.getSchema().getName());
        builder.setMaxFeatures(2);
        Query q = builder.buildQuery();
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(q);
        
        // check size
        assertEquals(2, features.size());
        
        // check actual iteration
        Iterator<SimpleFeature> it = features.iterator();
        int count = 0;
        JTSEnvelope2D env = new JTSEnvelope2D(features.getSchema().getCoordinateReferenceSystem());
        while(it.hasNext()) {
            SimpleFeature f = it.next();
            env.expandToInclude(JTSEnvelope2D.reference(f.getBounds()));
            count++;
        }
        assertEquals(2, count);
        features.close(it);
        
        //assertEquals(env, features.getBounds());
        assertTrue(areReferencedEnvelopesEuqal(env, features.getBounds()));
    }
    
    public void testGetFeaturesWithOffset() throws Exception {
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(featureSource.getSchema().getName());
        builder.setSortBy(new SortBy[] {dataStore.getFilterFactory().sort(aname("intProperty"), SortOrder.ASCENDING)});
        builder.setStartIndex(2);
        Query q = builder.buildQuery();
                
                
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(q);
        
        // check size
        assertEquals(1, features.size());
        
        // check actual iteration
        Iterator<SimpleFeature> it = features.iterator();
        assertTrue(it.hasNext());
        SimpleFeature f = it.next();
        JTSEnvelope2D fe = JTSEnvelope2D.reference(f.getBounds());
        assertEquals(2, ((Number) f.getAttribute(aname("intProperty"))).intValue());
        assertFalse(it.hasNext());
        features.close(it);
        //assertEquals(fe, features.getBounds());
        assertTrue(areReferencedEnvelopesEuqal(fe, features.getBounds()));
    }
    
    public void testGetFeaturesWithOffsetLimit() throws Exception {
        final QueryBuilder builder = new QueryBuilder();
        builder.setTypeName(featureSource.getSchema().getName());
        builder.setStartIndex(1);
        builder.setMaxFeatures(1);
        Query q = builder.buildQuery();
        // no sorting, let's see if the database can use native one
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures(q);
        
        // check size
        assertEquals(1, features.size());
        
        // check actual iteration
        Iterator<SimpleFeature> it = features.iterator();
        assertTrue(it.hasNext());
        SimpleFeature f = it.next();
        JTSEnvelope2D fe = JTSEnvelope2D.reference(f.getBounds());
        assertEquals(1, ((Number) f.getAttribute(aname("intProperty"))).intValue());
        assertFalse(it.hasNext());
        features.close(it);
        //assertEquals(fe, features.getBounds());
        assertTrue(areReferencedEnvelopesEuqal(fe, features.getBounds()));
    }
    
    /**
     * Makes sure the datastore works when the renderer uses the typical rendering hints
     * @throws Exception
     */
    public void testRendererBehaviour() throws Exception {
        Query query = QueryBuilder.all(featureSource.getSchema().getName());
//        query.setHints(new Hints(HintsPending.JTS_COORDINATE_SEQUENCE_FACTORY, new LiteCoordinateSequenceFactory()));
        FeatureCollection fc = featureSource.getFeatures(query);
        FeatureIterator fi = fc.features();
        while(fi.hasNext()) {
            fi.next();
        }
        fi.close();
    }
    
    public void testQueryCapabilitiesSort() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();
        QueryCapabilities caps = featureSource.getQueryCapabilities();
        
        // check we advertise support for sorting on basic attributes 
        assertTrue(caps.supportsSorting(new SortBy[] {ff.sort(aname("intProperty"), SortOrder.ASCENDING)}));
        assertTrue(caps.supportsSorting(new SortBy[] {ff.sort(aname("stringProperty"), SortOrder.DESCENDING)}));
        assertTrue(caps.supportsSorting(new SortBy[] {ff.sort(aname("doubleProperty"), SortOrder.ASCENDING)}));
        
        // but we cannot sort geometries
        assertFalse(caps.supportsSorting(new SortBy[] {ff.sort(aname("geometry"), SortOrder.ASCENDING)}));
    }
    
    public void testQueryCapabilitiesReliableFid() throws Exception {
        QueryCapabilities caps = featureSource.getQueryCapabilities();
        // we have a primary key, right?
        assertTrue(caps.isReliableFIDSupported());
    }
    
    public void testNaturalSortingAsc() throws Exception {
        Query q = QueryBuilder.sorted(featureSource.getSchema().getName(),new SortBy[] {SortBy.NATURAL_ORDER});
        FeatureIterator<SimpleFeature> features = featureSource.getFeatures(q).features();
        String prevId = null;
        while(features.hasNext()) {
            String currId = features.next().getID();
            if(prevId != null)
                assertTrue(prevId.compareTo(currId) <= 0);
            prevId = currId;
        }
        features.close();
    }
    
    public void testNaturalSortingdesc() throws Exception {
        Query q = QueryBuilder.sorted(featureSource.getSchema().getName(),new SortBy[] {SortBy.REVERSE_ORDER});
        FeatureIterator<SimpleFeature> features = featureSource.getFeatures(q).features();
        String prevId = null;
        while(features.hasNext()) {
            String currId = features.next().getID();
            if(prevId != null)
                assertTrue(prevId.compareTo(currId) >= 0);
            prevId = currId;
        }
        features.close();
    }
}
