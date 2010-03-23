/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import junit.framework.TestCase;

import org.geotoolkit.data.iterator.CheckCloseFeatureIterator;
import org.geotoolkit.data.memory.MemoryDataStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;

import org.junit.Test;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DataUtilitiesTest extends TestCase{

    private final DataStore store;
    private final Name name1;
    private final Name name2;
    private final Name name3;

    public DataUtilitiesTest() throws DataStoreException{
        store = new MemoryDataStore();

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        name1 = new DefaultName("http://test.com", "type1");
        name2 = new DefaultName("http://test.com", "type2");
        name3 = new DefaultName("http://test.com", "type3");
        builder.reset();
        builder.setName(name1);
        builder.add("att_string", String.class);
        SimpleFeatureType sft1 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName(name2);
        builder.add("att_string", String.class);
        SimpleFeatureType sft2 = builder.buildSimpleFeatureType();

        builder.reset();
        builder.setName(name3);
        builder.add("att_string", String.class);
        SimpleFeatureType sft3 = builder.buildSimpleFeatureType();

        store.createSchema(sft1.getName(), sft1);
        store.createSchema(sft2.getName(), sft2);
        store.createSchema(sft3.getName(), sft3);

        FeatureWriter writer = store.getFeatureWriterAppend(name1);
        for(int i=0; i<10; i++){
            writer.next();
            writer.write();
        }
        writer.close();

        writer = store.getFeatureWriterAppend(name2);
        for(int i=0; i<10; i++){
            writer.next();
            writer.write();
        }
        writer.close();

        writer = store.getFeatureWriterAppend(name3);
        for(int i=0; i<10; i++){
            writer.next();
            writer.write();
        }
        writer.close();

    }

    /**
     * Test that the collection id is correctly set.
     */
    @Test
    public void testCollectionId() throws Exception{
        FeatureTypeBuilder sftb = new FeatureTypeBuilder();
        sftb.setName("temp");
        sftb.add("att1", String.class);
        FeatureType ft = sftb.buildSimpleFeatureType();

        FeatureCollection col = DataUtilities.collection("myId", ft);

        assertEquals("myId", col.getID());

    }

    @Test
    public void testReaderSequence() throws DataStoreException{

        CheckCloseFeatureIterator reader1 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name1)));
        CheckCloseFeatureIterator reader2 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name2)));
        CheckCloseFeatureIterator reader3 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name3)));

        FeatureReader fr = DataUtilities.sequence(reader1,reader2,reader3);
        assertFalse(reader1.isClosed());
        assertFalse(reader2.isClosed());
        assertFalse(reader3.isClosed());

        int i=0;
        while(fr.hasNext()){
            fr.next();
            i++;
        }
        fr.close();

        assertEquals(30,i);

        assertTrue(reader1.isClosed());
        assertTrue(reader2.isClosed());
        assertTrue(reader3.isClosed());


        //check ending in the middle--------------------------------------------
        reader1 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name1)));
        reader2 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name2)));
        reader3 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name3)));

        fr = DataUtilities.sequence(reader1,reader2,reader3);
        assertFalse(reader1.isClosed());
        assertFalse(reader2.isClosed());
        assertFalse(reader3.isClosed());

        fr.next();
        fr.next();
        fr.close();

        assertTrue(reader1.isClosed());
        assertTrue(reader2.isClosed());
        assertTrue(reader3.isClosed());

    }

    @Test
    public void testIteratorSequence() throws DataStoreException{

        CheckCloseFeatureIterator reader1 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name1)));
        CheckCloseFeatureIterator reader2 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name2)));
        CheckCloseFeatureIterator reader3 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name3)));

        FeatureIterator fr = DataUtilities.sequence((FeatureIterator)reader1,(FeatureIterator)reader2,(FeatureIterator)reader3);
        assertFalse(reader1.isClosed());
        assertFalse(reader2.isClosed());
        assertFalse(reader3.isClosed());

        int i=0;
        while(fr.hasNext()){
            fr.next();
            i++;
        }
        fr.close();

        assertEquals(30,i);

        assertTrue(reader1.isClosed());
        assertTrue(reader2.isClosed());
        assertTrue(reader3.isClosed());


        //check ending in the middle--------------------------------------------
        reader1 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name1)));
        reader2 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name2)));
        reader3 = new CheckCloseFeatureIterator(store.getFeatureReader(QueryBuilder.all(name3)));

        fr = DataUtilities.sequence((FeatureIterator)reader1,(FeatureIterator)reader2,(FeatureIterator)reader3);
        assertFalse(reader1.isClosed());
        assertFalse(reader2.isClosed());
        assertFalse(reader3.isClosed());

        fr.next();
        fr.next();
        fr.close();

        assertTrue(reader1.isClosed());
        assertTrue(reader2.isClosed());
        assertTrue(reader3.isClosed());

    }

    @Test
    public void testCollectionSequence() throws DataStoreException{

        FeatureCollection fc1 = store.createSession(false).getFeatureCollection(QueryBuilder.all(name1));
        FeatureCollection fc2 = store.createSession(false).getFeatureCollection(QueryBuilder.all(name2));
        FeatureCollection fc3 = store.createSession(false).getFeatureCollection(QueryBuilder.all(name3));
        FeatureCollection col = DataUtilities.sequence("id", fc1, fc2, fc3);

        CheckCloseFeatureIterator reader = new CheckCloseFeatureIterator(col.iterator());
        assertFalse(reader.isClosed());

        int i=0;
        while(reader.hasNext()){
            reader.next();
            i++;
        }
        reader.close();

        assertEquals(30,i);
        assertTrue(reader.isClosed());


        //check ending in the middle--------------------------------------------
        reader = new CheckCloseFeatureIterator(col.iterator());

        assertFalse(reader.isClosed());

        reader.next();
        reader.next();
        reader.close();

        assertTrue(reader.isClosed());
    }

}
