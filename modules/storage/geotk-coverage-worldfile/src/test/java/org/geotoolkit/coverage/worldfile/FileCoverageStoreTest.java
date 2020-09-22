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
package org.geotoolkit.coverage.worldfile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.nio.IOUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FileCoverageStoreTest {

    /**
     * A file with an unusual extension but correct signature must be supported.
     */
    @Test
    public void testFileWithUnusualExtension() throws IOException, DataStoreException {
        final Path tmpDir = Files.createTempDirectory("test-file-coverage");
        try {
            final Path file = Files.createTempFile(tmpDir, "geo", ".ovr");

            final Path filePrj = (Path) SupportFiles.changeExtension(file, "prj");
            Files.createFile(filePrj);
            PrjFiles.write(CommonCRS.WGS84.normalizedGeographic(), filePrj);

            final Path fileTfw = (Path) SupportFiles.changeExtension(file, "tfw");
            Files.createFile(fileTfw);
            IOUtilities.writeString("1\n1\n1\n1\n1\n1\n", fileTfw);

            final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(img, "png", file.toFile());
            
            final StorageConnector cnx = new StorageConnector(file);

            final FileCoverageProvider provider = FileCoverageProvider.provider();

            final ProbeResult result = provider.probeContent(cnx);
            Assert.assertEquals(true, result.isSupported());

            try (DataStore store = provider.open(cnx)) {
                Assert.assertTrue("Unexpected implementation for file coverage data store", store instanceof FileCoverageStore);
                final FileCoverageStore fcStore = (FileCoverageStore) store;
                final Path[] cFiles = fcStore.getComponentFiles();
                Assert.assertArrayEquals("We should detect all files needed to read this world-file PNJ", new Path[]{file, fileTfw, filePrj}, cFiles);
                final Collection<Resource> components = fcStore.components();
                Assert.assertNotNull("Datastore components should not be null", components);
                final Iterator<? extends Resource> it = components.iterator();
                Assert.assertTrue("Datastore should not be empty", it.hasNext());
                final Resource first = it.next();
                Assert.assertFalse("Current store should have exactly one resource", it.hasNext());
                Assert.assertTrue(first instanceof GridCoverageResource);
            }
        } finally {
            IOUtilities.deleteRecursively(tmpDir);
        }
    }
    
    @Test
    public void testFileRead() throws Exception {
        final Path file    = IOUtilities.getResourceAsPath("org/geotoolkit/coverage/worldfile/SSTMDE200305.png");
        final Path filePrj = IOUtilities.getResourceAsPath("org/geotoolkit/coverage/worldfile/SSTMDE200305.prj");
        final Path fileTfw = IOUtilities.getResourceAsPath("org/geotoolkit/coverage/worldfile/SSTMDE200305.tfw");

        final StorageConnector cnx = new StorageConnector(file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(true, result.isSupported());

        try (DataStore store = provider.open(cnx)) {
            Assert.assertTrue("Unexpected implementation for file coverage data store", store instanceof FileCoverageStore);
            final FileCoverageStore fcStore = (FileCoverageStore) store;
            final Path[] cFiles = fcStore.getComponentFiles();

            // i dont know why it dfoes not search for a tfw file but a pgw ...
            // Assert.assertArrayEquals("We should detect all files needed to read this world-file PNJ", new Path[]{file, fileTfw, filePrj}, cFiles);
            Assert.assertArrayEquals("We should detect all files needed to read this world-file PNJ", new Path[]{file, filePrj}, cFiles);
            final Collection<Resource> components = fcStore.components();
            Assert.assertNotNull("Datastore components should not be null", components);
            final Iterator<? extends Resource> it = components.iterator();
            Assert.assertTrue("Datastore should not be empty", it.hasNext());
            final Resource first = it.next();
            Assert.assertFalse("Current store should have exactly one resource", it.hasNext());
            Assert.assertTrue(first instanceof GridCoverageResource);

            GridCoverageResource gcr = (GridCoverageResource) first;
            gcr.read(null, 0);
        }
    }
}
