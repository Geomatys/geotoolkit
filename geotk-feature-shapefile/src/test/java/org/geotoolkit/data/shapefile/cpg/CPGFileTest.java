/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.data.shapefile.cpg;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.data.shapefile.ShapefileProvider;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.storage.feature.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CPGFileTest {

    /**
     * Test reading a shapefile with an UTF-8 cpg file.
     *
     * @throws DataStoreException
     */
    @Test
    public void testReadUTF8() throws DataStoreException, MalformedURLException, URISyntaxException {

        final URL url = CPGFileTest.class.getResource("/org/geotoolkit/test-data/shapes/utf8.shp");

        try (final ShapefileFeatureStore store = new ShapefileFeatureStore(url.toURI())) {
            assertUtf8Text(store);
        }

        // Test using another opening method, because until 2024-10-08,
        // CPG was not properly handled when opening a datastore using ShapefileProvider#open(StorageConnector)
        try (var store = new ShapefileProvider().open(new StorageConnector(url.toURI()))) {
            assertUtf8Text((ShapefileFeatureStore) store);
        }
    }

    static void assertUtf8Text(ShapefileFeatureStore store) throws DataStoreException {
        try(final FeatureReader reader = store.getFeatureReader(new Query(store.getName()))) {
            Assert.assertTrue(reader.hasNext());
            final Feature feature = reader.next();
            Assert.assertEquals("&éè\"'(-_çà)=@%$*:test",feature.getProperty("text").getValue());
        }
    }
}
