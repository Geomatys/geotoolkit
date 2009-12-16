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


package org.geotoolkit.data.memory;

import java.io.IOException;
import java.util.Set;
import junit.framework.TestCase;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryDatastoreTest extends TestCase{

    public MemoryDatastoreTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreateDataStore() throws IOException {
        MemoryDataStore store = new MemoryDataStore();
    }

    @Test
    public void testSchemas() throws IOException {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        final MemoryDataStore store = new MemoryDataStore();
        Set<Name> names;

        names = store.getNames();
        assertEquals(names.size(), 0);

        //test creation of one schema ------------------------------------------
        Name name = new DefaultName("http://test.com", "TestSchema1");
        builder.reset();
        builder.setName(name);
        builder.add("att1", String.class);
        final SimpleFeatureType type1 = builder.buildFeatureType();

        store.createSchema(name,type1);

        names = store.getNames();
        assertEquals(names.size(), 1);
        Name n = names.iterator().next();

        assertEquals(n.getLocalPart(), "TestSchema1");
        assertEquals(n.getNamespaceURI(), "http://test.com");

        SimpleFeatureType t = (SimpleFeatureType) store.getSchema(n);
        assertEquals(t, type1);

        try{
            store.getSchema(new DefaultName("http://not", "exist"));
            throw new Exception("Asking for a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

        //test update schema ---------------------------------------------------
        builder.reset();
        builder.setName("http://test.com", "TestSchema1");
        builder.add("att1", String.class);
        builder.add("att2", Double.class);
        SimpleFeatureType type2 = builder.buildFeatureType();

        store.updateSchema(name, type2);

        names = store.getNames();
        assertEquals(names.size(), 1);
        n = names.iterator().next();

        assertEquals(n.getLocalPart(), "TestSchema1");
        assertEquals(n.getNamespaceURI(), "http://test.com");

        t = (SimpleFeatureType) store.getSchema(n);
        assertEquals(t, type2);


        try{
            store.updateSchema(new DefaultName("http://not", "exist"),type2);
            throw new Exception("Updating a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

        //test delete schema ---------------------------------------------------

        names = store.getNames();
        assertEquals(names.size(), 1);

        store.deleteSchema(name);

        names = store.getNames();
        assertEquals(names.size(), 0);

        try{
            store.deleteSchema(new DefaultName("http://not", "exist"));
            throw new Exception("Deleting a schema that doesnt exist should have raised an error");
        }catch(Exception ex){
            //ok
        }

    }

}