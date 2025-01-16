/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage.uri;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.security.DefaultClientSecurity;
import org.geotoolkit.util.NamesExt;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class URIResolverTest {

    /**
     * Test resolving files on the local file system.
     */
    @Test
    public void testLocalFileSystem() throws IOException, DataStoreException {

        final Path dir = Files.createTempDirectory("uritilematrix");
        try {
            final URI dirUri = dir.toUri();
            final URIResolver resolver = new URIResolver(dirUri);
            final GeneralDirectPosition corner = new GeneralDirectPosition(CommonCRS.WGS84.normalizedGeographic());
            corner.setCoordinates(0,0);
            final URITileMatrix matrix = new URITileMatrix(null, dirUri, resolver,
                    DefaultClientSecurity.NO_SECURITY, URITileFormat.PNG,
                    NamesExt.create("test"),
                    corner,
                    new Dimension(100, 100),
                    new int[]{256,256}, 10);

            //test missing tile
            Assert.assertTrue(matrix.getTile(10,20).isEmpty());

            //test existing tile
            Path dir15 = Files.createDirectory(dir.resolve("25"));
            Path file25 = Files.createFile(dir15.resolve("15.png"));
            Assert.assertTrue(matrix.getTile(15,25).isPresent());
        } finally {
            IOUtilities.deleteSilently(dir);
        }
    }

    /**
     * Test resolving files in a zip archive.
     */
    @Test
    public void testZipFileSystem() throws IOException, DataStoreException, URISyntaxException {

        final Path dir = Files.createTempDirectory("uritilematrixzip");
        final Path zip = dir.resolve("test.zip");
        //create empty zip
        Files.write(zip, new byte[]{80,75,05,06,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00});

        try {
            final URI dirUri = new URI(zip.toUri().toString()+"!/");
            final URIResolver resolver = new URIResolver(dirUri);
            final GeneralDirectPosition corner = new GeneralDirectPosition(CommonCRS.WGS84.normalizedGeographic());
            corner.setCoordinates(0,0);
            final URITileMatrix matrix = new URITileMatrix(null, dirUri, resolver,
                    DefaultClientSecurity.NO_SECURITY, URITileFormat.PNG,
                    NamesExt.create("test"),
                    corner,
                    new Dimension(100, 100),
                    new int[]{256,256}, 10);

            //test missing tile
            Assert.assertTrue(matrix.getTile(10,20).isEmpty());

            //create and test existing tile
            Path baseInZip = resolver.toPath(new URI(zip.toUri()+"!/"));
            Path dir15 = Files.createDirectory(baseInZip.resolve("25"));
            Path file25 = Files.createFile(dir15.resolve("15.png"));
            Assert.assertTrue(matrix.getTile(15,25).isPresent());
        } finally {
            IOUtilities.deleteSilently(dir);
        }
    }

    /**
     * Test resolving files in a zip archive using java jar: prefix
     */
    @Test
    public void testJarZipFileSystem() throws IOException, DataStoreException, URISyntaxException {

        final Path dir = Files.createTempDirectory("uritilematrixzip");
        final Path zip = dir.resolve("test.zip");
        //create empty zip
        Files.write(zip, new byte[]{80,75,05,06,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00});

        try {
            final URI dirUri = new URI("jar:"+zip.toUri().toString()+"!/");
            final URIResolver resolver = new URIResolver(dirUri);
            final GeneralDirectPosition corner = new GeneralDirectPosition(CommonCRS.WGS84.normalizedGeographic());
            corner.setCoordinates(0,0);
            final URITileMatrix matrix = new URITileMatrix(null, dirUri, resolver,
                    DefaultClientSecurity.NO_SECURITY, URITileFormat.PNG,
                    NamesExt.create("test"),
                    corner,
                    new Dimension(100, 100),
                    new int[]{256,256}, 10);

            //test missing tile
            Assert.assertTrue(matrix.getTile(10,20).isEmpty());

            //create and test existing tile
            Path baseInZip = resolver.toPath(new URI(zip.toUri()+"!/"));
            Path dir15 = Files.createDirectory(baseInZip.resolve("25"));
            Path file25 = Files.createFile(dir15.resolve("15.png"));
            Assert.assertTrue(matrix.getTile(15,25).isPresent());
        } finally {
            IOUtilities.deleteSilently(dir);
        }
    }
}
