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
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.ShapeTestData;
import org.geotoolkit.storage.feature.FeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.data.shapefile.AbstractTestCaseSupport;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.test.TestData;

import org.junit.Before;
import static org.junit.Assert.*;

/**
 * @module
 */
public class IndexedShapefileDataStoreFactoryTest extends AbstractTestCaseSupport {

    private ShapefileProvider factory;

    @Before
    public void setUp() throws Exception {
        factory = new ShapefileProvider();
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

    private ShapefileFeatureStore testCreateDataStore(final boolean newDS,
            final boolean createIndex) throws Exception {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);

        Map map = new HashMap();
        map.put(ShapefileProvider.PATH.getName().toString(), TestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP));
        map.put(ShapefileProvider.CREATE_SPATIAL_INDEX.getName().toString(),
                createIndex ? Boolean.TRUE : Boolean.FALSE);

        ShapefileFeatureStore ds;

        if (newDS) {
            // This may provided a warning if the file already is created
            ds = (ShapefileFeatureStore) DataStores.create(factory,map);
        } else {
            ds = (ShapefileFeatureStore) DataStores.open(factory,map);
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
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getParametersInfo()'
     */
    @Test
    public void testGetParametersInfo() {
        //check that we have those two parameters descriptors.
        factory.getOpenParameters().descriptor(ShapefileProvider.CREATE_SPATIAL_INDEX.getName().toString());
        factory.getOpenParameters().descriptor(ShapefileProvider.PATH.getName().toString());
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.getFileExtensions()'
     */
    @Test
    public void testGetFileExtensions() {
        Collection ext = factory.getSuffix();
        assertTrue(ext.contains("shp"));
    }

    /*
     * Test method for
     * 'org.geotoolkit.data.shapefile.indexed.IndexedShapefileDataStoreFactory.createDataStore(URL)'
     */
    @Test
    public void testCreateDataStoreURL() throws DataStoreException, IOException, URISyntaxException {
        copyShapefiles(IndexedShapefileDataStoreTest.STATE_POP);
        FeatureStore ds = factory.createDataStore(TestData.url(AbstractTestCaseSupport.class,
                IndexedShapefileDataStoreTest.STATE_POP).toURI());
        testDataStore(IndexType.QIX, true, (IndexedShapefileFeatureStore) ds);
    }

}
