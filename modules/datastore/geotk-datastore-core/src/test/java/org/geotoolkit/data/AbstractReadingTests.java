/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.data;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * Generic reading tests for datastore.
 * Tests schemas names and readers with queries.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractReadingTests{

    protected static final double DELTA = 0.000000001d;
    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static class ExpectedResult{

        public ExpectedResult(final Name name, final FeatureType type, final int size, final Envelope env){
            this.name = name;
            this.type = type;
            this.size = size;
            this.env = env;
        }

        public Name name;
        public FeatureType type;
        public int size;
        public Envelope env;
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    protected abstract DataStore getDataStore();

    protected abstract Set<Name> getExpectedNames();

    protected abstract List<ExpectedResult> getReaderTests();

    @Test
    public void testDataStore(){
        final DataStore store = getDataStore();
        assertNotNull(store);
    }

    /**
     * test session creation.
     */
    @Test
    public void testSession(){
        final DataStore store = getDataStore();

        Session async_session = store.createSession(true);
        Session sync_session = store.createSession(false);

        assertNotNull(async_session);
        assertTrue(async_session.isAsynchrone());
        assertNotNull(sync_session);
        assertFalse(sync_session.isAsynchrone());
    }

    /**
     * test schema names
     */
    @Test
    public void testSchemas() throws Exception{
        final DataStore store = getDataStore();
        final Set<Name> expectedTypes = getExpectedNames();

        //need at least one type to test
        assertTrue(expectedTypes.size() > 0);

        //check names-----------------------------------------------------------
        final Set<Name> founds = store.getNames();
        assertNotNull(founds);
        assertTrue(expectedTypes.size() == founds.size());
        assertTrue(expectedTypes.containsAll(founds));
        assertTrue(founds.containsAll(expectedTypes));

        for(Name name : founds){
            assertNotNull(name);
            assertNotNull(store.getFeatureType(name));
        }

        //check type names------------------------------------------------------
        final String[] typeNames = store.getTypeNames();

        assertNotNull(typeNames);
        assertTrue(typeNames.length == founds.size());

        check:
        for(String typeName : typeNames){
            for(Name n : founds){
                if(n.getLocalPart().equals(typeName)){
                    assertNotNull(typeName);
                    FeatureType type1 = store.getFeatureType(typeName);
                    FeatureType type2 = store.getFeatureType(n);
                    assertNotNull(type1);
                    assertNotNull(type2);
                    assertTrue(type1.equals(type2));
                    continue check;
                }
            }
            throw new Exception("A typename has no matching Name, datastore is not consistent.");
        }

        //check error on wrong type names---------------------------------------
        try{
            store.getFeatureType(new DefaultName("http://not", "exist"));
            throw new Exception("Asking for a schema that doesnt exist should have raised a datastore exception.");
        }catch(DataStoreException ex){
            //ok
        }

        try{
            store.getFeatureType("not-exist_stuff");
            throw new Exception("Asking for a schema that doesnt exist should have raised a datastore exception.");
        }catch(DataStoreException ex){
            //ok
        }

    }

    /**
     * test feature reader.
     */
    @Test
    public void testReader() throws Exception{
        final DataStore store = getDataStore();
        final List<ExpectedResult> candidates = getReaderTests();

        //need at least one type to test
        assertTrue(candidates.size() > 0);

        for(final ExpectedResult candidate : candidates){
            final Name name = candidate.name;
            final FeatureType type = store.getFeatureType(name);
            assertNotNull(type);
            assertEquals(candidate.type, type);

            testCounts(store, candidate);
            testReaders(store, candidate);
            testBounds(store, candidate);
        }

    }

    /**
     * test different count with filters.
     */
    private void testCounts(final DataStore store, final ExpectedResult candidate) throws Exception{

        assertEquals(candidate.size, store.getCount(QueryBuilder.all(candidate.name)));
        
        //todo make more generic count tests
    }

    /**
     * test different bounds with filters.
     */
    private void testBounds(final DataStore store, final ExpectedResult candidate) throws Exception{
        Envelope res = store.getEnvelope(QueryBuilder.all(candidate.name));

        if(candidate.env == null){
            //looks like we are testing a geometryless feature
            assertNull(res);
            return;
        }


        assertNotNull(res);

        assertEquals(res.getMinimum(0), candidate.env.getMinimum(0), DELTA);
        assertEquals(res.getMinimum(1), candidate.env.getMinimum(1), DELTA);
        assertEquals(res.getMaximum(0), candidate.env.getMaximum(0), DELTA);
        assertEquals(res.getMaximum(1), candidate.env.getMaximum(1), DELTA);

        //todo make generic bounds tests
    }

    /**
     * test different readers.
     */
    private void testReaders(final DataStore store, final ExpectedResult candidate) throws Exception{
        final FeatureType type = store.getFeatureType(candidate.name);
        final Collection<PropertyDescriptor> properties = type.getDescriptors();
        final QueryBuilder qb = new QueryBuilder();
        Query query = null;

        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader without any query whould raise an error.");
        }catch(Exception ex){
            //ok
        }

        query = QueryBuilder.all(new DefaultName(candidate.name.getNamespaceURI(), candidate.name.getLocalPart()+"fgresfds_not_exist"));
        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader without a wrong name should raise a datastore exception.");
        }catch(DataStoreException ex){
            //ok
        }

        query = QueryBuilder.all(new DefaultName(candidate.name.getNamespaceURI()+"resfsdfsdf_not_exist", candidate.name.getLocalPart()));
        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader without a wrong namespace should raise a datastore exception.");
        }catch(DataStoreException ex){
            //ok
        }
        

        //crs ------------------------------------------------------------------
        if(type.getGeometryDescriptor() != null){
            final GeometryDescriptor desc = type.getGeometryDescriptor();
            final CoordinateReferenceSystem originalCRS = desc.getCoordinateReferenceSystem();
            CoordinateReferenceSystem testCRS = CRS.decode("EPSG:3395");
            if(CRS.equalsIgnoreMetadata(originalCRS, testCRS)){
                //change the test crs
                testCRS = CRS.decode("EPSG:4326");
            }

            //handle geometry as object since they can be ISO or JTS
            final Map<FeatureId,Object> inOriginal = new HashMap<FeatureId, Object>();
            FeatureReader ite = store.getFeatureReader(QueryBuilder.all(candidate.name));
            try{
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    final FeatureId id = f.getIdentifier();
                    final Object geom = f.getDefaultGeometryProperty().getValue();
                    assertNotNull(id);
                    assertNotNull(geom);
                    inOriginal.put(id,geom);
                }
            }finally{
                ite.close();
            }

            //check that geometries are different in another projection
            //it's not a exact check but it's better than nothing
            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setCRS(testCRS);
            ite = store.getFeatureReader(qb.buildQuery());
            try{
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    final FeatureId id = f.getIdentifier();
                    final Object original = inOriginal.get(id);
                    final Object reprojected = f.getDefaultGeometryProperty().getValue();
                    assertNotNull(id);
                    assertNotNull(original);
                    assertNotNull(reprojected);
                    assertNotSame(original, reprojected);
                }
            }finally{
                ite.close();
            }

        }

        //property -------------------------------------------------------------
        
        //check only id query
        query = QueryBuilder.fids(candidate.name);
        FeatureReader ite = store.getFeatureReader(query);
        FeatureType limited = ite.getFeatureType();
        assertNotNull(limited);
        assertTrue(limited.getDescriptors().size() == 0);
        try{
            while(ite.hasNext()){
                final Feature f = ite.next();
                assertTrue(f.getProperties().size() == 0);
                assertNotNull(f.getIdentifier());
            }
        }finally{
            ite.close();
        }

        for(final PropertyDescriptor desc : properties){
            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setProperties(new String[]{desc.getName().getLocalPart()});
            query = qb.buildQuery();

            ite = store.getFeatureReader(query);
            limited = ite.getFeatureType();
            assertNotNull(limited);
            assertTrue(limited.getDescriptors().size() == 1);
            assertNotNull(limited.getDescriptor(desc.getName()));
            try{
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    assertTrue(f.getProperties().size() == 1);
                    assertNotNull(f.getProperty(desc.getName()));
                }
            }finally{
                ite.close();
            }

        }


        //sort by --------------------------------------------------------------
        for(final PropertyDescriptor desc : properties){
            final Class clazz = desc.getType().getBinding();

            if(!Comparable.class.isAssignableFrom(clazz) || Geometry.class.isAssignableFrom(clazz)){
                //can not make a sort by on this attribut.
                continue;
            }

            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setSortBy(new SortBy[]{FF.sort(desc.getName().getLocalPart(), SortOrder.ASCENDING)});
            query = qb.buildQuery();

            //count should not change with a sort by
            assertEquals(candidate.size, store.getCount(query));

            FeatureReader reader = store.getFeatureReader(query);
            try{
                Comparable last = null;
                while(reader.hasNext()){
                    final Feature f = reader.next();
                    final Comparable current = (Comparable) f.getProperty(desc.getName()).getValue();

                    if(current != null){
                        if(last != null){
                            //check we have the correct order.
                            assertTrue( current.compareTo(last) >= 0 );
                        }
                        last = current;
                    }else{
                        //any restriction about where should be placed the feature with null values ? before ? after ?
                    }

                }
            }finally{
                reader.close();
            }

            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setSortBy(new SortBy[]{FF.sort(desc.getName().getLocalPart(), SortOrder.DESCENDING)});
            query = qb.buildQuery();

            //count should not change with a sort by
            assertEquals(candidate.size, store.getCount(query));

            reader = store.getFeatureReader(query);
            try{
                Comparable last = null;
                while(reader.hasNext()){
                    final Feature f = reader.next();
                    final Comparable current = (Comparable) f.getProperty(desc.getName()).getValue();

                    if(current != null){
                        if(last != null){
                            //check we have the correct order.
                            assertTrue( current.compareTo(last) <= 0 );
                        }
                        last = current;
                    }else{
                        //any restriction about where should be placed the feature with null values ? before ? after ?
                    }

                }
            }finally{
                reader.close();
            }

        }

        //start ----------------------------------------------------------------
        if(candidate.size > 1){
            qb.reset();
            qb.setTypeName(candidate.name);
            query = qb.buildQuery();

            List<FeatureId> ids = new ArrayList<FeatureId>();
            ite = store.getFeatureReader(query);
            try{
                while(ite.hasNext()){
                    ids.add(ite.next().getIdentifier());
                }
            }finally{
                ite.close();
            }

            qb.reset();
            qb.setTypeName(candidate.name);
            //skip the first element
            qb.setStartIndex(1);
            query = qb.buildQuery();

            ite = store.getFeatureReader(query);
            try{
                int i = 1;
                while(ite.hasNext()){
                    assertEquals(ite.next().getIdentifier(), ids.get(i));
                    i++;
                }
            }finally{
                ite.close();
            }
        }


        //max ------------------------------------------------------------------
        if(candidate.size > 1){
            qb.reset();
            qb.setTypeName(candidate.name);
            //skip the first element
            qb.setMaxFeatures(1);
            query = qb.buildQuery();

            int i = 0;
            ite = store.getFeatureReader(query);
            try{
                while(ite.hasNext()){
                    ite.next();
                    i++;
                }
            }finally{
                ite.close();
            }

            assertEquals(1, i);
        }
        
        //filter ---------------------------------------------------------------
        //filters are tested more deeply in the filter module
        //we just make a few tests here for sanity check
        //todo should we make more deep tests ?


        Set<FeatureId> ids = new HashSet<FeatureId>();
        ite = store.getFeatureReader(QueryBuilder.fids(candidate.name));
        try{
            //peek only one on two ids
            boolean oneOnTwo = true;
            while(ite.hasNext()){
                final Feature feature = ite.next();
                if(oneOnTwo){
                    ids.add(feature.getIdentifier());
                }
                oneOnTwo = !oneOnTwo;
            }
        }finally{
            ite.close();
        }


        Set<FeatureId> remaining = new HashSet<FeatureId>(ids);
        ite = store.getFeatureReader(QueryBuilder.filtered(candidate.name,FF.id(ids)));

        try{
            while(ite.hasNext()){
                remaining.remove(ite.next().getIdentifier());
            }
        }finally{
            ite.close();
        }

         assertTrue(remaining.isEmpty() );

    }

}
