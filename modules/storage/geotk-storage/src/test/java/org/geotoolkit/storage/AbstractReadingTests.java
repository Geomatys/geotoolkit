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

package org.geotoolkit.storage;

import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortOrder;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.util.GenericName;

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

    protected abstract DataStore getDataStore();

    protected abstract Set<GenericName> getExpectedNames();

    protected abstract List<ExpectedResult> getReaderTests();

    @Test
    public void testDataStore(){
        final DataStore store = getDataStore();
        assertNotNull(store);
    }

    /**
     * test schema names
     */
    @Test
    public void testSchemas() throws Exception{
        final DataStore store = getDataStore();
        final Set<GenericName> expectedTypes = getExpectedNames();

        //need at least one type to test
        assertTrue(expectedTypes.size() > 0);

        //check names-----------------------------------------------------------
        final Collection<FeatureSet> featureSets = DataStores.flatten(store, true, FeatureSet.class);
        assertTrue(expectedTypes.size() == featureSets.size());

        for (FeatureSet fs : featureSets) {
            assertTrue(fs.getIdentifier().isPresent());
            assertNotNull(fs.getType());
            assertTrue(expectedTypes.contains(fs.getType().getName()));
            assertTrue(fs.getIdentifier().get().toString().equals(fs.getType().getName().toString()));

            //will cause an error if not found
            store.findResource(fs.getType().getName().toString());
        }

        //check error on wrong type names---------------------------------------
        try {
            store.findResource(NamesExt.create("http://not", "exist").toString());
            fail("Asking for a schema that doesnt exist should have raised an exception.");
        } catch(IllegalNameException ex) {
            //ok
        }

    }

    /**
     * test feature reader.
     */
    @Test
    public void testReader() throws Exception {
        final DataStore store = getDataStore();
        final List<ExpectedResult> candidates = getReaderTests();

        //need at least one type to test
        assertTrue(candidates.size() > 0);

        for (final ExpectedResult candidate : candidates) {
            final GenericName name = candidate.name;
            final Resource resource = store.findResource(name.toString());
            assertTrue(resource instanceof FeatureSet);
            final FeatureSet featureSet = (FeatureSet) resource;
            final FeatureType type = featureSet.getType();
            assertNotNull(type);
            assertTrue(FeatureTypeExt.equalsIgnoreConvention(candidate.type, type));

            testCounts(featureSet, candidate);
            testReaders(featureSet, candidate);
            testBounds(featureSet, candidate);
        }
    }

    /**
     * test different count with filters.
     */
    private void testCounts(final FeatureSet featureSet, final ExpectedResult candidate) throws Exception {

        assertEquals(candidate.size, FeatureStoreUtilities.getCount(featureSet, true).intValue());

        //todo make more generic count tests
    }

    /**
     * test different bounds with filters.
     */
    private void testBounds(final FeatureSet featureSet, final ExpectedResult candidate) throws Exception {
        Envelope res = FeatureStoreUtilities.getEnvelope(featureSet, true);

        if (candidate.env == null) {
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
    private void testReaders(final FeatureSet featureSet, final ExpectedResult candidate) throws Exception{
        final FeatureType type = featureSet.getType();
        final Collection<? extends PropertyType> properties = type.getProperties(true);


        try (Stream<Feature> stream = featureSet.features(true)) {
            stream.forEach((Feature t) -> {
                //do nothing
            });
            throw new Exception("Asking for a reader without any query whould raise an error.");
        } catch (Exception ex) {
            //ok
        }

        //property -------------------------------------------------------------

        {
            //check only id query
            final SimpleQuery query = new SimpleQuery();
            query.setColumns(new SimpleQuery.Column(FF.property(AttributeConvention.IDENTIFIER_PROPERTY.toString())));
            FeatureSet subset = featureSet.subset(query);
            FeatureType limited = subset.getType();
            assertNotNull(limited);
            assertTrue(limited.getProperties(true).size() == 1);

            try (Stream<Feature> stream = subset.features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                while (ite.hasNext()){
                    final Feature f = ite.next();
                    assertNotNull(FeatureExt.getId(f));
                }
            }

            for (final PropertyType desc : properties) {
                if (desc instanceof Operation) continue;

                final SimpleQuery sq = new SimpleQuery();
                sq.setColumns(new SimpleQuery.Column(FF.property(desc.getName().tip().toString())));

                subset = featureSet.subset(sq);
                limited = subset.getType();
                assertNotNull(limited);
                assertTrue(limited.getProperties(true).size() == 1);
                assertNotNull(limited.getProperty(desc.getName().toString()));

                try (Stream<Feature> stream = subset.features(false)) {
                    final Iterator<Feature> ite = stream.iterator();
                    while (ite.hasNext()) {
                        final Feature f = ite.next();
                        assertNotNull(f.getProperty(desc.getName().toString()));
                    }
                }
            }
        }

        //sort by --------------------------------------------------------------
        for (final PropertyType desc : properties) {
            if (!(desc instanceof AttributeType)) {
                continue;
            }

            final AttributeType att = (AttributeType) desc;
            if (att.getMaximumOccurs()>1) {
                //do not test sort by on multi occurence properties
                continue;
            }

            final Class clazz = att.getValueClass();

            if (!Comparable.class.isAssignableFrom(clazz) || Geometry.class.isAssignableFrom(clazz)) {
                //can not make a sort by on this attribut.
                continue;
            }

            final SimpleQuery query = new SimpleQuery();
            query.setSortBy(FF.sort(desc.getName().tip().toString(), SortOrder.ASCENDING));
            FeatureSet subset = featureSet.subset(query);

            //count should not change with a sort by
            assertEquals(candidate.size, FeatureStoreUtilities.getCount(subset, true).intValue());

            try (Stream<Feature> stream = subset.features(false)) {
                final Iterator<Feature> reader = stream.iterator();
                Comparable last = null;
                while (reader.hasNext()) {
                    final Feature f = reader.next();
                    Object obj = f.getProperty(desc.getName().toString()).getValue();
                    if (obj instanceof Identifier) obj = ((Identifier) obj).getCode();
                    final Comparable current = (Comparable) obj;

                    if (current != null) {
                        if (last != null) {
                            //check we have the correct order.
                            assertTrue( current.compareTo(last) >= 0 );
                        }
                        last = current;
                    } else {
                        //any restriction about where should be placed the feature with null values ? before ? after ?
                    }
                }
            }

            query.setSortBy(FF.sort(desc.getName().tip().toString(), SortOrder.DESCENDING));
            subset = featureSet.subset(query);

            //count should not change with a sort by
            assertEquals(candidate.size, FeatureStoreUtilities.getCount(subset, true).intValue());

            try (Stream<Feature> stream = subset.features(false)) {
                final Iterator<Feature> reader = stream.iterator();
                Comparable last = null;
                while (reader.hasNext()) {
                    final Feature f = reader.next();
                    Object obj = f.getProperty(desc.getName().toString()).getValue();
                    if (obj instanceof Identifier) obj = ((Identifier) obj).getCode();
                    final Comparable current = (Comparable) obj;

                    if (current != null) {
                        if (last != null) {
                            //check we have the correct order.
                            assertTrue( current.compareTo(last) <= 0 );
                        }
                        last = current;
                    } else {
                        //any restriction about where should be placed the feature with null values ? before ? after ?
                    }
                }
            }
        }

        //start ----------------------------------------------------------------
        if (candidate.size > 1) {

            List<FeatureId> ids = new ArrayList<>();
            try (Stream<Feature> stream = featureSet.features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                while (ite.hasNext()) {
                    ids.add(FeatureExt.getId(ite.next()));
                }
            }
            //skip the first element
            final SimpleQuery query = new SimpleQuery();
            query.setOffset(1);

            try (Stream<Feature> stream = featureSet.subset(query).features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                int i = 1;
                while (ite.hasNext()) {
                    assertEquals(FeatureExt.getId(ite.next()), ids.get(i));
                    i++;
                }
            }
        }


        //max ------------------------------------------------------------------
        if(candidate.size > 1){
            final SimpleQuery query = new SimpleQuery();
            query.setLimit(1);

            int i = 0;
            try (Stream<Feature> stream = featureSet.subset(query).features(false)) {
                final Iterator<Feature> ite = stream.iterator();
                while (ite.hasNext()) {
                    ite.next();
                    i++;
                }
            }

            assertEquals(1, i);
        }

        //filter ---------------------------------------------------------------
        //filters are tested more deeply in the filter module
        //we just make a few tests here for sanity check
        //todo should we make more deep tests ?

        Set<FeatureId> ids = new HashSet<>();
        try (Stream<Feature> stream = featureSet.features(false)) {
            final Iterator<Feature> ite = stream.iterator();
            //peek only one on two ids
            boolean oneOnTwo = true;
            while (ite.hasNext()) {
                final Feature feature = ite.next();
                if (oneOnTwo) {
                    ids.add(FeatureExt.getId(feature));
                }
                oneOnTwo = !oneOnTwo;
            }
        }

        Set<FeatureId> remaining = new HashSet<>(ids);
        final SimpleQuery query = new SimpleQuery();
        query.setFilter(FF.id(ids));
        try (Stream<Feature> stream = featureSet.subset(query).features(false)) {
            final Iterator<Feature> ite = stream.iterator();
            while (ite.hasNext()) {
                remaining.remove(FeatureExt.getId(ite.next()));
            }
        }

         assertTrue(remaining.isEmpty() );

    }

}
