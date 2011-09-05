/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.referencing.adapters.NetcdfCRSTest;
import org.geotoolkit.test.image.ImageReaderTestBase;
import org.geotoolkit.test.Depend;


/**
 * Base class (when possible) for testing various NetCDF files.
 * Those tests require large test files. For more information, see:
 * <p>
 * <a href="http://hg.geotoolkit.org/geotoolkit/raw-file/tip/modules/coverage/geotk-coverage-sql/src/test/resources/Tests/README.html">About large test files</a>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
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
