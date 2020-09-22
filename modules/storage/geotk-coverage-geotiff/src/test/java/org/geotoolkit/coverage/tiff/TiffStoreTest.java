/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.coverage.tiff;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.nio.IOUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TiffStoreTest {

    @Test
    public void testFileRead() throws Exception {
        final Path file    = IOUtilities.getResourceAsPath("org/geotoolkit/image/io/test-data/002025_0100_010722_l7_01_utm2.tiff");

        Assert.assertEquals(true, Files.exists(file));
        final StorageConnector cnx = new StorageConnector(file);

        final TiffProvider provider = new TiffProvider();

        // Not working yet
        //final ProbeResult result = provider.probeContent(cnx);
        //Assert.assertEquals(true, result.isSupported());

        try (DataStore store = provider.open(cnx)) {
            Assert.assertTrue("Unexpected implementation for file coverage data store", store instanceof TiffStore);
            final TiffStore tStore = (TiffStore) store;
            final Path[] cFiles = tStore.getComponentFiles();
            Assert.assertArrayEquals("We should detect the tiff file", new Path[]{file}, cFiles);
            
            final Resource res = tStore.findResource("002025_0100_010722_l7_01_utm2");
            Assert.assertNotNull("Datastore resource should not be null", res);
            
            Assert.assertTrue(res instanceof GridCoverageResource);

            GridCoverageResource gcr = (GridCoverageResource) res;
            GridCoverage gc = gcr.read(null, 0);
            
            Assert.assertNotNull(gc);
        }
    }
}
