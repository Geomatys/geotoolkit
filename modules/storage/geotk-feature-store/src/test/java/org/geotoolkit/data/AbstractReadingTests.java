/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2015, Geomatys
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

import org.locationtech.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.internal.system.DefaultFactories;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.util.NamesExt;
import org.apache.sis.referencing.CRS;
import org.junit.Test;
import org.opengis.util.GenericName;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import static org.junit.Assert.*;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.Utilities;
import org.opengis.metadata.Identifier;
import org.opengis.feature.Operation;

/**
 * Generic reading tests for datastore.
 * Tests schemas names and readers with queries.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractReadingTests {

    protected static final double DELTA = 0.000000001d;
    private static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    public static class ExpectedResult{

        public ExpectedResult(final GenericName name, final FeatureType type, final int size, final Envelope env){
            this.name = name;
            this.type = type;
            this.size = size;
            this.env = env;
        }

        public GenericName name;
        public FeatureType type;
        public int size;
        public Envelope env;
    }

    protected abstract FeatureStore getDataStore();

    protected abstract Set<GenericName> getExpectedNames();

    protected abstract List<ExpectedResult> getReaderTests();

    @Test
    public void testDataStore(){
        final FeatureStore store = getDataStore();
        assertNotNull(store);
    }

    /**
     * test session creation.
     */
    @Test
    public void testSession(){
        final FeatureStore store = getDataStore();

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
        final FeatureStore store = getDataStore();
        final Set<GenericName> expectedTypes = getExpectedNames();

        //need at least one type to test
        assertTrue(expectedTypes.size() > 0);

        //check names-----------------------------------------------------------
        final Set<GenericName> founds = store.getNames();
        assertNotNull(founds);
        assertTrue(expectedTypes.size() == founds.size());
        assertTrue(expectedTypes.containsAll(founds));
        assertTrue(founds.containsAll(expectedTypes));

        for(GenericName name : founds){
            assertNotNull(name);
            assertNotNull(store.getFeatureType(name.toString()));
        }

        //check type names------------------------------------------------------
        final Set<GenericName> typeNames = store.getNames();

        assertNotNull(typeNames);
        assertTrue(typeNames.size() == founds.size());

        check:
        for (GenericName typeName : typeNames) {
            for(GenericName n : founds){
                if(n.tip().toString().equals(typeName.tip().toString())){
                    assertNotNull(typeName);
                    FeatureType type1 = store.getFeatureType(typeName.toString());
                    FeatureType type2 = store.getFeatureType(n.toString());
                    assertNotNull(type1);
                    assertNotNull(type2);
                    assertTrue(type1.equals(type2));
                    continue check;
                }
            }
            throw new Exception("A typename has no matching Name, featurestore is not consistent.");
        }

        //check error on wrong type names---------------------------------------
        try{
            store.getFeatureType(NamesExt.create("http://not", "exist").toString());
            throw new Exception("Asking for a schema that doesnt exist should have raised a featurestore exception.");
        }catch(DataStoreException ex){
            //ok
        }

        try{
            store.getFeatureType("not-exist_stuff");
            throw new Exception("Asking for a schema that doesnt exist should have raised a featurestore exception.");
        }catch(DataStoreException ex){
            //ok
        }

    }

    /**
     * test feature reader.
     */
    @Test
    public void testReader() throws Exception{
        final FeatureStore store = getDataStore();
        final List<ExpectedResult> candidates = getReaderTests();

        //need at least one type to test
        assertTrue(candidates.size() > 0);

        for(final ExpectedResult candidate : candidates){
            final GenericName name = candidate.name;
            final FeatureType type = store.getFeatureType(name.toString());
            assertNotNull(type);
            assertTrue(FeatureTypeExt.equalsIgnoreConvention(candidate.type, type));

            testCounts(store, candidate);
            testReaders(store, candidate);
            testBounds(store, candidate);
        }

    }

    /**
     * test different count with filters.
     */
    private void testCounts(final FeatureStore store, final ExpectedResult candidate) throws Exception{

        assertEquals(candidate.size, store.getCount(QueryBuilder.all(candidate.name)));

        //todo make more generic count tests
    }

    /**
     * test different bounds with filters.
     */
    private void testBounds(final FeatureStore store, final ExpectedResult candidate) throws Exception{
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
    private void testReaders(final FeatureStore store, final ExpectedResult candidate) throws Exception{
        final FeatureType type = store.getFeatureType(candidate.name.toString());
        final Collection<? extends PropertyType> properties = type.getProperties(true);
        final QueryBuilder qb = new QueryBuilder();
        Query query = null;

        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader without any query whould raise an error.");
        }catch(Exception ex){
            //ok
        }

        query = QueryBuilder.all(NamesExt.create(NamesExt.getNamespace(candidate.name), candidate.name.tip().toString()+"fgresfds_not_exist"));
        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader with a wrong name should raise a featurestore exception.");
        }catch(DataStoreException ex){
            //ok
        }

        query = QueryBuilder.all(NamesExt.create(NamesExt.getNamespace(candidate.name)+"resfsdfsdf_not_exist", candidate.name.tip().toString()));
        try{
            store.getFeatureReader(query);
            throw new Exception("Asking for a reader with a wrong namespace should raise a featurestore exception.");
        }catch(DataStoreException ex){
            //ok
        }


        //crs ------------------------------------------------------------------
        final CoordinateReferenceSystem originalCRS = FeatureExt.getCRS(type);
        if(originalCRS!= null){
            CoordinateReferenceSystem testCRS = CRS.forCode("EPSG:3395");
            if(Utilities.equalsIgnoreMetadata(originalCRS, testCRS)){
                //change the test crs
                testCRS = CommonCRS.WGS84.geographic();
            }

            //handle geometry as object since they can be ISO or JTS
            final Map<FeatureId,Object> inOriginal = new HashMap<>();
            FeatureReader ite = store.getFeatureReader(QueryBuilder.all(candidate.name));
            try{
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    final FeatureId id = FeatureExt.getId(f);
                    assertNotNull(id);
                    final Optional<Object> geom = FeatureExt.getDefaultGeometryValue(f);
                    assertTrue(String.format("No geometry found in feature%n%s", f), geom.isPresent());
                    inOriginal.put(id,geom.get());
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
                    final FeatureId id = FeatureExt.getId(f);
                    assertNotNull(id);
                    final Object original = inOriginal.get(id);
                    assertNotNull(original);
                    final Optional<Object> reprojected = FeatureExt.getDefaultGeometryValue(f);
                    assertTrue(String.format("No geometry found in feature%n%s", reprojected), reprojected.isPresent());
                    assertNotSame(original, reprojected.get());
                }
            }finally{
                ite.close();
            }

        }

        //property -------------------------------------------------------------

        //check only id query
        query = QueryBuilder.fids(candidate.name.toString());
        FeatureReader ite = store.getFeatureReader(query);
        FeatureType limited = ite.getFeatureType();
        assertNotNull(limited);
        assertTrue(limited.getProperties(true).size() == 1);
        try{
            while(ite.hasNext()){
                final Feature f = ite.next();
                assertNotNull(FeatureExt.getId(f));
            }
        }finally{
            ite.close();
        }

        for(final PropertyType desc : properties){
            if(desc instanceof Operation) continue;
            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setProperties(new String[]{desc.getName().tip().toString()});
            query = qb.buildQuery();

            ite = store.getFeatureReader(query);
            limited = ite.getFeatureType();
            assertNotNull(limited);
            assertTrue(limited.getProperties(true).size() == 1);
            assertNotNull(limited.getProperty(desc.getName().toString()));
            try{
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    assertNotNull(f.getProperty(desc.getName().toString()));
                }
            }finally{
                ite.close();
            }

        }


        //sort by --------------------------------------------------------------
        for(final PropertyType desc : properties){
            if(!(desc instanceof AttributeType)){
                continue;
            }

            final AttributeType att = (AttributeType) desc;
            if(att.getMaximumOccurs()>1){
                //do not test sort by on multi occurence properties
                continue;
            }

            final Class clazz = att.getValueClass();

            if(!Comparable.class.isAssignableFrom(clazz) || Geometry.class.isAssignableFrom(clazz)){
                //can not make a sort by on this attribut.
                continue;
            }

            qb.reset();
            qb.setTypeName(candidate.name);
            qb.setSortBy(new SortBy[]{FF.sort(desc.getName().tip().toString(), SortOrder.ASCENDING)});
            query = qb.buildQuery();

            //count should not change with a sort by
            assertEquals(candidate.size, store.getCount(query));

            FeatureReader reader = store.getFeatureReader(query);
            try{
                Comparable last = null;
                while(reader.hasNext()){
                    final Feature f = reader.next();
                    Object obj = f.getProperty(desc.getName().toString()).getValue();
                    if (obj instanceof Identifier) obj = ((Identifier) obj).getCode();
                    final Comparable current = (Comparable) obj;

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
            qb.setSortBy(new SortBy[]{FF.sort(desc.getName().tip().toString(), SortOrder.DESCENDING)});
            query = qb.buildQuery();

            //count should not change with a sort by
            assertEquals(candidate.size, store.getCount(query));

            reader = store.getFeatureReader(query);
            try{
                Comparable last = null;
                while(reader.hasNext()){
                    final Feature f = reader.next();
                    Object obj = f.getProperty(desc.getName().toString()).getValue();
                    if (obj instanceof Identifier) obj = ((Identifier) obj).getCode();
                    final Comparable current = (Comparable) obj;

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
                    ids.add(FeatureExt.getId(ite.next()));
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
                    assertEquals(FeatureExt.getId(ite.next()), ids.get(i));
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


        Set<FeatureId> ids = new HashSet<>();
        ite = store.getFeatureReader(QueryBuilder.fids(candidate.name.toString()));
        try{
            //peek only one on two ids
            boolean oneOnTwo = true;
            while(ite.hasNext()){
                final Feature feature = ite.next();
                if(oneOnTwo){
                    ids.add(FeatureExt.getId(feature));
                }
                oneOnTwo = !oneOnTwo;
            }
        }finally{
            ite.close();
        }


        Set<FeatureId> remaining = new HashSet<>(ids);
        ite = store.getFeatureReader(QueryBuilder.filtered(candidate.name.toString(),FF.id(ids)));

        try{
            while(ite.hasNext()){
                remaining.remove(FeatureExt.getId(ite.next()));
            }
        }finally{
            ite.close();
        }

         assertTrue(remaining.isEmpty() );

    }

}
