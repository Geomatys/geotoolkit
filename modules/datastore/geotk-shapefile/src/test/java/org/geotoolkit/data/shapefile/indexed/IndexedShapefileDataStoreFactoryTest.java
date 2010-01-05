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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.shapefile.ShapefileDataStore;
import org.geotoolkit.data.shapefile.ShapefileDataStoreFactory;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;

/**
 * @module pending
 */
public class IndexedShapefileDataStoreFactoryTest extends AbstractTestCaseSupport {
    private ShapefileDataStoreFactory factory;

    public IndexedShapefileDataStoreFactoryTest() throws IOException {
        super("IndexedShapefileDataStoreFactoryTest");
    }

    protected void setUp() throws Exception {
        factory = new ShapefileDataStoreFactory();
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.canProcess(Map)'
     */
    public void testCanProcessMap() throws Exception {
        Map map = new HashMap();
        map.put(ShapefileDataStoreFactory.URLP.getName().toString(), ShapeTestData
                .url(IndexedShapefileDataStoreTest.STATE_POP));
        assertTrue(factory.canProcess(map));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createDataStore(Map)'
     */
    public void testCreateDataStoreMap() throws Exception {
        testCreateDataStore(true);

        ShapefileDataStore ds1 = testCreateDataStore(true, true);
        ShapefileDataStore ds2 = testCreateDataStore(true, true);

        assertNotSame(ds1, ds2);

        ds2 = testCreateDataStore(true, false);
        assertNotSame(ds1, ds2);
    }

    private ShapefileDataStore testCreateDataStore(boolean createIndex)
            throws Exception {
        return testCreateDataStore(true, createIndex);
    }

    public void testNamespace() throws Exception {
        ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
        Map map = new HashMap();
        URI namespace = new URI("http://jesse.com");
        map.put(ShapefileDataStoreFactory.NAMESPACEP.getName().toString(), namespace);
        map.put(ShapefileDataStoreFactory.URLP.getName().toString(), ShapeTestData
                .url(IndexedShapefileDataStoreTest.STATE_POP));

        DataStore store = factory.createDataStore(map);
        String typeName = IndexedShapefileDataStoreTest.STATE_POP.substring(
                IndexedShapefileDataStoreTest.STATE_POP.indexOf('/') + 1,
                IndexedShapefileDataStoreTest.STATE_POP.lastIndexOf('.'));
        assertEquals("http://jesse.com", store.getFeatureType(typeName).getName()
                .getNamespaceURI());
    }

    private ShapefileDataStore testCreateDataStore(boolean newDS,
            boolean createIndex) throws Exception {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);
        Map map = new HashMap();
        map.put(ShapefileDataStoreFactory.URLP.getName().toString(), ShapeTestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP));
        map.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.getName().toString(),
                createIndex ? Boolean.TRUE : Boolean.FALSE);

        ShapefileDataStore ds;

        if (newDS) {
            // This may provided a warning if the file already is created
            ds = (ShapefileDataStore) factory.createNewDataStore(map);
        } else {
            ds = (ShapefileDataStore) factory.createDataStore(map);
        }

        if (ds instanceof IndexedShapefileDataStore) {
            IndexedShapefileDataStore indexed = (IndexedShapefileDataStore) ds;
            testDataStore(IndexType.QIX, createIndex, indexed);
        }
        return ds;
    }

    private void testDataStore(IndexType treeType, boolean createIndex,
            IndexedShapefileDataStore ds) {
        assertNotNull(ds);
        assertEquals(treeType, ds.treeType);
        assertEquals(treeType != IndexType.NONE, ds.useIndex);
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createNewDataStore(Map)'
     */
    public void testCreateNewDataStore() throws Exception {
        ShapefileDataStore ds1 = testCreateDataStore(true, false);
        ShapefileDataStore ds2 = testCreateDataStore(true, true);

        assertNotSame(ds1, ds2);
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.isAvailable()'
     */
    public void testIsAvailable() {
        assertTrue(factory.availability().pass());
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getParametersInfo()'
     */
    public void testGetParametersInfo() {
        //check that we have those two parameters descriptors.
        factory.getParametersDescriptor().descriptor(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.getName().toString());
        factory.getParametersDescriptor().descriptor(ShapefileDataStoreFactory.URLP.getName().toString());
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getFileExtensions()'
     */
    public void testGetFileExtensions() {
        List ext = Arrays.asList(factory.getFileExtensions());
        assertTrue(ext.contains(".shp"));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.canProcess(URL)'
     */
    public void testCanProcessURL() throws FileNotFoundException {
        factory.canProcess(ShapeTestData.url(IndexedShapefileDataStoreTest.STATE_POP));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createDataStore(URL)'
     */
    public void testCreateDataStoreURL() throws DataStoreException,IOException {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);
        DataStore ds = factory.createDataStore(ShapeTestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP));
        testDataStore(IndexType.QIX, true, (IndexedShapefileDataStore) ds);
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getTypeName(URL)'
     */
    public void testGetTypeName() throws IOException,DataStoreException {
        factory.getTypeName(ShapeTestData.url(IndexedShapefileDataStoreTest.STATE_POP));
    }
}
