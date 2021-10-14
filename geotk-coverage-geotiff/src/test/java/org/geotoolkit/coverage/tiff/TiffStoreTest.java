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

import java.awt.image.RenderedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.ImageProcessor;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.nio.IOUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;

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

    /**
     * Ensure no error arise from resamplin/prefetching when using a Geotiff as source image. The problem has been
     * spotted on an image similar to the one used in the test, where SIS fails with the following error:
     *
     * <pre>TransformException: No category for value NaN #0.</pre>
     */
    @Test
    public void testBackgroundValue() throws Exception {
        final Path file = IOUtilities.getResourceAsPath("org/geotoolkit/image/io/test-data/nan_255.tif");
        try (TiffStore store = new TiffStore(file)) {
            GridCoverageProcessor processor = new GridCoverageProcessor();
            final GridGeometry baseGeom = store.getGridGeometry();
            final Envelope baseEnv = baseGeom.getEnvelope();
            final GeneralEnvelope resampleEnvelope = new GeneralEnvelope(baseGeom.getEnvelope());
            resampleEnvelope.setRange(0,
                    baseEnv.getMinimum(0) - baseEnv.getSpan(0) / 10,
                    baseEnv.getMaximum(0) + baseEnv.getSpan(0) / 10);
            final GridGeometry resampleGeom = new GridGeometry(
                    new GridExtent(256, 256),
                    resampleEnvelope,
                    GridOrientation.HOMOTHETY);
            GridCoverage dataImage = processor.resample(store.read(null), resampleGeom);
            RenderedImage img = dataImage.render(null);

            ImageProcessor improcessor = new ImageProcessor();
            improcessor.setExecutionMode(ImageProcessor.Mode.PARALLEL);
            img = improcessor.prefetch(img, null);
            Assert.assertNotNull(img);
        }
    }
}
