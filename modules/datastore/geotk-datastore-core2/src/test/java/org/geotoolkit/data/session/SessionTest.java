/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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


package org.geotoolkit.data.session;

import java.util.Collections;
import java.util.Date;
import junit.framework.TestCase;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SessionTest extends TestCase{

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    final MemoryDataStore store = new MemoryDataStore();


    public SessionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

        //create the schema
        final Name name = new DefaultName("http://test.com", "TestSchema1");
        builder.reset();
        builder.setName(name);
        builder.add("string", String.class);
        builder.add("double", Double.class);
        builder.add("date", Date.class);
        final SimpleFeatureType type = builder.buildFeatureType();
        store.createSchema(name,type);
        final QueryBuilder qb = new QueryBuilder(name);

        //create a few features
        FeatureWriter writer = store.getFeatureWriterAppend(name);
        try{
            SimpleFeature f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop3");
            f.setAttribute("double", 3d);
            f.setAttribute("date", new Date(1000L));
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop1");
            f.setAttribute("double", 1d);
            f.setAttribute("date", new Date(100000L));
            writer.write();

            f = (SimpleFeature) writer.next();
            f.setAttribute("string", "hop2");
            f.setAttribute("double", 2d);
            f.setAttribute("date", new Date(10000L));
            writer.write();

        }finally{
            writer.close();
        }

        //quick count check
        FeatureReader reader = store.getFeatureReader(QueryBuilder.all(name));
        int count = 0;
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }
        assertEquals(count, 3);
        assertEquals(store.getCount(QueryBuilder.all(name)), 3);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSessionReader() throws Exception {
        final Name name = store.getNames().iterator().next();
        final QueryBuilder qb = new QueryBuilder();
        Query query;
        

        final Session session = store.createSession();


        //test simple reader with no modification ------------------------------
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),3);

        //test an iterator------------------------------------------------------
        FeatureIterator reader = session.getFeatureIterator(QueryBuilder.sorted(name, new SortBy[]{FF.sort("string", SortOrder.ASCENDING)}));
        try{
            SimpleFeature sf;
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop1");
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop2");
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop3");

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        //test adding new features----------------------------------------------
        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder((SimpleFeatureType) store.getSchema(name));
        sfb.set("string", "hop4");
        sfb.set("double", 4d);
        sfb.set("date", new Date(100L));

        session.add(name, Collections.singletonList(sfb.buildFeature("temporary")));

        //check that he new feature is available in the session but not in the datastore
        qb.reset();
        qb.setTypeName(name);
        query = qb.buildQuery();

        assertEquals(store.getCount(query),3);
        assertEquals(session.getCount(query),4);

        reader = session.getFeatureIterator(QueryBuilder.filtered(name, FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            SimpleFeature sf;
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop4");
            assertEquals(sf.getAttribute("double"),4d);
            assertEquals(sf.getAttribute("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }


        //check that he new feature is available in the session and in the datastore
        session.commit();

        assertEquals(store.getCount(query),4);
        assertEquals(session.getCount(query),4);

        //make a more deep test to find our feature
        reader = session.getFeatureIterator(QueryBuilder.filtered(name, FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            SimpleFeature sf;
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop4");
            assertEquals(sf.getAttribute("double"),4d);
            assertEquals(sf.getAttribute("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }

        reader = store.getFeatureReader(QueryBuilder.filtered(name, FF.equals(FF.literal("hop4"), FF.property("string"))));
        try{
            SimpleFeature sf;
            reader.hasNext();
            sf = (SimpleFeature) reader.next();
            assertEquals(sf.getAttribute("string"),"hop4");
            assertEquals(sf.getAttribute("double"),4d);
            assertEquals(sf.getAttribute("date"),new Date(100L));

            assertFalse(reader.hasNext());
        }finally{
            reader.close();
        }


    }


}