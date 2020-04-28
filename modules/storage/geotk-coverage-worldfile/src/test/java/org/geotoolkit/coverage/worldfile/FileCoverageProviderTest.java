/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.test.VerifiableStorageConnector;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

/**
 * Tests for FileCoverageStore
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileCoverageProviderTest extends org.geotoolkit.test.TestBase {

    public FileCoverageProviderTest() {
    }

    @Test
    public void testFactory() {

        boolean found = false;
        for(DataStoreProvider fact : DataStores.providers()){
            if(fact instanceof FileCoverageProvider){
                found = true;
            }
        }

        if(!found){
            fail("Factory not found");
        }
    }

    /**
     * Ensure the provider probeContent match for simple image files with *.prj or *.tfw.
     */
    @Test
    public void testProbeContentWorldFile() throws IOException, DataStoreException {

        final Path file = Files.createTempFile("geo", ".png");
        file.toFile().deleteOnExit();
        final Path filePrj = (Path) SupportFiles.changeExtension(file, "prj");
        Files.createFile(filePrj);
        filePrj.toFile().deleteOnExit();
        final Path fileTfw = (Path) SupportFiles.changeExtension(file, "pgw");
        Files.createFile(fileTfw);
        fileTfw.toFile().deleteOnExit();

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file.toFile());

        final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on World-file", file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(true, result.isSupported());
        cnx.closeAllExcept(null);
    }

    /**
     * Ensure the provider probeContent do not match for simple image files
     * without *.prj or *.tfw.
     *
     */
    @Test
    public void testProbeContentNotWorldFile() throws IOException, DataStoreException {

        final Path file = Files.createTempFile("nogeo", ".png");
        file.toFile().deleteOnExit();

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file.toFile());

        final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on single image", file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(false, result.isSupported());
        cnx.closeAllExcept(null);
    }

    /**
     * Verify that probing on an unsupported file format wont corrupt source storage connector
     */
    @Test
    public void probeUnrelatedContent() throws Exception {
        final Path target = Files.createTempFile("unrelated", ".shp");
        try {
            Files.write(target, Arrays.asList("Lorem ipsum", "etc.", "don't want empty file", "end"));

            final StorageConnector cnx = new VerifiableStorageConnector("FileCoverage on unrelated file", target);
            FileCoverageProvider.provider().probeContent(cnx);
            cnx.closeAllExcept(null);
        } finally {
            Files.delete(target);
        }
    }

    @Test
    public void probeAllImageSpis() throws Exception {
        final Path randomData = Files.createTempFile("allSpis", "");
        try {
            final byte[] data = new byte[1024];
            new Random().nextBytes(data);
            Files.write(randomData, data);
            final VerifiableStorageConnector connector = new VerifiableStorageConnector("Probe All Image SPIs", randomData);
            final ImageInputStream iim = connector.getStorageAs(ImageInputStream.class);
            assumeFalse("Image input stream needed.", iim == null);
            for (ImageReaderSpi spi : FileCoverageProvider.SPIS.keySet()) {
                try {
                    spi.canDecodeInput(iim);
                    connector.verifyAll();
                    for (Class type : spi.getInputTypes()) {
                        try {
                            final Object storage = connector.getStorageAs(type);
                            if (storage != null) {
                                spi.canDecodeInput(storage);
                                connector.verifyAll();
                            }
                        } catch (UnconvertibleObjectException e) {
                            // Ignore
                        }
                    }
                } catch (AssertionError e) {
                    throw new AssertionError("SPI: " + spi, e);
                }
            }
        } finally {
            Files.delete(randomData);
        }
    }
}
