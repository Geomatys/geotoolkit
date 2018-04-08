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
package org.geotoolkit.coverage.filestore;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import javax.imageio.ImageIO;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
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

        final Path file = Files.createTempFile("geo", ".ovr");
        file.toFile().deleteOnExit();

        final Path filePrj = (Path) SupportFiles.changeExtension(file, "prj");
        Files.createFile(filePrj);
        PrjFiles.write(CommonCRS.WGS84.normalizedGeographic(), filePrj);
        filePrj.toFile().deleteOnExit();

        final Path fileTfw = (Path) SupportFiles.changeExtension(file, "tfw");
        Files.createFile(fileTfw);
        IOUtilities.writeString("1\n1\n1\n1\n1\n1\n", fileTfw);
        fileTfw.toFile().deleteOnExit();

        final BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", file.toFile());

        final StorageConnector cnx = new StorageConnector(file);

        final FileCoverageProvider provider = FileCoverageProvider.provider();

        final ProbeResult result = provider.probeContent(cnx);
        Assert.assertEquals(true, result.isSupported());

        try (DataStore store = provider.open(cnx)) {
            Collection<? extends Resource> resources = DataStores.flatten(store, true);
        }


    }

}
