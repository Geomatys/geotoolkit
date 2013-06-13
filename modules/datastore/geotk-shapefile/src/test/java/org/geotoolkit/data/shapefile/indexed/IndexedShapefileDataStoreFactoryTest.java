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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.test.TestData;

import org.junit.Before;
import static org.junit.Assert.*;

/**
 * @module pending
 */
public class IndexedShapefileDataStoreFactoryTest extends AbstractTestCaseSupport {
    
    private ShapefileFeatureStoreFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new ShapefileFeatureStoreFactory();
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.canProcess(Map)'
     */
    @Test
    public void testCanProcessMap() throws Exception {
        Map map = new HashMap();
        map.put(ShapefileFeatureStoreFactory.URLP.getName().toString(), ShapeTestData
                .url(IndexedShapefileDataStoreTest.STATE_POP));
        assertTrue(factory.canProcess(map));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createDataStore(Map)'
     */
    @Test
    public void testCreateDataStoreMap() throws Exception {
        testCreateDataStore(true);

        ShapefileFeatureStore ds1 = testCreateDataStore(true, true);
        ShapefileFeatureStore ds2 = testCreateDataStore(true, true);

        assertNotSame(ds1, ds2);

        ds2 = testCreateDataStore(true, false);
        assertNotSame(ds1, ds2);
    }

    private ShapefileFeatureStore testCreateDataStore(final boolean createIndex)
            throws Exception {
        return testCreateDataStore(true, createIndex);
    }

    @Test
    public void testNamespace() throws Exception {
        ShapefileFeatureStoreFactory factory = new ShapefileFeatureStoreFactory();
        Map map = new HashMap();
        String namespace = "http://jesse.com";
        map.put(ShapefileFeatureStoreFactory.NAMESPACE.getName().toString(), namespace);
        map.put(ShapefileFeatureStoreFactory.URLP.getName().toString(), ShapeTestData
                .url(IndexedShapefileDataStoreTest.STATE_POP));

        FeatureStore store = factory.open(map);
        String typeName = IndexedShapefileDataStoreTest.STATE_POP.substring(
                IndexedShapefileDataStoreTest.STATE_POP.indexOf('/') + 1,
                IndexedShapefileDataStoreTest.STATE_POP.lastIndexOf('.'));
        assertEquals("http://jesse.com", store.getFeatureType(typeName).getName()
                .getNamespaceURI());
    }

    private ShapefileFeatureStore testCreateDataStore(final boolean newDS,
            final boolean createIndex) throws Exception {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);
        Map map = new HashMap();
        map.put(ShapefileFeatureStoreFactory.URLP.getName().toString(), TestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP));
        map.put(ShapefileFeatureStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(),
                createIndex ? Boolean.TRUE : Boolean.FALSE);

        ShapefileFeatureStore ds;

        if (newDS) {
            // This may provided a warning if the file already is created
            ds = (ShapefileFeatureStore) factory.create(map);
        } else {
            ds = (ShapefileFeatureStore) factory.open(map);
        }

        if (ds instanceof IndexedShapefileFeatureStore) {
            IndexedShapefileFeatureStore indexed = (IndexedShapefileFeatureStore) ds;
            testDataStore(IndexType.QIX, createIndex, indexed);
        }
        return ds;
    }

    private void testDataStore(final IndexType treeType, final boolean createIndex,
            final IndexedShapefileFeatureStore ds) {
        assertNotNull(ds);
        assertEquals(treeType, ds.treeType);
        assertEquals(treeType != IndexType.NONE, ds.useIndex);
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createNewDataStore(Map)'
     */
    @Test
    public void testCreateNewDataStore() throws Exception {
        ShapefileFeatureStore ds1 = testCreateDataStore(true, false);
        ShapefileFeatureStore ds2 = testCreateDataStore(true, true);

        assertNotSame(ds1, ds2);
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.isAvailable()'
     */
    @Test
    public void testIsAvailable() {
        assertTrue(factory.availability().pass());
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getParametersInfo()'
     */
    @Test
    public void testGetParametersInfo() {
        //check that we have those two parameters descriptors.
        factory.getParametersDescriptor().descriptor(ShapefileFeatureStoreFactory.CREATE_SPATIAL_INDEX.getName().toString());
        factory.getParametersDescriptor().descriptor(ShapefileFeatureStoreFactory.URLP.getName().toString());
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getFileExtensions()'
     */
    @Test
    public void testGetFileExtensions() {
        List ext = Arrays.asList(factory.getFileExtensions());
        assertTrue(ext.contains(".shp"));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.canProcess(URL)'
     */
    @Test
    public void testCanProcessURL() throws FileNotFoundException {
        factory.canProcess(ShapeTestData.url(IndexedShapefileDataStoreTest.STATE_POP));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createDataStore(URL)'
     */
    @Test
    public void testCreateDataStoreURL() throws DataStoreException,IOException {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);
        FeatureStore ds = factory.createDataStore(TestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP));
        testDataStore(IndexType.QIX, true, (IndexedShapefileFeatureStore) ds);
    }

}
