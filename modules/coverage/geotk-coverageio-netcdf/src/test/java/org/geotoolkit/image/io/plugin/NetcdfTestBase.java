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

import java.io.IOException;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;

import ucar.nc2.NetcdfFile;
import org.opengis.wrapper.netcdf.IOTestCase;

import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.referencing.adapters.NetcdfCRSTest;
import org.geotoolkit.test.image.ImageReaderTestBase;
import org.geotoolkit.test.Depend;


/**
 * Base class (when possible) for testing various NetCDF files.
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
public abstract strictfp class NetcdfTestBase extends ImageReaderTestBase {
    /**
     * Default constructor for subclasses.
     */
    protected NetcdfTestBase() {
        super(NetcdfImageReader.class);
    }

    /**
     * Opens the given NetCDF file. The file argument should be one of the names listed in the
     * {@link IOUtilities} class. If a test file of the given name exists in this package, it
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

    /**
     * Reads the image using the given image reader, and returns the data as a single raster.
     * The image is optionally shown in a widget if the {@link #viewEnabled} field is set to
     * {@code true}.
     */
    final Raster read(final String method, final NetcdfImageReader reader, final int imageIndex,
            final SpatialImageReadParam param) throws IOException
    {
        final BufferedImage image = reader.read(imageIndex, param);
        this.image = image;
        showCurrentImage(method);
        return image.getRaster();
    }
}
