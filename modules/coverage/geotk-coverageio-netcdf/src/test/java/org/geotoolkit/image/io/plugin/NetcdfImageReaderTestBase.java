/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import ucar.nc2.NetcdfFile;
import org.opengis.wrapper.netcdf.IOTestCase;
import org.opengis.test.coverage.image.ImageReaderTestCase;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.referencing.adapters.NetcdfCRSTest;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Base class for testing read operations on various NetCDF files.
 * Those tests require large test files. For more information, see:
 * <p>
 * <a href="http://hg.geotoolkit.org/geotoolkit/files/tip/modules/coverage/geotk-coverage-sql/src/test/resources/Tests/README.html">About large test files</a>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.10
 */
@Depend(NetcdfCRSTest.class)
public abstract strictfp class NetcdfImageReaderTestBase extends ImageReaderTestCase {
    /**
     * Default constructor for subclasses.
     */
    protected NetcdfImageReaderTestBase() {
    }

    /**
     * Tests the registration of the image reader in the Image I/O framework.
     */
    @Test
    public void testRegistrationByFormatName() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("NetCDF");
        assertTrue("Expected a reader.", it.hasNext());
        assertTrue(it.next() instanceof NetcdfImageReader);
        assertFalse("Expected no more reader.", it.hasNext());
    }

    /**
     * Tests the registration by MIME type.
     * Note that more than one writer may be registered.
     */
    @Test
    public void testRegistrationByMIMEType() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType("application/netcdf");
        while (it.hasNext()) {
            if (it.next() instanceof NetcdfImageReader) {
                return;
            }
        }
        fail("Reader not found.");
    }

    /**
     * Returns the file of the given name in the {@code "Geotoolkit.org/Tests"} directory.
     * This directory contains data too big for inclusion in the source code repository.
     * The file is tested for existence using:
     *
     * {@code java
     *     assumeTrue(file.canRead());
     * }
     *
     * Consequently if the file can not be read (typically because the users did not installed
     * those data on its local directory), then the tests after the call to this method are
     * completely skipped.
     *
     * @param  filename The name of the file to get, or {@code null}.
     * @return The name of directory of the given name in the {@code "Geotoolkit.org/Tests"}
     *         directory (never {@code null}).
     *
     * @since 3.20
     */
    protected static File getLocallyInstalledFile(final String filename) {
        return ImageTestBase.getLocallyInstalledFile(filename);
    }

    /**
     * Opens the given NetCDF file. The file argument should be one of the names listed in the
     * {@link IOTestCase} class. If a test file of the given name exists in this package, it
     * will have precedence over the test file defines in the {@code geoapi-netcdf} test module.
     *
     * @param  file The file name.
     * @return The NetCDF file.
     * @throws IOException If an error occurred while opening the file.
     *
     * @since 3.20
     */
    protected static NetcdfFile open(final String file) throws IOException {
        return new IOTestCase() {
            @Override public NetcdfFile open(final String file) throws IOException {
                return super.open(file);
            }
        }.open(file);
    }
}
