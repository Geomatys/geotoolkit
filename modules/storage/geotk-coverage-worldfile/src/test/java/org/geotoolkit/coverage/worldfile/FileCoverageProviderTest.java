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
import javax.imageio.ImageIO;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

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

        final StorageConnector cnx = new StorageConnector(file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(true, result.isSupported());
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

        final StorageConnector cnx = new StorageConnector(file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(false, result.isSupported());

    }

}
